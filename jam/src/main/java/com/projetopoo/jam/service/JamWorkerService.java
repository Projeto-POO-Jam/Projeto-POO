package com.projetopoo.jam.service;

import com.projetopoo.jam.config.rabbitmq.JamStatusRabbitMQConfig;
import com.projetopoo.jam.dto.jam.JamSseDTO;
import com.projetopoo.jam.model.Jam;
import com.projetopoo.jam.model.JamStatus;
import com.projetopoo.jam.repository.JamRepository;
import com.projetopoo.jam.repository.SubscribeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Classe worker do RabbitMQ para mudar o status das jams
 */
@Service
public class JamWorkerService {
    private final JamRepository jamRepository;
    private final SubscribeRepository subscribeRepository;
    private final ModelMapper modelMapper;
    private final SseNotificationService sseNotificationService;
    private final JamProducerService rabbitMQProducerService;
    private final NotificationService notificationService;

    @Autowired
    public JamWorkerService(JamRepository jamRepository,
                            SubscribeRepository subscribeRepository,
                            ModelMapper modelMapper,
                            SseNotificationService sseNotificationService,
                            JamProducerService rabbitMQProducerService,
                            NotificationService notificationService) {
        this.jamRepository = jamRepository;
        this.subscribeRepository = subscribeRepository;
        this.modelMapper = modelMapper;
        this.sseNotificationService = sseNotificationService;
        this.rabbitMQProducerService = rabbitMQProducerService;
        this.notificationService = notificationService;
    }

    @Transactional
    @RabbitListener(queues = JamStatusRabbitMQConfig.QUEUE_NAME)
    public void updateJamStatus(Map<String, Object> messageBody) {
        // Pega informações passadas pela fila
        Long jamId = ((Number) messageBody.get("jamId")).longValue();
        String statusStr = (String) messageBody.get("newJamStatus");
        String jamToken = (String) messageBody.get("jamToken");
        Boolean jamReschedule = (Boolean) messageBody.get("jamReschedule");

        JamStatus newJamStatus = JamStatus.valueOf(statusStr);


        // Verifica se a jam existe
        Optional<Jam> optionalJam = jamRepository.findById(jamId);

        if (optionalJam.isPresent()) {
            Jam jam = optionalJam.get();

            // Verifica se o token recebido é igual ao armazenado no banco de dados
            if(jam.getJamToken().equals(jamToken)) {
                LocalDateTime now = LocalDateTime.now();

                // Verifica se é uma jam que deve ser reagendada
                if(jamReschedule) {
                    if((newJamStatus == JamStatus.ACTIVE) && (jam.getJamStartDate().isAfter(now))){
                        long startDelay = Duration.between(now, jam.getJamStartDate()).toMillis();
                        rabbitMQProducerService.scheduleJamStatusUpdate(jamId, startDelay, newJamStatus.name(), jamToken);
                        return;
                    } else if ((newJamStatus == JamStatus.FINISHED) && (jam.getJamEndDate().isAfter(now))) {
                        long startDelay = Duration.between(now, jam.getJamEndDate()).toMillis();
                        rabbitMQProducerService.scheduleJamStatusUpdate(jamId, startDelay, newJamStatus.name(), jamToken);
                        return;
                    }
                }

                // Atualiza o status
                jam.setJamStatus(newJamStatus);

                // Salva a jam
                jamRepository.save(jam);

                notificationService.createAndSendJamStatusNotification(jam, newJamStatus);

                // Passa a jam para o formato de resposta SSE
                JamSseDTO jamSseDTO = modelMapper.map(jam, JamSseDTO.class);
                jamSseDTO.setJamTotalSubscribers(subscribeRepository.countBySubscribeJam_JamId(jamSseDTO.getJamId()));

                // Envia eventos SSE avisando que o status da jam foi alterado
                sseNotificationService.sendEventToTopic("jams-list-update", "jam-status-update", jamSseDTO);
                sseNotificationService.sendEventToTopic("jams-update", "jam-status-update-" + jam.getJamId(), jamSseDTO);
            }
        }
    }
}

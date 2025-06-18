package com.projetopoo.jam.service;

import com.projetopoo.jam.config.rabbitmq.JamStatusRabbitMQConfig;
import com.projetopoo.jam.dto.JamSseDTO;
import com.projetopoo.jam.model.Jam;
import com.projetopoo.jam.model.JamStatus;
import com.projetopoo.jam.repository.JamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class JamWorkerService {
    @Autowired
    private JamRepository jamRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SseNotificationService sseNotificationService;


    @Transactional
    @RabbitListener(queues = JamStatusRabbitMQConfig.QUEUE_NAME)
    public void updateJamStatus(Map<String, Object> messageBody) {
        Long jamId = ((Number) messageBody.get("jamId")).longValue();
        String statusStr = (String) messageBody.get("newJamStatus");
        JamStatus newJamStatus = JamStatus.valueOf(statusStr);

        Optional<Jam> optionalJam = jamRepository.findById(jamId);

        if (optionalJam.isPresent()) {
            Jam jam = optionalJam.get();
            jam.setJamStatus(newJamStatus);
            jamRepository.save(jam);

            JamSseDTO jamSseDTO = modelMapper.map(jam, JamSseDTO.class);

            sseNotificationService.sendEventToTopic("jams-list-update", "jam-status-update", jamSseDTO);

            sseNotificationService.sendEventToTopic("jams-update", "jam-status-update-" + jam.getJamId(), jamSseDTO);
        }
    }
}

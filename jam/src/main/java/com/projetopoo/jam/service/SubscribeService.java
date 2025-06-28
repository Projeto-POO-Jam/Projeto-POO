package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.subscribe.SubscribeRequestDTO;
import com.projetopoo.jam.dto.subscribe.SubscribeResponseDTO;
import com.projetopoo.jam.dto.subscribe.SubscribeSseDTO;
import com.projetopoo.jam.dto.subscribe.SubscribeTotalResponseDTO;
import com.projetopoo.jam.exception.UserValidationException;
import com.projetopoo.jam.model.*;
import com.projetopoo.jam.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Classe service para classe Jam, responsável pela lógica de negócios
 */
@Service
public class SubscribeService {
    private final UserRepository userRepository;
    private final JamRepository jamRepository;
    private final SubscribeRepository subscribeRepository;
    private final SseNotificationService sseNotificationService;

    /**
     * Constrói uma nova instância de SubscribeService com suas dependências
     * @param userRepository Repository para comunicação com o banco de dados da classe User
     * @param jamRepository Repository para comunicação com o banco de dados da classe Jam
     * @param subscribeRepository Repository para comunicação com o banco de dados da classe Subscribe
     * @param sseNotificationService Classe para envio de eventos via SSE
     */
    @Autowired
    public SubscribeService(UserRepository userRepository,
                            JamRepository jamRepository,
                            SubscribeRepository subscribeRepository,
                            SseNotificationService sseNotificationService) {
        this.userRepository = userRepository;
        this.jamRepository = jamRepository;
        this.subscribeRepository = subscribeRepository;
        this.sseNotificationService = sseNotificationService;
    }

    /**
     * Função para alternar uma inscrição de um usuário,
     * se a inscrição já existe ela será apagada, se não existe ela será criada.
     * @param subscribeRequestDTO Informações sobre a inscrição
     * @param identifier Identificador do usuário
     * @return Inscrição atual
     */
    @Transactional
    public SubscribeResponseDTO toggleSubscribe(SubscribeRequestDTO subscribeRequestDTO, String identifier) {
        SubscribeResponseDTO subscribeResponseDTO = new SubscribeResponseDTO();
        Subscribe subscribe = getSubscribe(subscribeRequestDTO.getSubscribeJamId(), identifier);

        // Verifica se a subscribe existe
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findBySubscribeUserAndSubscribeJam(subscribe.getSubscribeUser(), subscribe.getSubscribeJam());
        if(optionalSubscribe.isPresent()) {
            // Se já existir deleta ela
            subscribeRepository.delete(optionalSubscribe.get());
            subscribeResponseDTO.setSubscribed(false);
        } else {
            // Se não existir salva ela
            subscribeRepository.save(subscribe);
            subscribeResponseDTO.setSubscribed(true);
        }

        // Pega quantidade de inscrições atuais
        SubscribeTotalResponseDTO subscribeTotalResponseDTO = totalSubscribes(subscribeRequestDTO.getSubscribeJamId());
        SubscribeSseDTO subscribeSseDTO = new SubscribeSseDTO();
        subscribeSseDTO.setSubscribeJamId(subscribeRequestDTO.getSubscribeJamId());
        subscribeSseDTO.setSubscribeTotal(subscribeTotalResponseDTO.getSubscribeTotal());

        // Envia eventos SSE avisando que o total de inscritos foi alterado na jam
        sseNotificationService.sendEventToTopic("jams-list-update", "jam-subscribes-update", subscribeSseDTO);
        sseNotificationService.sendEventToTopic("jams-update", "jam-subscribes-update-" + subscribeRequestDTO.getSubscribeJamId(), subscribeTotalResponseDTO);

        return subscribeResponseDTO;
    }

    /**
     * Função para buscar o status atual de inscrição de um usuário em uma jam
     * @param jamId Id da jam que está sendo usada na consulta
     * @param identifier Identificador do usuário
     * @return Inscrição atual
     */
    @Transactional
    public SubscribeResponseDTO findSubscribe(Long jamId, String identifier) {
        SubscribeResponseDTO subscribeResponseDTO = new SubscribeResponseDTO();
        Subscribe subscribe = getSubscribe(jamId, identifier);

        // Verifica se a inscrição existe
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findBySubscribeUserAndSubscribeJam(subscribe.getSubscribeUser(), subscribe.getSubscribeJam());
        subscribeResponseDTO.setSubscribed(optionalSubscribe.isPresent());
        return subscribeResponseDTO;
    }

    /**
     * Função para buscar o total de inscrições em uma jam
     * @param jamId Id da jam que está sendo usada na consulta
     * @return Total de inscrições na jam
     */
    @Transactional
    public SubscribeTotalResponseDTO totalSubscribes(Long jamId) {
        SubscribeTotalResponseDTO subscribeTotalResponseDTO = new SubscribeTotalResponseDTO();

        // Busca total de inscritos na jam
        subscribeTotalResponseDTO.setSubscribeTotal(subscribeRepository.countBySubscribeJam_JamId(jamId));
        return subscribeTotalResponseDTO;
    }

    /**
     * Função que constrói uma Subscribe
     * @param jamId Id da jam que será usada na construção
     * @param identifier Identificador do usuário
     * @return Subscribe com Jam e User
     */
    @Transactional
    public Subscribe getSubscribe(Long jamId, String identifier) {
        Subscribe subscribe = new Subscribe();

        // Busca as informações sobre o usuário que fez a solicitação
        subscribe.setSubscribeUser(userRepository.findByIdentifier(identifier));

        // Verifica se a jam existe
        Optional<Jam> optionalJam= jamRepository.findByJamId(jamId);
        if (optionalJam.isEmpty()) {
            throw new EntityNotFoundException("Jogo com o ID " + jamId + " não encontrado.");
        }

        subscribe.setSubscribeJam(optionalJam.get());
        return subscribe;
    }
}

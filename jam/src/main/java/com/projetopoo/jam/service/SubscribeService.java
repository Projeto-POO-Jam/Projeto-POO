package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.SubscribeRequestDTO;
import com.projetopoo.jam.dto.SubscribeResponseDTO;
import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.Jam;
import com.projetopoo.jam.model.Subscribe;
import com.projetopoo.jam.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SubscribeService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JamRepository jamRepository;
    @Autowired
    private SubscribeRepository subscribeRepository;

    @Transactional
    public SubscribeResponseDTO toggleSubscribe(SubscribeRequestDTO subscribeRequestDTO, String identifier) {

        SubscribeResponseDTO subscribeResponseDTO = new SubscribeResponseDTO();

        Subscribe subscribe = getSubscribe(subscribeRequestDTO, identifier);
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findBySubscribeUserAndSubscribeJam(subscribe.getSubscribeUser(), subscribe.getSubscribeJam());

        if(optionalSubscribe.isPresent()) {
            subscribeRepository.delete(optionalSubscribe.get());
            subscribeResponseDTO.setSubscribed(false);
            return subscribeResponseDTO;
        } else {
            subscribeRepository.save(subscribe);
            subscribeResponseDTO.setSubscribed(true);
            return subscribeResponseDTO;
        }
    }

    @Transactional
    public Subscribe getSubscribe(SubscribeRequestDTO subscribeRequestDTO, String identifier) {
        Subscribe subscribe = new Subscribe();

        subscribe.setSubscribeUser(userRepository.findByIdentifier(identifier));

        Optional<Jam> optionalJam= jamRepository.findByJamId(subscribeRequestDTO.getSubscribeJamId());
        if (optionalJam.isEmpty()) {
            throw new EntityNotFoundException("Jogo com o ID " + subscribeRequestDTO.getSubscribeJamId() + " não encontrado.");
        }
        subscribe.setSubscribeJam(optionalJam.get());
        return subscribe;
    }

}

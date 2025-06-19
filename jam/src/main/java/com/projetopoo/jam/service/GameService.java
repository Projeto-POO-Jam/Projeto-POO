package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.GameResquestDTO;
import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.Jam;
import com.projetopoo.jam.model.Subscribe;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.GameRepository;
import com.projetopoo.jam.repository.JamRepository;
import com.projetopoo.jam.repository.SubscribeRepository;
import com.projetopoo.jam.repository.UserRepository;
import com.projetopoo.jam.util.ImageUtil;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private JamRepository jamRepository;

    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/upload/game/";

    @Transactional
    public void createGame(GameResquestDTO gameResquestDTO, String identifier) throws IOException {

        Game game = modelMapper.map(gameResquestDTO, Game.class);
        User user = userRepository.findByIdentifier(identifier);
        Optional<Jam> jam = jamRepository.findByJamId(gameResquestDTO.getJamId());

        if(jam.isPresent()){
           Optional<Subscribe> subscribe = subscribeRepository.findBySubscribeUserAndSubscribeJam(user,jam.get());
           if(subscribe.isPresent()){
               if(gameRepository.existsGameByGameSubscribe(subscribe.get())){
                   throw new IllegalArgumentException("Jogo já cadastrado ");
               }
               else{
                   String uuid = UUID.randomUUID().toString();
                   game.setGamePhoto(ImageUtil.createImage(gameResquestDTO.getGamePhoto(), UPLOAD_DIRECTORY + uuid + "/img", "/upload/game/" + uuid + "/img/"));
                   game.setGameFile(ImageUtil.createImage(gameResquestDTO.getGameFile(), UPLOAD_DIRECTORY + uuid + "/file", "/upload/game/" + uuid + "/file/"));
                   //Descompactar
                   gameRepository.save(game);
               }
           }
           else {
               throw new EntityNotFoundException("Subscribe não encontrada");
           }
        }
        else{
            throw new EntityNotFoundException("Jam não encontrada");
        }
    }
}

package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.GameResquestDTO;
import com.projetopoo.jam.dto.JamResponse;
import com.projetopoo.jam.dto.GameResponseDTO;
import com.projetopoo.jam.dto.UserResponseDTO;
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
        game.setGameId(null);
        User user = userRepository.findByIdentifier(identifier);
        Optional<Jam> jam = jamRepository.findByJamId(gameResquestDTO.getJamId());

        if(jam.isPresent()){
            Optional<Subscribe> subscribe = subscribeRepository.findBySubscribeUserAndSubscribeJam(user,jam.get());
            if(subscribe.isPresent()){
                Subscribe gameSubscribe = subscribe.get();
                if(gameSubscribe.getSubscribeGame() != null){
                    throw new IllegalArgumentException("Jogo já cadastrado ");
                }
                else{
                    String uuid = UUID.randomUUID().toString();
                    game.setGamePhoto(ImageUtil.createImage(gameResquestDTO.getGamePhoto(), UPLOAD_DIRECTORY + uuid + "/img", "/upload/game/" + uuid + "/img/"));
                    game.setGameFile(ImageUtil.createImage(gameResquestDTO.getGameFile(), UPLOAD_DIRECTORY + uuid + "/file", "/upload/game/" + uuid + "/file/"));
                    //Descompactar
                    game = gameRepository.save(game);
                    gameSubscribe.setSubscribeGame(game);
                    subscribeRepository.save(gameSubscribe);
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

    @Transactional
    public GameResponseDTO findGame(Long gameId) {
        Optional<Game> optionalGame = gameRepository.findByGameId(gameId);
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("Game com o ID " + gameId + " não encontrado.");
        }
        GameResponseDTO gameResponse = modelMapper.map(optionalGame.get(), GameResponseDTO.class);

        Optional<Subscribe> subcribe = subscribeRepository.findBySubscribeGame(optionalGame.get());
        if (subcribe.isEmpty()) {
            throw new EntityNotFoundException("Incrição com o GameID " + gameId + " não encontrado.");
        }

        UserResponseDTO userResponseDTO = modelMapper.map(subcribe.get().getSubscribeUser(), UserResponseDTO.class);
        gameResponse.setUserResponseDTO(userResponseDTO);
        return gameResponse;
    }
}
package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        User user = subscribeRepository.findBySubscribeGame(optionalGame.get());
        gameResponse.setUserResponseDTO(subscribeRepository.findBySubscribeUser(user));
        return gameResponse;
    }

    /*
    @Transactional(readOnly = true)
    public GameResponseDTO findGameList(int idJam, int offset, int limit) {

        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, "jamStartDate"));

        List<GameResponseDTO> gameResponseDTOList = jamPage.getContent().stream()
                .map(game -> {
                    GameResponseDTO gameResponseDTO = modelMapper.map(Game, GameResponseDTO.class);
                    //GameSummaryDTO.setJamTotalSubscribers(subscribeRepository.countBySubscribeJam_JamId(jam.getJamId()));
                    return gameResponseDTO;
                })
                .collect(Collectors.toList());

        //return new JamPaginatedResponseDTO(gameResponseDTOList, jamPage.getTotalElements());
    }*/

}
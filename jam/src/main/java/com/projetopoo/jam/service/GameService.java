package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.*;
import com.projetopoo.jam.model.*;
import com.projetopoo.jam.repository.*;
import com.projetopoo.jam.util.ImageUtil;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @Autowired
    private VoteRepository voteRepository;

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

    @Transactional
    public GamePaginatedResponseDTO findGameList(Long jamId, int offset, int limit){

        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, "gameId"));

        Page<Game> gamePage = gameRepository.findByGameSubscribe_SubscribeJam_JamId(jamId, pageable);

        List<GameSummaryDTO> gameSummaryDTOList = gamePage.getContent().stream()
                .map(game -> {
                    GameSummaryDTO gameSummaryDTO = modelMapper.map(game, GameSummaryDTO.class);
                    gameSummaryDTO.setGameVoteTotal(voteRepository.countByVoteGame_GameId(game.getGameId()));
                    return gameSummaryDTO;
                })
                .collect(Collectors.toList());

        return new GamePaginatedResponseDTO(gameSummaryDTOList, gamePage.getTotalElements());
    }

    @Transactional
    public void updateGame(GameResquestDTO gameRequestDTO, String identifier) throws IOException {
        List<String> validationErrors = new ArrayList<>();

        Game game = modelMapper.map(gameRequestDTO, Game.class);

        Optional<Game> optionalGame = gameRepository.findByGameId(game.getGameId());

        if (optionalGame.isPresent()) {
            Game existingGame = optionalGame.get();

            User user = userRepository.findByIdentifier(identifier);

            if(subscribeRepository.existsBySubscribeGameAndSubscribeUser(optionalGame.get(),user)) {
                if (gameRequestDTO.getGamePhoto() != null && !gameRequestDTO.getGamePhoto().isEmpty()) {
                    String oldPhotoPath = existingGame.getGamePhoto();

                    String uuid = UUID.randomUUID().toString();
                    String directoryCover = UPLOAD_DIRECTORY + "/" + uuid + "/img";
                    existingGame.setGamePhoto(ImageUtil.createImage(gameRequestDTO.getGamePhoto(), directoryCover, "/upload/game/" + uuid + "/img/"));

                    ImageUtil.deleteImage(oldPhotoPath);
                    gameRequestDTO.setGamePhoto(null);
                }

                if (gameRequestDTO.getGameFile() != null && !gameRequestDTO.getGameFile().isEmpty()) {
                    String oldPhotoPath = existingGame.getGameFile();

                    String uuid = UUID.randomUUID().toString();
                    String directoryCover = UPLOAD_DIRECTORY + "/"  + uuid + "/file";
                        existingGame.setGameFile(ImageUtil.createImage(gameRequestDTO.getGameFile(), directoryCover, "/upload/game/" + uuid + "/file/"));

                    ImageUtil.deleteImage(oldPhotoPath);
                    gameRequestDTO.setGameFile(null);
                }

                modelMapper.map(gameRequestDTO, existingGame);
                gameRepository.save(existingGame);


            } else {
                throw new AccessDeniedException("Usuário não autorizado a alterar a Game.");
            }
        } else {
            throw new EntityNotFoundException("Game com o ID " + game.getGameId() + " não encontrada.");
        }
    }

}
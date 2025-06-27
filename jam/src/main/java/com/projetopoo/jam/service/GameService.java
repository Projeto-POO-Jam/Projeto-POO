package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.game.GamePaginatedResponseDTO;
import com.projetopoo.jam.dto.game.GameResponseDTO;
import com.projetopoo.jam.dto.game.GameResquestDTO;
import com.projetopoo.jam.dto.game.GameSummaryDTO;
import com.projetopoo.jam.dto.user.UserResponseDTO;
import com.projetopoo.jam.dto.user.UserWithCurrentResponseDTO;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

    @Autowired
    private GameProducerService gameProducerService;

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
                    game.setGamePhoto(ImageUtil.createImage(gameResquestDTO.getGamePhoto(), UPLOAD_DIRECTORY + "img/" + uuid, "/upload/game/img/" + uuid + "/"));

                    uuid = UUID.randomUUID().toString();
                    game.setGameFile(ImageUtil.createImage(gameResquestDTO.getGameFile(), UPLOAD_DIRECTORY + "file/" + uuid, "/upload/game/file/" + uuid + "/"));

                    uuid = UUID.randomUUID().toString();
                    game.setGameToken(uuid);

                    game = gameRepository.save(game);
                    gameSubscribe.setSubscribeGame(game);
                    subscribeRepository.save(gameSubscribe);

                    gameProducerService.scheduleGameStatusUpdate(game.getGameId(), uuid);
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
    public GameResponseDTO findGame(Long gameId, String identifier) {
        Optional<Game> optionalGame = gameRepository.findByGameId(gameId);
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("Game com o ID " + gameId + " não encontrado.");
        }
        GameResponseDTO gameResponse = modelMapper.map(optionalGame.get(), GameResponseDTO.class);

        Optional<Subscribe> subcribe = subscribeRepository.findBySubscribeGame(optionalGame.get());
        if (subcribe.isEmpty()) {
            throw new EntityNotFoundException("Incrição com o GameID " + gameId + " não encontrado.");
        }

        UserWithCurrentResponseDTO userWithCurrentResponseDTO = modelMapper.map(subcribe.get().getSubscribeUser(), UserWithCurrentResponseDTO.class);
        User user = userRepository.findByIdentifier(identifier);
        userWithCurrentResponseDTO.setUserCurrent(user.getUserId().equals(userWithCurrentResponseDTO.getUserId()));
        gameResponse.setUserResponseDTO(userWithCurrentResponseDTO);
        return gameResponse;
    }

    @Transactional
    public GamePaginatedResponseDTO findGameList(Long jamId, int offset, int limit){

        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, "gameId"));

        Page<Game> gamePage = gameRepository.findByJamIdOrderByVotes(jamId, pageable);

        return addGameTotal(gamePage);
    }

    @Transactional
    public GamePaginatedResponseDTO findGameListByUserId(Long userId, int offset, int limit){

        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, "gameId"));

        Page<Game> gamePage = gameRepository.findByUserIdOrderByVotes(userId, pageable);

        return addGameTotal(gamePage);
    }

    @Transactional
    public GamePaginatedResponseDTO findGameCompleteList(int offset, int limit){

        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.ASC, "gameId"));

        Page<Game> gamePage = gameRepository.findAllByOrderByVotes(pageable);

        return addGameTotal(gamePage);
    }

    private GamePaginatedResponseDTO addGameTotal(Page<Game> gamePage) {
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
        //Game game = modelMapper.map(gameRequestDTO, Game.class);
        User user = userRepository.findByIdentifier(identifier);
        Optional<Jam> jam = jamRepository.findByJamId(gameRequestDTO.getJamId());

        if(jam.isPresent()) {
            Optional<Subscribe> subscribe = subscribeRepository.findBySubscribeUserAndSubscribeJam(user, jam.get());
            if (subscribe.isPresent()) {
                Game existingGame = subscribe.get().getSubscribeGame();
                if (existingGame != null) {
                    if (gameRequestDTO.getGamePhoto() != null && !gameRequestDTO.getGamePhoto().isEmpty()) {
                        String oldPhotoPath = existingGame.getGamePhoto();

                        String uuid = UUID.randomUUID().toString();
                        String directoryCover = UPLOAD_DIRECTORY + "img/" + uuid;
                        existingGame.setGamePhoto(ImageUtil.createImage(gameRequestDTO.getGamePhoto(), directoryCover, "/upload/game/img/" + uuid + "/"));

                        ImageUtil.deleteImage(oldPhotoPath);
                        gameRequestDTO.setGamePhoto(null);
                    }

                    if (gameRequestDTO.getGameFile() != null && !gameRequestDTO.getGameFile().isEmpty()) {
                        String oldPhotoPath = existingGame.getGameFile();

                        String uuid = UUID.randomUUID().toString();
                        String directoryCover = UPLOAD_DIRECTORY + "file/" + uuid;
                        existingGame.setGameFile(ImageUtil.createImage(gameRequestDTO.getGameFile(), directoryCover, "/upload/game/file/" + uuid + "/"));

                        ImageUtil.deleteImage(oldPhotoPath);
                        ImageUtil.deleteDirectory(oldPhotoPath);

                        gameRequestDTO.setGameFile(null);
                    }

                    gameRequestDTO.setJamId(null);
                    modelMapper.map(gameRequestDTO, existingGame);

                    String uuid = UUID.randomUUID().toString();
                    existingGame.setGameToken(uuid);

                    gameRepository.save(existingGame);

                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            gameProducerService.scheduleGameStatusUpdate(existingGame.getGameId(), uuid);
                        }
                    });


                } else {
                    throw new EntityNotFoundException("Game não encontrada.");
                }
            } else {
                throw new EntityNotFoundException("Inscrição não encontrada.");
            }

        } else {
            throw new EntityNotFoundException("Jam com o ID " + gameRequestDTO.getJamId() + " não encontrada.");
        }
    }

}
package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.game.*;
import com.projetopoo.jam.dto.user.UserWithCurrentResponseDTO;
import com.projetopoo.jam.model.*;

import com.projetopoo.jam.repository.*;
import com.projetopoo.jam.util.FileUtil;

import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe service para classe Game, responsável pela lógica de negócios
 */
@Service
public class GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final SubscribeRepository subscribeRepository;
    private final JamRepository jamRepository;
    private final VoteRepository voteRepository;
    private final ModelMapper modelMapper;
    private final GameProducerService gameProducerService;
    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/upload/game/";

    /**
     * Constrói uma nova instância de GameService com suas dependências
     * @param gameRepository Repository para comunicação com o banco de dados da classe Game
     * @param userRepository Repository para comunicação com o banco de dados da classe User
     * @param subscribeRepository Repository para comunicação com o banco de dados da classe Subscribe
     * @param jamRepository Repository para comunicação com o banco de dados da classe Jam
     * @param voteRepository Repository para comunicação com o banco de dados da classe Vote
     * @param modelMapper Classe para mapear transformações entre models e DTOs
     * @param gameProducerService Classe para adicionar itens a fila do RabbitMQ
     */
    @Autowired
    public GameService(GameRepository gameRepository,
                       UserRepository userRepository,
                       SubscribeRepository subscribeRepository,
                       JamRepository jamRepository,
                       VoteRepository voteRepository,
                       ModelMapper modelMapper,
                       GameProducerService gameProducerService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.subscribeRepository = subscribeRepository;
        this.jamRepository = jamRepository;
        this.voteRepository = voteRepository;
        this.modelMapper = modelMapper;
        this.gameProducerService = gameProducerService;
    }

    /**
     * Função para criar um novo jogo
     * @param gameRequestDTO Informações sobre o jogo
     * @param identifier Identificador do usuário
     * @throws IOException Pode gerar exceção no caso de erro ao salvar alguma imagem ou arquivo
     */
    @Transactional
    public void createGame(GameRequestDTO gameRequestDTO, String identifier) throws IOException {

        // Passa as informações recebidas para o formato de Game
        Game game = modelMapper.map(gameRequestDTO, Game.class);

        // Coloca o valor do gameId como null para evitar conflito com o jamId da gameRequestDTO no modelMapper
        game.setGameId(null);

        // Busca usuário que fez a requisição
        User user = userRepository.findByIdentifier(identifier);

        // Verifica se a jam existe
        Optional<Jam> optionalJam = jamRepository.findByJamId(gameRequestDTO.getJamId());
        if(optionalJam.isPresent()){

            // Verifica se a inscrição existe
            Optional<Subscribe> optionalSubscribe = subscribeRepository.findBySubscribeUserAndSubscribeJam(user,optionalJam.get());
            if(optionalSubscribe.isPresent()){
                Subscribe gameSubscribe = optionalSubscribe.get();

                // Verifica se a inscrição já não tem um jogo cadastrado
                if(gameSubscribe.getSubscribeGame() != null){
                    throw new IllegalArgumentException("Jogo já cadastrado ");
                } else {
                    // Salva a imagem
                    game.setGamePhoto(FileUtil.createFile(gameRequestDTO.getGamePhoto(), UPLOAD_DIRECTORY + "img/", "/upload/game/img/"));

                    // Salva o arquivo compactado do jogo
                    String uuid = UUID.randomUUID().toString();
                    game.setGameFile(FileUtil.createFile(gameRequestDTO.getGameFile(), UPLOAD_DIRECTORY + "file/" + uuid, "/upload/game/file/" + uuid + "/"));

                    // Gera um token de versão para o worker do RabbitMQ
                    uuid = UUID.randomUUID().toString();
                    game.setGameToken(uuid);

                    // Salva o jogo
                    game = gameRepository.save(game);

                    // Atualiza a inscrição
                    gameSubscribe.setSubscribeGame(game);
                    subscribeRepository.save(gameSubscribe);

                    // Adiciona o jogo na fila do RabbitMQ para descompactar
                    gameProducerService.scheduleGameStatusUpdate(game.getGameId(), uuid);
                }
            } else {
                throw new EntityNotFoundException("Subscribe não encontrada");
            }
        } else {
            throw new EntityNotFoundException("Jam não encontrada");
        }
    }

    /**
     * Função para atualizar as informações sobre um jogo
     * @param gameUpdateRequestDTO Novas informações sobre o jogo
     * @param identifier Identificador do usuário
     * @throws IOException Pode gerar exceção no caso de erro ao salvar alguma imagem ou arquivo
     */
    @Transactional
    public void updateGame(GameUpdateRequestDTO gameUpdateRequestDTO, String identifier) throws IOException {
        // Busca usuário que fez a requisição
        User user = userRepository.findByIdentifier(identifier);

        // Verifica se o jogo existe
        Optional<Game> optionalGame = gameRepository.findByGameId(gameUpdateRequestDTO.getGameId());
        if(optionalGame.isPresent()){
            Game existingGame = optionalGame.get();

            // Verifica se o usuário que fez a requisição é dono do jogo
            if(user.getUserId().equals(existingGame.getGameSubscribe().getSubscribeUser().getUserId())) {

                // Verifica se a foto foi modificada
                if (gameUpdateRequestDTO.getGamePhoto() != null && !gameUpdateRequestDTO.getGamePhoto().isEmpty()) {
                    String oldPhotoPath = existingGame.getGamePhoto();

                    // Salva a foto nova
                    String directoryCover = UPLOAD_DIRECTORY + "img/";
                    existingGame.setGamePhoto(FileUtil.createFile(gameUpdateRequestDTO.getGamePhoto(), directoryCover, "/upload/game/img/"));

                    // Exclui a foto antiga
                    FileUtil.deleteFile(oldPhotoPath);
                    gameUpdateRequestDTO.setGamePhoto(null);
                }

                // Verifica se o arquivo do jogo foi modificado
                if (gameUpdateRequestDTO.getGameFile() != null && !gameUpdateRequestDTO.getGameFile().isEmpty()) {
                    String oldPhotoPath = existingGame.getGameFile();

                    // Salva novo arquivo
                    String uuid = UUID.randomUUID().toString();
                    String directoryCover = UPLOAD_DIRECTORY + "file/" + uuid;
                    existingGame.setGameFile(FileUtil.createFile(gameUpdateRequestDTO.getGameFile(), directoryCover, "/upload/game/file/" + uuid + "/"));

                    // Exclui diretório do jogo antigo
                    FileUtil.deleteFile(oldPhotoPath);
                    FileUtil.deleteDirectory(oldPhotoPath);

                    gameUpdateRequestDTO.setGameFile(null);
                }

                // Passa as informações recebidas para o formato de Game
                modelMapper.map(gameUpdateRequestDTO, existingGame);

                // Gera um token de versão para o worker do RabbitMQ
                String uuid = UUID.randomUUID().toString();
                existingGame.setGameToken(uuid);

                // Salva o jogo
                gameRepository.save(existingGame);

                // Espera até que o save ser finalizado
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // Adiciona o jogo na fila do RabbitMQ para descompactar
                        gameProducerService.scheduleGameStatusUpdate(existingGame.getGameId(), uuid);
                    }
                });
            } else {
                throw new AccessDeniedException("O usuário não tem permissão para editar esse jogo");
            }


        } else {
            throw new EntityNotFoundException("Game com o ID " + gameUpdateRequestDTO.getGameId() + " não encontrada.");
        }
    }

    /**
     * Função para buscar um jogo pelo Id dele
     * @param gameId Id do jogo que está sendo usado na consulta
     * @param identifier Identificador do usuário
     * @return Informações sobre o jogo
     */
    @Transactional
    public GameResponseDTO findGame(Long gameId, String identifier) {
        // Verifica se o jogo existe
        Optional<Game> optionalGame = gameRepository.findByGameId(gameId);
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("Game com o ID " + gameId + " não encontrado.");
        }

        // Passa o jogo para o formato de resposta
        GameResponseDTO gameResponse = modelMapper.map(optionalGame.get(), GameResponseDTO.class);

        // Verifica se a inscrição existe
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findBySubscribeGame(optionalGame.get());
        if (optionalSubscribe.isEmpty()) {
            throw new EntityNotFoundException("inscrição com o GameID " + gameId + " não encontrado.");
        }

        // Passa o usuário da inscrição para o formato de resposta
        UserWithCurrentResponseDTO userWithCurrentResponseDTO = modelMapper.map(optionalSubscribe.get().getSubscribeUser(), UserWithCurrentResponseDTO.class);

        // Verifica se o usuário que fez a requisição é o dono do jogo
        User user = userRepository.findByIdentifier(identifier);
        userWithCurrentResponseDTO.setUserCurrent(user.getUserId().equals(userWithCurrentResponseDTO.getUserId()));
        gameResponse.setUserResponseDTO(userWithCurrentResponseDTO);

        return gameResponse;
    }

    /**
     * Função para buscar todos os jogos de uma jam de forma paginada
     * @param jamId Id da jam que está sendo usado na consulta
     * @param offset Offset para a busca
     * @param limit Limite de itens a serem retornados
     * @return Lista com informações dos jogos, com o total de jogos que podem ser buscados
     */
    @Transactional
    public GamePaginatedResponseDTO findGameList(Long jamId, int offset, int limit){
        // Define qual é a pagina de interesse
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        // Busca a lista paginada de jogos
        Page<Game> gamePage = gameRepository.findByJamIdOrderByVotes(jamId, pageable);

        // Adiciona a informação do total de votos em cada jogo
        return addGameTotal(gamePage);
    }

    /**
     * Função para buscar todos os jogos de um usuário de forma paginada
     * @param userId Id do usuário que está sendo usado na consulta
     * @param offset Offset para a busca
     * @param limit Limite de itens a serem retornados
     * @return Lista com informações dos jogos, com o total de jogos que podem ser buscados
     */
    @Transactional
    public GamePaginatedResponseDTO findGameListByUserId(Long userId, int offset, int limit){
        // Define qual é a pagina de interesse
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        // Busca a lista paginada de jogos
        Page<Game> gamePage = gameRepository.findByUserIdOrderByVotes(userId, pageable);

        // Adiciona a informação do total de votos em cada jogo
        return addGameTotal(gamePage);
    }

    /**
     * Função para buscar todos os jogos de forma paginada
     * @param offset Offset para a busca
     * @param limit Limite de itens a serem retornados
     * @return Lista com informações dos jogos, com o total de jogos que podem ser buscados
     */
    @Transactional
    public GamePaginatedResponseDTO findGameCompleteList(int offset, int limit){
        // Define qual é a pagina de interesse
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        // Busca a lista paginada de jogos
        Page<Game> gamePage = gameRepository.findAllByOrderByVotes(pageable);

        // Adiciona a informação do total de votos em cada jogo
        return addGameTotal(gamePage);
    }

    /**
     * Função para buscar todos os jogos em que o usuário votou, de forma paginada
     * @param offset Offset para a busca
     * @param limit Limite de itens a serem retornados
     * @return Lista com informações dos jogos, com o total de jogos que podem ser buscados
     */
    @Transactional
    public GamePaginatedResponseDTO findGameListByUserIdVote(Long userId, int offset, int limit){
        // Define qual é a pagina de interesse
        int pageNumber = offset / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit);

        // Busca a lista paginada de jogos
        Page<Game> gamePage = gameRepository.findByUserIdOrderByGameId(userId, pageable);

        // Adiciona a informação do total de votos em cada jogo
        return addGameTotal(gamePage);
    }

    /**
     * Função para adicionar o total de votos de cada jogo
     * @param gamePage Lista paginada de jogos
     * @return Lista com informações dos jogos, com o total de jogos que podem ser buscados e o total de votos em cada jogo
     */
    private GamePaginatedResponseDTO addGameTotal(Page<Game> gamePage) {

        // Passa a lista paginada para o formato da resposta
        List<GameSummaryDTO> gameSummaryDTOList = gamePage.getContent().stream()
                .map(game -> {
                    GameSummaryDTO gameSummaryDTO = modelMapper.map(game, GameSummaryDTO.class);

                    //Adiciona o total de votos
                    gameSummaryDTO.setGameVoteTotal(voteRepository.countByVoteGame_GameId(game.getGameId()));
                    return gameSummaryDTO;
                })
                .collect(Collectors.toList());

        return new GamePaginatedResponseDTO(gameSummaryDTOList, gamePage.getTotalElements());
    }

    /**
     * Função para excluir o total um jogo
     * @param gameId Id do game que está sendo usado
     * @param identifier Identificador do usuário
     */
    @Transactional
    public void deleteGame(Long gameId, String identifier) throws IOException {
        // Busca o jogo que será apagado
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("game com o ID " + gameId + " não encontrado.");
        }

        Game game = optionalGame.get();

        // Busca usuário que fez a requisição
        User requestingUser = userRepository.findByIdentifier(identifier);

        if (!(Objects.equals(game.getGameSubscribe().getSubscribeUser().getUserId(), requestingUser.getUserId()))) {
            throw new AccessDeniedException("Usuário não autorizado a excluir este game.");
        }

        // Busca a inscrição associada
        Optional<Subscribe> optionalSubscribe = subscribeRepository.findBySubscribeGame(game);
        if (optionalSubscribe.isPresent()) {
            Subscribe subscribe = optionalSubscribe.get();

            // Desvincula o jogo da inscrição
            subscribe.setSubscribeGame(null);
            subscribeRepository.save(subscribe);
        }

        // Apaga a foto do jogo
        if (game.getGamePhoto() != null && !game.getGamePhoto().isEmpty()) {
            FileUtil.deleteDirectory(game.getGamePhoto());
        }

        // Apaga o arquivo do jogo
        if (game.getGameFile() != null && !game.getGameFile().isEmpty()) {
            FileUtil.deleteDirectory(game.getGameFile());
        }

        // Apaga o jogo
        gameRepository.delete(game);
    }
}
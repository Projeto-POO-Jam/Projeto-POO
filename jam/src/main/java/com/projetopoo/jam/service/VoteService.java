package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.vote.VoteRequestDTO;
import com.projetopoo.jam.dto.vote.VoteResponseDTO;
import com.projetopoo.jam.dto.vote.VoteTotalResponseDTO;
import com.projetopoo.jam.exception.UserValidationException;
import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.Vote;
import com.projetopoo.jam.repository.GameRepository;
import com.projetopoo.jam.repository.UserRepository;
import com.projetopoo.jam.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Classe service para classe Vote, responsável pela lógica de negócios
 */
@Service
public class VoteService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final VoteRepository voteRepository;
    private final SseNotificationService sseNotificationService;

    /**
     * Constrói uma nova instância de VoteService com suas dependências
     * @param userRepository Repository para comunicação com o banco de dados da classe User
     * @param gameRepository Repository para comunicação com o banco de dados da classe Game
     * @param voteRepository Repository para comunicação com o banco de dados da classe Vote
     * @param sseNotificationService Classe para envio de eventos via SSE
     */
    @Autowired
    public VoteService(UserRepository userRepository,
                       GameRepository gameRepository,
                       VoteRepository voteRepository,
                       SseNotificationService sseNotificationService) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.voteRepository = voteRepository;
        this.sseNotificationService = sseNotificationService;
    }

    /**
     * Função para alternar um voto de um usuário,
     * se o voto já existe ele será apaga, se não existe ele será criado.
     * @param voteRequestDTO Informações sobre o voto
     * @param identifier Identificador do usuário
     * @return Voto atual
     */
    @Transactional
    public VoteResponseDTO toggleVote(VoteRequestDTO voteRequestDTO, String identifier) {
        VoteResponseDTO voteResponseDTO = new VoteResponseDTO();

        Vote vote = getVote(voteRequestDTO.getVoteGameId(), identifier);

        // Verifica se o voto já existe
        Optional<Vote> optionalVote = voteRepository.findByVoteUserAndVoteGame(vote.getVoteUser(), vote.getVoteGame());
        if(optionalVote.isPresent()) {
            // Se já existir deleta ele
            voteRepository.delete(optionalVote.get());
            voteResponseDTO.setVoted(false);
        } else {
            // Se não existir salva ele
            voteRepository.save(vote);
            voteResponseDTO.setVoted(true);
        }

        // Pega quantidade de votos atuais
        VoteTotalResponseDTO voteTotalResponseDTO = totalVotes(voteRequestDTO.getVoteGameId());

        // Envia eventos SSE avisando que o total de votos foi alterado no jogo
        sseNotificationService.sendEventToTopic("games-update", "votes-update-" + voteRequestDTO.getVoteGameId(), voteTotalResponseDTO);

        return voteResponseDTO;
    }

    /**
     * Função para buscar o status atual de voto de um usuário em um jogo
     * @param gameId Id do jogo que está sendo usado na consulta
     * @param identifier Identificador do usuário
     * @return Inscrição atual
     */
    @Transactional
    public VoteResponseDTO findVote(Long gameId, String identifier) throws UserValidationException {
        VoteResponseDTO voteResponseDTO = new VoteResponseDTO();
        Vote vote = getVote(gameId, identifier);

        // Verifica se a inscrição existe
        Optional<Vote> optionalVote = voteRepository.findByVoteUserAndVoteGame(vote.getVoteUser(), vote.getVoteGame());
        voteResponseDTO.setVoted(optionalVote.isPresent());
        return voteResponseDTO;
    }

    /**
     * Função para buscar o total de votos em um jogo
     * @param gameId Id do jogo que está sendo usado na consulta
     * @return Total de inscrições na jam
     */
    @Transactional
    public VoteTotalResponseDTO totalVotes(Long gameId) {
        VoteTotalResponseDTO voteTotalResponseDTO = new VoteTotalResponseDTO();

        // Busca total de inscritos na jam
        voteTotalResponseDTO.setVoteTotal(voteRepository.countByVoteGame_GameId(gameId));
        return voteTotalResponseDTO;
    }

    /**
     * Função que constrói um Vote
     * @param gameId Id da jam que será usado na construção
     * @param identifier Identificador do usuário
     * @return Subscribe com Game e User
     */
    @Transactional
    public Vote getVote(Long gameId, String identifier) {
        Vote vote = new Vote();

        vote.setVoteUser(userRepository.findByIdentifier(identifier));

        Optional<Game> optionalGame = gameRepository.findByGameId(gameId);
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("Jogo com o ID " + gameId + " não encontrado.");
        }
        vote.setVoteGame(optionalGame.get());
        return vote;
    }
}

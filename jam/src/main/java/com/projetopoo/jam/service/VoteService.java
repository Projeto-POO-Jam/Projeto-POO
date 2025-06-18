package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.*;
import com.projetopoo.jam.dto.VoteRequestDTO;
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

@Service
public class VoteService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private SseNotificationService sseNotificationService;

    @Transactional
    public VoteResponseDTO findVote(VoteRequestDTO voteRequestDTO, String identifier) throws UserValidationException {
        VoteResponseDTO voteResponseDTO = new VoteResponseDTO();

        Vote vote = getVote(voteRequestDTO, identifier);
        Optional<Vote> optionalVote = voteRepository.findByVoteUserAndVoteGame(vote.getVoteUser(), vote.getVoteGame());

        if(optionalVote.isPresent()) {
            voteResponseDTO.setVoted(true);
            return voteResponseDTO;
        } else {
            voteResponseDTO.setVoted(false);
            return voteResponseDTO;
        }
    }

    @Transactional
    public VoteResponseDTO toggleVote(VoteRequestDTO voteRequestDTO, String identifier) {
        VoteResponseDTO voteResponseDTO = new VoteResponseDTO();

        Vote vote = getVote(voteRequestDTO, identifier);
        Optional<Vote> optionalVote = voteRepository.findByVoteUserAndVoteGame(vote.getVoteUser(), vote.getVoteGame());

        if(optionalVote.isPresent()) {
            voteRepository.delete(optionalVote.get());
            voteResponseDTO.setVoted(false);
        } else {
            voteRepository.save(vote);
            voteResponseDTO.setVoted(true);
        }

        VoteTotalResponseDTO voteTotalResponseDTO = totalVotes(voteRequestDTO);

        sseNotificationService.sendEventToTopic("games-update", "votes-update-" + voteRequestDTO.getVoteGameId(), voteTotalResponseDTO);

        return voteResponseDTO;
    }

    @Transactional
    public Vote getVote(VoteRequestDTO voteRequestDTO, String identifier) {
        Vote vote = new Vote();

        vote.setVoteUser(userRepository.findByIdentifier(identifier));

        Optional<Game> optionalGame = gameRepository.findByGameId(voteRequestDTO.getVoteGameId());
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("Jogo com o ID " + voteRequestDTO.getVoteGameId() + " não encontrado.");
        }
        vote.setVoteGame(optionalGame.get());
        return vote;
    }
  
    @Transactional
    public VoteTotalResponseDTO totalVotes(VoteRequestDTO voteRequestDTO) {
        VoteTotalResponseDTO voteTotalResponseDTO = new VoteTotalResponseDTO();
        voteTotalResponseDTO.setVoteTotal(voteRepository.countByVoteGame_GameId(voteRequestDTO.getVoteGameId()));
        return voteTotalResponseDTO;
    }

}

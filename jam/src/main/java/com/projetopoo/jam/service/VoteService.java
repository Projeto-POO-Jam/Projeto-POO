package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.UserResponseDTO;
import com.projetopoo.jam.exception.UserValidationException;
import com.projetopoo.jam.model.Comment;
import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.model.Vote;
import com.projetopoo.jam.repository.GameRepository;
import com.projetopoo.jam.repository.UserRepository;
import com.projetopoo.jam.repository.VoteRepository;
import com.projetopoo.jam.util.ImageUtil;
import com.projetopoo.jam.util.UpdateUtil;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private VoteRepository voteRepository;

    @Transactional
    public boolean toggleVote(Long gameId, String identifier) {
        Vote vote = new Vote();

        vote.setUser(userRepository.findByIdentifier(identifier));

        Optional<Game> optionalGame = gameRepository.findByGameId(gameId);
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("Jogo com o ID " + gameId + " não encontrado.");
        }
        vote.setGame(optionalGame.get());

        Optional<Vote> optionalVote = voteRepository.findByVoteUserAndVoteGame(vote.getUser(), vote.getGame());
        if(optionalVote.isPresent()) {
            voteRepository.delete(optionalVote.get());
            return false;
        } else {
            voteRepository.save(vote);
            return true;
        }
    }

}

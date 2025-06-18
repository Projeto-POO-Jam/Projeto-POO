package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.CommentRequestDTO;
import com.projetopoo.jam.dto.CommentResponseDTO;
import com.projetopoo.jam.model.Comment;
import com.projetopoo.jam.model.Game;
import com.projetopoo.jam.model.User;
import com.projetopoo.jam.repository.CommentRepository;
import com.projetopoo.jam.repository.GameRepository;
import com.projetopoo.jam.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public void createComment(CommentRequestDTO commentRequestDTO, String identifier) {

        Comment comment = new Comment();
        comment.setCommentText(commentRequestDTO.getCommentText());
        comment.setCommentDate(LocalDateTime.now());

        comment.setCommentUser(userRepository.findByIdentifier(identifier));

        Optional<Game> optionalGame = gameRepository.findByGameId(commentRequestDTO.getGameId());
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("Jogo com o ID " + commentRequestDTO.getGameId() + " não encontrado.");
        }
        comment.setCommentGame(optionalGame.get());

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String identifier) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new EntityNotFoundException("Comentário com o ID " + commentId + " não encontrado.");
        }
        Comment comment = optionalComment.get();

        User requestingUser = userRepository.findByIdentifier(identifier);

        if (!(Objects.equals(comment.getCommentUser().getUserId(), requestingUser.getUserId()))) {
            throw new AccessDeniedException("Usuário não autorizado a excluir este comentário.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public List<CommentResponseDTO> findCommentsList(Long gameId) {
        List<Comment> comments = commentRepository.findByCommentGame_GameId(gameId);
        return comments.stream()
                .map(comment -> modelMapper.map(comment, CommentResponseDTO.class))
                .collect(Collectors.toList());
    }

}

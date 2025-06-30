package com.projetopoo.jam.service;

import com.projetopoo.jam.dto.comment.CommentRequestDTO;
import com.projetopoo.jam.dto.comment.CommentResponseDTO;
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

/**
 * Classe service para classe Comment, responsável pela lógica de negócios
 */
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SseNotificationService sseNotificationService;

    /**
     * Constrói uma nova instância de CommentService com suas dependências
     * @param commentRepository Repository para comunicação com o banco de dados da classe Comment
     * @param gameRepository Repository para comunicação com o banco de dados da classe Game
     * @param userRepository Repository para comunicação com o banco de dados da classe User
     * @param modelMapper Classe para mapear transformações entre models e DTOs
     * @param sseNotificationService Classe para envio de eventos via SSE
     */
    @Autowired
    public CommentService(CommentRepository commentRepository,
                          GameRepository gameRepository,
                          UserRepository userRepository,
                          ModelMapper modelMapper,
                          SseNotificationService sseNotificationService) {
        this.commentRepository = commentRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.sseNotificationService = sseNotificationService;
    }

    /**
     * Função para criar um novo comentário
     * @param commentRequestDTO Informações sobre o comentário
     * @param identifier Identificador do usuário
     */
    @Transactional
    public void createComment(CommentRequestDTO commentRequestDTO, String identifier) {

        Comment comment = new Comment();

        // Passa informações recebidas para uma variável do tipo Comment
        comment.setCommentText(commentRequestDTO.getCommentText());
        comment.setCommentDate(LocalDateTime.now());

        // Busca dados do usuário
        comment.setCommentUser(userRepository.findByIdentifier(identifier));

        // Verifica se o jogo votado realmente existe
        Optional<Game> optionalGame = gameRepository.findByGameId(commentRequestDTO.getGameId());
        if (optionalGame.isEmpty()) {
            throw new EntityNotFoundException("Jogo com o ID " + commentRequestDTO.getGameId() + " não encontrado.");
        }
        comment.setCommentGame(optionalGame.get());

        // Salva o comentário
        commentRepository.save(comment);

        // Passa o comentário para o formato de resposta
        CommentResponseDTO commentResponseDTO= modelMapper.map(comment, CommentResponseDTO.class);

        // Envia um evento SSE avisando que um novo comentário foi criado
        sseNotificationService.sendEventToTopic("games-update", "comments-insert-" + commentRequestDTO.getGameId(), commentResponseDTO);
    }

    /**
     * Função para excluir um comentário pelo id
     * @param commentId Id do comentário a ser deletado
     * @param identifier Identificador do usuário
     */
    @Transactional
    public void deleteComment(Long commentId, String identifier) {
        // Verifica se o comentário existe
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new EntityNotFoundException("Comentário com o ID " + commentId + " não encontrado.");
        }
        Comment comment = optionalComment.get();

        // Busca as informações sobre o usuário que fez a solicitação
        User requestingUser = userRepository.findByIdentifier(identifier);

        // Verifica se o usuário é o dono do comentário
        if (!(Objects.equals(comment.getCommentUser().getUserId(), requestingUser.getUserId()))) {
            throw new AccessDeniedException("Usuário não autorizado a excluir este comentário.");
        }

        // Passa o comentário para o formato de resposta
        CommentResponseDTO commentResponseDTO= modelMapper.map(comment, CommentResponseDTO.class);
        commentRepository.delete(comment);

        // Envia um evento SSE avisando que um comentário foi excluído
        sseNotificationService.sendEventToTopic("games-update", "comments-delete-" + comment.getCommentGame().getGameId(), commentResponseDTO);
    }

    /**
     * Função para buscar a lista de comentários de um jogo
     * @param gameId Id do jogo que está sendo usado na consulta
     * @param identifier Identificador do usuário
     * @return Lista com comentários do jogo
     */
    @Transactional
    public List<CommentResponseDTO> findCommentsList(Long gameId, String identifier) {
        // Busca a lista de comentários
        List<Comment> comments = commentRepository.findByCommentGame_GameId(gameId);

        // Busca o usuário que fez a requisição
        User user = userRepository.findByIdentifier(identifier);

        // Passa o comentário para o formato de resposta
        return comments.stream()
                .map(comment -> {
                    boolean currentUser = user.getUserId().equals(comment.getCommentUser().getUserId());
                    CommentResponseDTO commentResponseDTO = modelMapper.map(comment, CommentResponseDTO.class);

                    // Adiciona a informação se o usuário é o dono do comentário ou não
                    commentResponseDTO.getCommentUser().setUserCurrent(currentUser);

                    return commentResponseDTO;
                })
                .collect(Collectors.toList());
    }

}

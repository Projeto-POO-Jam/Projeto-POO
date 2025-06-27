import { fetchGame, isLike, toggleVote, fetchTotalVotes } from '../../services/gameService.js';
import { fetchCurrentUser } from '../../services/userService.js';
import { fetchComments, postComment, deleteComment } from '../../services/commentService.js';
import { bindDataFields } from '../../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';
import { showError } from '../../common/notifications.js';

$(function() {
    //Pega id na URL
    const pathSegments = window.location.pathname.split('/').filter(Boolean);
    const gameId = pathSegments[pathSegments.length - 1];
    const root = 'main.game-card';

    //Seletores de elementos
    const likeButton = $('.button-like');
    const likeCountSpan = $('.like-count');
    const commentsContainer = $('#comments');
    const commentInput = $('#comment-user-input');
    const sendCommentBtn = $('#send-comment-btn');
    const userCommentIcon = $('.post-comment .icone-actor-comments');
    let activeUser = null;

    applySkeleton(root);

    //Configuração do SSE
    const stream = new EventSource('/api/events?topic=games-update');

    //SSE para votos
    const voteEventName = `votes-update-${gameId}`;
    stream.addEventListener(voteEventName, (e) => {
        const payload = JSON.parse(e.data);
        if (payload.voteTotal !== undefined) {
            likeCountSpan.text(payload.voteTotal);
        }
    });

    //SSE para novos comentarios
    const commentsUpdateEventName = `comments-update-${gameId}`;
    stream.addEventListener(commentsUpdateEventName, (e) =>{
        const newComment = JSON.parse(e.data);

        //Verifique se o novo comentário é do usuário ativo
        if (activeUser && newComment.commentUser && newComment.commentUser.userId === activeUser.userId) {
            newComment.commentUser.userCurrent = true;
        }

        if(commentsContainer.find('p.not-comments').length > 0){
            commentsContainer.empty();
        }

        const newCommentElement = renderComment(newComment);
        commentsContainer.prepend(newCommentElement);
    })

    //SSE para exclusão de comentários
    const commentsDeleteEventName = `comments-delete-${gameId}`;
    stream.addEventListener(commentsDeleteEventName, (e) => {
        const payload = JSON.parse(e.data);
        if (payload.commentId) {
            $(`.comment-card[data-comment-id="${payload.commentId}"]`).remove();

            if (commentsContainer.children().length === 0) {
                commentsContainer.html('<p class="not-comments">Ainda não há comentários. Seja o primeiro a comentar!</p>');
            }
        }
    });

    stream.onerror = (err) => {
        console.error(`SSE connection error on topic 'games-update':`, err);
        stream.close();
    };

    //Função para renderizar um comentário
    function renderComment(comment) {
        let deleteButtonHtml = '';
        if (comment.commentUser && comment.commentUser.userCurrent) {
            deleteButtonHtml = `
                <div class="delete-comments">
                    <span class="material-symbols-outlined">delete</span>
                </div>
            `;
        }

        const commentHtml = `
            <div class="comment-card" data-comment-id="${comment.commentId}">
                <div class="container-user-comment">
                     <img
                        src="${comment.commentUser.userPhoto || '/images/iconePadrao.svg'}"
                        data-default="/images/iconePadrao.svg"
                        alt="comment actor icon"
                        class="icone-actor-comments"
                        onerror="this.src=this.dataset.default"
                    />
                    <div class="comment-content">
                        <p class="comment-username">${comment.commentUser.userName}</p>
                        <p class="comment-text">${$('<div>').text(comment.commentText).html()}</p>
                    </div>
                </div>
                ${deleteButtonHtml} </div>
        `;
        return $(commentHtml);
    }

    //Excluir Comentario
    commentsContainer.on('click', '.delete-comments', function() {
        const commentCard = $(this).closest('.comment-card');
        const commentId = commentCard.data('comment-id');

        if (!commentId) {
            console.error('ID do comentário não encontrado.');
            return;
        }

        deleteComment(commentId)
            .fail(() => {
                showError('Ocorreu um erro ao excluir o comentário.');
            });
    });

    //Carregamento de dados iniciais
    $.when(
        fetchGame(gameId),
        isLike(gameId),
        fetchTotalVotes(gameId),
        fetchCurrentUser(),
        fetchComments(gameId)
    )
        .done((gameData, likeStatus, voteCount, currentUser, comments) => {
            activeUser = currentUser;

            //Preenche os dados do game
            bindDataFields(gameData, root);

            //Lógica para exibir o botão de editar o game
            if (gameData.userResponseDTO && gameData.userResponseDTO.userCurrent) {
                const editButton = $('#edit-game-btn');
                editButton.attr('href', `/updateJam/${gameId}`);
                editButton.show();
            }

            $('.options-view-game button').on('click', function() {
                if (gameData.gameFile) {
                    window.open(gameData.gameFile, '_blank');
                } else {
                    showError('A URL do game não está disponível.');
                }
            });

            const container = $('.container-view-game');
            const userHtml = gameData.gameContent?.trim();
            if (userHtml) {
                const sanitizedHtml = $('<div>').html(userHtml);
                sanitizedHtml.find('script').remove();
                container.html(sanitizedHtml.html());
            } else {
                container.html('<p>O desenvolvedor não adicionou conteúdo adicional.</p>');
            }

            //Configura o estado inicial do botão de Like
            if (likeStatus && likeStatus.voted) {
                likeButton.addClass('liked');
            }

            //Configura a contagem inicial de votos
            if (voteCount) {
                likeCountSpan.text(voteCount.voteTotal ?? 0);
            }

            //Lógica de Comentários
            //Define a foto do usuário
            if (currentUser && currentUser.userPhoto) {
                userCommentIcon.attr('src', currentUser.userPhoto);
            }

            //Renderiza os comentários existentes
            commentsContainer.empty();
            if (comments && comments.length > 0) {
                comments.forEach(comment => {
                    commentsContainer.append(renderComment(comment));
                });
            } else {
                commentsContainer.html('<p class="not-comments">Ainda não há comentários. Seja o primeiro a comentar!</p>');
            }
        })
        .fail(err => {
            console.error('Uma ou mais chamadas iniciais falharam:', err);
            showError('Não foi possível carregar as informações desta página.');
        })
        .always(() => {
            setTimeout(() => {
                removeSkeleton(root);
                $(root).find('.skeleton').removeClass('skeleton');
            }, 150);
        });


    //Lógica de clique do Like
    likeButton.on('click', function() {
        $(this).toggleClass('liked');
        toggleVote(gameId)
            .fail(() => {
                showError('Ocorreu um erro ao registrar seu voto.');
                $(this).toggleClass('liked');
            });
    });

    //Lógica para enviar comentário
    sendCommentBtn.on('click', function() {
        const commentText = commentInput.val().trim();
        if (!commentText) {
            showError('O comentário não pode estar vazio.');
            return;
        }

        const originalIcon = $(this).text();
        $(this).text('pending');

        postComment(commentText, gameId)
            .done((newComment) => {
                commentInput.val('');
            })
            .fail(() => {
                showError('Ocorreu um erro ao postar seu comentário.');
            })
            .always(() => {
                $(this).text(originalIcon);
            });
    });

    //Permite enviar com a tecla Enter
    commentInput.on('keypress', function(e) {
        if (e.which === 13) {
            e.preventDefault();
            sendCommentBtn.click();
        }
    });
});
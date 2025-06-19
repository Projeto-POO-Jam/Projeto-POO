import { fetchViewJam } from '../services/jamService.js';
import { bindDataFields } from '../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { showError } from '../common/notifications.js';

$(function() {
    //Extrai o ID da Jam da URL
    const pathSegments = window.location.pathname
        .split('/')
        .filter(Boolean);
    const jamId = pathSegments[pathSegments.length - 1];

    const root = 'main';

    //Skeleton
    applySkeleton(root);

    $.when(fetchViewJam(jamId))
        .done(data => {
            //Img cover
            const coverContainer = $('#jam-cover-container');
            const coverImg = $('img[data-field="jamCover"]');

            if (!data.jamCover) {
                coverContainer.remove();
            } else {
                coverImg.attr('src', data.jamCover)
                    .removeClass('skeleton');
            }

            //Preenche os campos estáticos
            bindDataFields(data, root);

            $(root).find('.skeleton').removeClass('skeleton');

            //Aplica wallpaper se existir
            if (data.jamWallpaper) {
                $('body')
                    .css('background-image', `url(${data.jamWallpaper})`)
                    .css('background-size', 'cover')
                    .css('background-position', 'center');
            }

            //Monta o card de duração
            if (data.jamStartDate && data.jamEndDate) {
                const start = new Date(data.jamStartDate);
                const end = new Date(data.jamEndDate);

                //Formata as datas ("20 de junho de 2025 14:01")
                const opts = {
                    day: 'numeric',
                    month: 'long',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                };

                const fmtStart = start.toLocaleString('pt-BR', opts);
                const fmtEnd = end.toLocaleString('pt-BR', opts);

                //Monta o HTML
                $('#jam-duration-container').html(`
                    <div class="card-duration-view-jam-id duration-card">
                        <p class="range-text">
                            Submissões abertas de ${fmtStart} até ${fmtEnd}
                        </p>
                        <div class="container-card-duration-view-jam-id">
                            <div class="countdown-text">
                                <span id="cd-prefix">Inicia em</span>
                                <span id="cd-days">--</span> dias
                                <span id="cd-hours">--</span> horas
                                <span id="cd-minutes">--</span> minutos
                                <span id="cd-seconds">--</span> segundos
                            </div>
                            <button id="join-jam-btn" class="join-btn">Participar</button>
                        </div>
                    </div>
                `);

                //Função que atualiza o contador a cada segundo
                function updateCountdown() {
                    const now = new Date();
                    let diff = start - now;
                    let prefix = 'Inicia em';

                    if (diff <= 0 && now < end) {
                        diff = end - now;
                        prefix = 'Encerra em';
                    } else if (now >= end) {
                        $('#cd-prefix').text('Encerrado');
                        $('#cd-days, #cd-hours, #cd-minutes, #cd-seconds').text('00');
                        clearInterval(timer);
                        return;
                    }

                    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
                    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
                    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

                    $('#cd-prefix').text(prefix);
                    $('#cd-days').text(String(days).padStart(2, '0'));
                    $('#cd-hours').text(String(hours).padStart(2, '0'));
                    $('#cd-minutes').text(String(minutes).padStart(2, '0'));
                    $('#cd-seconds').text(String(seconds).padStart(2, '0'));
                }

                //Dispara o timer
                updateCountdown();
                const timer = setInterval(updateCountdown, 1000);
            }

            //HTML livre do usuário (jamContent)
            const cardWrapper = $('.card-view-jam-id');
            const userCard = cardWrapper.find('.page-user-jam-card');
            const userHtml = data.jamContent?.trim();

            if (userHtml) {
                userCard.html(userHtml);
            } else {
                userCard.html(`
                    <div class="default-jam-card">
                        <p>Descrição da Jam.</p>
                    </div>
                `);
            }

            //Remove as classes skeleton dos containers
            userCard.removeClass('skeleton');
            $('.container-jam-card').removeClass('skeleton');

            // Pega o primeiro filho dentro do conteúdo do usuário
            const firstChild = userCard.children().first()[0];

            if (firstChild) {
                const styles = {};

                //Background-color
                if (firstChild.style.backgroundColor) {
                    styles['background-color'] = firstChild.style.backgroundColor;
                }

                //Border
                if (firstChild.style.border && firstChild.style.border !== 'none') {
                    styles['border'] = firstChild.style.border;
                }

                if (Object.keys(styles).length) {
                    cardWrapper.css(styles);

                    firstChild.style.backgroundColor = '';
                    firstChild.style.border = '';
                }
            }
        })
        .fail(err => {
            console.error('Erro ao carregar Jam:', err);
            showError('Não foi possível carregar esta Jam.');
            //Redirecionar para uma página 404, se quiser
        })
        .always(() => {
            //Remove o placeholder skeleton
            removeSkeleton(root);
        });
});

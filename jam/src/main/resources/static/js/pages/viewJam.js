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
            const $userCard = $('.page-user-jam-card');
            const userHtml = data.jamContent?.trim();
            if (userHtml) {
                $userCard.html(userHtml);
            } else {
                $userCard.html(`
                    <div class="default-jam-card">
                        <p>Descrição da Jam.</p>
                    </div>
                `);
            }

            //Remove as classes skeleton dos containers
            $userCard.removeClass('skeleton');
            $('.container-jam-card').removeClass('skeleton');

            //Transfere apenas estilos válidos do primeiro filho
            const $first = $userCard.children().first();
            if ($first.length) {
                const styles = {};

                //Background-color
                const bgColor = $first.css('background-color');
                if (bgColor && bgColor !== 'rgba(0, 0, 0, 0)' && bgColor !== 'transparent') {
                    styles['background-color'] = bgColor;
                }

                //Background-image
                const bgImage = $first.css('background-image');
                if (bgImage && bgImage !== 'none') {
                    styles['background-image']  = bgImage;
                    styles['background-repeat'] = $first.css('background-repeat');
                    styles['background-position'] = $first.css('background-position');
                    styles['background-size'] = $first.css('background-size');
                }

                //Border
                const border = $first.css('border');
                if (border && border !== '0px none rgb(0, 0, 0)') {
                    styles['border'] = border;
                }

                //Aplica ou cai no padrão
                if (Object.keys(styles).length) {
                    $('.card-view-jam-id').css(styles);
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

import { fetchViewJam } from '../services/jamService.js';
import { bindDataFields } from '../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { showError } from '../common/notifications.js';

$(function() {
    const jamId = $('body').data('jam-id');
    const root = 'main';

    //Aplica skeleton
    applySkeleton(root);

    //Chama a API
    fetchViewJam(jamId)
        .then(data => {
            //Preenche os campos estáticos
            bindDataFields(data, root);

            //Se tiver wallpaper, aplica no <body>
            if (data.wallpaperImgUrl) {
                $('body')
                    .css('background-image', `url(${data.wallpaperImgUrl})`)
                    .css('background-size', 'cover')
                    .css('background-position','center');
            }

            // Monta o card de duração
            if (data.startDate && data.endDate) {
                const start = new Date(data.startDate);
                const end = new Date(data.endDate);
                const days = Math.round((end - start) / (1000 * 60 * 60 * 24));
                $('#jam-duration-container').html(`
                    <div class="card duration-card">
                        <h3>Duração</h3>
                        <p>${days} dia${days>1?'s':''} (${data.startDate} até ${data.endDate})</p>
                        <button>Postar Game</button>
                    </div>
                `);
            }

            //HTML livre do usuário
            const $userCard = $('.page-user-jam-card');
            const userHtml = data.htmlContent?.trim();

            if (userHtml) {
                $userCard.html(userHtml);
            } else {
                // fallback padrão
                $userCard.html(`
                    <div class="default-jam-card">
                        <p>Descrição da Jam.</p>
                    </div>
                `);
            }

            $userCard.removeClass('skeleton');

            //Transfere background/borda
            const $first = $userCard.children().first();
            if ($first.length) {
                const styles = {
                    'background-color': $first.css('background-color'),
                    'background-image': $first.css('background-image'),
                    'background-repeat': $first.css('background-repeat'),
                    'background-position': $first.css('background-position'),
                    'background-size': $first.css('background-size'),
                    'border': $first.css('border'),
                };
                $('.card-view-jam').css(styles);
            }

        })
        .catch(err => {
            console.error('Erro ao carregar Jam:', err);
            showError('Não foi possível carregar esta Jam.');
            //redirecionar para 404, se quiser
        })
        .finally(() => {
            removeSkeleton(root);
        });
});

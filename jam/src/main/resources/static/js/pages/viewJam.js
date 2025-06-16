import { fetchViewJam } from '../services/jamService.js';
import { bindDataFields } from '../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { showError } from '../common/notifications.js';

$(function() {
    const jamId = $('body').data('jam-id');
    const root = 'main';

    //Aplica skeleton
    applySkeleton(root);

    //Chama o API
    fetchViewJam(jamId)
        .then(data => {
            //Mapeia os valor na tela
            bindDataFields(data, root);

            //Aplica wallpaper
            if (data.wallpaperImgUrl) {
                $('body')
                    .css('background-image', `url(${data.wallpaperImgUrl})`)
                    .css('background-size', 'cover')
                    .css('background-position', 'center');
            }

            //Aplica o cover dentro do container
            if (data.coverImgUrl) {
                $('#jam-cover-container').html(`
                    <img
                        src="${data.coverImgUrl}"
                        alt="Cover da Jam"
                        style="width:100%; height:auto;"
                    />
                `);
            }

            //Monta o seu card de duração
            const start = new Date(data.startDate);
            const end = new Date(data.endDate);
            const days = Math.round((end - start) / (1000 * 60 * 60 * 24));
            $('#jam-duration-container').html(`
                <div class="card duration-card">
                    <h3>Duração</h3>
                    <p>
                        ${days} dia${days > 1 ? 's' : ''}
                        (${data.startDate} até ${data.endDate})
                    </p>
                    <button>Postar Game</button>
                </div>
            `);

            //Injeta o HTML livre do usuário
            const userHtml = data.htmlContent?.trim();
            if (userHtml) {
                $('.page-user-jam-card').html(userHtml);

                //Detecta fundo/borda no primeiro elemento do userHtml
                const $first = $('.page-user-jam-card').children().first();
                if ($first.length) {
                    const bgColor = $first.css('background-color');
                    const bgImage = $first.css('background-image');
                    const bgRepeat = $first.css('background-repeat');
                    const bgPosition = $first.css('background-position');
                    const bgSize = $first.css('background-size');
                    const border = $first.css('border');

                    // só aplica se existir alguma definição de fundo
                    const hasBgColor = bgColor && bgColor !== 'rgba(0, 0, 0, 0)';
                    const hasBgImage = bgImage && bgImage !== 'none';
                    if (hasBgColor || hasBgImage) {
                        $('.card-view-jam').css({
                            'background-color': bgColor,
                            'background-image': bgImage,
                            'background-repeat': bgRepeat,
                            'background-position': bgPosition,
                            'background-size': bgSize
                        });
                    }

                    // aplica borda caso o usuário tenha definido
                    if (border && border !== '0px none rgb(0, 0, 0)') {
                        $('.card-view-jam').css('border', border);
                    }
                }

            } else {
                // Card Padrão
                $('.page-user-jam-card').html(`
                    <div class="default-jam-card">
                        <p>Descrição da Jam.</p>
                    </div>
                `);
            }
        })
        .catch(err => {
            console.error('Erro ao carregar Jam:', err);
            showError('Não foi possível carregar esta Jam.');
        })
        .finally(() => {
            removeSkeleton(root);
        });
});

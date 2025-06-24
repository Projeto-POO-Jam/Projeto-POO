import { toggleSubscription, checkSubscriptionStatus } from '../../../services/jamService.js';
import { applySkeleton } from '../../../common/skeleton.js';

//Função que apenas desenha os botões corretos na tela.
function updateSubscriptionUI(isSubscribed) {
    const actionContainer = $('#jam-action-container');

    if (isSubscribed) {
        actionContainer.html(`
            <button id="post-game-btn" class="join-btn">Postar Jogo</button>
            <button id="toggle-subscription-btn" class="leave-jam-link">Sair da Jam</button>
        `);
    } else {
        actionContainer.html('<button id="toggle-subscription-btn" class="join-btn">Participar da Jam</button>');
    }
}

export function init(data, jamId) {
    if (data.jamStartDate && data.jamEndDate) {
        const start = new Date(data.jamStartDate);
        const end = new Date(data.jamEndDate);
        const now = new Date();

        //Monta o template
        $('#jam-duration-container').html(`
            <div class="card-duration-view-jam-id duration-card">
                <p class="range-text">Inscrições de ${start.toLocaleDateString('pt-BR')} até ${end.toLocaleDateString('pt-BR')}</p>
                <div class="participants-jam">
                    <h1>${data.jamTotalSubscribers}</h1>
                    <p>Participantes</p>
                </div>
                <div class="container-card-duration-view-jam-id">
                    <div class="countdown-text">
                        <span id="cd-prefix">Inicia em</span><span id="cd-timer">--:--:--:--</span>
                    </div>
                    <div id="jam-action-container"></div>
                </div>
            </div>
        `);

        const actionContainer = $('#jam-action-container');

        if (now < end) {
            const skeletonButtonHtml = '<button data-field="action-button-placeholder" class="join-btn"></button>';
            actionContainer.html(skeletonButtonHtml);
            applySkeleton(actionContainer);

            //Chama o verificador
            checkSubscriptionStatus(jamId)
                .done(response => {
                    updateSubscriptionUI(response.subscribed);
                })
                .fail(() => actionContainer.html('<p class="error-message">Erro ao verificar inscrição.</p>'));
        }

        $('#jam-duration-container').on('click', '#toggle-subscription-btn', function() {
            const btn = $(this);
            const participantsH1 = $('.participants-jam h1');
            const currentCount = parseInt(participantsH1.text(), 10);

            //Verifica o estado ANTES do clique, para saber se vamos somar ou subtrair
            const wasSubscribed = btn.text().includes('Sair da Jam');

            btn.prop('disabled', true).text('Processando...');

            //Chama a API
            toggleSubscription(jamId)
                .done(() => {
                    checkSubscriptionStatus(jamId)
                        .done(response => {
                            //Atualiza a UI com os botões corretos
                            updateSubscriptionUI(response.subscribed);

                            if (response.subscribed && !wasSubscribed) {
                                participantsH1.text(currentCount + 1);
                            } else if (!response.subscribed && wasSubscribed) {
                                participantsH1.text(currentCount - 1);
                            }
                        })
                        .fail(() => actionContainer.html('<p class="error-message">Erro ao atualizar status.</p>'));
                })
                .fail(() => {
                    updateSubscriptionUI(wasSubscribed);
                });
        });

        $('#jam-duration-container').on('click', '#post-game-btn', function() {
            window.location.href = `/jams/registerGame/${jamId}`;
        });

        const timer = setInterval(() => updateCountdown(start, end, timer), 1000);
        updateCountdown(start, end, timer);
    }

    const userCard = $('.page-user-jam-card');
    const userHtml = data.jamContent?.trim();
    if (userHtml) {
        const sanitizedHtml = $('<div>').html(userHtml);
        sanitizedHtml.find('script').remove();
        userCard.html(sanitizedHtml.html());
    } else {
        userCard.html('<p>O organizador ainda não adicionou uma descrição para esta Jam.</p>');
    }
}

function updateCountdown(start, end, timer) {
    const now = new Date();
    let diff = start - now;
    let period = 'registration';

    if (diff <= 0) {
        if (now < end) {
            diff = end - now;
            period = 'running';
        } else {
            period = 'ended';
        }
    }

    if (period === 'ended') {
        $('#cd-prefix').text('Encerrado');
        $('#cd-timer').text('00:00:00:00');
        $('#jam-action-container').empty();
        clearInterval(timer);
        return;
    }

    const joinBtn = $('#toggle-subscription-btn');
    if (period === 'running' && joinBtn.text().includes('Participar')) {
        joinBtn.prop('disabled', true).text('Inscrições encerradas');
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    const pad = (num) => String(num).padStart(2, '0');
    const formattedTime = `${pad(days)}:${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;

    $('#cd-prefix').text(period === 'running' ? 'Encerra em' : 'Inicia em');
    $('#cd-timer').text(formattedTime);
}
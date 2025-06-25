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
    //Configuração do SSE
    const stream = new EventSource('/api/events?topic=jams-update');

    //Handler para atualização de inscritos
    const subscribesEventName = `jam-subscribes-update-${jamId}`;
    stream.addEventListener(subscribesEventName, (e) => {
        const payload = JSON.parse(e.data);
        if (payload.subscribeTotal !== undefined) {
            $('.participants-jam h1').text(payload.subscribeTotal ?? 0);
        }
    });

    //Handler para atualização de status
    const statusEventName = `jam-status-update-${jamId}`;
    stream.addEventListener(statusEventName, (e) => {
        const payload = JSON.parse(e.data);
        if (payload.jamStatus) {
            data.jamStatus = payload.jamStatus;

            //Força a re-execução da função de contagem regressiva
            updateCountdown(data, start, end, timer);
        }
    });

    //Tratamento de erro
    stream.onerror = (err) => {
        console.error(`SSE connection error on topic 'jams-update':`, err);
        stream.close();
    };
    //FIM da configuração do SSE

    let start, end, timer;

    if (data.jamStartDate && data.jamEndDate) {
        start = new Date(data.jamStartDate);
        end = new Date(data.jamEndDate);
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

            checkSubscriptionStatus(jamId)
                .done(response => {
                    updateSubscriptionUI(response.subscribed);
                })
                .fail(() => actionContainer.html('<p class="error-message">Erro ao verificar inscrição.</p>'));
        }

        $('#jam-duration-container').on('click', '#toggle-subscription-btn', function() {
            const btn = $(this);
            const wasSubscribed = btn.text().includes('Sair da Jam');

            btn.prop('disabled', true).text('Processando...');

            toggleSubscription(jamId)
                .done(() => {
                    checkSubscriptionStatus(jamId)
                        .done(response => {
                            updateSubscriptionUI(response.subscribed);
                        })
                        .fail(() => actionContainer.html('<p class="error-message">Erro ao atualizar status.</p>'));
                })
                .fail(() => {
                    updateSubscriptionUI(wasSubscribed);
                });
        });

        timer = setInterval(() => updateCountdown(data, start, end, timer), 1000);
        updateCountdown(data, start, end, timer);

        $('#jam-duration-container').on('click', '#post-game-btn', function() {
            window.location.href = `/jams/registerGame/${jamId}`;
        });
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


//A função oara atualizar a duração
function updateCountdown(data, start, end, timer) {
    //Verifica primeiro o status vindo do servidor via SSE
    if (data.jamStatus === 'FINISHED') {
        $('#cd-prefix').text('Encerrado');
        $('#cd-timer').text('00:00:00:00');
        $('#jam-action-container').empty();
        clearInterval(timer);
        return;
    }

    const now = new Date();
    let diff = start - now;
    let period = 'registration';

    if (diff <= 0) { //Se a data de início já passou
        if (now < end) {
            diff = end - now; //Calcula o tempo restante até o fim
            period = 'running';
        } else {
            period = 'ended';
        }
    }

    //A verificação abaixo ainda é útil para o carregamento inicial da página,
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
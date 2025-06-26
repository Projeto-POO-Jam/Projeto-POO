import { toggleSubscription, checkSubscriptionStatus } from '../../../services/jamService.js';
import { applySkeleton } from '../../../common/skeleton.js';

//Função que apenas desenha os botões corretos na tela.
function updateSubscriptionUI(isSubscribed) {
    const actionContainer = $('#jam-action-container');

    if (isSubscribed) {
        actionContainer.html(`
            <button id="post-game-btn" class="join-btn bg-jam-color">Postar Jogo</button>
            <a id="toggle-subscription-btn" class="leave-jam-link">Sair da Jam</a>
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
                <div class="participants-jam">
                    <div class="info-participants-jam">
                        <h1>${data.jamTotalSubscribers}</h1>
                        <p>Participantes</p>
                    </div>
                </div>
                
                <div class="info-duration-card">
                    <p class="range-text">Inscrições - ${start.toLocaleDateString('pt-BR')} até ${end.toLocaleDateString('pt-BR')}</p>
                    <div class="container-card-duration-view-jam-id">
                        <div class="countdown-text">
                            <span id="cd-prefix">Inicia em</span>
                        </div>

                        <div id="cd-timer" class="countdown-container">
                            <div class="countdown-item">
                                <span id="cd-days" class="countdown-value">--</span>
                                <span class="countdown-label">Dias</span>
                            </div>
                            <div class="countdown-item">
                                <span id="cd-hours" class="countdown-value">--</span>
                                <span class="countdown-label">Horas</span>
                            </div>
                            <div class="countdown-item">
                                <span id="cd-minutes" class="countdown-value">--</span>
                                <span class="countdown-label">Minutos</span>
                            </div>
                            <div class="countdown-item">
                                <span id="cd-seconds" class="countdown-value">--</span>
                                <span class="countdown-label">Segundos</span>
                            </div>
                        </div>
                        
                        <div id="jam-action-container"></div>
                    </div>
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
                .fail(() => {
                    showError('Erro ao verificar o status da sua inscrição.');
                });
        }

        $('#jam-duration-container').on('click', '#toggle-subscription-btn', function() {
            const btn = $(this);
            const isLeaving = btn.text().includes('Sair da Jam');

            if (isLeaving) {
                Swal.fire({
                    title: 'Tem certeza?',
                    text: "Você realmente deseja sair desta Jam? isso irar apagar o poste do seu Game! Você pode se inscrever novamente depois.",
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#e75050',
                    cancelButtonColor: '#5865f2',
                    confirmButtonText: 'Sim, quero sair!',
                    cancelButtonText: 'Cancelar',
                    customClass: {
                        popup: 'swal-custom-popup',
                        title: 'swal-custom-title',
                        htmlContainer: 'swal-custom-html-container'
                    }
                }).then((result) => {
                    if (result.isConfirmed) {
                        btn.prop('disabled', true).text('Processando...');
                        toggleSubscription(jamId)
                            .done(() => {
                                updateSubscriptionUI(false);
                            })
                            .fail(() => {
                                showError('Ocorreu um erro ao tentar sair da Jam. Tente novamente.');
                                updateSubscriptionUI(true);
                            });
                    }
                });
            } else {
                // Lógica original para se inscrever
                btn.prop('disabled', true).text('Processando...');
                toggleSubscription(jamId)
                    .done(() => {
                        updateSubscriptionUI(true);
                    })
                    .fail(() => {
                        showError('Ocorreu um erro ao tentar se inscrever na Jam. Tente novamente.');
                        updateSubscriptionUI(false);
                    });
            }
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

const pad = (num) => String(num).padStart(2, '0');

//A função oara atualizar a duração
function updateCountdown(data, start, end, timer) {
    const actionContainer = $('#jam-action-container');

    //Verifica primeiro o status vindo do servidor via SSE
    if (data.jamStatus === 'FINISHED') {
        $('#cd-prefix').text('Encerrado');
        $('#cd-days, #cd-hours, #cd-minutes, #cd-seconds').text('00');
        actionContainer.empty(); // Limpa os botões
        clearInterval(timer);
        return;
    }

    const now = new Date();
    let diff = start - now;


    if (diff > 0) { //am ainda não começou
        $('#cd-prefix').text('Inicia em');
        actionContainer.hide(); //Esconde os botões até a Jam começar

    } else { //Jam está rolando ou acabou
        actionContainer.show();

        if (now < end) { //Jam está rolando
            diff = end - now; //Calcula o tempo restante até o fim
            $('#cd-prefix').text('Encerra em');
        } else { // Jam terminou
            $('#cd-prefix').text('Encerrado');
            $('#cd-days, #cd-hours, #cd-minutes, #cd-seconds').text('00');
            actionContainer.empty();
            clearInterval(timer);
            return;
        }
    }

    //O resto da função continua igual, calculando e exibindo o tempo.
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    $('#cd-days').text(pad(days));
    $('#cd-hours').text(pad(hours));
    $('#cd-minutes').text(pad(minutes));
    $('#cd-seconds').text(pad(seconds));
}
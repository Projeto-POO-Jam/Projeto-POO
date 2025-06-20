import { subscribeToJam, leaveJam } from '../../../services/jamService.js';

//Função para ser chamada quando os dados da Jam estiverem prontos
export function init(data, jamId) { // Recebe o jamId
    //Monta o card de duração
    if (data.jamStartDate && data.jamEndDate) {
        const start = new Date(data.jamStartDate);
        const end = new Date(data.jamEndDate);
        const now = new Date();

        const fmtStart = start.toLocaleDateString('pt-BR');
        const fmtEnd = end.toLocaleDateString('pt-BR');

        $('#jam-duration-container').html(`
            <div class="card-duration-view-jam-id duration-card">
                <p class="range-text">
                    Tempo de Inscrição: ${fmtStart} até ${fmtEnd}
                </p>
                <div class="participants-jam">
                    <h1 class="skeleton">${data.jamTotalSubscribers}</h1>
                    <p>Ingressou</p>
                </div>
                <div class="container-card-duration-view-jam-id">
                    <div class="countdown-text">
                        <span id="cd-prefix">Inicia em</span>
                        <span id="cd-timer">--:--:--:--</span>
                    </div>
                    <div id="jam-action-container"></div>
                </div>
            </div>
        `);

        const actionContainer = $('#jam-action-container');

        //Verifica se a Jam já acabou. Se sim, não mostra nenhum botão.
        if (now < end) {
            if (data.subscribed) {
                showJoinedState();
            } else {
                showJoinButton();
            }
        }

        function showJoinButton() {
            actionContainer.html('<button id="join-jam-btn" class="join-btn">Participar</button>');
        }

        function showJoinedState() {
            actionContainer.html(`
                <button id="post-game-btn" class="join-btn">Postar Game</button>
                <a href="#" id="leave-jam-btn" class="leave-jam-link">Sair da Jam</a>
            `);
        }


        // Evento para entrar na Jam
        $('#jam-duration-container').on('click', '#join-jam-btn', function() {
            const btn = $(this);
            btn.prop('disabled', true).text('Processando...');

            subscribeToJam(jamId)
                .done(response => {
                    showJoinedState();
                })
                .fail(err => {
                    btn.prop('disabled', false).text('Participar');
                });
        });

        //Evento para sair da Jam
        $('#jam-duration-container').on('click', '#leave-jam-btn', function(e) {
            e.preventDefault();
            const link = $(this);
            link.css('pointer-events', 'none').text('Saindo...');

            leaveJam(jamId)
                .done(response => {
                    showJoinButton();
                })
                .fail(err => {
                    link.css('pointer-events', 'auto').text('Sair da Jam');
                });
        });

        //Evento para postar o Game
        $('#jam-duration-container').on('click', '#post-game-btn', function() {
            window.location.href = `/registerGame?jamId=${jamId}`;
        });

        //Inicia o countdown
        const timer = setInterval(() => updateCountdown(start, end, timer), 1000);
        updateCountdown(start, end, timer);
    }

    //Lógica do HTML do usuario (seu código original, sem alterações)
    const userCard = $('.page-user-jam-card');
    const userHtml = data.jamContent?.trim();

    if (userHtml) {
        const sanitizedHtml = $('<div>').html(userHtml);
        sanitizedHtml.find('script').remove();
        userCard.html(sanitizedHtml.html());
    } else {
        userCard.html(`
            <div class="default-jam-card">
                <p>Descrição da Jam.</p>
            </div>
        `);
    }
}

//Função de countdown (seu código original, sem alterações)
function updateCountdown(start, end, timer) {
    const now = new Date();
    let diff = start - now;
    let prefix = 'Inicia em';

    if (diff <= 0 && now < end) {
        diff = end - now;
        prefix = 'Encerra em';
        //Fazer logica para não deixar o usuario não se inclever ainda!!!!!!!!!!!!!!!!!!!!!!!!!

    } else if (now >= end) {
        $('#cd-prefix').text('Encerrado');
        $('#cd-timer').text('00:00:00:00');
        $('#jam-action-container').empty(); // Remove os botões se a jam acabou
        clearInterval(timer);
        return;
    }

    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((diff % (1000 * 60)) / 1000);

    const pad = (num) => String(num).padStart(2, '0');
    const formattedTime = `${pad(days)}:${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;

    $('#cd-prefix').text(prefix);
    $('#cd-timer').text(formattedTime);
}
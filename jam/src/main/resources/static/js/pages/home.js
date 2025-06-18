import { fetchJamsByMonth } from '../services/jamService.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';

$(function() {

    //Mostrar Jams
    const container = $('.container-Jams');
    const limitPerPage = 10;
    let loadedMonths = [];
    let monthOffsets = {};
    let isLoading = false;

    //Pega o mês atual
    function getCurrentMonth() {
        const date = new Date();
        return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,'0')}`;
    }

    //Formata mês para título
    function formatMonthTitle(ym) {
        const [y, m] = ym.split('-');
        const date = new Date(y, m-1);
        return date.toLocaleString('pt-BR', { month: 'long', year: 'numeric' });
    }

    //Função auxiliar para verificae se é mes/dia/hora/minuto
    function formatRelativeTime(diffMs) {
        const totalSeconds = Math.floor(diffMs / 1000);
        const totalDays = Math.floor(totalSeconds / 86400);
        const months = Math.floor(totalDays / 30);
        const days = totalDays % 30;
        const hours = Math.floor((totalSeconds % 86400) / 3600);
        const minutes = Math.floor((totalSeconds % 3600) / 60);

        if (months > 0) {
            return `${months} mês${months > 1 ? 'es' : ''}`;
        }
        if (days > 0) {
            return `${days} dia${days > 1 ? 's' : ''}`;
        }
        if (hours > 0) {
            return `${hours} hora${hours > 1 ? 's' : ''}`;
        }
        if (minutes > 0) {
            return `${minutes} minuto${minutes > 1 ? 's' : ''}`;
        }
        return 'agora';
    }

    //Função auxiliar que recebe uma jam e retorna um elemento jQuery pronto
    function createJamCard(jam) {
        const statusMap = {
            SCHEDULED: 'Agendada',
            ACTIVE: 'Em andamento',
            FINISHED: 'Finalizada',
        };
        const statusText = statusMap[jam.jamStatus] || jam.jamStatus;

        const agora = new Date();
        const dataInicio = new Date(jam.jamStartDate);
        const dataFim = new Date(jam.jamEndDate);

        const diffStart = dataInicio - agora;
        const diffEnd = dataFim - agora;

        let durationHtml;

        if (diffStart > 0) {
            // Acontece no futuro
            durationHtml = `
                <div class="duration-jam-card">
                    <p data-field class="skeleton">Começa em ${formatRelativeTime(diffStart)}</p>
                    <p data-field class="skeleton">Termina em ${formatRelativeTime(diffEnd)}</p>
                </div>
            `;
        } else if (diffEnd > 0) {
            // Já começou, mas ainda não acabou
            durationHtml = `
                <div class="duration-jam-card">
                    <p data-field class="skeleton">Termina em ${formatRelativeTime(diffEnd)}</p>
                </div>
            `;
        } else {
            // Já terminou
            durationHtml = `
                <div class="duration-jam-card">
                    <p data-field class="skeleton">Essa jam acabou</p>
                </div>
            `;
        }

        const card = `
            <div class="jam-card-home">
                <div class="header-jam-card-home">
                    <h1 data-field class="status-jam-card-home skeleton">${statusText}</h1>
                    <div class="aling-qtd-members-jam-card-home">
                        <span class="material-symbols-outlined">account_circle</span>
                        <p data-field class="skeleton">${jam.subscribersCount}</p>
                    </div>
                </div>
                <div class="container-jam-card-home">
                    <h1 data-field class="skeleton">${jam.jamTitle}</h1>
                    ${durationHtml}
                </div>
            </div>
        `;

        const newCard = $(card);

        const btnWrapper = $('<div>')
            .addClass('jam-btn-wrapper');

        const btn = $('<button>')
            .addClass('jam-btn-home')
            .text('Ver Jam')
            .on('click', () => window.location.href = `/jams/${jam.jamId}`);

        btnWrapper.append(btn);
        newCard.append(btnWrapper);

        return newCard;
    }

    //Mostra Jams de um mês
    function renderMonthSection(month, jams, total) {
        const section = $('<section>').addClass('month-section').attr('data-month', month);
        const title = $('<h2>').addClass('month-title').text(formatMonthTitle(month));
        const cardsContainer = $('<div>').addClass('cards-container');

        jams.forEach(jam => {
            const $card = createJamCard(jam);
            cardsContainer.append($card);
        });

        const shown = jams.length + monthOffsets[month];
        if (shown < total) {
            const $btnLoadMore = $('<button>')
                .addClass('load-more')
                .text('Carregar mais')
                .on('click', () => loadMore(month));
            section.append($btnLoadMore);
        }

        section.prepend(cardsContainer);
        container.append(title, section);
    }

    // Faz fetch e renderiza primeira página de um mês
    function loadMonth(month) {
        if (month < getCurrentMonth()) return;
        if (loadedMonths.includes(month) || isLoading) return;
        isLoading = true;
        monthOffsets[month] = 0;

        fetchJamsByMonth(month, 0, limitPerPage)
            .done(({ jams, total }) => {
                // só renderiza se vier pelo menos 1 jam
                if (jams.length > 0) {
                    renderMonthSection(month, jams, total);
                    const $section = container.find(`.month-section[data-month='${month}']`);
                    removeSkeleton($section);
                }

                loadedMonths.push(month);
                monthOffsets[month] = jams.length;
            })
            .fail(err => console.error(`Erro ao carregar mês ${month}:`, err))
            .always(() => { isLoading = false; });
    }

    // Carrega mais do mes
    function loadMore(month) {
        if (isLoading) return;
        isLoading = true;

        fetchJamsByMonth(month, monthOffsets[month], limitPerPage) //Chama api
            .done(({ jams, total }) => {
                const section = container.find(`.month-section[data-month='${month}']`);
                const cardsContainer = section.find('.cards-container');
                const oldLoad = section.find('.load-more');
                oldLoad.remove();

                jams.forEach(jam => {
                    const $card = createJamCard(jam);
                    cardsContainer.append($card);
                });

                const $section = container.find(`.month-section[data-month='${month}']`);
                removeSkeleton($section);

                monthOffsets[month] += jams.length; //atualiza qtd de jams mostrada no mes
                //se ainda tem mais mostra o btn de carregar mais
                if (monthOffsets[month] < total) {
                    const $btnLoadMore = $('<button>')
                    .addClass('load-more')
                    .text('Carregar mais')
                    .on('click', () => loadMore(month));
                    section.append($btnLoadMore);
                }
            })
            .fail(err => console.error(`Erro ao carregar mais em ${month}:`, err))
            .always(() => isLoading = false);
    }

    // Infinite scroll: quando chegar ao fim, carrega mês anterior
    $(window).on('scroll', () => {
        const scrollBottom = $(window).scrollTop() + $(window).height();
        if (scrollBottom + 100 >= $(document).height()) {
            let nextMonth = loadedMonths.length === 0
                ? getCurrentMonth()
                : (() => {
                    const [y, m] = loadedMonths[loadedMonths.length - 1].split('-').map(Number);
                    const date = new Date(y, m, 1);
                    return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,'0')}`;
                })();
            loadMonth(nextMonth);
        }
    });

    // Inicializa com mês atual
    loadMonth(getCurrentMonth());
});
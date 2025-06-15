import { fetchJamsByMonth } from '../services/jamService.js';

$(function() {

    //Mostrar Jams
    const container = $('.container-Jams');
    const limitPerPage = 20;
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

    //Função auxiliar que recebe uma jam e retorna um elemento jQuery pronto
    function createJamCard(jam) {
        const hoje = new Date();
        const dataInicio = new Date(jam.startDate);
        const dataFim = new Date(jam.endDate);

        const formato = { day: '2-digit', month: '2-digit', year: 'numeric' };
        const inicioStr = dataInicio.toLocaleDateString('pt-BR', formato);
        const fimStr = dataFim.toLocaleDateString('pt-BR', formato);

        const durationHtml = hoje > dataFim
            ? `<p>Essa jam acabou</p>`
            : ` <div class="duration-jam-card">
                    <p>Começa no dia ${inicioStr}</p>
                    <p>Termina no dia ${fimStr}</p>
                 </div>`;

        const card = `
            <div class="jam-card">
                <div class="header-jam-card">
                    <h1 class="status-jam-card">${jam.status}</h1>
                    <div class="aling-qtd-members-jam-card">
                        <span class="material-symbols-outlined">account_circle</span>
                        <p>${jam.qtdMembers}</p>
                    </div>
                </div>
                <div class="container-jam-card">
                    <h1>${jam.name}</h1>
                    ${durationHtml}
                </div>
            </div>
        `;

        const newCard = $(card);
        const btn = $('<button>')
            .addClass('jam-btn')
            .text('Ver Jam')
            .on('click', () => window.location.href = `/jams/${jam.id}`);
        newCard.append(btn);

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

        // Se houver mais jams, adiciona botão de carregar mais
        if (jams.length + monthOffsets[month] < total) {
            const loadMore = $('<button>')
                .addClass('load-more')
                .text('Carregar mais')
                .on('click', () => loadMore(month));
            section.append(loadMore);
        }

        section.prepend(cardsContainer);
        container.append(title, section);
    }

    // Faz fetch e renderiza primeira página de um mês
    function loadMonth(month) {
        if(loadedMonths.includes(month) || isLoading) return; //ja tem o mes ou tem requisição ainda!
        isLoading = true;
        monthOffsets[month] = 0;

        fetchJamsByMonth(month, 0, limitPerPage) //chama api para pegar dados
            .then(({jams, total}) => {
                renderMonthSection(month, jams, total) //mapeia na tela
                monthOffsets[month] += jams.length;
                loadedMonths.push(month);
            })
            .catch(err => console.error(`Erro ao carregar mês ${month}:`, err))
            .finally(() => isLoading = false)
    }

    // Carrega mais do mes
    function loadMore(month) {
        if (isLoading) return;
        isLoading = true;

        fetchJamsByMonth(month, monthOffsets[month], limitPerPage) //Chama api
            .then(({ jams, total }) => {
                const section = container.find(`.month-section[data-month='${month}']`);
                const cardsContainer = section.find('.cards-container');
                const oldLoad = section.find('.load-more');
                oldLoad.remove();

                jams.forEach(jam => {
                    const $card = createJamCard(jam);
                    cardsContainer.append($card);
                });

                monthOffsets[month] += jams.length; //atualiza qtd de jams mostrada no mes
                //se ainda tem mais mostra o btn de carregar mais
                if (monthOffsets[month] < total) {
                    const loadMore = $('<button>')
                        .addClass('load-more')
                        .text('Carregar mais')
                        .on('click', () => loadMore(month));
                    section.append(loadMore);
                }
            })
            .catch(err => console.error(`Erro ao carregar mais em ${month}:`, err))
            .finally(() => isLoading = false);
    }

    // Infinite scroll: quando chegar ao fim, carrega mês anterior
    $(window).on('scroll', () => {
        const scrollBottom = $(window).scrollTop() + $(window).height();
        if (scrollBottom + 100 >= $(document).height()) {
            let nextMonth;
            if (loadedMonths.length === 0) nextMonth = getCurrentMonth();
            else {
                const last = loadedMonths[loadedMonths.length - 1];
                const [y, m] = last.split('-').map(Number);
                const date = new Date(y, m-2);
                nextMonth = `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,'0')}`;
            }
            loadMonth(nextMonth);
        }
    });

    // Inicializa com mês atual
    loadMonth(getCurrentMonth());
});
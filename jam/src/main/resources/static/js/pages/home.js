import { fetchJamsByMonth, fetchBannerJams } from '../services/jamService.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { createJamCard } from '../common/cardBuilder.js';

// Função para inicializar o carrossel
async function initializeCarousel() {
    const carouselContainer = $('.home-carousel');
    const limit = 5;

    //Chama a API
    try {
        const { jams } = await fetchBannerJams(limit);

        if (!jams || jams.length === 0) {
            carouselContainer.hide();
            return;
        }

        //Itera sobre cada Jam retornada para criar um slide.
        jams.forEach(jam => {
            let slideHtml;
            if (jam.jamBanner) {
                slideHtml = `
                    <div>
                        <a href="/jams/${jam.id}" class="carousel-slide-link">
                            <img src="${jam.jamBanner}" alt="Banner para ${jam.jamTitle}" class="carousel-image"/>
                        </a>
                    </div>
                `;
            } else {
                slideHtml = `
                    <div>
                        <a href="/jams/${jam.id}" class="carousel-slide-link">
                            <div class="carousel-fallback">
                                <h1>${jam.jamTitle}</h1>
                            </div>
                        </a>
                    </div>
                `;
            }
            carouselContainer.append(slideHtml);
        });

        //Apenas inicialize o Slick se houver maus de um item.
        if (jams.length > 1) {
            carouselContainer.slick({
                dots: true,
                infinite: true,
                speed: 500,
                fade: true,
                cssEase: 'linear',
                autoplay: true,
                autoplaySpeed: 4000
            });
        }

    } catch (err) {
        console.error('Erro ao carregar banners da Jam:', err);
        carouselContainer.hide();
    }
}

$(function() {
    initializeCarousel();

    //Mostrar Jams
    const container = $('.container-Jams');
    const limitPerPage = 4;
    let loadedMonths = [];
    let monthOffsets = {};
    let isLoading = false;

    //Garante que uma função seja executada no máximo uma vez a cada X milissegundos.
    function throttle(func, limit) {
        let inThrottle;
        return function() {
            const args = arguments;
            const context = this;
            if (!inThrottle) {
                func.apply(context, args);
                inThrottle = true;
                setTimeout(() => inThrottle = false, limit);
            }
        }
    }

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


    //Mostra Jams de um mês
    function renderMonthSection(month, jams, total) {
        const section = $('<section>').addClass('month-section').attr('data-month', month);
        const title = $('<h2>').addClass('month-title').text(formatMonthTitle(month));
        const cardsContainer = $('<div>').addClass('cards-container');

        jams.forEach(jam => {
            const card = createJamCard(jam);
            cardsContainer.append(card);
        });

        const shown = jams.length + monthOffsets[month];
        if (shown < total) {
            const btnLoadMore = $('<button>')
                .addClass('load-more')
                .text('Carregar mais')
                .on('click', () => loadMore(month));
            section.append(btnLoadMore);
        }

        section.prepend(cardsContainer);
        container.append(title, section);
    }

    //Faz fetch e renderiza primeira página de um mês
    async function loadMonth(month) {
        if (month < getCurrentMonth() && month !== getCurrentMonth()) return;
        if (loadedMonths.includes(month) || isLoading) return;

        isLoading = true;
        loadedMonths.push(month);
        monthOffsets[month] = 0;

        const lastLoadMoreButton = $('.load-more').last();
        if (lastLoadMoreButton.length) {
            lastLoadMoreButton.text('Buscando mais Jams...');
        }

        try {
            const { jams, total } = await fetchJamsByMonth(month, 0, limitPerPage);
            if (jams.length > 0) {
                renderMonthSection(month, jams, total);
                const section = container.find(`.month-section[data-month='${month}']`);
                removeSkeleton(section);
                monthOffsets[month] = jams.length;
            }
        } catch (err) {
            console.error(`Erro ao carregar mês ${month}:`, err);
        } finally {
            isLoading = false;
            if (lastLoadMoreButton.length) {
                lastLoadMoreButton.text('Carregar mais');
            }
            setTimeout(checkAndLoadUntilScrollable, 0);
            $('footer').css({
                'transition': 'opacity 0.5s ease-in-out',
                'opacity': 1,
                'visibility': 'visible'
            });
        }

    }

    // ela vai verificar se o mês retornado do fetch estava vazio e carregar o próximo
    function checkAndLoadUntilScrollable() {
        if (!isLoading && $(document).height() <= $(window).height()) {
            const lastLoaded = loadedMonths[loadedMonths.length - 1];
            if (!lastLoaded) return;

            const section = $(`.month-section[data-month='${lastLoaded}']`);
            if (section.find('.jam-card-home').length === 0 || $(document).height() <= $(window).height()) {
                const [y, m] = lastLoaded.split('-').map(Number);
                const date = new Date(y, m - 1, 1);
                date.setMonth(date.getMonth() + 1);
                const nextMonth = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
                loadMonth(nextMonth);
            }
        }
    }

    //SSE handlers:
    const stream = new EventSource('/api/events?topic=jams-list-update');

    function updateJamCard(jamId, updater, payload) {
        if (!jamId) return;
        const card = $(`.jam-card-home[data-jamid="${jamId}"]`);
        if (card.length) {
            updater(card, payload);
        }
    }

    //Cria atualização em tempo real nas jams
    const sseHandlers = {
        'jam-insert': (e) => {
            const raw = JSON.parse(e.data);
            const jam = {
                ...raw,
                subscribersCount: raw.jamTotalSubscribers ?? raw.subscribeTotal ?? 0
            };

            const monthKey = jam.jamStartDate.slice(0, 7);
            const section = $(`.month-section[data-month="${monthKey}"]`);
            const cardsContainer = section.find('.cards-container');
            const newCard = createJamCard(jam);
            removeSkeleton(newCard);

            if (cardsContainer.length) {
                cardsContainer.prepend(newCard);
            } else {
                renderMonthSection(monthKey, [jam], 1);
            }
        },

        'subscriber-update': (e) => {
            const payload = JSON.parse(e.data);
            updateJamCard(payload.subscribeJamId, (card, { subscribeTotal }) => {
                card.find('.aling-qtd-members-jam-card-home p[data-field]')
                    .removeClass('skeleton')
                    .text(subscribeTotal ?? 0);
            }, payload);
        },

        'status-update': (e) => {
            const payload = JSON.parse(e.data);
            updateJamCard(payload.jamId, (card, { jamStatus }) => {
                const statusMap = { SCHEDULED: 'Agendada', ACTIVE: 'Em andamento', FINISHED: 'Finalizada' };
                const statusText = statusMap[jamStatus] || jamStatus;
                card.find('h1.status-jam-card-home[data-field]')
                    .removeClass('skeleton')
                    .text(statusText);
            }, payload);
        }
    };

    //Adiciona os listeners de evento a partir do mapa de handlers
    Object.entries(sseHandlers).forEach(([eventName, handler]) => {
        stream.addEventListener(eventName, handler);
    });

    //Tratamento de erro centralizado
    stream.onerror = (err) => {
        console.error('SSE connection error:', err);
        stream.close();
    };

    //Carrega mais do mes
    async function loadMore(month) {
        if (isLoading) return;
        isLoading = true;

        try {
            const { jams, total } = await fetchJamsByMonth(month, monthOffsets[month], limitPerPage);

            const section = container.find(`.month-section[data-month='${month}']`);
            const cardsContainer = section.find('.cards-container');
            const oldLoad = section.find('.load-more');
            oldLoad.remove();

            jams.forEach(jam => {
                const card = createJamCard(jam);
                cardsContainer.append(card);
            });

            const sectionM = container.find(`.month-section[data-month='${month}']`);
            removeSkeleton(sectionM);
            monthOffsets[month] += jams.length; //atualiza qtd de jams mostrada no mes

            //se ainda tem mais mostra o btn de carregar mais
            if (monthOffsets[month] < total) {
                const btnLoadMore = $('<button>')
                    .addClass('load-more')
                    .text('Carregar mais')
                    .on('click', () => loadMore(month));
                section.append(btnLoadMore);
            }
        }catch (err) {
            console.error(`Erro ao carregar mais em ${month}:`, err);
        } finally {
            isLoading = false;
            setTimeout(checkAndLoadUntilScrollable, 0);
        }

    }

    //Carregar proximos meses com Scroll
    function handleInfiniteScroll() {
        const scrollBottom = $(window).scrollTop() + $(window).height();
        if (scrollBottom + 100 >= $(document).height() && !isLoading) {

            const lastLoaded = loadedMonths[loadedMonths.length - 1];
            if (!lastLoaded) return;

            const [y, m] = lastLoaded.split('-').map(Number);
            const date = new Date(y, m - 1, 1);
            date.setMonth(date.getMonth() + 1);

            const nextMonth = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;

            loadMonth(nextMonth);
        }
    }

    $(window).on('scroll', throttle(handleInfiniteScroll, 100));

    // Inicializa com o mês atual
    loadMonth(getCurrentMonth());
});
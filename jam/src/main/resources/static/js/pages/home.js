import { fetchJamsByMonth, fetchBannerJams } from '../services/jamService.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';
import { createJamCardTemplate, populateJamCard } from '../common/cardBuilder.js';
import { bindDataFields } from '../common/bindDataFields.js';

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
                        <a href="/jams/${jam.jamId}" class="carousel-slide-link">
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
    let noMoreMonthsToLoad = false;

    function checkAndShowEmptyMessage() {
        setTimeout(() => {
            if (noMoreMonthsToLoad && container.children('.month-section').length === 0) {
                container.html('<div><h1>Nenhuma jam foi postada ainda.</h1></div>');
                $('footer').css({ 'opacity': 1, 'visibility': 'visible' });
            } else if (container.children('.month-section').length > 0) {
                $('footer').css({ 'opacity': 1, 'visibility': 'visible' });
            }
        }, 100);
    }

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
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
    }

    //Formata mês para título
    function formatMonthTitle(ym) {
        const [y, m] = ym.split('-');
        const date = new Date(y, m - 1);
        return date.toLocaleString('pt-BR', { month: 'long', year: 'numeric' });
    }

    //Faz fetch e renderiza primeira página de um mês
    async function loadMonth(month) {
        if (loadedMonths.includes(month) || isLoading || noMoreMonthsToLoad) return;
        isLoading = true;

        try {
            const { jams, total } = await fetchJamsByMonth(month, 0, limitPerPage);
            loadedMonths.push(month);

            if (jams && jams.length > 0) {
                //Cria a seção e seus elementos em memória
                const section = $('<section>').addClass('month-section').attr('data-month', month);
                const title = $('<h2>').addClass('month-title').text(formatMonthTitle(month));
                const cardsContainer = $('<div>').addClass('cards-container');
                section.append(title, cardsContainer);

                jams.forEach(jam => {
                    const newCardTemplate = createJamCardTemplate();
                    populateJamCard(newCardTemplate, jam);
                    cardsContainer.append(newCardTemplate);
                });

                monthOffsets[month] = jams.length;
                if (monthOffsets[month] < total) {
                    const btnLoadMore = $('<button>')
                        .addClass('load-more')
                        .text('Carregar mais')
                        .on('click', () => loadMore(month));
                    section.append(btnLoadMore);
                }

                //Adiciona a seção ao DOM.
                container.append(section);

                setTimeout(() => {
                    section.addClass('is-visible');
                }, 50); // 50ms é suficiente

            } else {
                noMoreMonthsToLoad = true;
            }

            checkAndLoadUntilScrollable();

        } catch (err) {
            console.error(`Erro ao carregar mês ${month}:`, err);
            noMoreMonthsToLoad = true;
        } finally {
            isLoading = false;
        }
    }

    //Verificar se o mês retornado do fetch estava vazio e carregar o próximo
    function checkAndLoadUntilScrollable() {
        if (isLoading || noMoreMonthsToLoad) return;

        const isScrollable = $(document).height() > $(window).height() + 50;

        if (!isScrollable) {
            const lastLoaded = loadedMonths[loadedMonths.length - 1];
            if (!lastLoaded) return;

            const [y, m] = lastLoaded.split('-').map(Number);
            const date = new Date(y, m, 1);
            const nextMonth = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;

            loadMonth(nextMonth);
        }
    }

    //SSE handlers:
    const stream = new EventSource('/api/events?topic=jams-list-update');

    //Cria atualização em tempo real nas jams
    const sseHandlers = {
        'jam-insert': (e) => {
            const jam = JSON.parse(e.data);
            const monthKey = jam.jamStartDate.slice(0, 7);
            let section = $(`.month-section[data-month="${monthKey}"]`);

            if (section.length === 0) {
                const monthsOnScreen = $('.month-section').map(function() { return $(this).data('month') }).get();
                const sortedMonths = [...monthsOnScreen, monthKey].sort().reverse();
                const newIndex = sortedMonths.indexOf(monthKey);

                section = $('<section>').addClass('month-section').attr('data-month', monthKey);
                const title = $('<h2>').addClass('month-title').text(formatMonthTitle(monthKey));
                const cardsContainer = $('<div>').addClass('cards-container');
                section.append(title, cardsContainer);

                if (newIndex === 0 || container.children().length === 0) {
                    container.prepend(section);
                } else {
                    const prevMonth = sortedMonths[newIndex - 1];
                    container.find(`.month-section[data-month='${prevMonth}']`).after(section);
                }
            }

            const cardsContainer = section.find('.cards-container');
            const newCardTemplate = createJamCardTemplate();
            populateJamCard(newCardTemplate, jam);
            cardsContainer.prepend(newCardTemplate);
        },

        'jam-subscribes-update': (e) => {
            const payload = JSON.parse(e.data);
            const card = $(`.jam-card-home[data-jamid="${payload.subscribeJamId}"]`);
            if (card.length) {
                bindDataFields({ jamTotalSubscribers: payload.subscribeTotal ?? 0 }, card);
            }
        },

        'jam-status-update': (e) => {
            const payload = JSON.parse(e.data);
            const card = $(`.jam-card-home[data-jamid="${payload.jamId}"]`);
            if (card.length) {
                const statusMap = { SCHEDULED: 'Agendada', ACTIVE: 'Em andamento', FINISHED: 'Finalizada' };
                const statusText = statusMap[payload.jamStatus] || payload.jamStatus;
                bindDataFields({ jamStatusText: statusText }, card);
            }
        }
    };

    Object.entries(sseHandlers).forEach(([eventName, handler]) => {
        stream.addEventListener(eventName, handler);
    });

    stream.onerror = (err) => {
        console.error('SSE connection error:', err);
        stream.close();
    };

    //Carrega mais do mes
    async function loadMore(month) {
        if (isLoading) return;
        isLoading = true;

        const section = container.find(`.month-section[data-month='${month}']`);
        const btnLoadMore = section.find('.load-more');
        btnLoadMore.text('Buscando mais Jams...').prop('disabled', true);

        const cardsContainer = section.find('.cards-container');

        try {
            const { jams, total } = await fetchJamsByMonth(month, monthOffsets[month], limitPerPage);

            jams.forEach((jam, index) => {
                const newCardTemplate = createJamCardTemplate();
                populateJamCard(newCardTemplate, jam);

                //Começa invisível
                newCardTemplate.css('opacity', 0);
                cardsContainer.append(newCardTemplate);

                //Aplica a animação com um pequeno atraso para cada card
                setTimeout(() => {
                    newCardTemplate.addClass('animate-in');
                }, index * 100); //Atraso de 100ms
            });

            monthOffsets[month] += jams.length;

            if (monthOffsets[month] >= total) {
                btnLoadMore.remove();
            } else {
                btnLoadMore.text('Carregar mais').prop('disabled', false);
            }

        }catch (err) {
            console.error(`Erro ao carregar mais em ${month}:`, err);
            btnLoadMore.text('Erro! Tente novamente').prop('disabled', false);
        } finally {
            isLoading = false;
        }
    }

    //Carregar proximos meses com Scroll
    function handleInfiniteScroll() {
        const scrollBottom = $(window).scrollTop() + $(window).height();
        if (scrollBottom + 150 >= $(document).height() && !isLoading && !noMoreMonthsToLoad) {
            const lastLoaded = loadedMonths[loadedMonths.length - 1];
            if (!lastLoaded) return;

            const [y, m] = lastLoaded.split('-').map(Number);
            const date = new Date(y, m, 1);
            const nextMonth = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;

            loadMonth(nextMonth);
        }
    }

    $(window).on('scroll', throttle(handleInfiniteScroll, 200));

    // Inicializa com o mês atual
    loadMonth(getCurrentMonth());
});
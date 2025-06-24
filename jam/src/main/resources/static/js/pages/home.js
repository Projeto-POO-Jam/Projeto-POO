import { fetchJamsByMonth, fetchBannerJams } from '../services/jamService.js';
import { applySkeleton, removeSkeleton } from '../common/skeleton.js';

// Função para inicializar o carrossel
function initializeCarousel() {
    const carouselContainer = $('.home-carousel');
    const limit = 5;

    //Chama a API
    fetchBannerJams(limit)
        .done(({ jams }) => {
            //Verifica se a API retornou alguma Jam para o banner.
            if (!jams || jams.length === 0) {
                carouselContainer.hide();
                return;
            }

            //Itera sobre cada Jam retornada para criar um slide.
            jams.forEach(jam => {
                let slideHtml;

                //Lógica para criar o slide:
                // Se a Jam tiver um 'jamBanner'.
                if (jam.jamBanner) {
                    slideHtml = `
                            <div>
                                <a href="/jams/${jam.jamId}" class="carousel-slide-link">
                                    <img src="${jam.jamBanner}" alt="Banner para ${jam.jamTitle}" class="carousel-image"/>
                                </a>
                            </div>
                        `;
                }
                //Cria um slide de "fallback" padrão.
                else {
                    slideHtml = `
                            <div>
                                <a href="/jams/${jam.jamId}" class="carousel-slide-link">
                                    <div class="carousel-fallback">
                                        <h1>${jam.jamTitle}</h1>
                                    </div>
                                </a>
                            </div>
                        `;
                }
                carouselContainer.append(slideHtml);
            });

            //inicializa a biblioteca Slick Carousel.
            carouselContainer.slick({
                dots: true,
                infinite: true,
                speed: 500,
                fade: true,
                cssEase: 'linear',
                autoplay: true,
                autoplaySpeed: 4000
            });
        })
        .fail(err => {
            console.error('Erro ao carregar banners da Jam:', err);
            carouselContainer.hide();
        });
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
            //Acontece no futuro
            durationHtml = `
                <div class="duration-jam-card">
                    <p data-field class="skeleton">Começa em ${formatRelativeTime(diffStart)}</p>
                    <p data-field class="skeleton">Termina em ${formatRelativeTime(diffEnd)}</p>
                </div>
            `;
        } else if (diffEnd > 0) {
            //Já começou, mas ainda não acabou
            durationHtml = `
                <div class="duration-jam-card">
                    <p data-field class="duration-solo-jam-card skeleton">Termina em ${formatRelativeTime(diffEnd)}</p>
                </div>
            `;
        } else {
            //Já terminou
            durationHtml = `
                <div class="duration-jam-card">
                    <p data-field class="skeleton">Essa jam acabou</p>
                </div>
            `;
        }

        const count = jam.jamTotalSubscribers
            ?? jam.jamTotalSubscribers
            ?? jam.subscribeTotal
            ?? 0;

        const card = `
            <div class="jam-card-home" data-jamid="${jam.jamId}">
                <div class="header-jam-card-home">
                    <h1 data-field class="status-jam-card-home skeleton">${statusText}</h1>
                    <div class="aling-qtd-members-jam-card-home">
                        <span class="material-symbols-outlined">account_circle</span>
                        <p data-field class="skeleton">${jam.jamTotalSubscribers}</p>
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
    function loadMonth(month) {
        if (month < getCurrentMonth() && month !== getCurrentMonth()) return;
        if (loadedMonths.includes(month) || isLoading) return;

        isLoading = true;
        loadedMonths.push(month);
        monthOffsets[month] = 0;

        const lastLoadMoreButton = $('.load-more').last();
        if (lastLoadMoreButton.length) {
            lastLoadMoreButton.text('Buscando mais Jams...');
        }

        fetchJamsByMonth(month, 0, limitPerPage)
            .done(({ jams, total }) => {
                if (jams.length > 0) {
                    renderMonthSection(month, jams, total);
                    const section = container.find(`.month-section[data-month='${month}']`);
                    removeSkeleton(section);
                    monthOffsets[month] = jams.length;
                }
            })
            .fail(err => {
                console.error(`Erro ao carregar mês ${month}:`, err);
            })
            .always(() => {
                isLoading = false; // Garante que terminou o estado de carregamento
                if (lastLoadMoreButton.length) {
                    lastLoadMoreButton.text('Carregar mais');
                }
                // Agora, com o estado atualizado, verifica se precisa de mais
                setTimeout(checkAndLoadUntilScrollable, 0);
            });
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
            })
            .fail(err => console.error(`Erro ao carregar mais em ${month}:`, err))
            .always(() => {
                isLoading = false;
                setTimeout(checkAndLoadUntilScrollable, 0);
            });
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
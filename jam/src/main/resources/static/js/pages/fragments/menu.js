import { fetchCurrentUser } from '../../services/userService.js';
import {
    fetchJamsSearch,
    fetchNotificationTotal,
    fetchNotifications,
    fetchMarkNotificationsAsRead
} from '../../services/menuService.js';
import { bindDataFields } from '../../common/bindDataFields.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';

$(async function () {
    const root = '.site-header';
    let activeUser = null;
    applySkeleton(root);

    try {
        activeUser = await fetchCurrentUser();
        bindDataFields(activeUser, root);

        if (activeUser && activeUser.userId) {
            $('#profile-link').attr('href', `/perfil/${activeUser.userId}`);
        }

        removeSkeleton(root);

    } catch (err) {
        console.error('Erro ao buscar usuário:', err);
        removeSkeleton(root);
    }

    //Lógica dos dropdowns
    const triggers = $('[data-dropdown-trigger]');
    const dropdowns = $('.menu-dropdown');

    triggers.on('click', function (e) {
        e.stopPropagation();

        const searchBarMobile = $('#search-mobile .bar-search-menu');
        if ($(this).is(searchBarMobile) && window.innerWidth <= 940) {
            if (!searchBarMobile.hasClass('search-expanded')) {
                e.preventDefault();

                $('header.site-header').addClass('search-active-mobile');

                searchBarMobile.addClass('is-animating');
                searchBarMobile.addClass('search-expanded');
                searchBarMobile.find('input').focus();

                setTimeout(() => {
                    searchBarMobile.removeClass('is-animating');
                }, 400);

                return;
            }
        }

        const targetId = $(this).data('dropdown-trigger');
        const targetDropdown = $(`#${targetId}-dropdown`);
        const isAlreadyActive = targetDropdown.hasClass('active');

        dropdowns.removeClass('active');
        $('.search-expanded').removeClass('search-expanded');

        if (!isAlreadyActive) {
            targetDropdown.addClass('active');
        }
    });

    //Fecha os dropdowns se clicar fora
    $(document).on('click', function (e) {
        if (!$(e.target).closest('[data-dropdown-trigger]').length && !$(e.target).closest('.menu-dropdown').length) {
            dropdowns.removeClass('active');
        }
        // Fecha busca mobile
        if (window.innerWidth <= 940 && !$(e.target).closest('#search-mobile').length) {
            $('#search-mobile .bar-search-menu').removeClass('search-expanded');
            $('header.site-header').removeClass('search-active-mobile');
        }
    });

    //barra de pesquisa em tela menores
    const searchBar = $('.bar-search-menu');

    searchBar.on('click', function (e) {
        // Só executa a lógica de expandir em telas menores
        if (window.innerWidth <= 940) {
            if (!searchBar.hasClass('search-expanded')) {
                e.preventDefault();
                searchBar.addClass('search-expanded');
                $('#jam-search-input').focus();
            }
        }
    });

    //Fecha a barra de busca se clicar fora dela
    $(document).on('click', function (e) {
        if (window.innerWidth <= 900) {
            if (!$(e.target).closest('.bar-search-menu').length && !$(e.target).closest('#search-dropdown').length) {
                searchBar.removeClass('search-expanded');
            }
        }
    });

    //Logica buscar jams
    const searchInputs = $('.jam-search-input');
    const searchResultsContainers = $('.search-results-container');
    const loadMoreButtons = $('.load-more-jams');

    //Variáveis de estado da busca
    let currentQuery = '';
    let searchOffset = 0;
    const searchLimit = 10;
    let totalJams = 0;
    let isSearchLoading = false;
    let debounceTimeout;

    //Função auxiliar para formatar a data.
    function formatDate(dateString) {
        if (!dateString) return 'Data não definida';
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR');
    }

    //Função para criar o card.
    function createJamCard(jam) {
        const startDate = formatDate(jam.jamStartDate);
        const endDate = formatDate(jam.jamEndDate);

        const card = $(`
        <a href="/jams/${jam.jamId}" class="jam-card-search">
            <div class="jam-card-search-info">
                <p class="jam-card-search-title">${jam.jamTitle}</p>
                <p class="jam-card-search-dates">${startDate} até ${endDate}</p>
                <p class="jam-card-search-status">${jam.jamStatus}</p>
            </div>
            <button class="jam-card-search-button">Acessar Jam</button>
        </a>
    `);

        //Previne o comportamento padrão do link ao clicar no botão
        card.find('.jam-card-search-button').on('click', function(e) {
            e.preventDefault();
            window.location.href = `/jams/${jam.jamId}`;
        });

        return card;
    }

    //Função principal que realiza a busca e exibe os resultados.
    async function performSearch(query, isLoadMore = false, activeContainer) {
        if (isSearchLoading) return;
        isSearchLoading = true;

        const resultsContainer = activeContainer.find('.search-results-container');
        const loadMoreButton = activeContainer.find('.load-more-jams');

        if (!isLoadMore) {
            searchOffset = 0;
            resultsContainer.html('');
            applySkeleton(resultsContainer);
        }

        try {
            const { jams, total } = await fetchJamsSearch(query, searchOffset, searchLimit);
            totalJams = total;

            if (!isLoadMore) {
                resultsContainer.html(''); // Limpa o skeleton
            }

            if (jams && jams.length > 0) {
                jams.forEach(jam => {
                    const jamCardElement = createJamCard(jam);
                    resultsContainer.append(jamCardElement);
                });
                searchOffset += jams.length;
            } else if (!isLoadMore) {
                resultsContainer.html('<p style="padding: 10px;">Nenhuma jam encontrada.</p>');
            }

            // Exibe ou esconde o botão "Carregar mais" correto
            if (searchOffset < totalJams) {
                loadMoreButton.show();
            } else {
                loadMoreButton.hide();
            }

        } catch (error) {
            console.error('Erro ao buscar jams:', error);
            resultsContainer.html('<p style="padding: 10px;">Erro ao buscar. Tente novamente.</p>');
        } finally {
            isSearchLoading = false;
        }
    }

    //Evento de "digitar" no input de busca
    searchInputs.on('keyup', function () {
        const query = $(this).val().trim();
        const activeSearchContainer = $(this).closest('.search-container');
        searchInputs.val(query);

        if (query === currentQuery) return;
        currentQuery = query;

        clearTimeout(debounceTimeout);

        if (query) {
            debounceTimeout = setTimeout(() => {
                performSearch(query, false, activeSearchContainer);
            }, 500);
        } else {
            searchResultsContainers.html('');
            loadMoreButtons.hide();
        }
    });

    // Evento de clique para AMBOS os botões "Carregar mais"
    loadMoreButtons.on('click', function () {
        if (currentQuery && !isSearchLoading) {
            const activeSearchContainer = $(this).closest('.search-container');
            performSearch(currentQuery, true, activeSearchContainer);
        }
    });

    //Logica de notifications

    const notificationBadge = $('#notification-count-badge');
    const notificationDropdown = $('#notifications-dropdown');
    const notificationListContainer = $('#notification-list-container');
    const loadMoreNotificationsButton = $('#load-more-notifications');

    let notificationOffset = 0;
    const notificationLimit = 10;
    let totalNotifications = 0;
    let isNotificationLoading = false;
    let notificationsInitialized = false;

    //Função para formatar datas relativas
    function formatRelativeTime(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diff = now - date;
        const seconds = Math.floor(diff / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);
        const days = Math.floor(hours / 24);

        if (days > 0) return `há ${days} dia(s)`;
        if (hours > 0) return `há ${hours} hora(s)`;
        if (minutes > 0) return `há ${minutes} minuto(s)`;
        return 'agora mesmo';
    }

    //Função para criar o card de notificação
    function createNotificationCard(notification) {
        //Bolinha da notificação não lida
        const unreadDot = notification.notificationRead === false ? '<div class="notification-unread-dot"></div>' : '';
        return `
            <a href="${notification.notificationLink}" class="notification-card">
                ${unreadDot}
                <div class="notification-content">
                    <p class="notification-message">${notification.notificationMessage}</p>
                    <p class="notification-date">${formatRelativeTime(notification.notificationDate)}</p>
                </div>
            </a>
        `;
    }

    //Função para buscar e exibir notificações
    async function loadNotifications(isInitialLoad = false) {
        if (isNotificationLoading) return;
        isNotificationLoading = true;
        loadMoreNotificationsButton.text('Carregando...').prop('disabled', true);

        if (isInitialLoad) {
            notificationListContainer.html('<div class="skeleton" style="height: 50px; margin: 10px;"></div><div class="skeleton" style="height: 50px; margin: 10px;"></div>');
        }

        try {
            const data = await fetchNotifications(notificationOffset, notificationLimit);
            if (isInitialLoad) {
                notificationListContainer.empty();
            }

            if (data.notifications && data.notifications.length > 0) {
                const notificationsHtml = data.notifications.map(createNotificationCard).join('');
                notificationListContainer.append(notificationsHtml);
                notificationOffset += data.notifications.length;
                totalNotifications = data.total;
            } else if (isInitialLoad) {
                notificationListContainer.html('<p style="padding: 15px; text-align: center;">Nenhuma notificação encontrada.</p>');
            }

            //Controla o botão "Carregar mais"
            if (notificationOffset < totalNotifications) {
                loadMoreNotificationsButton.show();
            } else {
                loadMoreNotificationsButton.hide();
            }

        } catch (error) {
            console.error("Erro ao buscar notificações:", error);
            notificationListContainer.html('<p style="padding: 15px; text-align: center;">Erro ao carregar.</p>');
        } finally {
            isNotificationLoading = false;
            loadMoreNotificationsButton.text('Carregar mais').prop('disabled', false);
        }
    }

    //Função para atualizar o contador de notificações
    async function updateNotificationCount() {
        try {
            const data = await fetchNotificationTotal();
            const count = data.notificationTotal;
            if (count > 0) {
                notificationBadge.text(count).show();
            } else {
                notificationBadge.hide();
            }
        } catch (error) {
            console.error("Erro ao buscar total de notificações:", error);
        }
    }

    //Clique no botão de "Carregar mais"
    loadMoreNotificationsButton.on('click', () => loadNotifications(false));

    //Clique no ícone de sino
    $('[data-dropdown-trigger="notifications"]').on('click', async function () {
        const dropdown = $('#notifications-dropdown');

        //Marca como lido ao abrir
        if (parseInt(notificationBadge.text()) > 0) {
            try {
                await fetchMarkNotificationsAsRead();
                notificationBadge.hide();
                //Remove os pontos azuis das notificações visíveis
                notificationListContainer.find('.notification-unread-dot').remove();
            } catch (error) {
                console.error("Erro ao marcar notificações como lidas:", error);
            }
        }

        //Carrega as notificações
        notificationOffset = 0;
        await loadNotifications(true);
        notificationsInitialized = true;

    });

    //SSE notification

    await updateNotificationCount();

    //Configuração do SSE para notificações em tempo real
    if (activeUser && activeUser.userId) {
        const stream = new EventSource(`/api/events?topic=notification-update`);
        const eventName = `user-notifications-${activeUser.userId}`;

        stream.addEventListener(eventName, function(event) {
            const newNotification = JSON.parse(event.data);
            updateNotificationCount();

            //Se o dropdown estiver aberto, adiciona a nova notificação no topo
            if (notificationDropdown.hasClass('active')) {
                notificationListContainer.prepend(createNotificationCard(newNotification));
            } else {
                //Se estiver fechado, marca que precisa recarregar da próxima vez para mostrar a nova notificação
                notificationsInitialized = false;
            }
        });

        stream.onerror = (err) => {
            console.error('SSE connection error for notifications:', err);
            stream.close();
        };
    }
});
import { applySkeleton, removeSkeleton } from '../../../common/skeleton.js';
import { showError } from "../../../common/notifications.js";

const PAGE_LIMIT = 10;

/**
 * Inicializa e gerencia uma aba com conteúdo paginado.
 * @param {object} config - Objeto de configuração.
 * @param {string} config.tabName - O nome da aba (ex: 'games', 'jams-created').
 * @param {string} config.userId - O ID do usuário.
 * @param {function} config.fetchFunction - A função de serviço para buscar os dados.
 * @param {function} config.cardCreator - A função para criar o card de um item.
 * @param {function} config.dataExtractor - A função para extrair o array de dados da resposta da API.
 */
export function initializePaginatedTab(config) {
    const { tabName, userId, fetchFunction, cardCreator, dataExtractor } = config;

    let currentPage = 0;
    let isLoading = false;
    let noMoreItems = false;

    const container = $(`#user-${tabName}-list-container`);
    const buttonContainer = $(`#load-more-${tabName}-container`);

    async function loadItems() {
        if (isLoading || noMoreItems || !userId) return;

        isLoading = true;
        const button = buttonContainer.find('button');
        button.text('Carregando...').prop('disabled', true);

        try {
            const responseData = await fetchFunction(userId, PAGE_LIMIT, currentPage * PAGE_LIMIT);
            const items = dataExtractor(responseData);

            if (items && items.length > 0) {
                items.forEach((item, index) => {
                    const card = cardCreator(item);
                    card.css('opacity', 0);
                    container.append(card);
                    setTimeout(() => {
                        card.addClass('animate-in');
                    }, index * 100);
                });
                currentPage++;
            }

            if (!items || items.length < PAGE_LIMIT) {
                noMoreItems = true;
                buttonContainer.hide();
            }

        } catch (error) {
            console.error(`Erro ao buscar mais itens para ${tabName}:`, error);
            showError(`Não foi possível carregar mais itens.`);
            noMoreItems = true;
            buttonContainer.hide();
        } finally {
            isLoading = false;
            removeSkeleton(container);
            button.text('Carregar Mais').prop('disabled', false);
        }
    }

    function createLoadMoreButton() {
        buttonContainer.empty().show();
        const button = $('<button class="load-more-btn">Carregar Mais</button>');
        button.on('click', loadItems);
        buttonContainer.append(button);
    }

    //Função de inicialização da aba
    async function init() {
        currentPage = 0;
        isLoading = false;
        noMoreItems = false;

        container.empty();
        applySkeleton(container);

        await loadItems();

        if (!noMoreItems) {
            createLoadMoreButton();
        }
    }

    init();
}
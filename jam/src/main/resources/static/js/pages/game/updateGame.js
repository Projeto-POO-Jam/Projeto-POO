import { initializeGameForm } from '../../common/gameFormManager.js';
import { fetchDadoFormUpdate } from "../../services/gameService.js";
import { bindDataFields } from "../../common/bindDataFields.js";
import { showError } from '../../common/notifications.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';

//A função principal agora é 'async' para podermos usar 'await'
$(async function() {
    const pathParts = window.location.pathname.split('/');
    const gameId = pathParts[pathParts.length - 1];
    const root = '.container-register-game';

    if (!gameId) {
        showError("ID do jogo não encontrado!");
        return;
    }

    applySkeleton(root);

    //A inicialização continua a mesma.
    const pondInstances = initializeGameForm({
        mode: 'edit',
        method: 'PUT',
        apiUrl: `/api/games/${gameId}`,
        redirectUrl: `/viewGame/${gameId}`
    });

    try {
        //A busca de dados continua a mesma.
        const data = await fetchDadoFormUpdate(gameId);

        if (!data) {
            showError("Dados do jogo não foram encontrados.");
            return;
        }

        //Preenchimento dos campos simples e Summernote continua igual.
        const root = '.container-register-game';
        bindDataFields(data, root);
        if (data.gameContent) {
            $('#gameContent').summernote('code', data.gameContent);
        }

        //Helper function para converter uma URL em um objeto File
        const urlToFile = async (url, filename, mimeType) => {
            const response = await fetch(url);
            const blob = await response.blob();
            return new File([blob], filename, { type: mimeType || blob.type });
        };

        //Carrega a imagem de capa
        if (data.gamePhoto && pondInstances.gamePhoto) {
            const fileName = data.gamePhoto.split('/').pop(); // Extrai o nome do arquivo da URL
            const file = await urlToFile(data.gamePhoto, fileName);
            pondInstances.gamePhoto.addFile(file); // Adiciona como se fosse um novo arquivo
        }

        //Carrega o arquivo do jogo
        if (data.gameFile && pondInstances.gameFile) {
            const fileName = data.gameFile.split('/').pop();
            const file = await urlToFile(data.gameFile, fileName, 'application/x-rar-compressed');
            pondInstances.gameFile.addFile(file);
        }

    } catch (error) {
        console.error("Erro no processo de edição:", error);
        showError("Falha ao carregar ou processar os dados para edição.");
    } finally {
        removeSkeleton(root);
    }
});
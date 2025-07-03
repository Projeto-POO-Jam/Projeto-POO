import { initializeGameForm } from './gameForm.js';
import { fetchDadoFormUpdate, deleteGame } from "../../services/gameService.js";
import { bindDataFields } from "../../common/bindDataFields.js";
import { showError, showSuccess} from '../../common/notifications.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';

$(async function() {
    const pathParts = window.location.pathname.split('/');
    const gameId = pathParts[pathParts.length - 1];
    const root = '.container-register-game';

    if (!gameId) {
        showError("ID do jogo não encontrado!");
        const redirectUrl = document.referrer.includes('/404');
        window.location.href = redirectUrl;
        return;
    }

    applySkeleton(root);

    //A inicialização continua a mesma.
    const pondInstances = initializeGameForm({
        mode: 'edit',
        method: 'PUT',
        apiUrl: `api/games`,
        redirectUrl: `/viewGame/${gameId}`,
        entityId: gameId
    });

    try {
        const data = await fetchDadoFormUpdate(gameId);

        if (!data) {
            showError("Dados do jogo não foram encontrados.");
            return;
        }

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
            const fileName = data.gamePhoto.split('/').pop(); //Extrai o nome do arquivo da URL
            const file = await urlToFile(data.gamePhoto, fileName);
            pondInstances.gamePhoto.addFile(file); //Adiciona como se fosse um novo arquivo
        }

    } catch (error) {
        window.location.href = document.referrer.includes('/404');
        console.error("Erro no processo de edição:", error);
        showError("Falha ao carregar ou processar os dados para edição.");
    } finally {
        removeSkeleton(root);
    }

    $('#deleteButton').on('click', async function() {
        const result = await Swal.fire({
            title: 'Você tem certeza?',
            text: "Esta ação não pode ser desfeita! O seu jogo será excluído permanentemente.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#e75050',
            cancelButtonColor: '#5865f2',
            confirmButtonText: 'Sim, deletar jogo!',
            cancelButtonText: 'Cancelar',
            customClass: {
                popup: 'swal-custom-popup',
                title: 'swal-custom-title',
                htmlContainer: 'swal-custom-html-container'
            }
        });

        if (result.isConfirmed) {
            try {
                await deleteGame(gameId);
                showSuccess('Jogo deletado com sucesso!');
                setTimeout(() => {
                    const redirectUrl = document.referrer.includes('/jams/') ? document.referrer : '/home';
                    window.location.href = redirectUrl;
                }, 2000);
            } catch (err) {
                showError('Ocorreu um erro ao deletar o jogo.');
                console.error('Erro ao deletar o jogo:', err);
            }
        }
    });
});
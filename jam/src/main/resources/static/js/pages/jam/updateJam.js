import { initializeJamForm } from './jamForm.js';
import { fetchDadoFormUpdate } from "../../services/jamService.js";
import { bindDataFields } from "../../common/bindDataFields.js";
import { showError } from '../../common/notifications.js';
import { applySkeleton, removeSkeleton } from '../../common/skeleton.js';

/**
 * Converte uma data string (ISO) para o formato 'dd/mm/aaaa HH:MM'.
 * @param {string} isoString - A data no formato '2025-06-26T12:00:00'.
 * @returns {string} - A data formatada como '26/06/2025 12:00'.
 */
function formatISODateToInput(isoString) {
    if (!isoString) return '';

    const date = new Date(isoString);
    if (isNaN(date)) return '';

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // Mês é base 0
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${day}/${month}/${year} ${hours}:${minutes}`;
}

$(async function() {
    const pathParts = window.location.pathname.split('/');
    const jamId = pathParts[pathParts.length - 1];
    const root = '.container-register-jam';

    if (!jamId) {
        window.location.href = '/404';
        return;
    }

    applySkeleton(root);

    //Inicializa o formulário em modo 'edit'.
    const { pondInstances, pickrInstances } = initializeJamForm({
        mode: 'edit',
        method: 'PUT',
        apiUrl: `api/jams`,
        redirectUrl: `/jams/${jamId}`,
        entityId: jamId
    });

    try {
        //Busca os dados da Jam usando o service
        const data = await fetchDadoFormUpdate(jamId);

        if (!data) {
            showError("Dados da Jam não foram encontrados.");
            return;
        }

        //Preenche os campos simples e o Summernote
        bindDataFields(data, root);

        if (data.jamContent) {
            $('#content').summernote('code', data.jamContent);
        }

        //Preenchendo as datas com Flatpickr
        const formattedStartDate = formatISODateToInput(data.jamStartDate);
        $('#startDate').val(formattedStartDate);

        const formattedEndDate = formatISODateToInput(data.jamEndDate);
        $('#endDate').val(formattedEndDate);

        //Preenchendo as cores com Pickr
        if (data.jamBackgroundColor && pickrInstances.backgroundColor) {
            pickrInstances.backgroundColor.setColor(data.jamBackgroundColor);
        }
        if (data.jamBackgroundCardColor && pickrInstances.cardBackgroundColor) {
            pickrInstances.cardBackgroundColor.setColor(data.jamBackgroundCardColor);
        }
        if (data.jamTextColor && pickrInstances.textColor) {
            pickrInstances.textColor.setColor(data.jamTextColor);
        }
        if (data.jamLinkColor && pickrInstances.linkColor) {
            pickrInstances.linkColor.setColor(data.jamLinkColor);
        }

        //Carrega as imagens existentes no FilePond
        const urlToFile = async (url, filename, mimeType) => {
            const response = await fetch(url);
            const blob = await response.blob();
            return new File([blob], filename, { type: mimeType || blob.type });
        };

        if (data.jamCover && pondInstances.jamCover) {
            const fileName = data.jamCover.split('/').pop();
            const file = await urlToFile(data.jamCover, fileName);
            pondInstances.jamCover.addFile(file);
        }

        if (data.jamWallpaper && pondInstances.jamWallpaper) {
            const fileName = data.jamWallpaper.split('/').pop();
            const file = await urlToFile(data.jamWallpaper, fileName);
            pondInstances.jamWallpaper.addFile(file);
        }

        if (data.jamBanner && pondInstances.jamBanner) {
            const fileName = data.jamBanner.split('/').pop();
            const file = await urlToFile(data.jamBanner, fileName);
            pondInstances.jamBanner.addFile(file);
        }

    } catch (error) {
        console.error("Erro no processo de edição da Jam:", error);
        window.location.href = '/404';
    } finally {
        removeSkeleton(root);
    }

    //Adicione a lógica do botão de deletar aqui
    $('#deleteButton').on('click', async function() {
        const result = await Swal.fire({
            title: 'Você tem certeza?',
            text: "Isto irá apagar a Jam e TODOS os jogos enviados para ela. Esta ação é irreversível!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#e75050',
            cancelButtonColor: '#5865f2',
            confirmButtonText: 'Sim, deletar Jam!',
            cancelButtonText: 'Cancelar',
            customClass: {
                popup: 'swal-custom-popup',
                title: 'swal-custom-title',
                htmlContainer: 'swal-custom-html-container'
            }
        });

        if (result.isConfirmed) {
            try {
                await deleteJam(jamId);
                showSuccess('Jam deletada com sucesso!');
                setTimeout(() => {
                    window.location.href = '/home';
                }, 2000);
            } catch (err) {
                showError('Ocorreu um erro ao deletar a Jam.');
                console.error('Erro ao deletar a Jam:', err);
            }
        }
    });
});
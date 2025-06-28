import { apiRequest } from '../../../common/api.js';
import { showSuccess, showError } from '../../../common/notifications.js';
import { setupValidation, isFormValid } from '../../../common/validation.js';
import { bindDataFields } from '../../../common/bindDataFields.js';
import { setupImageCrop, getCroppedBlob } from '../../../common/imageCropUtils.js';
import { registerFilePondPlugins, createFilePondInstance } from '../../../common/filepond-helper.js';

let pondInstance = null;
let initialData = null;

//Função para carregar a imagem existente no FilePond
const urlToFile = async (url, filename, mimeType) => {
    if (!url) return null;
    try {
        const response = await fetch(url);
        const blob = await response.blob();
        return new File([blob], filename, { type: mimeType || blob.type });
    } catch (e) {
        console.error("Erro ao converter URL para arquivo:", e);
        return null;
    }
};

//Função de inicialização do modal
export async function initEditPerfilModal(userData) {
    initialData = userData;
    const form = $('#editPerfilForm');
    const modal = $('#edit-perfil-modal');

    //Preenche o formulário com os dados do usuário
    bindDataFields(userData, modal);

    //Configura o crop da imagem de perfil
    const $avatarPreview = $('#avatarPreview');
    const defaultAvatarUrl = '/img/icone-padrao.svg';
    if (userData.userPhoto) {
        $avatarPreview.attr('src', userData.userPhoto);
    } else {
        $avatarPreview.attr('src', defaultAvatarUrl);
    }

    setupImageCrop();

    //Configura o FilePond para o banner
    registerFilePondPlugins();
    pondInstance = createFilePondInstance('#userBanner', {
        labelIdle: `Arraste a <strong>imagem de banner</strong> ou <span class="filepond--label-action">Procure</span>`,
        stylePanelLayout: 'integrated',
        acceptedFileTypes: ['image/png', 'image/jpeg', 'image/gif'],
    });

    //Carrega a imagem do banner, se existir
    if (userData.userBanner) {
        const fileName = userData.userBanner.split('/').pop();
        const file = await urlToFile(userData.userBanner, fileName);
        if (file) {
            pondInstance.addFile(file);
        }
    }

    //Regras de validação
    const validationRules = {
        userName: [
            { validate: value => value && value.trim() !== '', message: 'Nome de usuário é obrigatório.' },
            { validate: value => value && !value.includes(' '), message: 'Não pode conter espaços.' }
        ],
        userEmail: [
            { validate: value => value.trim() !== '', message: 'E-mail obrigatório.' },
            { validate: value => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value), message: 'Formato de e-mail inválido.' }
        ],
        userBanner: [{
            validate: () => !pondInstance?.getFiles().some(file => file.status === 8),
            message: 'O banner possui um tipo de arquivo inválido.'
        }]
    };
    setupValidation(validationRules);

    // Evento de submit do formulário
    form.on('submit', async e => {
        e.preventDefault();
        if (!isFormValid(validationRules)) {
            showError('Por favor, corrija os campos inválidos.');
            return;
        }

        const submitButton = form.find('#submitButton');
        submitButton.prop('disabled', true).text('Salvando...');

        const formData = new FormData();
        formData.append('userName', $('#userName').val().trim());
        formData.append('userEmail', $('#userEmail').val().trim());
        formData.append('userGitHub', $('#userGitHub').val().trim());
        formData.append('userLinkedIn', $('#userLinkedIn').val().trim());
        formData.append('userFacebook', $('#userFacebook').val().trim());
        formData.append('userInstagram', $('#userInstagram').val().trim());

        //Adiciona a foto de perfil
        const croppedBlob = getCroppedBlob();
        if (croppedBlob instanceof Blob) {
            formData.append('userPhoto', croppedBlob, 'avatar.jpg');
        }

        //Adiciona o banner
        if (pondInstance.getFile() && pondInstance.getFile().origin === 1) { // 1 = INPUT
            formData.append('userBanner', pondInstance.getFile().file);
        }

        try {
            await apiRequest('PUT', 'api/users', formData, false);
            showSuccess('Perfil atualizado com sucesso!');
            setTimeout(() => window.location.reload(), 2000); //Recarrega a página
        } catch (err) {
            showError('Erro ao atualizar o perfil.');
            submitButton.prop('disabled', false).text('Salvar Alterações');
        }
    });

    modal.show();
}
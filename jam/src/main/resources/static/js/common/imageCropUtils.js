let cropper = null;
let croppedBlob = null;

/**
 * Inicializa o modal de crop usando seletores fixos.
 */
export function setupImageCrop() {
    const $input = $('#avatarInput');
    const $preview = $('#avatarPreview');
    const $modal = $('#cropModal');
    const $cropImage = $('#cropImage');
    const $confirm = $('#cropConfirm');
    const $cancel = $('#cropCancel');

    // Evento de seleção de arquivo
    $input.on('change', () => {
        const file = $input[0].files[0];
        if (!file || !file.type.startsWith('image/')) return;

        const url = URL.createObjectURL(file);
        $cropImage.attr('src', url);
        $modal.addClass('active');

        cropper = new Cropper($cropImage[0], {
            aspectRatio: 1,
            viewMode: 0,
            autoCropArea: 0.6,
            responsive: true,
            zoomOnWheel: true,
            wheelZoomRatio: 0.1
        });
    });

    // Cancelar edição
    $cancel.on('click', () => {
        if (cropper) {
            cropper.destroy();
            cropper = null;
        }
        $modal.removeClass('active');
    });

    // Confirmar crop
    $confirm.on('click', () => {
        if (!cropper) return;

        cropper.getCroppedCanvas({ imageSmoothingQuality: 'high' })
            .toBlob(blob => {
                croppedBlob = blob;
                const previewUrl = URL.createObjectURL(blob);
                $preview.css('background-image', `url(${previewUrl})`);
                $preview.addClass('has-image').removeClass('theme-aware-icon');
            }, 'image/jpeg', 0.9);

        cropper.destroy();
        cropper = null;
        $modal.removeClass('active');
    });
}

/**
 * Retorna o Blob do último crop (ou null se não houver).
 */
export function getCroppedBlob() {
    return croppedBlob;
}
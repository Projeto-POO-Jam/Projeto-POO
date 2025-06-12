export function attachImagePreview(inputSelector, previewSelector) {
    const input = $(inputSelector);
    const prev = $(previewSelector);
    const defaultSrc = prev.attr('src');
    input.on('change', () => {
        const file = input[0].files[0];
        if (!file || !file.type.startsWith('image/')) {
            prev.attr('src', defaultSrc);
            return;
        }
        const reader = new FileReader();
        reader.onload = e => prev.attr('src', e.target.result);
        reader.readAsDataURL(file);
    });
}

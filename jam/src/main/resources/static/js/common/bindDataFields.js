/**
 * Faz binding automático dos campos do objeto `data` em elementos com [data-field],
 * adicionando tratamento de skeleton (placeholder) para indicar carregamento em imagens, inputs e textos.
 * @param {object} data – objeto retornado pela API, ex: { userName: "...", userEmail: "...", userPhoto: "..." }
 * @param {string|jQuery|Element} [root] – escopo opcional para busca; se não informado, varre todo o documento.
 */
export function bindDataFields(data, root) {
    if (!data || typeof data !== 'object') return;
    const $scope = root ? $(root) : $(document);

    $scope.find('[data-field]').each(function() {
        const $el = $(this);
        const field = $el.data('field'); // ex: "userName", "userPhoto", etc.
        if (!field) return;

        const value = data[field];

        if ($el.is('img')) {
            const defaultSrc = $el.data('default') || '';
            const src = value ? value : (defaultSrc ? defaultSrc : null);

            if (src) {
                $el.off('load.skeleton error.skeleton')
                    .on('load.skeleton', function() {
                        $el.removeClass('skeleton');
                    })
                    .on('error.skeleton', function() {
                        $el.addClass('skeleton');
                        $el.removeAttr('src');
                    });
                $el.attr('src', src);
            } else {
                $el.removeClass('skeleton');
                $el.removeAttr('src');
            }
        }
        else if ($el.is('input') || $el.is('textarea') || $el.is('select')) {
            $el.val(value != null ? value : '');
            if ($el.hasClass('skeleton')) {
                $el.removeClass('skeleton');
            }
            $el.prop('disabled', false);
        }
        else {
            $el.text(value != null ? value : '');
            if ($el.hasClass('skeleton')) {
                $el.removeClass('skeleton');
            }
        }
    });
}

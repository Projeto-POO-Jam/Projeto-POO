/**
 * Faz binding automático dos campos do objeto `data` em elementos com [data-field],
 * suportando chaves aninhadas via ponto (ex: "jamUser.userName").
 * @param {object} data – objeto retornado pela API.
 * @param {string|jQuery|Element} [root] – escopo opcional para busca; se não informado, varre todo o documento.
 */
export function bindDataFields(data, root) {
    if (!data || typeof data !== 'object') return;
    const $scope = root ? $(root) : $(document);

    $scope.find('[data-field]').each(function() {
        const $el = $(this);
        const field = $el.data('field'); //Ex: "jamUser.userName"
        if (!field) return;

        //Percorre níveis via ponto:
        const value = field
            .split('.')
            .reduce((obj, key) => (obj != null ? obj[key] : undefined), data);

        //Preenche conforme tipo de elemento:
        if ($el.is('img')) {
            const defaultSrc = $el.data('default') || '';
            const src = value
                ? value
                : (defaultSrc ? defaultSrc : null);

            if (src) {
                $el
                    .off('load.skeleton error.skeleton')
                    .on('load.skeleton',() => $el.removeClass('skeleton'))
                    .on('error.skeleton',() => { $el.addClass('skeleton'); $el.removeAttr('src'); })
                    .attr('src', src);
            } else {
                $el.removeClass('skeleton').removeAttr('src');
            }
        }
        else if ($el.is('input') || $el.is('textarea') || $el.is('select')) {
            $el.val(value != null ? value : '')
                .removeClass('skeleton')
                .prop('disabled',false);
        }
        else {
            $el.text(value != null ? value : '')
                .removeClass('skeleton');
        }
    });
}

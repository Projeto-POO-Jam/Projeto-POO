/**
 * Faz binding automático dos campos do objeto `data` em elementos com [data-field].
 * @param {object} data – objeto retornado pela API, ex: { userName: "...", userEmail: "...", userPhoto: "..." }
 * @param {string|jQuery|Element} [root] – escopo opcional para busca; se não informado, varre todo o documento.
 */
export function bindDataFields(data, root) {
    if (!data || typeof data !== 'object') return;
    // Escopo de busca: se root informado, converte para jQuery; senão, document
    const $scope = root ? $(root) : $(document);
    $scope.find('[data-field]').each(function() {
        const $el = $(this);
        const field = $el.data('field'); // ex: "userName", "userEmail", "userPhoto"
        if (!field) return;
        const value = data[field];
        // Se for img
        if ($el.is('img')) {
            const defaultSrc = $el.data('default') || '';
            const src = value ? value : defaultSrc;
            if (src) {
                $el.attr('src', src);
            } else {
                $el.removeAttr('src');
            }
        }
        // Se for input/textarea/select, setar value
        else if ($el.is('input') || $el.is('textarea') || $el.is('select')) {
            $el.val(value != null ? value : '');
        }
        // Senão, setar texto
        else {
            $el.text(value != null ? value : '');
        }
    });
}

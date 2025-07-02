/**
 * Aplica placeholder skeleton em elementos [data-field] dentro de `root`.
 * Se for <img>, atribui src transparente para evitar erro, e adiciona classe .skeleton para shimmer.
 * Para inputs/textareas/selects, limpa valor e desabilita.
 * Para outros, limpa texto.
 * @param {string|jQuery|Element} [root] – escopo opcional. Se não informado, varre todo o documento.
 */
export function applySkeleton(root) {
    const $scope = root ? $(root) : $(document);
    $scope.find('[data-field]').each(function() {
        const $el = $(this);

        // Aplica a classe skeleton se ainda não estiver
        if (!$el.hasClass('skeleton')) {
            $el.addClass('skeleton');
        }

        if ($el.is('img')) {
            $el.removeAttr('src');
        }

        else if ($el.is('input') || $el.is('textarea') || $el.is('select')) {
            $el.val('');
            $el.prop('disabled', true);
        }
        else {
            $el.text('');
        }
    });
}

/**
 * Remove placeholder skeleton em elementos [data-field] dentro de `root`.
 * Remove classe .skeleton e reabilita inputs/textareas/selects.
 * @param {string|jQuery|Element} [root]
 */
export function removeSkeleton(root) {
    const $scope = root ? $(root) : $(document);

    // Encontra qualquer elemento com a classe .skeleton e a remove.
    $scope.find('.skeleton').removeClass('skeleton');

    //Reabilita os campos de formulário que possam ter sido desabilitados.
    $scope.find('input, textarea, select').prop('disabled', false);
}

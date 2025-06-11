/**
 * Configura validação (blur e input) para cada campo.
 * @param {Object} rulesMap - mapeia fieldId -> array de regras { validate: fn, message: string }
 */
export function setupValidation(rulesMap) {
    Object.entries(rulesMap).forEach(([fieldId, rules]) => {
        const input = $(`#${fieldId}`);
        const err = $(`#${fieldId}Error`);

        input.on('blur input',() => {
            const val = $.trim(input.val());
            const failed = rules.find(rule => !rule.validate(val));
            if (failed) {
                err.text(failed.message);
                input.addClass('error');
            } else {
                err.text('');
                input.removeClass('error');
            }
        });
    });
}

/**
 * Verifica se todos os campos estão válidos forçando o `blur`.
 * Retorna true se nenhum campo tiver mensagem de erro.
 */
export function isFormValid(rulesMap) {
    return Object.keys(rulesMap).every(id => {
        $(`#${id}`).trigger('blur');
        return $(`#${id}Error`).text() === '';
    });
}

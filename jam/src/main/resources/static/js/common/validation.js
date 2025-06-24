// Função auxiliar que faz a validação de um único campo
function runFieldValidation(fieldId, rules) {
    const input = $(`#${fieldId}`);
    const err = $(`#${fieldId}Error`);
    const container = input.hasClass('filepond') ? input.parent() : input;

    const value = input.hasClass('filepond') ? null : input.val();
    const failedRule = rules.find(rule => !rule.validate(value));

    if (failedRule) {
        err.text(failedRule.message).addClass('visible');
        container.addClass('error');
        return false;
    } else {
        err.removeClass('visible');
        container.removeClass('error');
        setTimeout(() => {
            if (!err.hasClass('visible')) {
                err.text('');
            }
        }, 200);
        return true;
    }
}

/**
 * Configura a validação em tempo real para um mapa de regras de um formulário.
 */
export function setupValidation(rulesMap) {
    Object.entries(rulesMap).forEach(([fieldId, rules]) => {
        const input = $(`#${fieldId}`);
        //O listener agora simplesmente chama a nossa função auxiliar
        const listener = () => runFieldValidation(fieldId, rules);

        if (input.hasClass('filepond')) {
            const element = input[0];
            element.addEventListener('FilePond:addfile', listener);
            element.addEventListener('FilePond:processfile', listener);
            element.addEventListener('FilePond:removefile', listener);
        } else {
            input.on('blur input', listener);
        }
    });
}

/**
 * Força a validação de todos os campos e retorna o estado de validade geral.
 */
export function isFormValid(rulesMap) {
    const validationResults = Object.entries(rulesMap).map(([fieldId, rules]) => {
        return runFieldValidation(fieldId, rules);
    });
    //Retorna true apenas se TODOS os resultados no array forem true
    return validationResults.every(isValid => isValid);
}
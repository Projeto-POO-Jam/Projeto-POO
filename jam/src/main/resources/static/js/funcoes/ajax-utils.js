var BASE_URL = 'http://localhost:8080';

/**
 * Função genérica para fazer requisição AJAX ao backend.
 * @param {string} type  – verbo HTTP ('GET', 'POST', 'PUT', 'DELETE', etc.).
 * @param {string} route – rota após o "/" base, ex: 'login', 'usuarios/123', 'produtos'.
 * @param {object} [data] – objeto com parâmetros.
 *                          Para GET: vira querystring;
 *                          para POST/PUT/DELETE que envia corpo, vira JSON.
 * @returns {Promise}    – retorna um Promise-like (jqXHR) que, em caso de sucesso,
 *                          resolve um objeto { status, data } e, em erro, rejeita { status, error }.
 */
function apiRequest(type, route, data) {
    var deferred = $.Deferred();

    var urlCompleta = BASE_URL + '/' + route;

    // Determina opções básicas do ajax
    var ajaxOptions = {
        url: urlCompleta,
        method: type.toUpperCase(),
        success: function(response, textStatus, xhr) {
            deferred.resolve({
                status: xhr.status,
                data: response
            });
        },
        error: function(xhr, textStatus, errorThrown) {
            deferred.reject({
                status: xhr.status,
                error: errorThrown
            });
        }
    };

    if (type.toUpperCase() === 'GET') {
        ajaxOptions.dataType = 'json';
        if (data) {
            ajaxOptions.data = data;
            // ex: { id: 5, filtro: 'ativo' } vira ?id=5&filtro=ativo
        }
    } else {
        ajaxOptions.contentType = 'application/json; charset=UTF-8';
        if (data) {
            ajaxOptions.data = JSON.stringify(data);
        }
    }

    $.ajax(ajaxOptions);
    return deferred.promise();
}

var BASE_URL = 'http://localhost:8080';

/**
 * Função genérica para requisições AJAX com contentType default ou customizado.
 * Se não passar contentType, usa 'application/json; charset=UTF-8'.
 * @param {string} type          – verbo HTTP ('GET', 'POST', etc.).
 * @param {string} route         – rota após '/', ex: 'perform_login'.
 * @param {object|string} [data] – dados: objeto para JSON ou para form-urlencoded.
 * @param {string} [ct]          – opcional: contentType; padrão application/json.
 * @returns {Promise}            – resolve {status, data} ou rejeita {status, error}.
 */
function apiRequest(type, route, data, ct) {
    var deferred = $.Deferred();
    var method = type.toUpperCase();
    var url = BASE_URL + '/' + route;

    var contentType = ct || 'application/json; charset=UTF-8';
    var sendData;
    var processData = true;

    if (data instanceof FormData) {
        sendData   = data;
        processData = false;
        contentType = false;
    } else if (contentType.indexOf('application/json') === 0) {
        sendData = data != null ? JSON.stringify(data) : null;
        processData = false;
    } else {
        sendData = data;
    }

    $.ajax({
        url: url,
        method: method,
        contentType: contentType,
        processData: processData,
        dataType: 'text',
        data: sendData,
        complete: function(xhr) {
            var status = xhr.status;
            var raw = xhr.responseText;
            var parsed;
            if (raw) {
                try { parsed = JSON.parse(raw); } catch (e) { parsed = undefined; }
            }
            if (status >= 200 && status < 300) {
                deferred.resolve({ status: status, data: parsed });
            } else {
                deferred.reject({ status: status, error: xhr.statusText });
            }
        }
    });

    return deferred.promise();
}

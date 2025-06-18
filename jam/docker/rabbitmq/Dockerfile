FROM rabbitmq:3.13-management

RUN apt-get update && apt-get install -y wget unzip && \
    echo "--- Conteúdo de /plugins ANTES do download ---" && \
    ls -l /plugins/

RUN wget https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.13.0/rabbitmq_delayed_message_exchange-3.13.0.ez -P /plugins/

RUN echo "--- Conteúdo de /plugins DEPOIS do download ---" && \
    ls -l /plugins/

RUN rabbitmq-plugins enable rabbitmq_delayed_message_exchange
FROM rabbitmq:3.13-management
ADD https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.13.0/rabbitmq_delayed_message_exchange-3.13.0.ez /plugins/
RUN rabbitmq-plugins enable rabbitmq_delayed_message_exchange
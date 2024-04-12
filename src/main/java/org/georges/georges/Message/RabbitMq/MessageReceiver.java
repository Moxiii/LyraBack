package org.georges.georges.Message.RabbitMq;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.rabbitmq.client.*;
import com.sun.jdi.PrimitiveValue;
import lombok.extern.slf4j.Slf4j;
import org.georges.georges.Message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageReceiver {

    //public static final String EXCHANGE_NAME = "direct_messages";
    public static final DeliverCallback callback =  (consumerTag , delivery)->{
        String message = new String(delivery.getBody() , "UTF-8");
        log.info("Received message :{}" , message);
    };

    public static void receiveDirectMessages(Long senderId, Long receiverId, MessageHandler messageHandler) throws Exception {
        GenerateQueueName generateQueueName = new GenerateQueueName();
        String QUEUE_NAME = generateQueueName.privateQueueName(senderId,receiverId);

        Connection connection = RabbitmqConnection.getConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        channel.basicConsume(QUEUE_NAME, true, (consumerTag, delivery) -> {
            String messageContent = new String(delivery.getBody(), "UTF-8");
            messageHandler.handleMessage(messageContent);
            try {
                // Désérialiser le contenu du message en un objet Message
                ObjectMapper mapper = new ObjectMapper();
                Message message = mapper.readValue(messageContent, Message.class);
                log.info("Contenu du mapper : {}" , mapper);
                log.info("Contenu du messqge deserialiser : {}" , message);

                // Enregistrer le message en base de données
                //messageRepository.save(message);

                log.info("Message enregistré en base de données avec succès.");
            } catch (Exception e) {
                log.error("Erreur lors de la sauvegarde du message en base de données : {}", e.getMessage());
            }
        }, consumerTag -> {
            // handle cancellation
            channel.basicCancel(QUEUE_NAME);
            connection.close();
        });
    }

    //public static void receiveGroupMessages(String queueName) throws Exception {
      //  try (Connection connection = RabbitmqConnection.getConnection();
          //   Channel channel = connection.createChannel()) {
          //  channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
          //  String queue = channel.queueDeclare(queueName, false, false, false, null).getQueue();
           // channel.queueBind(queue, EXCHANGE_NAME, "");
           // channel.basicConsume(queue, true, callback, consumerTag -> {});
        //}
    //}
}
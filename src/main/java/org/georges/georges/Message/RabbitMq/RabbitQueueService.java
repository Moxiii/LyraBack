package org.georges.georges.Message.RabbitMq;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitQueueService {

private final RabbitAdmin rabbitAdmin;
private final RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

private final ApplicationContext context ;
public RabbitQueueService(RabbitAdmin rabbitAdmin , RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry , ApplicationContext context){
    this.rabbitAdmin = rabbitAdmin;
    this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
    this.context = context;
}




    public void addNewQueue(String queueName, String exchangeName, String routingKey) {
        Queue queue = new Queue(queueName , true , false,false);
        Binding binding = new Binding(
                queueName,
                Binding.DestinationType.QUEUE,
                exchangeName,
                routingKey,
                null
        );
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);
        this.addQueueToListener(exchangeName,queueName);
        log.info("Queue added succesfully");
    }


    public void addQueueToListener(String listenerId, String queueName) {
        log.info("adding queue : " + queueName + " to listener with id : " + listenerId);
        log.info("Queue exist on  {} : {}" , listenerId ,checkQueueExistOnListener(listenerId,queueName) );
        log.warn("Checking contenerId {}," ,getMessageListenerContainerById(listenerId));
        RabbitListenerEndpointRegistry listenerRegistery = context.getBean(RabbitListenerEndpointRegistry.class);
        if (!checkQueueExistOnListener(listenerId,queueName)) {

            this.getMessageListenerContainerById(listenerId).addQueueNames(queueName);
            log.info("queue push to listener ");
        } else {
            log.info("given queue name : " + queueName + " not exist on given listener id : " + listenerId);
        }
    }


    public void removeQueueFromListener(String listenerId, String queueName) {
        log.info("removing queue : " + queueName + " from listener : " + listenerId);
        if (checkQueueExistOnListener(listenerId,queueName)) {
            this.getMessageListenerContainerById(listenerId).removeQueueNames(queueName);
            log.info("deleting queue from rabbit management");
            this.rabbitAdmin.deleteQueue(queueName);
        } else {
            log.info("given queue name : " + queueName + " not exist on given listener id : " + listenerId);
        }
    }


    public Boolean checkQueueExistOnListener(String listenerId, String queueName) {
        try {
            log.info("checking queueName : " + queueName + " exist on listener id : " + listenerId);
            log.info("getting queueNames");
            String[] queueNames = this.getMessageListenerContainerById(listenerId).getQueueNames();
            log.info("queueNames : " + new Gson().toJson(queueNames));
            if (queueNames != null) {
                log.info("checking " + queueName + " exist on active queues");
                for (String name : queueNames) {
                    log.info("name : " + name + " with checking name : " + queueName);
                    if (name.equals(queueName)) {
                        log.info("queue name exist on listener, returning true");
                        return Boolean.TRUE;
                    }
                }
                return Boolean.FALSE;
            } else {
                log.info("there is no queue exist on listener");
                return Boolean.FALSE;
            }
        } catch (Exception e) {
            log.error("Error on checking queue exist on listener");
            log.error("error message : " + e.getMessage());
            log.error("trace : " + e.getStackTrace());
            return Boolean.FALSE;
        }
    }

    public AbstractMessageListenerContainer getMessageListenerContainerById(String listenerId) {
        log.info("getting message listener container by id : " + listenerId);
        log.info("Value of listener :{}" , this.rabbitListenerEndpointRegistry.getListenerContainer(listenerId));
        return ((AbstractMessageListenerContainer) this.rabbitListenerEndpointRegistry.getListenerContainer(listenerId));
    }
}
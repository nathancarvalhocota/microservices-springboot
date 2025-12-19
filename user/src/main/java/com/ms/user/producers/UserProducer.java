package com.ms.user.producers;

import com.ms.user.models.UserModel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserProducer
{
    final RabbitTemplate rabbitTemplate;

    public UserProducer(RabbitTemplate rabbitTemplate)
    {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value(value = "${broker.queue.email.name}")
        private String routingKey;

    public void publishMessageEmail(UserModel userModel)
    {
        Message message = createMessage(userModel);
        rabbitTemplate.send("", routingKey, message);
        //System.out.println(message);
    }

    private Message createMessage(UserModel userModel) {

        String textMessage = String.format("%s;%s;%s;%s",
                userModel.getUserId(),
                userModel.getEmail(),
                "Cadastro realizado com sucesso",
                userModel.getName());

        byte[] messageBody = textMessage.getBytes();

        MessageProperties props = new MessageProperties();
        props.setContentType("text/plain");

        return new Message(messageBody, props);

    }

}

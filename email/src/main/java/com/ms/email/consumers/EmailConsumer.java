package com.ms.email.consumers;

import com.ms.email.dtos.EmailRecordDTO;
import com.ms.email.models.EmailModel;
import com.ms.email.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EmailConsumer
{
    final EmailService emailService;

    public EmailConsumer(EmailService emailService)
    {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void ListenEmailQueue(@Payload String message)
    {
        EmailRecordDTO emailRecordDTO = parseEmailRecord(message);
        var emailModel = new EmailModel();
        BeanUtils.copyProperties(emailRecordDTO, emailModel);
        emailService.sendEmail(emailModel);

        //System.out.println((message));

    }

    private static EmailRecordDTO parseEmailRecord(String line) {

        String[] parts = line.split(";");

        if (parts.length != 4)
            throw new IllegalArgumentException("Linha inválida: deve ter 4 campos separados por ';'");

        UUID userId = UUID.fromString(parts[0]);
        String emailTo = parts[1];
        String subject = parts[2];
        String text = parts[3];

        return new EmailRecordDTO(userId, emailTo, subject, text);
    }
}

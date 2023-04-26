package com.example.moneyapp;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/engine")
public class ApiController {
    @RabbitListener(autoStartup = "true", concurrency = "1-5", bindings = @QueueBinding(
            value = @Queue(name = "BATCH_SCORING_QUEUE1", durable = "true", autoDelete = "false", exclusive = "false"),
            exchange = @Exchange(name = "BATCH_SCORING_QUEUE1",type = "topic"), key = "BATCH_SCORING_QUEUE1"))
    public void scoringInitiation(Message message) {
        String body = new String(message.getBody());
        System.out.println("Received . {}"+ body);
    }

}

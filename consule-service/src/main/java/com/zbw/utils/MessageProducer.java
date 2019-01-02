package com.zbw.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * rabbitmq sender
 */
@Component
public class MessageProducer {

//	private static final MwLogger logger = new MwLogger(MessageProducer.class);

    @Autowired
	private RabbitTemplate rabbitTemplate;

	public void send(String exchange, String routingKey, Object object) {
		try {
			this.rabbitTemplate.convertAndSend(exchange, routingKey, object);
//			logger.info("[MessageProducer] exchange : " + exchange +
//					", routingKey : " + routingKey + ", queue : " + object.toString());
		} catch (Throwable e) {
//			logger.error("rabbitmq send error", e);
		}
	}




}

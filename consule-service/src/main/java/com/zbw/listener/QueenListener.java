package com.zbw.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "queen.zbw.demo")
public class QueenListener {

	@RabbitHandler
	public void onMessage(String text) {
		System.out.printf("接受消息："+text);
	}

}

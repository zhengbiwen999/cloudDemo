package com.zbw.config;

import com.zbw.constants.QueenConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMqConfig {

	@Bean
	public TopicExchange sendExchange() {
		return new TopicExchange(QueenConstants.EXCHANGE_ZBW_QUEEN, true, false);
	}

	@Bean
	public Queue queue() {
		Queue queue = new Queue(QueenConstants.QUEEN_ZBW_DEMO, true);
		return queue;
	}

	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queue()).to(sendExchange()).with(QueenConstants.QUEEN_ZBW_DEMO_KEY);
	}

	/**
	 * 美小店订单自动取消，消息发送到delaySendExchange
	 */
//	@Bean
//	public TopicExchange delaySendExchange() {
//		return new TopicExchange(SysConstant.EXCHANGE_NETWORK_MXD_ORDER_CANCEL_DELAYED_SEND, true, false);
//	}
//
//	@Bean
//	public Queue delaySendQueue() {
//		Map<String, Object> args = new HashMap<>();
//		args.put("x-dead-letter-exchange", SysConstant.EXCHANGE_NETWORK_MXD_ORDER_CANCEL_DELAYED_LISTENER);
//		args.put("x-dead-letter-routing-key", SysConstant.KEY_NETWORK_MXD_ORDER_CANCEL_DELAYED_LISTENER);
//		args.put("x-message-ttl", 3 * 60 * 1000);//3分钟
//		return new Queue(SysConstant.QUEUE_NETWORK_MXD_ORDER_CANCEL_DELAYED_SEND, true, false, false, args);
//	}
//
//	@Bean
//	public Binding delaySendBinding() {
//		return BindingBuilder.bind(delaySendQueue()).to(delaySendExchange()).with(SysConstant.KEY_NETWORK_MXD_ORDER_CANCEL_DELAYED_SEND);
//	}
//
//
//	/**
//	 * 美小店订单自动取消，消息从delayListenerExchange监听
//	 */
//	@Bean
//	public TopicExchange delayListenerExchange() {
//		return new TopicExchange(SysConstant.EXCHANGE_NETWORK_MXD_ORDER_CANCEL_DELAYED_LISTENER, true, false);
//	}
//
//	@Bean
//	public Queue delayListenerQueue() {
//		return new Queue(SysConstant.QUEUE_NETWORK_MXD_ORDER_CANCEL_DELAYED_LISTENER, true, false, false);
//	}
//
//	@Bean
//	public Binding delayListenerBinding() {
//		return BindingBuilder.bind(delayListenerQueue()).to(delayListenerExchange()).with(SysConstant.KEY_NETWORK_MXD_ORDER_CANCEL_DELAYED_LISTENER);
//	}
//
	
	
}

package com.example;

import com.example.listeners.impl.ListenerCountry;
import com.example.listeners.impl.ListenerCity;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class RabbitmqConsumerApplication {
	@Value("${queue.country.name}")
	private String qCountry;

	@Value("${queue.city.name}")
	private String qCity;

	@Value("${spring.rabbitmq.host}")
	private String brokerUrl;

	@Value("${topic.exchange.name}")
	private String topicName;

	@Value("${spring.rabbitmq.username}")
	private String user;

	@Value("${spring.rabbitmq.password}")
	private String pwd;


	private static final String LISTENER_METHOD = "receiveMessage";

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqConsumerApplication.class, args);
	}

	@Bean(name ="queueCountry")
	Queue queueCountry() {
		return new Queue(qCountry, true);
	}

	@Bean(name="exchangeCountry")
	TopicExchange exchangeCountry() {
		return new TopicExchange(topicName);
	}

	@Bean(name="bindingCountry")
	Binding bindingCustomer(Queue queueCountry, TopicExchange exchangeCountry) {
		return BindingBuilder.bind(queueCountry).to(exchangeCountry).with(qCountry);
	}

	@Bean(name="queueCity")
	Queue queueCity() {
		return new Queue(qCity, true);
	}

	@Bean(name="exchangeCity")
	TopicExchange exchangeCity() {
		return new TopicExchange(topicName);
	}

	@Bean(name="bindingCity")
	Binding bindingShop(Queue queueCity, TopicExchange exchangeCity) {
		return BindingBuilder.bind(queueCity).to(exchangeCity).with(qCity);
	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(brokerUrl);
		connectionFactory.setUsername(user);
		connectionFactory.setPassword(pwd);

		return connectionFactory;
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean(name="containerCountry")
	SimpleMessageListenerContainer containerCountry(ConnectionFactory connectionFactory,
													 MessageListenerAdapter listenerAdapterCountry) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setMessageConverter(jsonMessageConverter());
		container.setQueueNames(qCountry);
		container.setMessageListener(listenerAdapterCountry);
		return container;
	}

	@Bean(name="listenerAdapterCountry")
	public MessageListenerAdapter listenerAdapterCountry(ListenerCountry receiver) {
		MessageListenerAdapter msgAdapter = new MessageListenerAdapter(receiver);
		msgAdapter.setMessageConverter(jsonMessageConverter());
		msgAdapter.setDefaultListenerMethod(LISTENER_METHOD);

		return msgAdapter;
	}

	@Bean(name="containerCity")
	SimpleMessageListenerContainer containerCity(ConnectionFactory connectionFactory,
													 MessageListenerAdapter listenerAdapterCity) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setMessageConverter(jsonMessageConverter());
		container.setQueueNames(qCity);
		container.setMessageListener(listenerAdapterCity);
		return container;
	}

	@Bean(name="listenerAdapterCity")
	public MessageListenerAdapter listenerAdapterCity(ListenerCity receiver) {
		MessageListenerAdapter msgAdapter = new MessageListenerAdapter(receiver);
		msgAdapter.setMessageConverter(jsonMessageConverter());
		msgAdapter.setDefaultListenerMethod(LISTENER_METHOD);

		return msgAdapter;
	}
}

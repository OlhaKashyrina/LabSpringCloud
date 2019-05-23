package com.example.eurekaclient;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@RefreshScope
@Configuration
public class EurekaClientApplication {
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

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}

	@Bean(name ="queueCountry")
	public Queue queueCountry() {
		return new Queue(qCountry, true);
	}

	@Bean(name="exchangeCountry")
	public TopicExchange exchangeCountry() {
		return new TopicExchange(topicName);
	}

	@Bean(name="bindingCountry")
	public Binding bindingCustomer(Queue queueCountry, TopicExchange exchangeCountry) {
		return BindingBuilder.bind(queueCountry).to(exchangeCountry).with(qCountry);
	}

	@Bean(name="queueCity")
	public Queue queueCity() {
		return new Queue(qCity, true);
	}

	@Bean(name="exchangeCity")
	public TopicExchange exchangeCity() {
		return new TopicExchange(topicName);
	}

	@Bean(name="bindingCity")
	public Binding bindingShop(Queue queueCity, TopicExchange exchangeCity) {
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

	@Bean(name="rabbitTemplateCountry")
	@Primary
	public RabbitTemplate rabbitTemplateCountry() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setRoutingKey(qCountry);
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}

	@Bean(name="rabbitTemplateCity")
	public RabbitTemplate rabbitTemplateCity() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setRoutingKey(qCity);
		template.setMessageConverter(jsonMessageConverter());
		return template;
	}
}

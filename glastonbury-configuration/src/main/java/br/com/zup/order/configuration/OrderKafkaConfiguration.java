package br.com.zup.order.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.repositories.event.OrderCreatedEvent;
import br.com.zup.services.InventoryService;
import br.com.zup.services.OrderService;

@Configuration
public class OrderKafkaConfiguration {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrap;
	private ObjectMapper objectMapper;

	@Autowired
	private OrderService orderService;

	public OrderKafkaConfiguration(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrap,
			ObjectMapper objectMapper) {
		this.bootstrap = bootstrap;
		this.objectMapper = objectMapper;
	}

	@Bean
	public KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic message() {
		return new NewTopic("created-orders", 1, (short) 1);
	}

	@Bean
	public DefaultKafkaProducerFactory messageProducerFactory() {

		Map<String, Object> configProps = new HashMap<>();

		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, OrderCreatedEvent> messageKafkaTemplate() {
		return new KafkaTemplate<String, OrderCreatedEvent>(messageProducerFactory());
	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group-id");
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		return factory;
	}

	@KafkaListener(topics = "created-orders", groupId = "order-group-id")
	public void createdOrder(String message) throws IOException {
		System.out.println("Created Order");
		OrderCreatedEvent event = this.objectMapper.readValue(message, OrderCreatedEvent.class);
		System.out.println(event);
	}

	@KafkaListener(topics = "soldout-order", groupId = "order-group-id")
	public void soldoutOrder(String message) throws IOException {
		System.out.println("Sold-out Order");
		OrderCreatedEvent event = this.objectMapper.readValue(message, OrderCreatedEvent.class);
		orderService.soldoutOrder(event);
		System.out.println(event);
	}

	@KafkaListener(topics = "soldout-email-order", groupId = "order-group-id")
	public void soldoutEmailOrder(String message) throws IOException {
		System.out.println("Send an email to the customer, notifying them that the order has been canceled.");
		OrderCreatedEvent event = this.objectMapper.readValue(message, OrderCreatedEvent.class);
		System.out.println(event);
	}

}

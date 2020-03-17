package br.com.zup.inventory.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.inventory.event.OrderCreatedEvent;
import br.com.zup.inventory.service.InventoryService;

@Configuration
public class KafkaConfiguration {

	private String bootstrap;
	private ObjectMapper objectMapper;

	@Autowired
	private InventoryService inventoryService;

	public KafkaConfiguration(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrap,
			ObjectMapper objectMapper) {
		this.bootstrap = bootstrap;
		this.objectMapper = objectMapper;
	}

	@Bean
	public ConsumerFactory<String, String> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-group-id");
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

	@KafkaListener(topics = "created-orders", groupId = "inventory-group-id")
	public void listen(String message) throws IOException {
		System.out.println("Order Verify Inventory");
		System.out.println(message);
		OrderCreatedEvent event = this.objectMapper.readValue(message, OrderCreatedEvent.class);
		inventoryService.verifyInventory(event);
	}

}

package br.com.zup.order.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.zup.repositories.event.OrderCreatedEvent;
import br.com.zup.services.InventoryService;

@Configuration
public class InventoryKafkaConfiguration {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrap;
	private ObjectMapper objectMapper;

	@Autowired
	private InventoryService inventoryService;

	public InventoryKafkaConfiguration(@Value(value = "${spring.kafka.bootstrap-servers}") String bootstrap,
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

	@KafkaListener(topics = "verify-inventory", groupId = "inventory-group-id")
	public void verifyInventory(String message) throws IOException {
		System.out.println("Verify Inventory");
		OrderCreatedEvent event = this.objectMapper.readValue(message, OrderCreatedEvent.class);
		System.out.println(event);
		inventoryService.verifyInventory(event);
	}

}

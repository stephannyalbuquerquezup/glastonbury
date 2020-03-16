package br.com.zup.services;

import java.util.List;

import br.com.zup.repositories.event.OrderCreatedEvent;
import br.com.zup.repositories.request.CreateOrderRequest;
import br.com.zup.repositories.response.OrderResponse;;

public interface OrderService {

    String save(CreateOrderRequest request);

    List<OrderResponse> findAll();
    
    void soldoutOrder(OrderCreatedEvent event);

}

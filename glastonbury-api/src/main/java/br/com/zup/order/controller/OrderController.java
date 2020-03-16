package br.com.zup.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.zup.repositories.request.CreateOrderRequest;
import br.com.zup.repositories.response.OrderResponse;
import br.com.zup.services.OrderService;


@RestController
@RequestMapping("/orders")
public class OrderController {

	@Autowired
    private OrderService orderService;

//    @Autowired
//    public OrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public String create(@RequestBody CreateOrderRequest request) {
        return this.orderService.save(request);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderResponse> getOrders() {
        return this.orderService.findAll();
    }
}

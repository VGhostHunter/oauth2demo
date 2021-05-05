package com.demo.security.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private RestTemplate restTemplate = new RestTemplate();

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping
    public OrderInfo create(@RequestBody OrderInfo orderInfo, @RequestHeader String username) {
        logger.info("user username is : " + username);
//        PriceInfo priceInfo = restTemplate.getForObject("http://localhost:9060/price/" + orderInfo.getProductId(), PriceInfo.class);
//        logger.info("price is: {}", priceInfo.getPrice());

        return orderInfo;
    }

    @GetMapping("/{id}")
    public OrderInfo orderInfo(@PathVariable Long id, @RequestHeader String username) {
        logger.info("user is : " + username);
        logger.info("orderId is : " + id);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(id);
        orderInfo.setProductId(id * 5);
        return orderInfo;
    }
}

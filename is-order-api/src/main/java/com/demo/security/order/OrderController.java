package com.demo.security.order;

import com.demo.security.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private RestTemplate restTemplate = new RestTemplate();

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping
    public OrderInfo create(@RequestBody OrderInfo orderInfo, @AuthenticationPrincipal User user) {
        logger.info("user id is : " + user.getId());
        logger.info("user username is : " + user.getUsername());
//        PriceInfo priceInfo = restTemplate.getForObject("http://localhost:9060/price/" + orderInfo.getProductId(), PriceInfo.class);

//        logger.info("price is: {}", priceInfo.getPrice());

        return orderInfo;
    }

    @GetMapping("/{id}")
    public OrderInfo orderInfo(@PathVariable Long id) {
        logger.info("orderId is : " + id);
        return new OrderInfo();
    }
}
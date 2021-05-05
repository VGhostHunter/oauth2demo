package com.demo.security.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/orders")
public class OrderController {

    /**
     * 可以把token当前上下文拿出来 再放到请求头里发出去
     * 发出去的请求会带上token信息
     */
    @Autowired
    private OAuth2RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @PreAuthorize("#oauth2.hasScope('write')")  scope 一般是针对应用的授权
     * @param orderInfo
     * @param username
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OrderInfo create(@RequestBody OrderInfo orderInfo, @AuthenticationPrincipal String username) {
        logger.info("user username is : " + username);
        PriceInfo priceInfo = restTemplate.getForObject("http://localhost:9060/price/" + orderInfo.getProductId(), PriceInfo.class);
        logger.info("price is: {}", priceInfo.getPrice());

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

package com.demo.security.ispriceapi.price;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/price")
public class PriceController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/{id}")
    private PriceInfo priceInfo(@PathVariable Long id) {
        logger.info("productId is : " + id);

        PriceInfo priceInfo = new PriceInfo();

        priceInfo.setId(id);
        priceInfo.setPrice(new BigDecimal(100));

        return priceInfo;
    }
}

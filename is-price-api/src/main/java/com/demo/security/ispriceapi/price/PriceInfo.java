package com.demo.security.ispriceapi.price;

import java.math.BigDecimal;

public class PriceInfo {

    private Long id;

    private BigDecimal price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

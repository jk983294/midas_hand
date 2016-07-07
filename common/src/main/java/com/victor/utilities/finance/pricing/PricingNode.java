package com.victor.utilities.finance.pricing;

public class PricingNode {

    public double price, optionPrice;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOptionPrice() {
        return optionPrice;
    }

    public void setOptionPrice(double optionPrice) {
        this.optionPrice = optionPrice;
    }

    @Override
    public String toString() {
        return "PricingNode{" +
                "price=" + price +
                ", optionPrice=" + optionPrice +
                '}';
    }
}

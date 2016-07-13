package com.victor.utilities.finance.pricing;


import com.victor.utilities.visual.VisualAssist;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OptionPricingTest {

	private final static double epsilon = 1e-4;
	
	@Test
	public void test1(){
		OptionPricing optionPricing = new OptionPricing(50.0, 52.0, 0.05, 0.3, 2, 5);
		optionPricing.buyType = BuyType.Short;
        optionPricing.optionType = OptionType.American;
		optionPricing.pricingMethod = PricingMethod.BinaryTree;
		optionPricing.targetType = TargetType.Stock;
        optionPricing.calculate();
        assertEquals(7.6708887, optionPricing.nodes[0][0].optionPrice, epsilon);
        VisualAssist.print(optionPricing.getResultStringWithBinaryTree());
	}
	
	@Test
	public void test2(){
		OptionPricing optionPricing = new OptionPricing(50.0, 52.0, 0.05, 0.182321, 2, 2);
		optionPricing.buyType = BuyType.Short;
		optionPricing.optionType = OptionType.English;
		optionPricing.pricingMethod = PricingMethod.BinaryTree;
		optionPricing.calculate();
		//assertEquals(7.6708887, optionPricing.nodes[0][0].optionPrice, epsilon);
		VisualAssist.print(optionPricing.getResultStringWithBinaryTree());
	}

    @Test
    public void test3(){
        OptionPricing optionPricing = new OptionPricing(810.0, 800.0, 0.05, 0.2, 0.5, 2);
        optionPricing.buyType = BuyType.Long;
        optionPricing.optionType = OptionType.English;
        optionPricing.pricingMethod = PricingMethod.BinaryTree;
        optionPricing.targetType = TargetType.StockIndex;
        optionPricing.dividendRate = 0.02;
        optionPricing.calculate();
        assertEquals(53.394716374961305, optionPricing.nodes[0][0].optionPrice, epsilon);
        VisualAssist.print(optionPricing.getResultStringWithBinaryTree());
    }

    @Test
    public void test4(){
        OptionPricing optionPricing = new OptionPricing(0.61, 0.6, 0.05, 0.12, 0.25, 3);
        optionPricing.buyType = BuyType.Long;
        optionPricing.optionType = OptionType.American;
        optionPricing.pricingMethod = PricingMethod.BinaryTree;
        optionPricing.targetType = TargetType.Currency;
        optionPricing.foreignCurrencyInterestRate = 0.07;
        optionPricing.calculate();
        assertEquals(0.01888057792230972, optionPricing.nodes[0][0].optionPrice, epsilon);
        VisualAssist.print(optionPricing.getResultStringWithBinaryTree());
    }

    @Test
    public void test5(){
        OptionPricing optionPricing = new OptionPricing(31.0, 30.0, 0.05, 0.3, 0.75, 3);
        optionPricing.buyType = BuyType.Short;
        optionPricing.optionType = OptionType.American;
        optionPricing.pricingMethod = PricingMethod.BinaryTree;
        optionPricing.targetType = TargetType.Future;
        optionPricing.calculate();
        assertEquals(2.8356351571052603, optionPricing.nodes[0][0].optionPrice, epsilon);
        VisualAssist.print(optionPricing.getResultStringWithBinaryTree());
    }

    @Test
    public void test6(){
        OptionPricing optionPricing = new OptionPricing(42.0, 40.0, 0.1, 0.2, 0.5);
        optionPricing.optionType = OptionType.English;
        optionPricing.pricingMethod = PricingMethod.BlackScholes;
        optionPricing.targetType = TargetType.Stock;
        optionPricing.calculateOptionPriceWithBlackScholes();
        assertEquals(0.8085993729000887, optionPricing.p, epsilon);
        assertEquals(4.759422392871528, optionPricing.c, epsilon);
    }

    @Test
    public void test7(){
        OptionPricing optionPricing = new OptionPricing(930.0, 900.0, 0.08, 0.2, 2.0/12);
        optionPricing.optionType = OptionType.English;
        optionPricing.pricingMethod = PricingMethod.BlackScholes;
        optionPricing.targetType = TargetType.StockIndex;
        optionPricing.dividendRate = 0.03;
        optionPricing.calculateOptionPriceWithBlackScholes();
        assertEquals(14.550996773772397, optionPricing.p, epsilon);
        assertEquals(51.83295679649086, optionPricing.c, epsilon);
    }

    @Test
    public void test8(){
        OptionPricing optionPricing = new OptionPricing(1.6, 1.6, 0.08, 0.141, 4.0/12);
        optionPricing.optionType = OptionType.English;
        optionPricing.pricingMethod = PricingMethod.BlackScholes;
        optionPricing.targetType = TargetType.Currency;
        optionPricing.foreignCurrencyInterestRate = 0.11;
        optionPricing.calculateOptionPriceWithBlackScholes();
        assertEquals(0.058459066324003106, optionPricing.p, epsilon);
        assertEquals(0.042957730192595744, optionPricing.c, epsilon);
    }

}

package com.victor.utilities.finance.pricing;


import com.victor.utilities.finance.Annuity;
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
        optionPricing.calculate();
        assertEquals(7.6708887, optionPricing.nodes[0][0].optionPrice, epsilon);
        VisualAssist.print(optionPricing.getResultStringWithBinaryTree());
	}
	
	@Test
	public void test2(){

	}
}

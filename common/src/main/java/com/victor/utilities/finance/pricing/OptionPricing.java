package com.victor.utilities.finance.pricing;


import org.apache.commons.math3.distribution.NormalDistribution;

public class OptionPricing {

    public double s0;               // current Price
    public double f0;               // current future Price
    public double strike;           // exercise price
    public double r;                // risk free interest rate
    public double volatility;       // yearly
    public double u, d;             // up percentage / down percentage
    public double c, p;             // call / put price
    public double deltaC, deltaP;   // the quantity of asset to hedge one option contract
    public double gammaC, gammaP;   // ratio of delta against asset price
    public double thetaC, thetaP;   // ratio of asset value against time, time decay
    public double vegaC, vegaP;     // ratio of asset value against volatility
    public double rhoC, rhoP;       // ratio of asset value against interest rate
    public double t;                // time (year)
    public double dividendRate = 0d;// yearly
    public double foreignCurrencyInterestRate = 0d;
    public int steps;
    public double deltaT;           // step time
    public PricingNode[][] nodes;
    public OptionType optionType = OptionType.English;
    public TargetType targetType = TargetType.Stock;
    public BuyType buyType = BuyType.Long;
    public PricingMethod pricingMethod = PricingMethod.BlackScholes;

    public OptionPricing(double s0, double strike, double r, double volatility, double t, int steps) {
        this.s0 = s0;
        this.strike = strike;
        this.r = r;
        this.volatility = volatility;
        this.t = t;
        this.steps = steps;
        deltaT = t / steps;
    }

    public OptionPricing(double s0, double strike, double r, double volatility, double t) {
        this.s0 = s0;
        this.strike = strike;
        this.r = r;
        this.volatility = volatility;
        this.t = t;
        pricingMethod = PricingMethod.BlackScholes;
    }

    public void calculate(){
        if(pricingMethod == PricingMethod.BinaryTree){
            calculateTargetPriceWithBinaryTree();
            calculateOptionPriceWithBinaryTree();
        } else if(pricingMethod == PricingMethod.BlackScholes){
            calculateOptionPriceWithBlackScholes();
        }
    }

    NormalDistribution normal = new NormalDistribution(0, 1);
    public void calculateOptionPriceWithBlackScholes(){
        if(targetType == TargetType.Stock || targetType == TargetType.StockIndex){
            double d1 = (Math.log(s0 / strike) + (r - dividendRate + volatility * volatility / 2.0) * t) / (volatility * Math.sqrt(t));
            double d2 = (Math.log(s0 / strike) + (r - dividendRate - volatility * volatility / 2.0) * t) / (volatility * Math.sqrt(t));
            c = s0 * Math.exp(-dividendRate * t) * normal.cumulativeProbability(d1) - strike * Math.exp(-r * t) * normal.cumulativeProbability(d2);
            p = strike * Math.exp(-r * t) * normal.cumulativeProbability(-d2) - s0 * Math.exp(-dividendRate * t) * normal.cumulativeProbability(-d1);
            deltaC = Math.exp(-dividendRate * t) * normal.cumulativeProbability(d1);
            deltaP = Math.exp(-dividendRate * t) * (normal.cumulativeProbability(d1) - 1d);
            gammaC = Math.exp(-dividendRate * t) * normal.density(d1) / (s0 * volatility * Math.sqrt(t));
            gammaP = gammaC;
            thetaC = -s0 * normal.density(d1) * volatility * Math.exp(-dividendRate * t) / (2 * Math.sqrt(t))
                    + dividendRate * s0 * normal.cumulativeProbability(d1) * Math.exp(-dividendRate * t)
                    - r * strike * Math.exp(-r * t) * normal.cumulativeProbability(d2);
            thetaP = -s0 * normal.density(d1) * volatility * Math.exp(-dividendRate * t) / (2 * Math.sqrt(t))
                    - dividendRate * s0 * normal.cumulativeProbability(-d1) * Math.exp(-dividendRate * t)
                    + r * strike * Math.exp(-r * t) * normal.cumulativeProbability(-d2);
            vegaC = s0 * Math.sqrt(t) * normal.density(d1) * Math.exp(-dividendRate * t);
            vegaP = vegaC;
            rhoC = strike * t * Math.exp(-r * t) * normal.cumulativeProbability(d2);
            rhoP = -strike * t * Math.exp(-r * t) * normal.cumulativeProbability(-d2);
        } else if(targetType == TargetType.Currency){
            double d1 = (Math.log(s0 / strike) + (r - foreignCurrencyInterestRate + volatility * volatility / 2.0) * t) / (volatility * Math.sqrt(t));
            double d2 = (Math.log(s0 / strike) + (r - foreignCurrencyInterestRate - volatility * volatility / 2.0) * t) / (volatility * Math.sqrt(t));
            c = s0 * Math.exp(-foreignCurrencyInterestRate * t) * normal.cumulativeProbability(d1) - strike * Math.exp(-r * t) * normal.cumulativeProbability(d2);
            p = strike * Math.exp(-r * t) * normal.cumulativeProbability(-d2) - s0 * Math.exp(-foreignCurrencyInterestRate * t) * normal.cumulativeProbability(-d1);
            deltaC = Math.exp(-foreignCurrencyInterestRate * t) * normal.cumulativeProbability(d1);
            deltaP = Math.exp(-foreignCurrencyInterestRate * t) * (normal.cumulativeProbability(d1) - 1d);
            gammaC = Math.exp(-foreignCurrencyInterestRate * t) * normal.density(d1) / (s0 * volatility * Math.sqrt(t));
            gammaP = gammaC;
            thetaC = -s0 * normal.density(d1) * volatility * Math.exp(-foreignCurrencyInterestRate * t) / (2 * Math.sqrt(t))
                    + foreignCurrencyInterestRate * s0 * normal.cumulativeProbability(d1) * Math.exp(-foreignCurrencyInterestRate * t)
                    - r * strike * Math.exp(-r * t) * normal.cumulativeProbability(d2);
            thetaP = -s0 * normal.density(d1) * volatility * Math.exp(-foreignCurrencyInterestRate * t) / (2 * Math.sqrt(t))
                    - foreignCurrencyInterestRate * s0 * normal.cumulativeProbability(-d1) * Math.exp(-foreignCurrencyInterestRate * t)
                    + r * strike * Math.exp(-r * t) * normal.cumulativeProbability(-d2);
            vegaC = s0 * Math.sqrt(t) * normal.density(d1) * Math.exp(-foreignCurrencyInterestRate * t);
            vegaP = vegaC;
            rhoC = strike * t * Math.exp(-r * t) * normal.cumulativeProbability(d2);
            rhoP = -strike * t * Math.exp(-r * t) * normal.cumulativeProbability(-d2);
        } else if(targetType == TargetType.Future){
            double d1 = (Math.log(f0 / strike) + (volatility * volatility / 2.0) * t) / (volatility * Math.sqrt(t));
            double d2 = (Math.log(f0 / strike) + (- volatility * volatility / 2.0) * t) / (volatility * Math.sqrt(t));
            c = Math.exp(-r * t) * (f0 * normal.cumulativeProbability(d1) - strike * normal.cumulativeProbability(d2));
            p = Math.exp(-r * t) * (strike * normal.cumulativeProbability(-d2) - f0 * normal.cumulativeProbability(-d1));
            gammaC = normal.density(d1) / (f0 * volatility * Math.sqrt(t));
            gammaP = gammaC;
            thetaC = -f0 * normal.density(d1) * volatility / (2 * Math.sqrt(t))
                    - r * strike * Math.exp(-r * t) * normal.cumulativeProbability(d2);
            thetaP = -f0 * normal.density(d1) * volatility / (2 * Math.sqrt(t))
                    + r * strike * Math.exp(-r * t) * normal.cumulativeProbability(-d2);
            vegaC = f0 * Math.sqrt(t) * normal.density(d1);
            vegaP = vegaC;
            rhoC = strike * t * Math.exp(-r * t) * normal.cumulativeProbability(d2);
            rhoP = -strike * t * Math.exp(-r * t) * normal.cumulativeProbability(-d2);
        }
    }

    public double calculateImpliedVolatility(double optionPrice, boolean isCallPrice){
        double lowBound = 0d, upBound = 10d, optionPrice1, optionPrice2, optionPrice3;
        while (true){
            volatility = lowBound;
            calculateOptionPriceWithBlackScholes();
            optionPrice1 = isCallPrice ? c : p;
            volatility = upBound;
            calculateOptionPriceWithBlackScholes();
            optionPrice2 = isCallPrice ? c : p;
            volatility = (lowBound + upBound) / 2;
            calculateOptionPriceWithBlackScholes();
            optionPrice3 = isCallPrice ? c : p;
            if(Math.abs(optionPrice3 - optionPrice) < 1e-6){
                return volatility;
            } else if((optionPrice1 - optionPrice) * (optionPrice3 - optionPrice) < 0){
                upBound = volatility;
            } else {
                lowBound = volatility;
            }
        }
    }

    private void calculateOptionPriceWithBinaryTree(){
        // price last layer first, no matter it is English or American option, last layer is the same
        for (int j = 0; j <= steps; j++) {
            if(buyType == BuyType.Long){
                nodes[steps][j].optionPrice = Math.max(0d, nodes[steps][j].price - strike);
            } else if(buyType == BuyType.Short){
                nodes[steps][j].optionPrice = Math.max(0d, strike - nodes[steps][j].price);
            }
        }

        double a = 1;
        if(targetType == TargetType.Stock || targetType == TargetType.StockIndex){
            a = Math.exp((r - dividendRate) * deltaT);
        } else if(targetType == TargetType.Currency){
            a = Math.exp((r - foreignCurrencyInterestRate) * deltaT);
        } else if(targetType == TargetType.Future){
            a = 1;
        }
        double p = (a - d) / (u - d);
        double f = 0d;
        double discount = Math.exp(r * deltaT);
        for (int i = steps - 1; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                f = (p * nodes[i + 1][j].optionPrice + (1 - p) * nodes[i + 1][j + 1].optionPrice) / discount;
                if(optionType == OptionType.American){
                    // if American option, every step we can decide whether to exercise the option
                    if(buyType == BuyType.Long){
                        nodes[i][j].optionPrice = Math.max(f, nodes[i][j].price - strike);
                    } else if(buyType == BuyType.Short){
                        nodes[i][j].optionPrice = Math.max(f, strike - nodes[i][j].price);
                    }
                } else if(optionType == OptionType.English){
                    nodes[i][j].optionPrice = f;
                }
            }
        }
    }

    private void calculateTargetPriceWithBinaryTree(){
        nodes = new PricingNode[steps + 1][];
        for (int i = 0; i <= steps; i++) {
            nodes[i] = new PricingNode[i + 1];
            for (int j = 0; j < i + 1; j++) {
                nodes[i][j] = new PricingNode();
            }
        }
        u = Math.exp(volatility * Math.sqrt(deltaT));
        d = 1 / u;
        nodes[0][0].price = s0;
        for (int i = 1; i <= steps; i++) {
            for (int j = 0; j < i; j++) {
                nodes[i][j].price = nodes[i - 1][j].price * u;
            }
            nodes[i][i].price = nodes[i - 1][i - 1].price * d;
        }
    }

    public String getResultStringWithBinaryTree(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= steps; i++) {
            sb.append("\nstep ").append(i).append(" :\n");
            for (int j = 0; j < i + 1; j++) {
                sb.append("node ").append(j).append(" : ").append(nodes[i][j]).append("\n");
            }
        }
        return sb.toString();
    }
}

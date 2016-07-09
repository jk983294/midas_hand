package com.victor.utilities.finance.pricing;


public class OptionPricing {

    public double s0;               // current Price
    public double strike;           // exercise price
    public double r;                // risk free interest rate
    public double volatility;       // yearly
    public double u, d;             // up percentage / down percentage
    public double t;                // time (year)
    public double dividendRate = 0d;
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

    public void calculate(){
        if(pricingMethod == PricingMethod.BinaryTree){
            calculateTargetPriceWithBinaryTree();
            calculateOptionPriceWithBinaryTree();
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

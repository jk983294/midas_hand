package com.victor.midas.train;

import com.victor.midas.model.train.SingleParameterTrainResults;
import com.victor.midas.model.train.TrainType;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.Trainee;
import org.apache.log4j.Logger;

/**
 * used for train single parameter
 */
public class SingleParameterTrainer {

    private static final Logger logger = Logger.getLogger(SingleParameterTrainer.class);

    private Trainee trainee;
    private TrainType type;

    /** single parameter train related */
    public int singleIntStart, singleIntEnd, singleIntStep;
    public double singleDoubleStart, singleDoubleEnd, singleDoubleStep;

    private SingleParameterTrainResults results = new SingleParameterTrainResults();

    public SingleParameterTrainer(Trainee trainee, int start, int end, int step) {
        this.trainee = trainee;
        this.singleIntStart = start;
        this.singleIntEnd = end;
        this.singleIntStep = step;
        this.type = TrainType.SingleInt;
        results.setType(this.type);
    }

    public SingleParameterTrainer(Trainee trainee, double start, double end, double step) {
        this.trainee = trainee;
        this.singleDoubleStart = start;
        this.singleDoubleEnd = end;
        this.singleDoubleStep = step;
        this.type = TrainType.SingleDouble;
        results.setType(this.type);
    }

    public void process() throws Exception {
        CalcParameter parameter = new CalcParameter();
        if(type == TrainType.SingleInt){
            for (int i = singleIntStart; i <= singleIntEnd; i += singleIntStep) {
                parameter.singleInt = i;
                process(parameter);
                results.getLastResults().parameter = i;
            }
        } else if(type == TrainType.SingleDouble){
            for (double i = singleDoubleStart; i <= singleDoubleEnd; i += singleDoubleStep) {
                parameter.singleDouble = i;
                process(parameter);
                results.getLastResults().parameter = i;
            }
        }
    }

    private void process(CalcParameter parameter) throws Exception {
        trainee.apply(parameter);
        results.add(trainee.getPerformance());
    }



}

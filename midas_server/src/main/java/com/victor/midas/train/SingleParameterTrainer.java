package com.victor.midas.train;

import com.victor.midas.model.train.SingleParameterTrainResults;
import com.victor.midas.model.train.TrainType;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.train.common.Trainee;
import com.victor.midas.util.MidasConstants;
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

    private SingleParameterTrainResults results = new SingleParameterTrainResults(MidasConstants.MISC_SINGLE_TRAIN_RESULT);

    public SingleParameterTrainer(int start, int end, int step) {
        this.singleIntStart = start;
        this.singleIntEnd = end;
        this.singleIntStep = step;
        this.type = TrainType.SingleInt;
        results.setType(this.type);
    }

    public SingleParameterTrainer(double start, double end, double step) {
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
                process(parameter, i);
            }
        } else if(type == TrainType.SingleDouble){
            for (double i = singleDoubleStart; i <= singleDoubleEnd; i += singleDoubleStep) {
                parameter.singleDouble = i;
                process(parameter, i);
            }
        }
    }

    private void process(CalcParameter parameter, double param) throws Exception {
        logger.info("start training with parameter " + param);
        trainee.apply(parameter);
        results.add(trainee.getPerformance());
        results.getLastResults().parameter = param;
    }

    public SingleParameterTrainResults getResults() {
        return results;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }
}

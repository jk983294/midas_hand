package com.victor.midas.train.score;


/**
 * SingleParameterTrainer will set ScoreManager to training mode
 * In training mode, trainer will change parameter and then apply that parameter to ScoreManager's calculators,
 * then clear performance collector, set cob index to 0, then run score process once,
 * then record current run's performance from collector.
 * Before any run, ScoreManager will initialize calculators wrapper based on target calculator name, apply target calculator's training options,
 * then execute aggregation calculators for big data-set, initialize StockFilterUtil with market index & tradable stocks, initialize performance collector,
 * then do a training preparation.
 * For one particular run, for every cob, manager will iterator through all stocks to get scores,
 * if no signal option, then all stock will be chosen, it usually combined with select top option to narrow down to a few mount tradable stocks
 * if signal used option, then only signal > 1.0d will fire buy action, it usually combined with quit signal < -1d, and usually it will take all signaled stocks into account instead of top N.
 * One score object contains stock code & score & buy cob & sell cob. Sell timing is calculated via MidasTrainHelper.
 */
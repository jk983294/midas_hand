/**
 *
 */
midasApp.controller('indexController', function ($scope, $routeParams, MidasData, Utils, StockUtils) {
    /**
     * date pick for flot chart
     */
    $scope.datepicker = {
        opened1 : false,
        opened2 : false,
        format : MidasData.dateFormats[1],
        dateOptions : {
            startingDay: 1
        }
    };

    // Disable weekend selection
    $scope.disabled = Utils.disabledWeekend;

    $scope.open1 = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.datepicker.opened1 = true;
    };
    $scope.open2 = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.datepicker.opened2 = true;
    };

    /*** multi-selector */
    $scope.multipleChoice = {};
    $scope.multipleChoice.availableIndexes = ['end'];
    $scope.multipleChoice.indexes = ['end'];

    /**
     * flot chart
     */
    var stocks = [];

    //Grab stockCode off of the route
    var stockCode = ($routeParams.stockCode) ? $routeParams.stockCode : 'IDX999999';
    var stockData = MidasData.getStockDetail(stockCode);

    /**
     * when stock data is loaded, init both flot chart and date picker
     */
    stockData.$promise.then(function success(stockdetail){
        stocks = [stockdetail];
        init(stocks);
    }, Utils.errorHandler);

    function init(stocks){
        var minday = null;
        var maxday = null;
        for(var i = 0, len = stocks.length; i < len; ++i ){
            var stock = stocks[i];
            //console.log(stock);
            minday = minday ? Math.min(minday, stock.startDate) : stock.startDate;
            maxday = minday ? Math.max(maxday, stock.endDate) : stock.endDate;
        }
        $scope.datepicker.minDay = minday;
        $scope.datepicker.maxDay = maxday;
        $scope.datepicker.dt1 = minday;
        $scope.datepicker.dt2 = maxday;
        /*** multi-selector */
        var indexNames = StockUtils.getStockIndexNameSet(stocks[0]);
        $scope.multipleChoice.availableIndexes = StockUtils.getStockIndexCmpNameSet(stocks[0], indexNames);
        //$scope.multipleChoice.availableIndexes = StockUtils.getStockIndexNameSet(stocks[0]);

        /*** flot data */
        $scope.plotData = getPlotData();
    }

    /**
     * when date picker is changed, update the flot chart
     */
    $scope.updateDatePick = function(){
        if( Utils.isNull(stocks) || Utils.isNull($scope.multipleChoice.availableIndexes)) return;
        $scope.plotData = getPlotData();
    };

    function getPlotData(){
        return {
            stocks : stocks,
            startDay : $scope.datepicker.dt1,
            endDay : $scope.datepicker.dt2,
            showIndexes : $scope.multipleChoice.indexes
        };
    }
});
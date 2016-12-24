/**
 * flot chart angular js directive
 */
var flotang = angular.module('flotang', ['UtilService']);

flotang.directive('flotChart', function(chartService, Utils) {
    return {
        restrict: 'E',
        require: 'ngModel',
        scope: {
            ngModel: '=',
            chartType: '@',
            chartHeight: '@'
        },
        template: '<div></div>',
        replace: true,
        link: function(scope, elem, attrs) {
            elem.css('height', scope.chartHeight);
            scope.$watch('ngModel', function(data){
                var model = chartService.getModel(data, scope.chartType);
                if(Utils.isNull(model) || Utils.isNull(model.data)){}
                else{
                    $.plot(elem, model.data, model.options);
                    elem.show();
                }
            });
        }
    };
});

flotang.factory('chartService', ['Utils', 'StockUtils',
    function(Utils, StockUtils){
        var defaultData = [[[0, 0], [1, 0], [2, 0]]];
        var legendOpts = {
            backgroundOpacity: 0.5,
            noColumns: 0,
            backgroundColor: "#e5e4e2",
            position: "ne"
        };
        var tooltipOpts = {
            content: "'%s' of %x.1 is %y.4",
            dateFormat: "%y-%m-%d",
            shifts: {
                x: -60,
                y: 25
            }
        };
        var gridOpts = {
            labelMargin: 50,
            backgroundColor: '#f5f5f5',
            color: '#007fff',
            borderColor: null,
            hoverable: true
        };
        var xaxisOpts = {
            mode: "time",
            tickLength: 5
        };
        var stockChartOptions = {
            xaxis: xaxisOpts,
            grid: gridOpts,
            tooltip: true,
            tooltipOpts : tooltipOpts,
            legend : legendOpts
        };

        function getFlotOptions(yaxes){
            if(Utils.isNull(yaxes)) return stockChartOptions;
            return {
                xaxis: xaxisOpts,
                yaxes: yaxes,
                grid: gridOpts,
                tooltip: true,
                tooltipOpts : tooltipOpts,
                legend : legendOpts
            };
        }

        /**
         * for given chart type, generate plot data and its options
         */
        function getModel(data, chartType){
            if (chartType === 'stocks') {
                if( Utils.isNull(data) || Utils.isNull(data.stocks)){
                    return {
                        data : defaultData,
                        options : stockChartOptions
                    };
                } else {
                    return getStockPlotDatas(data.stocks, data.startDay, data.endDay, data.showIndexes);
                }
            } else if (chartType === 'TimeSeries') {    // show all time series, format is [{label : 't', data :[[x1, y1],[x2, y2]]}]
                if( Utils.isNull(data) || Utils.isNull(data.series)){
                    return {
                        data : defaultData,
                        options : stockChartOptions
                    };
                } else {
                    return getSeriesData(data.series);
                }
            }
        }

        function getSeriesData(series){
            // calculate how many y axis needed, based on y data range, same range could share y axis
            var sets = StockUtils.calcYaxisSets(series);
            if(Utils.isNull(sets) || (sets.length == 1)){
                return {
                    options : stockChartOptions,
                    data : series
                };
            } else {    // for more than one y axis, add corresponding y info
                var yaxes = addAxisInfo(series, sets);
                return {
                    options : getFlotOptions(yaxes),
                    data : series
                };
            }
        }

        function getStockPlotDatas(stocks, startDay, endDay, showIndexes){
            // combine each stock's index data into one array
            var data = [];
            for(var i = 0, len = stocks.length; i < len; ++i ){
                var stock = stocks[i];
                data = data.concat(StockUtils.getDataByTwoValidDate(stock, startDay, endDay, showIndexes));
            }

            // calculate how many y axis needed, based on y data range, same range could share y axis
            var sets = StockUtils.calcYaxisSets(data);
            if(Utils.isNull(sets) || (sets.length == 1)){
                return {
                    options : stockChartOptions,
                    data : data
                };
            } else {    // for more than one y axis, add corresponding y infos
                var yaxes = addAxisInfo(data, sets);
                return {
                    options : getFlotOptions(yaxes),
                    data : data
                };
            }
        }

        /**
         * generate infos for y axis
         */
        var axisPosition = ['left', 'right'];
        function addAxisInfo(data, sets) {
            var yaxes = [];
            for(var j = 0, len1 = sets.length; j < len1; j++ ){
                yaxes.push({position : axisPosition[j % 2]});
            }
            for(var i = 0, len = data.length; i < len; i++ ){
                var label = data[i].label;
                for(var j = 0, len1 = sets.length; j < len1; j++ ){
                    if(Utils.isInArray(sets[j].indexes, label)){
                        data[i].yaxis = j + 1;
                        break;
                    }
                }
            }
            return yaxes;
        }

        return {
            getModel : getModel
        };
    }
]);




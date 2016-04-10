/**
 *
 */

/*** stockInfoFactory will be auto injected by look up names*/
midasApp.controller('singleTrainController', function ($scope, $filter, MidasData, ngTableParams, Utils, $location) {
    $scope.singleTrainResult = {};
    $scope.results = [];
    $scope.singleTrainResult = MidasData.getSingleTrainResult();

    $scope.singleTrainResult.$promise.then(function success(data){
        $scope.results = data.results;
        initTable();
        initGraph();
    }, Utils.errorHandler);

    function initTable() {
        $scope.tableParams = new ngTableParams({
            page: 1,                // show first page
            count: 10,              // count per page
            sorting: {
                sharpeRatio : 'desc'     // initial sorting
            }
        },{
            total : $scope.results.length,
            getData : function($defer, params) {
                var orderedData = params.filter() ? $filter('filter')($scope.results, params.filter()) : $scope.results;
                orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;
                params.total(orderedData.length);
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });
    }

    function initGraph(){
        var parameter = Utils.extractProperty($scope.results, 'parameter');
        var dayPerformance = Utils.mergeForD3Point(parameter, Utils.extractProperty($scope.results, 'dayPerformance'));
        var kellyAnnualizedPerformance = Utils.mergeForD3Point(parameter, Utils.extractProperty($scope.results, 'kellyAnnualizedPerformance'));
        var kellyFraction = Utils.mergeForD3Point(parameter, Utils.extractProperty($scope.results, 'kellyFraction'));
        var stdDev = Utils.mergeForD3Point(parameter, Utils.extractProperty($scope.results, 'stdDev'));

        $scope.options1 = {
            chart: {
                type: 'multiChart',
                height: 350,
                margin : {
                    top: 30,
                    right: 60,
                    bottom: 50,
                    left: 70
                },
                color: d3.scale.category10().range(),
                useInteractiveGuideline: true,
                duration: 500,
                xAxis: {
                    axisLabel: 'parameter'
                },
                yAxis1: {
                    axisLabel: 'performance',
                    tickFormat: function(d){
                        return d3.format(',.4f')(d);
                    }
                },
                yAxis2: {
                    axisLabel: 'std dev',
                    tickFormat: function(d){
                        return d3.format(',.4f')(d);
                    }
                }
            },
            title: {
                enable: true,
                text: 'Performance Training Chart'
            //},
            //subtitle: {
            //    enable: true,
            //    text: 'This chart shows performance and its standard deviation.',
            //    css: {
            //        'text-align': 'center',
            //        'margin': '10px 13px 0px 7px'
            //    }
            //},
            //caption: {
            //    enable: true,
            //    html: '<b>Figure 1.</b> powered by Kun.',
            //    css: {
            //        'text-align': 'center',
            //        'margin': '10px 13px 0px 7px'
            //    }
            }
        };


        $scope.data1 = [{
                values: dayPerformance,      //values - represents the array of {x,y} data points
                key: 'Day Performance',
                color: '#ff7f0e',
                yAxis: 1,
                type: 'line'
            }, {
                values: stdDev,
                key: 'Day Performance StdDev',
                color: '#2ca02c',
                yAxis: 2,
                type: 'line'
            }
        ];

        $scope.options2 = {
            chart: {
                type: 'multiChart',
                height: 350,
                margin : {
                    top: 30,
                    right: 60,
                    bottom: 50,
                    left: 70
                },
                color: d3.scale.category10().range(),
                useInteractiveGuideline: true,
                duration: 500,
                xAxis: {
                    axisLabel: 'parameter'
                },
                yAxis1: {
                    axisLabel: 'Kelly Annualized Performance',
                    tickFormat: function(d){
                        return d3.format(',.4f')(d);
                    }
                },
                yAxis2: {
                    axisLabel: 'Kelly Fraction',
                    tickFormat: function(d){
                        return d3.format(',.4f')(d);
                    }
                }
            },
            title: {
                enable: true,
                text: 'Performance Training Chart'
            //},
            //subtitle: {
            //    enable: true,
            //    text: 'This chart shows kelly performance.',
            //    css: {
            //        'text-align': 'center',
            //        'margin': '10px 13px 0px 7px'
            //    }
            //},
            //caption: {
            //    enable: true,
            //    html: '<b>Figure 2.</b> powered by Kun.',
            //    css: {
            //        'text-align': 'center',
            //        'margin': '10px 13px 0px 7px'
            //    }
            }
        };


        $scope.data2 = [{
                values: kellyFraction,
                key: 'Kelly Fraction',
                color: '#c11ce1',
                yAxis: 1,
                type: 'line'
            },
            {
                values: kellyAnnualizedPerformance,
                key: 'Kelly Annualized Performance',
                color: '#7777ff',
                yAxis: 2,
                type: 'line'
            }
        ];
    }
});


midasApp.controller('aipController', function ($scope, $routeParams, $filter, MidasData, Utils, StockUtils,
                                               uiGridConstants, $location) {
    $scope.aipStockDetailGridOptions = {
        enableSorting: true,
        enableFiltering: true,
        enableRowSelection: true,
        enableGridMenu: true,
        columnDefs: [
            {
                field: 'performanceOrder', enableSorting: false, type: 'number', filters: [{
                condition: uiGridConstants.filter.GREATER_THAN,
                placeholder: 'greater than'
            }, {
                condition: uiGridConstants.filter.LESS_THAN,
                placeholder: 'less than'
            }]
            },
            {field: 'stockName', enableSorting: false},
            {
                field: 'startCob', enableSorting: false, type: 'number', filters: [{
                condition: uiGridConstants.filter.GREATER_THAN,
                placeholder: 'greater than'
            }, {
                condition: uiGridConstants.filter.LESS_THAN,
                placeholder: 'less than'
            }]
            },
            {
                field: 'endCob', enableSorting: false, type: 'number', filters: [{
                condition: uiGridConstants.filter.GREATER_THAN,
                placeholder: 'greater than'
            }, {
                condition: uiGridConstants.filter.LESS_THAN,
                placeholder: 'less than'
            }]
            },
            {
                field: 'monthCount', enableSorting: false, type: 'number', filters: [{
                condition: uiGridConstants.filter.GREATER_THAN,
                placeholder: 'greater than'
            }, {
                condition: uiGridConstants.filter.LESS_THAN,
                placeholder: 'less than'
            }]
            },
            {field: 'performanceTotal', enableSorting: false, type: 'number' },
            {
                field: 'performanceMonthly', type: 'number', sort: {
                direction: uiGridConstants.ASC,
                priority: 1
            }
            }
        ]
    };

    var aipData = MidasData.getAipData();
    aipData.$promise.then(function success(data) {
        console.log(data);
        $scope.aipStockDetailGridOptions.data = data.results;

        var month = Utils.extractProperty(data.statisticObjects, 'key');
        var perfMean = Utils.extractProperty(data.statisticObjects, 'mean');
        var perfStd = Utils.extractProperty(data.statisticObjects, 'std');

        $scope.aipPerformanceGraphData = [
            {
                values: Utils.mergeForD3Point(month, perfMean),
                key: 'performance',
                color: '#ff7f0e'
            }
        ];

    }, Utils.errorHandler);


    $scope.aipPerformanceGraphOptions = {
        chart: {
            type: 'lineChart',
            height: 350,
            margin: {
                top: 30,
                right: 60,
                bottom: 50,
                left: 70
            },
            color: d3.scale.category10().range(),
            x: function (d) {
                return d.x;
            },
            y: function (d) {
                return d.y;
            },
            useInteractiveGuideline: true,
            duration: 500,
            xAxis: {
                axisLabel: 'month'
            },
            yAxis: {
                axisLabel: 'performance',
                tickFormat: function (d) {
                    return d3.format(',.4f')(d);
                },
                axisLabelDistance: -10
            }
        },
        title: {
            enable: true,
            text: 'Average Performance along with Invested Month'
        }
    };

});
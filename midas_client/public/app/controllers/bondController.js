/**
 *
 */
midasApp.controller('bondController', function ($scope, $routeParams, $filter, MidasData, Utils, StockUtils,
                                                uiGridConstants, $location) {
    $scope.datepicker = {
        opened1 : false,
        opened2 : false,
        dt1 : 20150101,
        dt2 : new Date().getTime(),
        format : MidasData.dateFormats[1],
        dateOptions : {
            startingDay: 1
        }
    };
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
    $scope.multipleChoice = {
        available : [],
        selected: []
    };
    var stockData = MidasData.getStockDetail('IDX999999');
    stockData.$promise.then(function success(){
        $scope.updateDatePick();
    }, Utils.errorHandler);
    var bonds = [];

    $scope.bondRaw = MidasData.getNationalDebt();
    $scope.bondDetail = [];

    $scope.bondGridOptions = {
        enableSorting: true,
        enableFiltering: true,
        enableRowSelection: true,
        enableGridMenu: true,
        columnDefs: [
            {
                field: 'cob',
                sort: {
                    direction: uiGridConstants.DESC,
                    priority: 1
                },
                filters: [{
                        condition: uiGridConstants.filter.GREATER_THAN,
                        placeholder: 'greater than'
                    }, {
                        condition: uiGridConstants.filter.LESS_THAN,
                        placeholder: 'less than'
                    }
                ]
            },
            { field: 't1m', enableSorting: false },
            { field: 't2m', enableSorting: false, visible: false },
            { field: 't3m', enableSorting: false },
            { field: 't6m', enableSorting: false },
            { field: 't9m', enableSorting: false },
            { field: 't1y', enableSorting: false },
            { field: 't2y', enableSorting: false, visible: false },
            { field: 't3y', enableSorting: false, visible: false },
            { field: 't4y', enableSorting: false, visible: false },
            { field: 't5y', enableSorting: false },
            { field: 't6y', enableSorting: false, visible: false },
            { field: 't7y', enableSorting: false, visible: false },
            { field: 't8y', enableSorting: false, visible: false },
            { field: 't9y', enableSorting: false, visible: false },
            { field: 't10y', enableSorting: false },
            { field: 't15y', enableSorting: false },
            { field: 't20y', enableSorting: false, visible: false },
            { field: 't30y', enableSorting: false }
        ],
        appScopeProvider: {
            onDblClick : function(row) {
                $scope.bondDetailGridOptions.data = Utils.object2PropArray(row.entity);
                var result = $.grep($scope.bondRaw, function(e){ return e.cob == row.entity.cob; });
                if (result.length == 1) {
                    $scope.bondDetailGraphData = [
                        {
                            values: Utils.mergeForD3Point(result[0].term, result[0].yield),
                            key: 'yield',
                            color: '#ff7f0e'
                        }
                    ];
                }
            }
        },
        rowTemplate: "<div ng-dblclick=\"grid.appScope.onDblClick(row)\" " +
            "ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" " +
            "class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell ></div>",
        onRegisterApi: function( gridApi ) {
            $scope.grid1Api = gridApi;
        }
    };

    $scope.bondDetailGridOptions = {
        columnDefs: [
            { field: 'name', enableSorting: false },
            { field: 'value', enableSorting: false }
        ],
        onRegisterApi: function( gridApi ) {
            $scope.grid1Api = gridApi;
        }
    };

    $scope.bondDetailGraphOptions = {
        chart: {
            type: 'lineChart',
            height: 350,
            margin : {
                top: 30,
                right: 60,
                bottom: 50,
                left: 70
            },
            color: d3.scale.category10().range(),
            x: function(d){ return d.x; },
            y: function(d){ return d.y; },
            useInteractiveGuideline: true,
            duration: 500,
            xAxis: {
                axisLabel: 'term'
            },
            yAxis: {
                axisLabel: 'yield',
                tickFormat: function(d){
                    return d3.format(',.2f')(d);
                },
                axisLabelDistance: -10
            }
        },
        title: {
            enable: true,
            text: 'Term Structure of Interest Rate'
        }
    };

    /**
     * when results data is loaded, init three grid
     */
    $scope.bondRaw.$promise.then(function success(){
        var results = [];
        for(var i = 0, len = $scope.bondRaw.length; i < len; i++ ){
            var arrayData = $scope.bondRaw[i];
            var bond = { cob : arrayData.cob, time : Utils.toTime(arrayData.cob) };
            for(var j = 0, len1 = arrayData.termName.length; j < len1; j++ ){
                bond['t' + arrayData.termName[j]] = arrayData.yield[j];
            }
            results.push(bond);
        }
        bonds = results.slice();
        $scope.bondGridOptions.data = results.reverse();

        $scope.datepicker.dt1 = $scope.datepicker.minDay = Utils.toTime(Math.min(results[0].cob, results[results.length - 1].cob));
        $scope.datepicker.dt2 = $scope.datepicker.maxDay = Utils.toTime(Math.max(results[0].cob, results[results.length - 1].cob));
        console.log($scope.multipleChoice.available);
        for(var j = 0, len1 = $scope.bondRaw[0].termName.length; j < len1; j++ ){
            $scope.multipleChoice.available.push('t' +  $scope.bondRaw[0].termName[j]);
        }
        $scope.multipleChoice.selected = ['t1y'];
    }, Utils.errorHandler);

    /**
     * when date picker is changed, update the flot chart
     */
    $scope.updateDatePick = function(){
        if(Utils.isNull(bonds) || Utils.isNull($scope.multipleChoice.available)) return;
        $scope.plotData = getPlotData();
    }

    function getPlotData(){
        var series = [];
        for(var i = 0, len = $scope.multipleChoice.selected.length; i < len; i++ ){
            series.push(Utils.extractTimeSeries(bonds, 'time', $scope.multipleChoice.selected[i],
                'cob', Utils.date2int($scope.datepicker.dt1), Utils.date2int($scope.datepicker.dt2)));
        }
        console.log(stockData, Utils.date2int($scope.datepicker.dt1), Utils.date2int($scope.datepicker.dt2));
        if(stockData && stockData.$resolved){
            var x = StockUtils.getDataByTwoVaildDate(stockData, $scope.datepicker.dt1, $scope.datepicker.dt2, ['end']);
            series = series.concat(x);
            console.log(series, x);
        }
        return {
            series : series
        };
    }

});


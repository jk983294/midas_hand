/**
 *
 */

/*** stockInfoFactory will be auto injected by look up names*/
midasApp.controller('overviewController', function ($scope, $filter, MidasData, ngTableParams, Utils, $location) {
    $scope.stockInfos = [];
    $scope.dayStatsSample = [];
    $scope.dayStatsSelected = [];

    $scope.datepicker = {
        opened1 : false,
        dt1 : new Date(),
        minDay : new Date(1900, 8, 10),
        maxDay : new Date(2100, 8, 10),
        format : MidasData.dateFormats[1],
        dateOptions : {
            startingDay: 1
        }
    };

    $scope.open1 = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.datepicker.opened1 = true;
    };

    init();

    function init() {
        $scope.stockInfos = MidasData.getStockInfos();
        $scope.tableParams = new ngTableParams({
            page: 1,                // show first page
            count: 10,              // count per page
            sorting: {
                change : 'desc'     // initial sorting
            },
            filter: {
                name: ''            // initial filter
            }
        },{
            total : $scope.stockInfos.length,
            getData : function($defer, params) {
                var orderedData = params.filter() ? $filter('filter')($scope.stockInfos, params.filter()) : $scope.stockInfos;
                orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;
                params.total(orderedData.length);
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });



        //$scope.dayStatsSample = MidasData.getDayStats(-1);
    }

    $scope.dayStatsSample = MidasData.getDayStats(-1);
    $scope.dayStatsSample.$promise.then(function success(){
        $scope.dayStatsSampleTableParams.reload();
    }, Utils.errorHandler);

    $scope.updateDatePick = function(){
        updateDayStatsSelectedTableParams($scope.datepicker.dt1);
    };

    function updateDayStatsSelectedTableParams(date){
        $scope.dayStatsSelected = MidasData.getDayStats(Utils.date2int(date));
        $scope.dayStatsSelected.$promise.then(function success(){
            $scope.dayStatsSelectedTableParams.reload();
        }, Utils.errorHandler);

    }

    $scope.dayStatsSampleTableParams = new ngTableParams({
        page: 1,                // show first page
        count: 10,              // count per page
        sorting: {
            cob : 'desc'     // initial sorting
        }
    },{
        total : $scope.dayStatsSample.length,
        getData : function($defer, params) {
            var orderedData = params.filter() ? $filter('filter')($scope.dayStatsSample, params.filter()) : $scope.dayStatsSample;
            orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;
            params.total(orderedData.length);
            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

    $scope.dayStatsSelectedTableParams = new ngTableParams({
        page: 1,                // show first page
        count: 10,              // count per page
        sorting: {
            cob : 'desc'     // initial sorting
        }
    },{
        total : $scope.dayStatsSelected.length,
        getData : function($defer, params) {
            var orderedData = params.filter() ? $filter('filter')($scope.dayStatsSelected, params.filter()) : $scope.dayStatsSelected;
            orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;
            params.total(orderedData.length);
            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

    $scope.rowDoubleClick =  function(stockinfo){
        $location.path( "/Index/" + stockinfo.name );
    }
});


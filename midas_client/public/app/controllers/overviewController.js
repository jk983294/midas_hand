/**
 *
 */

/*** stockInfoFactory will be auto injected by look up names*/
midasApp.controller('overviewController', function ($scope, $filter, MidasData, ngTableParams, Utils, $location) {
    $scope.stockInfos = [];
    $scope.dayStatsSample = [];

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
    console.log($scope.dayStatsSample);
    $scope.dayStatsSample.$promise.then(function success(){
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
    }, Utils.errorHandler);

    $scope.rowDoubleClick =  function(stockinfo){
        $location.path( "/Index/" + stockinfo.name );
    }
});


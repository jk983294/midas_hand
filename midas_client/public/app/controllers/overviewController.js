/**
 *
 */

/*** stockInfoFactory will be auto injected by look up names*/
midasApp.controller('overviewController', function ($scope, $filter, MidasData, ngTableParams, $location) {
    $scope.stockInfos = [];

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
    }

    $scope.rowDoubleClick =  function(stockinfo){
        $location.path( "/Index/" + stockinfo.name );
    }
});


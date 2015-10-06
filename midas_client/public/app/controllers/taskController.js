/**
 *
 */

/*** taskController will be auto injected by look up names*/
midasApp.controller('taskController', function ($scope, $filter, MidasData, ngTableParams, $location) {
    $scope.tasks = [];

    init();

    function init() {
        $scope.tasks = MidasData.getTasks();

        $scope.tableParams = new ngTableParams({
            page: 1,                // show first page
            count: 10,               // count per page
            sorting: {
                submit : 'desc'     // initial sorting
            },
            filter: {
                desc : ''            // initial filter
            }
        },{
            total : $scope.tasks.length,
            getData : function($defer, params) {
                var orderedData = params.filter() ? $filter('filter')($scope.tasks, params.filter()) : $scope.tasks;
                orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;
                params.total("after orderBy", orderedData.length);
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });
    }

//    $scope.rowDoubleClick =  function(stockinfo){
//        $location.path( "/Index/" + stockinfo.name );
//    }
});


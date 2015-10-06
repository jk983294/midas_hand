/**
 *
 */
midasApp.controller('planController', function ($scope, $filter, $routeParams, MidasData, ngTableParams, Utils) {
    $scope.plans = MidasData.getPlans();


    $scope.plans.$promise.then(function success(){
        init();
    }, Utils.errorHandler);

    function init() {
        $scope.tableParams = new ngTableParams({
            page: 1,                // show first page
            count: 10,               // count per page
            sorting: {
                date : 'desc'     // initial sorting
            },
            filter: {
                desc : ''            // initial filter
            }
        },{
            total : $scope.plans.length,
            getData : function($defer, params) {
                var orderedData = params.sorting() ? $filter('orderBy')($scope.plans, params.orderBy()) : $scope.plans;
                params.total("after orderBy", orderedData.length);
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });
    }
});
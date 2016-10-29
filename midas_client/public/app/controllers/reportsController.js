/**
 *
 */
midasApp.controller('reportsController', function ($scope, $filter, $routeParams, MidasData, ngTableParams, Utils) {

    $scope.reportSearchText = '';
    
    $scope.reportSearchTextChange = function () {
        if($scope.reportSearchText.length >= 3){
            $scope.reports = MidasData.getReports($scope.reportSearchText);

            $scope.reports.$promise.then(function success(){
                var data = [];
                data = Utils.object2PropArray($scope.reports);
                for(var i = 0, len = data.length; i < len; i++ ) {
                    data[i].count = data[i].value.length;
                }

                if(data.length > 0){
                    $scope.tableParams = new ngTableParams({
                        page: 1,                    // show first page
                        count: 10,                  // count per page
                        sorting: {
                            cob : 'count'           // initial sorting
                        },
                        filter: {
                            desc : ''               // initial filter
                        }
                    },{
                        total : data.length,
                        getData : function($defer, params) {
                            var orderedData = params.sorting() ? $filter('orderBy')(data, params.orderBy()) : data;
                            params.total("after orderBy", orderedData.length);
                            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
                        }
                    });
                }
            }, Utils.errorHandler);
        }
    };

});
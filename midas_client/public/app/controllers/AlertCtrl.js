/**
 * show alert in bottom
 */
midasApp.controller('alertCtrl', function ($scope, $routeParams, MidasData, $interval) {
    $scope.alerts = MidasData.alerts;

    $scope.closeAlert = function(index) {
        $scope.alerts.splice(index, 1);
    };

    $interval(function(){
        if($scope.alerts.length > 0){
            $scope.alerts.splice(0, 1);
        }
    }, 8000);
});
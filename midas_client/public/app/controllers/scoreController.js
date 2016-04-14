/**
 *
 */
midasApp.controller('scoreController', function ($scope, $filter, $routeParams, MidasData, ngTableParams, Utils) {
    /**
     * date pick for flot chart
     */
    $scope.datepicker = {
        opened1 : false,
        opened2 : false,
        format : MidasData.dateFormats[1],
        dateOptions : {
            startingDay: 1
        }
    };

    // Disable weekend selection
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

    $scope.scores = MidasData.getScores();

    $scope.scores.$promise.then(function success(){
        initDefault();
    }, Utils.errorHandler);

    $scope.conceptScores = MidasData.getConceptScores();

    $scope.conceptScores.$promise.then(function success(){
        setConceptTableData();
    }, Utils.errorHandler);

    function initDefault() {
        setTableData();

        if($scope.scores.length > 0){
            var minday = Utils.toTime(20140101);
            var maxday = Utils.toTime(Math.max($scope.scores[0].cob, $scope.scores[$scope.scores.length - 1].cob));
            $scope.datepicker.minDay = minday;
            $scope.datepicker.maxDay = maxday;
            $scope.datepicker.dt1 = minday;
            $scope.datepicker.dt2 = maxday;
        }
    }

    /**
     * when date picker is changed, update the flot chart
     */
    $scope.updateDatePick = function(){
        var minday = Utils.date2int(Math.min($scope.datepicker.dt1, $scope.datepicker.dt2));
        var maxday = Utils.date2int(Math.max($scope.datepicker.dt1, $scope.datepicker.dt2));
        $scope.scores = MidasData.getScoresRange(minday, maxday);
        $scope.scores.$promise.then(function success(){
            setTableData();
        }, Utils.errorHandler);
        $scope.conceptScores = MidasData.getConceptScoresRange(minday, maxday);
        $scope.conceptScores.$promise.then(function success(){
            setConceptTableData();
        }, Utils.errorHandler);
    }

    function setTableData(){
        $scope.tableParams = new ngTableParams({
            page: 1,                // show first page
            count: 10,               // count per page
            sorting: {
                cob : 'desc'     // initial sorting
            },
            filter: {
                desc : ''            // initial filter
            }
        },{
            total : $scope.scores.length,
            getData : function($defer, params) {
                var orderedData = params.sorting() ? $filter('orderBy')($scope.scores, params.orderBy()) : $scope.scores;
                params.total("after orderBy", orderedData.length);
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });
    }

    function setConceptTableData(){
        $scope.conceptTableParams = new ngTableParams({
            page: 1,                // show first page
            count: 10,               // count per page
            sorting: {
                date : 'desc'     // initial sorting
            },
            filter: {
                desc : ''            // initial filter
            }
        },{
            total : $scope.conceptScores.length,
            getData : function($defer, params) {
                var orderedData = params.sorting() ? $filter('orderBy')($scope.conceptScores, params.orderBy()) : $scope.conceptScores;
                params.total("after orderBy", orderedData.length);
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });
    }
});
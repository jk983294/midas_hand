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

    $scope.collapseCtrl = {
        isSummaryCollapsed : false,
        isScoreRecordCollapsed : false
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

    var scoreData = MidasData.getScores();
    MidasData.getScores();

    scoreData.$promise.then(function success(data){
        initDefault(data);
    }, Utils.errorHandler);

    function initDefault(data) {
        setTableData(data);

        if($scope.scores.length > 0){
            var minday = Utils.toTime(Math.min($scope.scores[0].cob, $scope.scores[$scope.scores.length - 1].cob));
            var maxday = Utils.toTime(Math.max($scope.scores[0].cob, $scope.scores[$scope.scores.length - 1].cob));
            $scope.datepicker.minDay = minday;
            $scope.datepicker.maxDay = maxday;
            $scope.datepicker.dt1 = minday;
            $scope.datepicker.dt2 = maxday;
        }
    }

    /**
     * when date picker is changed, update grid
     */
    $scope.updateDatePick = function(){
        var minday = Utils.date2int(Math.min($scope.datepicker.dt1, $scope.datepicker.dt2));
        var maxday = Utils.date2int(Math.max($scope.datepicker.dt1, $scope.datepicker.dt2));
        var scoreData = MidasData.getScoresRange(minday, maxday);
        scoreData.$promise.then(function success(data){
            setTableData(data);
        }, Utils.errorHandler);
    };

    function setTableData(data){
        var scoreResult = data.scoreResult;
        var scoreRecordsTmp = [];
        data.stockScoreRecords.forEach(function (stockScoreRecord) {
            stockScoreRecord.records.forEach(function (record) {
                if(record.stockCode !== 'fake'){
                    scoreRecordsTmp.push(record);
                }
            });
        });

        $scope.scoreTime = scoreResult.time;
        $scope.scoreSummary = Utils.object2PropArray(scoreResult);
        $scope.scores = data.stockScoreRecords;
        $scope.scoreRecords = scoreRecordsTmp;

        $scope.tableParams = new ngTableParams({
            page: 1,                    // show first page
            count: 10,                  // count per page
            sorting: {
                cob : 'desc'
            },
            filter: {
                desc : ''
            }
        },{
            total : $scope.scores.length,
            getData : function($defer, params) {
                var orderedData = params.sorting() ? $filter('orderBy')($scope.scores, params.orderBy()) : $scope.scores;
                params.total("after orderBy", orderedData.length);
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });

        $scope.summaryTableParams = new ngTableParams({
            page: 1,                // show first page
            count: 50               // count per page
        }, {
            counts: [],             // hide page counts control
            total: 1,               // value less than count hide pagination
            getData: function($defer, params) {
                $defer.resolve($scope.scoreSummary.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });

        $scope.scoreRecordTableParams = new ngTableParams({
            page: 1,                    // show first page
            count: 10,                  // count per page
            sorting: {
                perf : 'asc'            // initial sorting
            },
            filter: {
            }
        },{
            total : $scope.scoreRecords.length,
            getData : function($defer, params) {
                var orderedData = params.filter() ? $filter('filter')($scope.scoreRecords, params.filter()) : $scope.scoreRecords;
                orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;
                params.total("after orderBy", orderedData.length);
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });
    }

});
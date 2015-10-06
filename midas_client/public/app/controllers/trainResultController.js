/**
 *
 */

/*** stockInfoFactory will be auto injected by look up names*/
midasApp.controller('trainResultController', function ($scope, $routeParams, $filter, MidasData, Utils, ngTableParams, $location) {
    $scope.trainResult = {};

    //Grab stockCode off of the route
    var trainId = ($routeParams.trainId) ? $routeParams.trainId : -1;
    $scope.trainResult = MidasData.getTrainResult(trainId);

    $scope.parameter = [];
    $scope.groupHistoryStatistics = [];
    $scope.historyData = [];
    $scope.historyStatistics = [];
    $scope.parameterTableParams = new ngTableParams({
        page: 1,   // show first page
        count: 50  // count per page
    }, {
        counts: [], // hide page counts control
        total: 1,  // value less than count hide pagination
        getData: function($defer, params) {
            $defer.resolve($scope.parameter.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });
    $scope.groupHistoryTableParams = new ngTableParams({
        page: 1,                // show first page
        count: 50,              // count per page
        sorting: {
            r : 'asc'     // initial sorting
        },
        filter: {
            stockName: ''            // initial filter
        }
    },{
        total : $scope.groupHistoryStatistics.length,
        getData : function($defer, params) {
            var orderedData = params.filter() ? $filter('filter')($scope.groupHistoryStatistics, params.filter()) : $scope.groupHistoryStatistics;
            orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;
            params.total(orderedData.length);
            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });
    $scope.historyTableParams = new ngTableParams({
        page: 1,                // show first page
        count: 50,              // count per page
        sorting: {
            r : 'asc'     // initial sorting
        },
        filter: {
            stockName: ''            // initial filter
        }
    },{
        total : $scope.historyData.length,
        getData : function($defer, params) {
            var orderedData = params.filter() ? $filter('filter')($scope.historyData, params.filter()) : $scope.historyData;
            initHistoryStatistics(orderedData);
            orderedData = params.sorting() ? $filter('orderBy')(orderedData, params.orderBy()) : orderedData;
            params.total(orderedData.length);
            $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });
    $scope.historyStatisticsTableParams = new ngTableParams({
        page: 1,   // show first page
        count: 50  // count per page
    }, {
        counts: [], // hide page counts control
        total: 1,  // value less than count hide pagination
        getData: function($defer, params) {
            $defer.resolve($scope.historyStatistics.slice((params.page() - 1) * params.count(), params.page() * params.count()));
        }
    });

    /**
     * when results data is loaded, init three grid
     */
    $scope.trainResult.$promise.then(function success(){
        $scope.subRunSelectData = initSubRunSelect($scope.trainResult.items.length);
        $scope.subRunSelected = $scope.subRunSelectData[0];

        initGrids($scope.subRunSelected.subRunId);

        $scope.$watch('subRunSelected', function() {
            console.log('sub Run changed to ', $scope.subRunSelected);
            initGrids($scope.subRunSelected.subRunId);
        });
    }, Utils.errorHandler);

    function initGrids(runIndex) {
        var item = $scope.trainResult.items[runIndex];
        $scope.groupHistoryStatistics = initGroupHistoryStatistics(item.history);
        $scope.parameter = Utils.object2PropArray(item.parameter);
        $scope.historyData = item.history;

        $scope.parameterTableParams.reload();
        $scope.groupHistoryTableParams.reload();
        $scope.historyTableParams.reload();
    }

    function initSubRunSelect(len){
        var rangeArray = Utils.rangeArray(len);
        var selectArray = Utils.arrayConcat(rangeArray, 'Sub Run', true);
        for(var i = 0; i < len; ++i){
            selectArray[i] = {
                subRunId : rangeArray[i],
                subRunDesp : selectArray[i]
            };
        }
        return selectArray;
    }

    $scope.status = {
        isopen: false
    };

    $scope.collapseCtrl = {
        isCollapsed1 : true,
        isCollapsed2 : true,
        isCollapsed3 : false
    };

    function initGroupHistoryStatistics(historyRecords){
        var groupStock = {};
        for(var i = 0, len = historyRecords.length; i < len; ++i){
            if(Utils.isNull(groupStock[historyRecords[i].stockName])){
                groupStock[historyRecords[i].stockName] = [];
            }
            groupStock[historyRecords[i].stockName].push(historyRecords[i]);
        }
        var groupArray = [];
        for (var stockname in groupStock) {
            var group = getStatistics(groupStock[stockname]);
            group['stockName'] = stockname;
            groupArray.push(group);
        }
        return groupArray;
    }

    function initHistoryStatistics(stockHistory){
        $scope.historyStatistics = Utils.object2PropArray(getStatistics(stockHistory));
        $scope.historyStatisticsTableParams.reload();
    }

    function getStatistics(historyRecords){
        var result = {
            recordCount : historyRecords.length,
            r : 0.0,
            rDev : 0.0,
            winProbability : 0.0
        };
        var winCount = 0;
        var rArray = [];
        for(var i = 0, len = historyRecords.length; i < len; ++i){
            var changePct = historyRecords[i].sellPrice / historyRecords[i].buyPrice - 1.0;
            winCount += (changePct > 0.0 ? 1 : 0);
            rArray.push(changePct);
            result.r += changePct;
        }
        result.r = Utils.average(rArray);
        result.rDev = Utils.standardDeviation(rArray);
        result.winProbability = winCount / result.recordCount;
        return result;
    }

    $scope.toggled = function(open) {
        $log.log('Dropdown is now: ', open);
    };

    $scope.toggleDropdown = function($event) {
        $event.preventDefault();
        $event.stopPropagation();
        $scope.status.isopen = !$scope.status.isopen;
    };

    $scope.rowDoubleClick =  function(stockinfo){
        $location.path( "/Index/" + stockinfo.name );
    }
});


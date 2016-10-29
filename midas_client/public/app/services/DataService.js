/**
 * for stock info retrieve
 */

var dataService = angular.module('DataService', ['ngResource']);

var serviceUrls = (function(){
    var isRemote = true;
    var urlBase = 'http://localhost:8080/';
    var stockInfosUrl = isRemote ? urlBase + 'stocks/stockinfos' : 'app/data/stockInfo.json';
    var stockDetailUrl = isRemote ? urlBase + 'stocks/:stockCode' : 'app/data/:stockCode.json';
    var multiStockDetailUrl = isRemote ? urlBase + 'stocks/multiply/:stockCodes' : 'app/data/:stockCodes.json';
    var stockCmpUrl = isRemote ? urlBase + '' : 'app/data/:stockCode.json';
    var typeAheadTipsUrl = isRemote ? urlBase + 'typeahead/:queryStr' : 'app/data/Tips.json';
    var typeAheadActionUrl = isRemote ? urlBase + 'typeahead/action/:queryStr' : 'app/data/Action.json';
    var taskUrl = isRemote ? urlBase + 'task/alltasks' : 'app/data/task.json';
    var planUrl = isRemote ? urlBase + 'stocks/plan/focus' : 'app/data/plan.json';
    var trainResultUrl = isRemote ? urlBase + 'stocks/trainResult/:trainId' : 'app/data/trainResult:trainId.json';
    var scoreUrl = isRemote ? urlBase + 'stocks/score' : 'app/data/score.json';
    var nationalDebtUrl = isRemote ? urlBase + 'stocks/national-debt' : 'app/data/nationalDebt.json';
    var scoreRangeUrl = isRemote ? urlBase + 'stocks/score/:cobFrom/:cobTo' : 'app/data/score.json';
    var dayStatsRangeUrl = isRemote ? urlBase + 'stocks/day-stats/:cob' : 'app/data/dayStats.json';
    var conceptScoreUrl = isRemote ? urlBase + 'stocks/conceptScore' : 'app/data/conceptScore.json';
    var conceptScoreRangeUrl = isRemote ? urlBase + 'stocks/conceptScore/:cobFrom/:cobTo' : 'app/data/conceptScore.json';
    var singleTrainResultUrl = isRemote ? urlBase + 'stocks/singleTrainResult' : 'app/data/SingleTrainResults.json';
    var reportsUrl = isRemote ? urlBase + 'stocks/reports/:queryStr' : 'app/data/reports.json';
    return {
        stockInfosUrl : stockInfosUrl,
        stockDetailUrl : stockDetailUrl,
        multiStockDetailUrl : multiStockDetailUrl,
        stockCmpUrl : stockCmpUrl,
        typeAheadTipsUrl : typeAheadTipsUrl,
        typeAheadActionUrl : typeAheadActionUrl,
        taskUrl : taskUrl,
        trainResultUrl : trainResultUrl,
        scoreUrl : scoreUrl,
        nationalDebtUrl : nationalDebtUrl,
        scoreRangeUrl : scoreRangeUrl,
        conceptScoreUrl : conceptScoreUrl,
        conceptScoreRangeUrl : conceptScoreRangeUrl,
        planUrl : planUrl,
        singleTrainResultUrl : singleTrainResultUrl,
        dayStatsRangeUrl : dayStatsRangeUrl,
        reportsUrl : reportsUrl
    };
})();

dataService.factory('StockInfos', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.stockInfosUrl,
            { }, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('StockDetail', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.stockDetailUrl,
            { stockCode:'IDX999999' }, // Query parameters
            {'query': { method: 'GET'}}
        );
    }
]);

dataService.factory('TrainResult', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.trainResultUrl,
            { trainId:'-1' }, // Query parameters
            {'query': { method: 'GET'}}
        );
    }
]);

dataService.factory('StocksCmp', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.multiStockDetailUrl,
            {
                stockCodes:'IDX999999_SZ002320'
            }, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('TypeAheadTips', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.typeAheadTipsUrl,
            { queryStr:'IDX999999' }, // Query parameters
            {'query': { method: 'GET'}}
        );
    }
]);

dataService.factory('TypeAheadAction', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.typeAheadActionUrl,
            { queryStr:'IDX999999' }, // Query parameters
            {'query': { method: 'GET'}}
        );
    }
]);

dataService.factory('TaskQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.taskUrl,
            { }, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('PlanQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.planUrl,
            { }, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('ScoreQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.scoreUrl,
            {}, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('NationalDebtQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.nationalDebtUrl,
            {}, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('ScoreRangeQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.scoreRangeUrl,
            {
                cobFrom : 20150110,
                cobTo : 20150201
            }, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('ConceptScoreQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.conceptScoreUrl,
            { }, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('ConceptScoreRangeQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.conceptScoreRangeUrl,
            {
                cobFrom : 20150110,
                cobTo : 20150201
            }, // Query parameters
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('singleTrainResultQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.singleTrainResultUrl,
            {},
            {'query': { method: 'GET'}}
        );
    }
]);

dataService.factory('DayStatsRangeQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.dayStatsRangeUrl,
            {
                cob : -1
            },
            {'query': { method: 'GET' , isArray : true}}
        );
    }
]);

dataService.factory('ReportsQuery', ['$resource',
    function($resource){
        return $resource(
            serviceUrls.reportsUrl,
            { queryStr:'' },
            {'query': { method: 'GET'}}
        );
    }
]);

dataService.factory('MidasData', ['$resource','StockInfos', 'StockDetail', 'StocksCmp',
    'TypeAheadTips', 'TypeAheadAction', 'TaskQuery', 'TrainResult', 'PlanQuery',
    'ScoreQuery', 'NationalDebtQuery', 'ScoreRangeQuery', 'ConceptScoreQuery',
    'ConceptScoreRangeQuery', 'singleTrainResultQuery', 'DayStatsRangeQuery',
    'ReportsQuery',
    function($resource, StockInfos, StockDetail, StocksCmp,
             TypeAheadTips, TypeAheadAction, TaskQuery, TrainResult, PlanQuery,
             ScoreQuery, NationalDebtQuery, ScoreRangeQuery, ConceptScoreQuery,
             ConceptScoreRangeQuery, singleTrainResultQuery, DayStatsRangeQuery,
             ReportsQuery){
        var stockInfos = {};

        // get future object
        stockInfos = StockInfos.query();

        // return future object
        function getStockInfos(){
            return stockInfos;
        }
        function getStockDetail(newStockCode){
            return StockDetail.get({stockCode: newStockCode});
        }

        function getTrainResult(newTrainId){
            return TrainResult.get({trainId: newTrainId});
        }

        function getStocksCmp(stockCodes){
            return StocksCmp.query({
                stockCodes : stockCodes
            });
        }

        function getTips(queryStr){
            return TypeAheadTips.get({queryStr : queryStr});
        }

        function getAction(queryStr){
            return TypeAheadAction.get({queryStr : queryStr});
        }

        function getTasks(){
            return TaskQuery.query();
        }

        function getPlans(){
            return PlanQuery.query();
        }

        function getScores(){
            return ScoreQuery.query();
        }

        function getDayStats(cob){
            return DayStatsRangeQuery.query({
                cob : cob
            });
        }

        function getNationalDebt(){
            return NationalDebtQuery.query();
        }

        function getScoresRange(cobFrom, cobTo){
            return ScoreRangeQuery.query({
                cobFrom : cobFrom,
                cobTo : cobTo
            });
        }

        function getConceptScores(){
            return ConceptScoreQuery.query();
        }

        function getConceptScoresRange(cobFrom, cobTo){
            return ConceptScoreRangeQuery.query({
                cobFrom : cobFrom,
                cobTo : cobTo
            });
        }

        function getSingleTrainResult(){
            return singleTrainResultQuery.query();
        }

        function getReports(queryStr){
            return ReportsQuery.get({queryStr : queryStr});
        }

        var dateFormats = ['dd-MMMM-yyyy', 'yyyy-MM-dd', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];

        var alerts = [];
        function addAlert(msg){
            alerts.push(msg);
        }

        return {
            getStockInfos : getStockInfos,
            getStockDetail : getStockDetail,
            getTrainResult : getTrainResult,
            getStocksCmp : getStocksCmp,
            getTips : getTips,
            getAction : getAction,
            getTasks : getTasks,
            getPlans : getPlans,
            getScores : getScores,
            getNationalDebt : getNationalDebt,
            getScoresRange : getScoresRange,
            getConceptScores : getConceptScores,
            getConceptScoresRange : getConceptScoresRange,
            getSingleTrainResult : getSingleTrainResult,
            getDayStats : getDayStats,
            getReports : getReports,

            dateFormats : dateFormats,

            alerts : alerts,
            addAlert : addAlert
        };
    }
]);

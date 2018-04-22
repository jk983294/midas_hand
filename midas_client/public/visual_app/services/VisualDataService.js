/**
 * for stock info retrieve
 */

var dataService = angular.module('DataService', ['ngResource']);

var serviceUrls = (function() {
    var isRemote = true;
    var urlBase = 'http://localhost:8080/visual/';
    var typeAheadTipsUrl = isRemote ? urlBase + 'typeahead/:queryStr' : 'visual_app/data/Tips.json';
    var tableUrl = isRemote ? urlBase + 'table/:queryStr' : 'visual_app/data/table.json';
    return {
        typeAheadTipsUrl: typeAheadTipsUrl,
        tableUrl: tableUrl
    };
})();

dataService.factory('TypeAheadTips', ['$resource',
    function($resource) {
        return $resource(
            serviceUrls.typeAheadTipsUrl, {
                queryStr: ''
            }, // Query parameters
            {
                'query': {
                    method: 'GET'
                }
            }
        );
    }
]);

dataService.factory('TableQuery', ['$resource',
    function($resource) {
        return $resource(
            serviceUrls.tableUrl, {
                queryStr: ''
            }, {
                'query': {
                    method: 'GET'
                }
            }
        );
    }
]);

dataService.factory('VisualData', ['$resource', 'TypeAheadTips', 'TableQuery',
    function($resource, TypeAheadTips, TableQuery) {
        function getTips(queryStr) {
            return TypeAheadTips.get({
                queryStr: queryStr
            });
        }

        function getTableData(queryStr) {
            return TableQuery.query({
                queryStr: queryStr
            });
        }

        var dateFormats = ['dd-MMMM-yyyy', 'yyyy-MM-dd', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];

        return {
            getTips: getTips,
            getTableData: getTableData,
            dateFormats: dateFormats
        };
    }
]);

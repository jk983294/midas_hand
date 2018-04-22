var urlBase = 'visual_app/partials/';
var visualApp = angular.module('visualApp', ['ngRoute', 'ngTable', 'flotang',
    'ui.bootstrap', 'ui.select', 'nvd3',
    'formatFilters', 'DataService', 'UtilService', 'ui.grid'
]);

visualApp.config(['$routeProvider',
    function($routeProvider) {
        $routeProvider.when('/Chart', {
                controller: 'chartController',
                templateUrl: urlBase + 'chartContent.html'
            })
            .when('/Table', {
                controller: 'tableController',
                templateUrl: urlBase + 'tableContent.html'
            })
            .otherwise({
                redirectTo: '/Table'
            });
    }
]);

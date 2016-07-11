/**
 *
 */
midasApp.controller('bondController', function ($scope, $routeParams, $filter, MidasData, Utils, uiGridConstants, $location) {
    $scope.bondRaw = MidasData.getNationalDebt();
    $scope.bondDetail = [{}];

    $scope.bondGridOptions = {
        enableSorting: true,
        enableFiltering: true,
        enableRowSelection: true,
        enableGridMenu: true,
        columnDefs: [
            {
                field: 'cob',
                sort: {
                    direction: uiGridConstants.DESC,
                    priority: 1
                },
                filters: [
                    {
                        condition: uiGridConstants.filter.GREATER_THAN,
                        placeholder: 'greater than'
                    },
                    {
                        condition: uiGridConstants.filter.LESS_THAN,
                        placeholder: 'less than'
                    }
                ]
            },
            { field: 't1m', enableSorting: false },
            { field: 't2m', enableSorting: false, visible: false },
            { field: 't3m', enableSorting: false },
            { field: 't6m', enableSorting: false },
            { field: 't9m', enableSorting: false },
            { field: 't1y', enableSorting: false },
            { field: 't2y', enableSorting: false, visible: false },
            { field: 't3y', enableSorting: false, visible: false },
            { field: 't4y', enableSorting: false, visible: false },
            { field: 't5y', enableSorting: false },
            { field: 't6y', enableSorting: false, visible: false },
            { field: 't7y', enableSorting: false, visible: false },
            { field: 't8y', enableSorting: false, visible: false },
            { field: 't9y', enableSorting: false, visible: false },
            { field: 't10y', enableSorting: false },
            { field: 't15y', enableSorting: false },
            { field: 't20y', enableSorting: false, visible: false },
            { field: 't30y', enableSorting: false }
        ],
        appScopeProvider: {
            onDblClick : function(row) {
                console.log(row);
                $scope.bondDetailGridOptions.data = Utils.object2PropArray(row.entity);

            }
        },
        rowTemplate: "<div ng-dblclick=\"grid.appScope.onDblClick(row)\" ng-repeat=\"(colRenderIndex, col) in colContainer.renderedColumns track by col.colDef.name\" class=\"ui-grid-cell\" ng-class=\"{ 'ui-grid-row-header-cell': col.isRowHeader }\" ui-grid-cell ></div>",
        onRegisterApi: function( gridApi ) {
            $scope.grid1Api = gridApi;
        }
    };

    $scope.bondDetailGridOptions = {
        columnDefs: [
            { field: 'name', enableSorting: false },
            { field: 'value', enableSorting: false }
        ],
        onRegisterApi: function( gridApi ) {
            $scope.grid1Api = gridApi;
        }
    };

    /**
     * when results data is loaded, init three grid
     */
    $scope.bondRaw.$promise.then(function success(){
        var results = [];
        for(var i = 0, len = $scope.bondRaw.length; i < len; i++ ){
            var arrayData = $scope.bondRaw[i];
            var bond = { cob : arrayData.cob };
            for(var j = 0, len1 = arrayData.termName.length; j < len1; j++ ){
                bond['t' + arrayData.termName[j]] = arrayData.yield[j];
            }
            results.push(bond);
        }
        $scope.bondGridOptions.data = results.reverse();
    }, Utils.errorHandler);

});


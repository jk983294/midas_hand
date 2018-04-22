visualApp.controller('tableController', function($scope, $routeParams, $filter, VisualData, Utils, StockUtils,
    uiGridConstants) {
    $scope.typeahead = {
        allFiles: [],
        queryStr: '',
        loadingTips: {}
    };

    $scope.getTips = function(queryStr) {
        return VisualData.getTips(queryStr.replace(/\//g, "|").replace(/\./g, "@")).$promise.then(function(response) {
            return response.tips.map(function(item) {
                return item;
            });
        });
    };

    $scope.inputKeyPress = function(event) {
        if (event.which === 13) {
            // console.log("enter pressed", $scope.typeahead.queryStr);
        }
    };

    $scope.getFileContent = function(queryStr) {
        var tableData = VisualData.getTableData(queryStr.replace(/\//g, "|").replace(/\./g, "@"));
        tableData.$promise.then(function success(data) {
            var columnDefs = [];
            for (var i = 0; i < data.columnSize; ++i) {
                columnDefs.push({
                    enableSorting: true,
                    type: 'number',
                    field: data.rowMeta[i],
                    filters: [{
                        condition: uiGridConstants.filter.GREATER_THAN,
                        placeholder: 'greater than'
                    }, {
                        condition: uiGridConstants.filter.LESS_THAN,
                        placeholder: 'less than'
                    }]
                });
            }

            var rowObjects = [];
            for (var i = 0, len = data.rows.length; i < len; ++i) {
                var obj = {};
                for (var j = 0; j < data.columnSize; ++j) {
                    var propStr = data.rowMeta[j];
                    obj[propStr] = data.rows[i][j];
                    if (Utils.isNumeric(obj[propStr])) {
                        obj[propStr] = parseFloat(obj[propStr]);
                    } else {
                        columnDefs[j].type = null;
                        columnDefs[j].filters = null;
                    }
                }
                rowObjects.push(obj);
            }

            $scope.tableDetailGridOptions = {
                enableSorting: true,
                enableFiltering: true,
                enableRowSelection: true,
                enableGridMenu: true,
                columnDefs: columnDefs
            };

            $scope.tableDetailGridOptions.data = rowObjects;
        }, Utils.errorHandler);
    };

    $scope.tableDetailGridOptions = {
        enableSorting: true,
        enableFiltering: true,
        enableRowSelection: true,
        enableGridMenu: true,
        columnDefs: []
    };
});

visualApp.controller('chartController', function($scope, $routeParams, VisualData, Utils, StockUtils) {
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

    /**
     * date pick for flot chart
     */
    $scope.datepicker = {
        opened1: false,
        opened2: false,
        format: VisualData.dateFormats[1],
        dateOptions: {
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

    /*** multi-selector */
    $scope.multipleChoice = {};
    $scope.multipleChoice.availableIndexes = [];
    $scope.multipleChoice.indexes = [];

    /**
     * flot chart
     */
    var stocks = [];

    $scope.getFileContent = function(queryStr) {
        var tableData = VisualData.getTableData(queryStr.replace(/\//g, "|").replace(/\./g, "@"));
        tableData.$promise.then(function success(data) {
            var columnDefs = [];
            for (var i = 0; i < data.columnSize; ++i) {
                columnDefs.push({
                    type: 'number',
                    field: data.rowMeta[i],
                    data: []
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
                    }
                    columnDefs[j].data.push(obj[propStr]);
                }
                rowObjects.push(obj);
            }

            var instrument = {
                'stockName': data.name,
                'desp': data.name,
                'start': 0,
                'end': 0,
                'startDate': 0,
                'endDate': 0,
                'datesDate': [],
                'datesInt': [],
                'indexInts': {},
                'indexDoubles': {},
                'indexName2indexCmp': {}
            };

            for (var i = 0; i < data.columnSize; ++i) {
                if (columnDefs[i].field === 'Date' || columnDefs[i].field === 'date' ||
                    columnDefs[i].field === 'Time' || columnDefs[i].field === 'time') {
                    instrument.datesDate = Utils.string2times(columnDefs[i].data);
                    instrument.datesInt = Utils.toDateInts(instrument.datesDate);

                    if (instrument.datesInt.length > 0) {
                        instrument.start = instrument.datesInt[0];
                        instrument.end = instrument.datesInt[instrument.datesInt.length - 1];
                        instrument.startDate = instrument.datesDate[0];
                        instrument.endDate = instrument.datesDate[instrument.datesInt.length - 1];
                    }
                } else {
                    instrument.indexDoubles[columnDefs[i].field] = columnDefs[i].data;
                    instrument.indexName2indexCmp[columnDefs[i].field] = [columnDefs[i].field];
                }
            }

            if (instrument && instrument.indexDoubles['end']) {
                $scope.multipleChoice.indexes = ['end'];
            } else if (instrument && instrument.indexDoubles['close']) {
                $scope.multipleChoice.indexes = ['close'];
            }

            stocks = [instrument];
            init(stocks);
        }, Utils.errorHandler);
    };

    function init(stocks) {
        var minDay = null;
        var maxDay = null;
        for (var i = 0, len = stocks.length; i < len; ++i) {
            var stock = stocks[i];
            minDay = minDay ? Math.min(minDay, stock.startDate) : stock.startDate;
            maxDay = minDay ? Math.max(maxDay, stock.endDate) : stock.endDate;
        }
        $scope.datepicker.minDay = minDay;
        $scope.datepicker.maxDay = maxDay;
        $scope.datepicker.dt1 = minDay;
        $scope.datepicker.dt2 = maxDay;
        /*** multi-selector */
        var indexNames = StockUtils.getStockIndexNameSet(stocks[0]);
        $scope.multipleChoice.availableIndexes = StockUtils.getStockIndexCmpNameSet(stocks[0], indexNames);

        /*** flot data */
        $scope.plotData = getPlotData();
    }

    /**
     * when date picker is changed, update the flot chart
     */
    $scope.updateDatePick = function() {
        if (Utils.isNull(stocks) || Utils.isNull($scope.multipleChoice.availableIndexes)) return;
        $scope.plotData = getPlotData();
    };

    function getPlotData() {
        return {
            stocks: stocks,
            startDay: $scope.datepicker.dt1,
            endDay: $scope.datepicker.dt2,
            showIndexes: $scope.multipleChoice.indexes
        };
    }
});

visualApp.controller('lineController', function($scope, $routeParams, VisualData, Utils, StockUtils) {
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

    var myChart = echarts.init(document.getElementById('LineMain'));

    function getOption(data) {
        var titleText = (data.name ? data.name : 'instrument');
        var localOption = {
            title: {
                text: titleText,
                left: 0
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'line'
                }
            },
            legend: {
                data: [],
                selected: {}
            },
            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [{
                type: 'category',
                data: data.sequences,
                axisLine: {
                    onZero: false
                }
            }],
            yAxis: [],
            series: [],
            dataZoom: [{
                    show: true,
                    realtime: true,
                    start: 65,
                    end: 85
                },
                {
                    type: 'inside',
                    realtime: true,
                    start: 65,
                    end: 85
                }
            ]
        };

        var ignoreList = {
            'sequences': true
        };

        var count = 0,
            yIndex = 0;
        var sets = [];
        var isFindSet = false;
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                if (data[key] instanceof Array && !ignoreList[key]) {
                    localOption.legend.data.push(key);

                    if (data[key].length > 0 && Utils.isNumeric(data[key][0])) {
                        var sampleValue = data[key][0];
                        // check if exist set is suitable for this index
                        isFindSet = false;
                        for (var j = 0, len1 = sets.length; j < len1; ++j) {
                            if (Utils.deviateLevel(sampleValue, sets[j].average) < 0.3) {
                                sets[j].average = (sets[j].average + sampleValue) / 2;
                                yIndex = sets[j].setIndex;
                                isFindSet = true;
                                break;
                            }
                        }
                        // not find in any set, create a new set
                        if (!isFindSet) {
                            yIndex = count;
                            count += 1;
                            sets.push({
                                average: sampleValue,
                                setIndex: yIndex
                            });
                            localOption.yAxis.push({
                                gridIndex: 0,
                                scale: true
                            });
                        }

                        localOption.series.push({
                            name: key,
                            type: 'line',
                            xAxisIndex: 0,
                            yAxisIndex: yIndex,
                            data: data[key]
                        });
                    }
                }
            }
        }
        return localOption;
    }

    $scope.getFileContent = function(queryStr) {
        var tableData = VisualData.getTableData(queryStr.replace(/\//g, "|").replace(/\./g, "@"));
        tableData.$promise.then(function success(rawData) {
            var columnDefs = [];
            var rowMeta = {};
            for (var i = 0; i < rawData.columnSize; ++i) {
                columnDefs.push({
                    type: 'number',
                    field: rawData.rowMeta[i],
                    data: []
                });
                rowMeta[rawData.rowMeta[i]] = i;
            }

            var rowObjects = [];
            for (var i = 0, len = rawData.rows.length; i < len; ++i) {
                var obj = {};
                for (var j = 0; j < rawData.columnSize; ++j) {
                    var propStr = rawData.rowMeta[j];
                    obj[propStr] = rawData.rows[i][j];
                    if (Utils.isNumeric(obj[propStr])) {
                        obj[propStr] = parseFloat(obj[propStr]);
                    } else {
                        columnDefs[j].type = null;
                    }
                    columnDefs[j].data.push(obj[propStr]);
                    rawData.rows[i][j] = obj[propStr];
                }
                rowObjects.push(obj);
            }

            var data = StockUtils.row2column(rawData.rows, rowMeta, true);

            if (data.times && data.times instanceof Array && data.times.length > 0 &&
                !Utils.isNumeric(data.times[0])) {
                data.times = Utils.string2timeFloats(data.times);
            }

            data = Utils.extractNumericArrayOfObject(data);
            data.name = rawData.name;

            var option = getOption(data);
            myChart.setOption(option);
        }, Utils.errorHandler);
    };
});

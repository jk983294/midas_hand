visualApp.controller('candleController', function($scope, $routeParams, VisualData, Utils, StockUtils) {
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

    var myChart = echarts.init(document.getElementById('CandleMain'));

    function getOption(data) {
        var titleText = (data.instrumentName ? data.instrumentName : 'instrument');
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
                data: ['KLine', 'Close'],
                selected: {
                    'Close': false
                }
            },
            grid: [{
                left: '3%',
                right: '1%',
                height: '60%'
            }, {
                left: '3%',
                right: '1%',
                top: '72.5%',
                height: '9%'
            }, {
                left: '3%',
                right: '1%',
                top: '83%',
                height: '14%'
            }],
            xAxis: [{
                type: 'category',
                gridIndex: 0,
                data: data.times,
                boundaryGap: false,
                axisLine: {
                    onZero: false
                },
                splitLine: {
                    show: false
                },
                min: 'dataMin',
                max: 'dataMax'
            }, {
                type: 'category',
                gridIndex: 1,
                data: data.times,
                axisLabel: {
                    show: false
                }
            }, {
                type: 'category',
                gridIndex: 2,
                data: data.times,
                axisLabel: {
                    show: false
                }
            }],
            yAxis: [{
                gridIndex: 0,
                scale: true,
                splitArea: {
                    show: false
                }
            }],
            dataZoom: [{
                type: 'inside',
                xAxisIndex: [0, 0],
                start: 20,
                end: 100
            }, {
                show: true,
                xAxisIndex: [0, 1],
                type: 'slider',
                top: '97%',
                start: 20,
                end: 100
            }, {
                show: false,
                xAxisIndex: [0, 2],
                type: 'slider',
                start: 20,
                end: 100
            }],
            series: [{
                name: 'KLine',
                type: 'candlestick',
                data: data.oclh,
                itemStyle: {
                    normal: {
                        color: '#ef232a',
                        color0: '#14b143',
                        borderColor: '#ef232a',
                        borderColor0: '#14b143'
                    }
                }
            }, {
                name: 'Close',
                type: 'line',
                data: data.close,
                smooth: true,
                lineStyle: {
                    show: false
                }
            }]
        };

        var ignoreList = {
            'open': true,
            'close': true,
            'high': true,
            'low': true,
            'times': true,
            'oclh': true,
            'macd': true,
            'dif': true,
            'dea': true,
            'volume': true,
            'ma10': true,
            'ma60': true
        };

        var count = 0,
            yIndex = 0;
        var sets = [];
        var isFindSet = false;
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                if (data[key] instanceof Array && !ignoreList[key]) {
                    var sampleValue = data[key][0];
                    if (Utils.deviateLevel(data.close[0], sampleValue) > 0.3) {
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
                            count += 1;
                            yIndex = count;
                            sets.push({
                                average: sampleValue,
                                setIndex: yIndex
                            });
                            localOption.yAxis.push({
                                gridIndex: 0,
                                scale: true,
                                splitArea: {
                                    show: false
                                }
                            });
                        }
                    } else {
                        // same y asix with close index
                        yIndex = 0;
                    }

                    localOption.legend.data.push(key);
                    localOption.series.push({
                        name: key,
                        type: 'line',
                        xAxisIndex: 0,
                        yAxisIndex: yIndex,
                        data: data[key],
                        smooth: true
                    });
                }
            }
        }
        localOption.yAxis = localOption.yAxis.concat([{
            gridIndex: 1,
            splitNumber: 3,
            axisLine: {
                onZero: false
            },
            axisTick: {
                show: false
            },
            splitLine: {
                show: false
            },
            axisLabel: {
                show: true
            }
        }, {
            gridIndex: 2,
            splitNumber: 4,
            axisLine: {
                onZero: false
            },
            axisTick: {
                show: false
            },
            splitLine: {
                show: false
            },
            axisLabel: {
                show: true
            }
        }]);
        localOption.series = localOption.series.concat([{
            name: 'Volume',
            type: 'bar',
            xAxisIndex: 1,
            yAxisIndex: 1 + count,
            data: data.volume,
            itemStyle: {
                normal: {
                    color: function(params) {
                        if (data.close[params.dataIndex] > data.open[params.dataIndex]) {
                            return '#ef232a';
                        } else {
                            return '#14b143';
                        }
                    }
                }
            }
        }, {
            name: 'MACD',
            type: 'bar',
            xAxisIndex: 2,
            yAxisIndex: 2 + count,
            data: data.macd,
            itemStyle: {
                normal: {
                    color: function(params) {
                        var colorList;
                        if (params.data >= 0) {
                            colorList = '#ef232a';
                        } else {
                            colorList = '#14b143';
                        }
                        return colorList;
                    }
                }
            }
        }, {
            name: 'DIF',
            type: 'line',
            xAxisIndex: 2,
            yAxisIndex: 2 + count,
            data: data.dif
        }, {
            name: 'DEA',
            type: 'line',
            xAxisIndex: 2,
            yAxisIndex: 2 + count,
            data: data.dea
        }]);
        console.log('localOption', localOption);
        return localOption;
    }


    myChart.setOption(getOption({
        times: []
    }));

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

            var instrument = {
                'instrumentName': rawData.name,
                'rowMeta': rowMeta,
                'rows': rawData.rows
            };

            var data = StockUtils.formatData(instrument);

            if (data && data.times && data.times.length > 2) {
                $scope.datesDate = Utils.string2times(data.times);
                $scope.datesInt = Utils.toDateInts($scope.datesDate);

                if ($scope.datesInt.length > 2) {
                    var minDay = $scope.datesDate[0];
                    var maxDay = $scope.datesDate[$scope.datesDate.length - 1];
                    $scope.datepicker.minDay = minDay;
                    $scope.datepicker.maxDay = maxDay;
                    $scope.datepicker.dt1 = minDay;
                    $scope.datepicker.dt2 = maxDay;
                }
            }

            var option = getOption(data);
            myChart.setOption(option);
            $scope.rawData = data;
        }, Utils.errorHandler);
    };

    /**
     * when date picker is changed, update the flot chart
     */
    $scope.updateDatePick = function() {
        if (Utils.isNull($scope.rawData)) return;
        var startCob = Utils.date2int($scope.datepicker.dt1);
        var endCob = Utils.date2int($scope.datepicker.dt2);

        var index1 = Math.abs(Utils.binaryIndexOf($scope.datesInt, startCob));
        var index2 = Math.abs(Utils.binaryIndexOf($scope.datesInt, endCob));
        var data = Utils.subArrayOfObject($scope.rawData, index1, index2);
        var option = getOption(data);
        myChart.setOption(option);
    };
});

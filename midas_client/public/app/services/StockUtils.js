/**
 * provide stock related utils
 */
utilService.factory('StockUtils',
    function(Utils) {
        /**
         * get stock times, this times is Date type, not the int type
         */
        function getStockTimeSeries(stock) {
            if (Utils.isNull(stock)) return null;
            return stock.datesDate;
        }

        /**
         * extract stock data's index
         */
        function getStockIndexCmp(stock, indexCmpName) {
            if (stock != null && indexCmpName != null) {
                if (stock.indexInts[indexCmpName] != null) return stock.indexInts[indexCmpName];
                else if (stock.indexDoubles[indexCmpName] != null) return stock.indexDoubles[indexCmpName];
                else return null;
            }
        }

        /**
         * extract stock data's index name, not include date
         */
        function getStockIndexNameSet(stock) {
            if (stock != null && stock.indexName2indexCmp != null) {
                return Utils.object2array(stock.indexName2indexCmp);
            } else return [];
        }

        function getStockIndexCmpNameSet(stock, indexNames) {
            if (stock != null && indexNames != null) {
                var cmpNames = [];
                for (var i = 0, len = indexNames.length; i < len; ++i) {
                    cmpNames = cmpNames.concat(stock.indexName2indexCmp[indexNames[i]]);
                }
                return cmpNames;
            } else return [];
        }

        /**
         * get data for showIndexes set between two days
         */
        function getDataByTwoValidDate(stock, startDay, endDay, showIndexes) {
            if (Utils.isNull(startDay) && Utils.isNull(endDay)) {
                var times = getStockTimeSeries(stock);
                startDay = times[0];
                endDay = times[times.length - 1];
            }
            var t1 = Utils.date2int(startDay);
            var t2 = Utils.date2int(endDay);
            if (Utils.isNull(t1)) {
                t1 = t2;
                t2 = stock.maxDayInt;
            } else if (Utils.isNull(t2)) {
                t2 = stock.maxDayInt;
            } else {
                if (t1 > t2) { //swap two date
                    t2 = [t1, t1 = t2][0];
                }
            }

            var stockName = stock.stockName;
            var times = getStockTimeSeries(stock);
            var date = stock.datesInt;
            var index1 = Utils.binaryIndexOf(date, t1),
                index2 = Utils.binaryIndexOf(date, t2);
            if (Math.abs(index2) - Math.abs(index1) < 3) return null;
            else {
                index1 = (index1 >= 0) ? index1 : -index1;
                index2 = (index2 >= 0) ? index2 + 1 : -index2;
                if (!Utils.isNull(showIndexes)) {
                    var showIndexCmps = showIndexes; //getStockIndexCmpNameSet(stock, showIndexes);
                    var plotData = [];
                    for (var i = 0, len = showIndexCmps.length; i < len; i++) {
                        plotData.push({
                            label: stockName + '.' + showIndexCmps[i],
                            data: Utils.merge2Array(times.slice(index1, index2), getStockIndexCmp(stock, showIndexCmps[i]).slice(index1, index2))
                        });
                    }
                    return plotData;
                } else {
                    var defaultData = {
                        label: 'end',
                        data: []
                    };
                    if (stock && stock.indexDoubles['end']) {
                        defaultData.label = stockName + '.end';
                        defaultData.data = Utils.merge2Array(times.slice(index1, index2), getStockIndexCmp(stock, 'end').slice(index1, index2));
                    } else if (stock && stock.indexDoubles['close']) {
                        defaultData.label = stockName + '.close';
                        defaultData.data = Utils.merge2Array(times.slice(index1, index2), getStockIndexCmp(stock, 'close').slice(index1, index2));
                    }
                    return [defaultData];
                }
            }
        }

        /**
         * calculate the change percentage class
         */
        var levels = ["danger", "success"];

        function calcChangePctLevel(stockinfos) {
            for (var i = 0, len = stockinfos.length; i < len; i++) {
                if (stockinfos[i].change <= 0.0) stockinfos[i].level = levels[0];
                else stockinfos[i].level = levels[1];
            }
        }

        /**
         * calculate Y axis sets according to plot data set
         * extract the first y value, compare it to existing set to check if it belongs to
         * based on y data range, same range could share y axis
         */
        function calcYaxisSets(data) {
            if (Utils.isNull(data)) return null;
            var sets = [];
            var isFindSet = false;
            for (var i = 0, len = data.length; i < len; ++i) {
                var sampleValue = data[i].data[0][1]; // get y axis value
                var indexName = data[i].label;
                // check if exist set is suitable for this index
                isFindSet = false;
                for (var j = 0, len1 = sets.length; j < len1; ++j) {
                    if (Utils.deviateLevel(sampleValue, sets[j].average) < 0.3) {
                        sets[j].average = (sets[j].average + sampleValue) / 2;
                        sets[j].indexes.push(indexName);
                        sets[j].indexCnt++;
                        isFindSet = true;
                        break;
                    }
                }
                // not find in any set, create a new set
                if (!isFindSet) {
                    sets.push({
                        average: sampleValue,
                        indexes: new Array(indexName),
                        indexCnt: 1
                    });
                }
            }
            return sets.sort(sortSetCnt).reverse();
        }

        /**
         * help to sort the Y axis set by index number
         */
        function sortSetCnt(a, b) {
            return a.indexCnt - b.indexCnt;
        }

        /**
         * does this string match the stock code regex
         */
        function isStockCode(stockCodeStr) {
            if (typeof stockCodeStr === 'string') {
                return stockCodeStr.match(/\w{2,3}\d{6}/);
            } else if (stockCodeStr instanceof Array) {
                for (var i = 0, len = stockCodeStr.length; i < len; i++) {
                    if (!stockCodeStr[i].match(/\w{2,3}\d{6}/)) return false;
                }
                return true;
            }
            return false;
        }

        function row2column(rows, rowMeta, addSequence) {
            var result = {};

            if (rows && rows.length) {
                for (var prop in rowMeta) {
                    if (rowMeta.hasOwnProperty(prop)) {
                        result[prop] = [];
                    }
                }
                for (var i = 0, len = rows.length; i < len; ++i) {
                    for (var key in rowMeta) {
                        if (rowMeta.hasOwnProperty(key)) {
                            result[key].push(rows[i][rowMeta[key]]);
                        }
                    }
                }
            }

            if (result.time && result.time.length) {
                result.times = result.time;
                result.time = null;
            } else if (result.date && result.date.length) {
                result.times = result.date;
                result.date = null;
            }
            if (result.start && result.start.length) {
                result.open = result.start;
                result.start = null;
            }
            if (result.end && result.end.length) {
                result.close = result.end;
                result.end = null;
            }

            if (addSequence && rows && rows.length) {
                var sequences = [];
                for (var j = 0; j < rows.length; j++) {
                    sequences.push(j);
                }
                result.sequences = sequences;
            }
            return result;
        }

        function getAlpha(interval) {
            if (interval > 3) {
                return 2.0 / (interval + 1);
            }
            return 0.7;
        }

        function calculateSma(data, interval) {
            var result = [];
            var sum = 0;
            for (var i = 0; i < data.length; i++) {
                sum += data[i];
                if (i < interval) {
                    result.push((sum / (i + 1)).toFixed(4));
                } else {
                    sum -= data[i - interval];
                    result.push((sum / interval).toFixed(4));
                }
            }
            return result;
        }

        function calculateEma(data, interval) {
            var alpha = getAlpha(interval);
            var result = [];
            result.push(data[0]);
            for (var i = 1; i < data.length; i++) {
                result.push((alpha * data[i] + (1 - alpha) * result[i - 1]).toFixed(4));
            }
            return result;
        }

        function calculateMacd(price) {
            if (!price) return {
                macd: [],
                dif: [],
                dea: []
            };

            var pMaFast = calculateEma(price, 12);
            var pMaSlow = calculateEma(price, 26);
            var dif = [];

            for (var j = 0; j < price.length; j++) {
                dif[j] = (pMaFast[j] - pMaSlow[j]).toFixed(4);
            }

            var dea = calculateEma(dif, 9);
            var macd = [];
            for (var k = 0; k < price.length; k++) {
                macd[k] = ((dif[k] - dea[k]) * 2.0).toFixed(4);
            }
            return {
                macd: macd,
                dif: dif,
                dea: dea
            };
        }

        function formatData(rawData) {
            var result = row2column(rawData.rows, rawData.rowMeta);

            if (rawData.columns) {
                result = Object.assign(result, rawData.columns);
            }

            var macdResult = calculateMacd(result.close);
            result.macd = macdResult.macd;
            result.dif = macdResult.dif;
            result.dea = macdResult.dea;

            result.oclh = [];
            for (var i = 0; i < result.close.length; i++) {
                result.oclh.push([result.open[i], result.close[i], result.low[i], result.high[i]]);
            }
            result.instrumentName = rawData.instrumentName;
            return result;
        }

        return {
            isStockCode: isStockCode,
            calcChangePctLevel: calcChangePctLevel,
            getStockTimeSeries: getStockTimeSeries,
            getStockIndexCmp: getStockIndexCmp,
            getDataByTwoValidDate: getDataByTwoValidDate,
            getStockIndexNameSet: getStockIndexNameSet,
            getStockIndexCmpNameSet: getStockIndexCmpNameSet,
            calcYaxisSets: calcYaxisSets,
            row2column: row2column,
            calculateMacd: calculateMacd,
            formatData: formatData
        };
    }
);

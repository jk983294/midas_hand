/**
 * provide stock related utils
 */
utilService.factory('StockUtils',
    function(Utils){
        /**
         * get stock times, this times is Date type, not the int type
         */
        function getStockTimeSeries(stock){
            if( Utils.isNull(stock) ) return null;
            return stock.datesDate;
        }

        /**
         * extract stock data's index
         */
        function getStockIndexCmp(stock, indexCmpName){
            if( stock != null && indexCmpName != null){
                if(stock.indexInts[indexCmpName] != null) return stock.indexInts[indexCmpName];
                else if(stock.indexDoubles[indexCmpName] != null) return stock.indexDoubles[indexCmpName];
                else return null;
            }
        }

        /**
         * extract stock data's index name, not include date
         */
        function getStockIndexNameSet(stock){
            if( stock != null && stock.indexName2indexCmp != null ){
                return Utils.object2array(stock.indexName2indexCmp);
            } else return [];
        }

        function getStockIndexCmpNameSet(stock, indexNames){
            if( stock != null && indexNames != null ){
                var cmpNames = [];
                for(var i = 0, len = indexNames.length; i < len; ++i ){
                    cmpNames = cmpNames.concat(stock.indexName2indexCmp[indexNames[i]]);
                }
                return cmpNames;
            } else return [];
        }

        /**
         * get data for showIndexes set between two days
         */
        function getDataByTwoVaildDate(stock, startDay, endDay, showIndexes){
            if( Utils.isNull(startDay) && Utils.isNull(endDay)) {
                var times = getStockTimeSeries(stock);
                startDay = times[0];
                endDay = times[times.length - 1];
            }
            var t1 = Utils.date2int(startDay);
            var t2 = Utils.date2int(endDay);
            if( Utils.isNull(t1) ) {
                t1 = t2;
                t2 = stock.maxDayInt;
            } else if( Utils.isNull(t2) ) {
                t2 = stock.maxDayInt;
            } else {
                if( t1 > t2){       //swap two date
                    t2 = [t1, t1 = t2][0];
                }
            }

            var stockName = stock.stockName;
            var times = getStockTimeSeries(stock);
            var date = stock.datesInt;
            var index1 = Utils.binaryIndexOf(date, t1), index2 = Utils.binaryIndexOf(date, t2);
            if(Math.abs(index2) - Math.abs(index1) < 3) return null;
            else {
                index1 = (index1 >= 0) ? index1 : -index1;
                index2 = (index2 >= 0) ? index2 + 1 : -index2;
                if( ! Utils.isNull(showIndexes) ){
                    var showIndexCmps = showIndexes; //getStockIndexCmpNameSet(stock, showIndexes);
                    var plotData = [];
                    for(var i = 0, len = showIndexCmps.length; i < len; i++ ){
                        plotData.push({
                            label : stockName + '.' + showIndexCmps[i],
                            data : Utils.merge2Array(times.slice(index1, index2), getStockIndexCmp(stock , showIndexCmps[i]).slice(index1, index2))
                        });
                    }
                    return plotData;
                }else {
                    return [{
                        label : stockName + '.end',
                        data : Utils.merge2Array(times.slice(index1, index2), getStockIndexCmp(stock , 'end').slice(index1, index2))
                    }];
                }
            }
        }

        /**
         * calculate the change percentage class
         */
        var levels = ["danger", "success"];
        function calcChangePctLevel(stockinfos){
            for(var i = 0, len = stockinfos.length; i < len; i++ ){
                if( stockinfos[i].change <= 0.0  ) stockinfos[i].level = levels[0];
                else  stockinfos[i].level = levels[1];
            }
        }

        /**
         * calculate Y axis sets according to plot data set
         * extract the first y value, compare it to existing set to check if it belongs to
         * based on y data range, same range could share y axis
         */
        function calcYaxisSets(data){
            if(Utils.isNull(data)) return null;
            var sets = [];
            var isFindSet = false;
            for(var i = 0, len = data.length; i < len; ++i){
                var sampleValue = data[i].data[0][1];       // get y axis value
                var indexName = data[i].label;
                // check if exist set is suitable for this index
                isFindSet = false;
                for(var j = 0, len1 = sets.length; j < len1; ++j){
                    if(Utils.deviateLevel(sampleValue, sets[j].average) < 0.3){
                        sets[j].average = (sets[j].average + sampleValue) / 2;
                        sets[j].indexes.push(indexName);
                        sets[j].indexCnt++;
                        isFindSet = true;
                        break;
                    }
                }
                // not find in any set, create a new set
                if( !isFindSet){
                    sets.push({
                        average : sampleValue,
                        indexes : new Array(indexName),
                        indexCnt : 1
                    });
                }
            }
            return sets.sort(sortSetCnt).reverse();
        }

        /**
         * help to sort the Y axis set by index number
         */
        function sortSetCnt(a,b){
            return a.indexCnt - b.indexCnt;
        }

        /**
         * does this string match the stock code regex
         */
        function isStockCode(stockCodeStr){
            if(typeof stockCodeStr === 'string'){
                return stockCodeStr.match(/\w{2,3}\d{6}/);
            } else if(stockCodeStr instanceof Array){
                for(var i = 0, len = stockCodeStr.length; i < len; i++ ){
                    if(!stockCodeStr[i].match(/\w{2,3}\d{6}/)) return false;
                }
                return true;
            }
            return false;
        }

        return {
            isStockCode : isStockCode,
            calcChangePctLevel : calcChangePctLevel,
            getStockTimeSeries : getStockTimeSeries,
            getStockIndexCmp : getStockIndexCmp,
            getDataByTwoVaildDate : getDataByTwoVaildDate,
            getStockIndexNameSet : getStockIndexNameSet,
            getStockIndexCmpNameSet : getStockIndexCmpNameSet,
            calcYaxisSets : calcYaxisSets
        };
    }
);
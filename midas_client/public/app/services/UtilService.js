/**
 * Created by Administrator on 2014/10/8.
 */
var utilService = angular.module('UtilService', []);

utilService.factory('Utils',
    function(){
        function rangeArray(len){
            var data = new Array();
            for(var i = 0; i < len; ++i ) data.push(i);
            return data;
        }

        function arrayConcat(array, str, isbefore){
            var data = new Array();
            for(var i = 0, len = array.length; i < len; ++i ){
                data.push(isbefore ? str + ' ' + array[i] : array[i] + ' ' + str );
            }
            return data;
        }
        /**
         * data is an object array, extract those object's property to an array
         */
        function extractProperty(array, property){
            var properties = new Array();
            for(var i = 0, len = array.length; i < len; i++ ) properties.push(array[i][property]);
            return properties;
        }

        /**
         * data is [{x : x1, y: y1, filter : filter}],
         * get {label: yName, data : [[x1, y1]]} and filter between start and end if exists filter condition
         */
        function extractTimeSeries(array, xName, yName, filterName, start, end){
            var results = new Array();
            var minDay, maxDay
            if(filterName){
                minDay = Math.min(start, end);
                maxDay = Math.max(start, end);
            }
            for(var i = 0, len = array.length; i < len; i++ ){
                if(array[i][xName] && array[i][yName]){
                    if(!filterName || (array[i][filterName] >= minDay && array[i][filterName] <= maxDay) ){
                        results.push([array[i][xName], array[i][yName]]);
                    }
                }
            }
            return {
                label: yName,
                data : results
            };
        }


        function isInArray(array, toSearch){
            for(var i = 0, len = array.length; i < len; i++ ){
                if(array[i] === toSearch) return true;
            }
            return false;
        }

        /**
         * remove something in array
         */
        function array2remove(array, toRemove){
            var index = array.indexOf(toRemove);
            if (index > -1) {
                array.splice(index, 1);
            }
            return array;
        }

        /**
         * array and object convertor
         */
        function array2object(array){
            var object = {};
            for (var i = 0, len = array.length; i < len; ++i) {
                object[array[i]] = array[i];
            }
            return object;
        }
        function object2array(object){
            var array = [];
            for (var prop in object) {
                if( prop !== 'date') array.push(prop);
            }
            return array;
        }

        function object2PropArray(object){
            var array = [];
            for (var prop in object) {
                var propStr = prop.toString();
                if(prop && !(propStr.startsWith("$") || propStr == 'toJSON')){
                    var attr = { name : prop, value : object[prop]};
                    array.push(attr);
                }
            }
            return array;
        }

        /**
         * merge two array into one array
         */
        function merge2Array(p1, p2){
            var properties = new Array();
            for(var i = 0, len = p1.length; i < len; i++ ) properties.push( [ p1[i] , p2[i] ] );
            return properties;
        }

        function mergeForD3Point(x, y){
            var data = new Array();
            for(var i = 0, len = x.length; i < len; i++ ) data.push( { x : x[i], y : y[i]} );
            return data;
        }

        function urlWithOutHash(url){
            return url.substr(0,url.indexOf('#'));
        }
        function urlAfterHash(url){
            return url.substring(url.indexOf('#')+1,url.length);
        }


        function isNull(){
            for (var i = 0; i < arguments.length; i++) {
                var data = arguments[i];
                if( data === null || data === undefined || (typeof data === 'string' && data === '')
                    || (data instanceof Array && data.length === 0)){
                    return true;}
            }
            return false;
        }

        function errorHandler(msg){
            console.error('Failure : ', msg);
        }

        /**
         * date and int converter
         */
        function toTimes(dates){
            var times = new Array();
            for(var i = 0, len = dates.length; i < len; ++i ){
                times[i] = toTime(dates[i]);
            }
            return times;
        }

        function toTime(date){
            var year = Math.floor(date / 10000);
            var month = Math.floor(date / 100) % 100;
            var day = date % 100;
            return new Date(year, month - 1, day).getTime();
        }

        function date2int(date){
            if( date instanceof Date){
                return date.getDate() + (date.getMonth() + 1) * 100 + date.getFullYear() * 10000;
            } else {    // seconds from 1970.1.1
                date = new Date(date);
                return date.getDate() + (date.getMonth() + 1) * 100 + date.getFullYear() * 10000;
            }
        }

        /**
         * if not found, return the up bound, but not exceed the array length
         */
        function binaryIndexOf(array, searchElement) {
            var len = array.length - 1;
            var minIndex = 0;
            var maxIndex = len;
            var currentIndex = 0;
            while (minIndex <= maxIndex) {
                currentIndex = Math.floor( (minIndex + maxIndex) / 2 );
                if (array[currentIndex] < searchElement) {
                    minIndex = currentIndex + 1;
                }
                else if (array[currentIndex] > searchElement) {
                    maxIndex = currentIndex - 1;
                }
                else {
                    return currentIndex;
                }
            }
            return ( minIndex > len) ? ( len == 0 ? 0: -len) : ( minIndex == 0 ? 0: -minIndex);
        }

        function deviateLevel(value, benchmark){
            return Math.abs( (value - benchmark) / benchmark);
        }

        /**
         * Statistics Functions
         */
        function standardDeviation(values){
            var avg = average(values);

            var squareDiffs = values.map(function(value){
                var diff = value - avg;
                var sqrDiff = diff * diff;
                return sqrDiff;
            });

            var avgSquareDiff = average(squareDiffs);

            var stdDev = Math.sqrt(avgSquareDiff);
            return stdDev;
        }

        function average(data){
            var sum = data.reduce(function(sum, value){
                return sum + value;
            }, 0);

            var avg = sum / data.length;
            return avg;
        }

        function disabledWeekend(date, mode) {
            return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
        };

        /**
         *   Usage:  format_number(12345.678, 2);
         *   result: 12345.68
         **/
        function format_number(pnumber, decimals){
            if (isNaN(pnumber)) { return 0};
            if (pnumber=='') { return 0};

            var snum = new String(pnumber);
            var sec = snum.split('.');
            var whole = parseFloat(sec[0]);
            var result = '';

            if(sec.length > 1){
                var dec = new String(sec[1]);
                dec = String(parseFloat(sec[1])/Math.pow(10,(dec.length - decimals)));
                dec = String(whole + Math.round(parseFloat(dec))/Math.pow(10,decimals));
                var dot = dec.indexOf('.');
                if(dot == -1){
                    dec += '.';
                    dot = dec.indexOf('.');
                }
                while(dec.length <= dot + decimals) { dec += '0'; }
                result = dec;
            } else{
                var dot;
                var dec = new String(whole);
                dec += '.';
                dot = dec.indexOf('.');
                while(dec.length <= dot + decimals) { dec += '0'; }
                result = dec;
            }
            return result;
        }



        return {
            array2object : array2object,
            object2array : object2array,
            object2PropArray : object2PropArray,
            extractProperty : extractProperty,
            extractTimeSeries : extractTimeSeries,
            merge2Array : merge2Array,
            mergeForD3Point : mergeForD3Point,
            binaryIndexOf : binaryIndexOf,
            array2remove : array2remove,
            isInArray : isInArray,
            rangeArray : rangeArray,
            arrayConcat : arrayConcat,

            urlWithOutHash : urlWithOutHash,
            urlAfterHash : urlAfterHash,

            isNull : isNull,
            errorHandler : errorHandler,
            disabledWeekend : disabledWeekend,

            toTimes : toTimes,
            toTime : toTime,
            date2int : date2int,
            format_number : format_number,

            deviateLevel : deviateLevel,

            standardDeviation : standardDeviation,
            average : average
        };
    }
);
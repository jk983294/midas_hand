/**
 * used to format web GUI
 */
var levels = new Array("danger", "warning", "success");
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

angular.module('formatFilters', []).filter('addPct', function() {
    return function(input) {
        return input + '%';
    };
}).filter('stockSeverityLevel', function() {
    return function(data) {
        if( data === 'Warning'  ) return levels[1];
        else if( data === 'Danger'  ) return levels[0];
        else if( data === 'Disaster'  ) return levels[0];
        else return '';
    };
}).filter('changePctLevel', function() {
    return function(change) {
        if( change <= 0.0  ) return levels[0];
        else  return levels[2];
    };
}).filter('taskClass', function() {
    return function(status) {
        if( status === "Finished"  ) return levels[2];
        else  return levels[0];
    };
}).filter('keyValueList', function() {
    function keyValue2Html(data){
        return data.value + ' '+ format_number(data.key, 2);
    };
    return function(keyValueList) {
        var result = keyValue2Html(keyValueList[0]);
        for(var i = 1, len = keyValueList.length; i < len; ++i){
            result += (', ' + keyValue2Html(keyValueList[i]));
        }
        return result;
    };
}).filter('newlines', function () {
    return function(text) {
        return text.replace(/\n/g, '<br/>');
    }
}).filter('scoreList', function() {
    function sortScoreRecord(a, b){
        return b.score - a.score;
    };
    function toHtml(score){
        return score.stockCode + ' '+ format_number(score.score, 2)
            + (score.conceptName === null ? '' : (' ' + score.conceptName ));
    };
    return function(scores) {
        var newScores = scores.slice(); // copy a new array to avoid touch original array for infinite digest loop
        newScores.sort(sortScoreRecord);
        var result = toHtml(newScores[0]);
        for(var i = 1, len = newScores.length; i < len; ++i ){
            result += (', ' + toHtml(newScores[i]));
        }
        return result;
    };
}).filter('reportList', function() {
    return function(reports) {
        var newReports = reports.slice(); // copy a new array to avoid touch original array for infinite digest loop
        var result = newReports[0];
        for(var i = 1, len = newReports.length; i < len; ++i ){
            result += ('\n' + newReports[i]);
        }
        return result;
    };
}).filter('stocksList', function() {
    return function(stocks) {
//        var result = ('<span href="#/Index/' + stocks[0] + '">' + stocks[0] +'</span>');
//        for(var i = 1, len = stocks.length; i < len; ++i ){
//            result += (', <span href="#/Index/' + stocks[i] + '">' + stocks[i] +'</span>');
//        }
        var result = (stocks[0]);
        for(var i = 1, len = stocks.length; i < len; ++i ){
            result += (', ' + stocks[i]);
        }
        return result;
    };
});

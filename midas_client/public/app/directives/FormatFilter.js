/**
 * used to format web GUI
 */
var levels = new Array("danger", "success");
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
}).filter('changePctLevel', function() {
    return function(change) {
        if( change <= 0.0  ) return levels[0];
        else  return levels[1];
    };
}).filter('taskClass', function() {
    return function(status) {
        if( status === "Finished"  ) return levels[1];
        else  return levels[0];
    };
}).filter('scoreList', function() {
    function sortScoreRecord(a, b){
        return b.score - a.score;
    };
    function toHtml(score){
        return score.stockCode + ' '+ format_number(score.score, 2)
            + ' ' + (score.conceptName === null ? '' : (score.conceptName + ' '));
    };
    return function(scores) {
        scores = scores.sort(sortScoreRecord);
        var result = toHtml(scores[0]);
        for(var i = 1, len = scores.length; i < len; ++i ){
            result += (', ' + toHtml(scores[i]));
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

/**
 * フォーマット関数
 */
var $format = function(fmt, a) {
    var rep_fn = undefined;
    if (typeof a == "object") {
        rep_fn = function(m, k) { return a[ k ]; }
    } else {
        var args = arguments;
        rep_fn = function(m, k) { return args[ parseInt(k)+1 ]; }
    }
    return fmt.replace( /\{(\w+)\}/g, rep_fn);
};
var padding = function(n, d, p) {
    p = p || '0';
    return (p.repeat(d) + n).slice(-d);
};
var formatDt = function(date) {
    return $format('{0}/{1} {2}:{3}', padding(date.getMonth()+1,2), padding(date.getDate(),2), padding(date.getHours(),2), padding(date.getMinutes(),2));
};
var formatDtYYYYMMDD = function(date) {
    return $format('{0}{1}{2}', date.getFullYear(), padding(date.getMonth()+1,2), padding(date.getDate(),2));
};
var formatDtYYYYMM = function(date) {
    return $format('{0}{1}', date.getFullYear(), padding(date.getMonth()+1,2));
};
/**
 * Created with IntelliJ IDEA.
 * User: asoadmin
 * Date: 27/10/15
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
/*global cordova, module*/
var exec = require('cordova/exec');

var gpsCordovaPlugin = {
    getGPSPosition: function (successCallback, errorCallback) {

        exec(successCallback, errorCallback,'gpsCordovaPlugin','getGPSPosition',[]);
    },
    startWatchGPSPosition: function (distance,time,successCallback, errorCallback) {

        exec(successCallback, errorCallback,'gpsCordovaPlugin','startWatchGPSPosition',[{"distance":distance,"time":time}]);
    },

    stopWatchGPSPosition: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback,'gpsCordovaPlugin','stopWatchGPSPosition',[]);
    }
};

module.exports = gpsCordovaPlugin;
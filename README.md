This plugin implements a requesting GPS location from native API for the application webview on Cordova 3.0 and above

## Supported Cordova Platforms

* Android 4.0.0 or above

## Features available on Android
 Get current known location from Network provider
 Watch GPS location from GPS provider
## Functions
 getGPSPosition(successCallback, errorCallback)  - gets last known location from NETWORK Provider

 startWatchGPSPosition(distance,time,successCallback, errorCallback) - start watching position with distance and time between updates. Parameters "distance" and "time" are integers.

 stopWatchGPSPosition(successCallback, errorCallback) - stop watching position

 ## Installation

 cordova plugin add ...

 ## Usage


 Get current location (NOTE! the phone should have INTERNET CONNECTION)

    gpsCordovaPlugin.getGPSPosition(function(location){
            console.log(location.latitude + " ;" + location.longitude + "; " + location.source);

       },function(error){
          console.log(error);
     });


 Starting watching position

     gpsCordovaPlugin.startWatchGPSPosition(0,0,function(location){
         console.log(location.latitude + " ;" + location.longitude + "; " + location.source);

     },function(error){
          console.log(error);
     });

Stopping watching position

     gpsCordovaPlugin.stopWatchGPSPosition(function(result){
         console.log(result);
     },function(error){
         console.log(error);
     });
package gps.cordova.plugin;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import android.widget.Toast;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.pm.PackageManager;
import android.content.Context;
import android.app.Activity;
import android.Manifest;
import android.util.Log;
import 	java.util.Timer;
import 	java.util.TimerTask;



public class gpsCordovaPlugin extends CordovaPlugin {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 second
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private Location location;
    private Context context;

    static PluginResult watchGPSresult;
    private GpsListener gpsListener;


    @Override
    public boolean execute(final String action,final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        context = cordova.getActivity().getApplicationContext();


        if (action.equals("getGPSPosition")) {

              getLocation(callbackContext);

        } else if(action.equals("startWatchGPSPosition")){
            try{


            JSONObject params = args.getJSONObject(0);
            int distance = params.getInt("distance");
            int time = params.getInt("time");

            startWatchGPSPosition(distance,time,callbackContext);
            }catch (Exception e) {
                e.printStackTrace();

                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Specify distance and time parameters in Integer format" ));

            }


        }
        else if(action.equals("stopWatchGPSPosition")){
            stopWatchGPSPosition(callbackContext);

        }

        return true;
    }


    /**
     * Get Locaiton from NETWORK provider
     * @param callbackContext
     */
    private void getLocation(CallbackContext callbackContext) {



                try{
                    Activity activity = cordova.getActivity();

                    //Check app has the necessary permissions
                    if (!hasLocationPermission()) {


                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Application does not have ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permissions"));

                    }

                   locationManager = (LocationManager) activity
                            .getSystemService(Context.LOCATION_SERVICE);


                    // getting network status
                    boolean isNetworkEnabled = locationManager
                            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);





                    if (!isNetworkEnabled) {
                        callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.ERROR, "Application does not has  Network Provider"));
                    }
                    else {

                        // Get location from Network Provider
                        if (isNetworkEnabled) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, new NetworkGPSListener(callbackContext));


                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    JSONObject result = new JSONObject();
                                    result.put("latitude",location.getLatitude());
                                    result.put("longitude",location.getLongitude());
                                    result.put("source", "NETWORK");
                                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,result));
                                }
                                else {
                                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR,"No Interenet Connection or Network Provider"));
                                }
                            }
                        }


                    }
                }  catch (Exception e) {
                    e.printStackTrace();

                    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Errro getting location " + e.getMessage()));

                }





    }

    private void startWatchGPSPosition(int distance, int time,CallbackContext callbackContext) {



        try{
            Activity activity = cordova.getActivity();

            //Check app has the necessary permissions
            if (!hasLocationPermission()) {


                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Application does not have ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permissions"));

            }

            locationManager = (LocationManager) activity
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);



            if (!isGPSEnabled) {
                callbackContext.sendPluginResult( new PluginResult(PluginResult.Status.ERROR, "Application does not has turn on GPS"));
            }
            else {


                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    gpsListener = new GpsListener(callbackContext);
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            time,
                            distance, gpsListener);

                    if (location == null) {

                        if (locationManager != null) {
                            final Location location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            //the firts location will be null, the nest one will be handled in GpsListener
                            if (location != null) {


                            }

                        }
                    }
                }

            }
        }  catch (Exception e) {
            e.printStackTrace();

            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Errro getting location " + e.getMessage()));

        }


    }



    private void stopWatchGPSPosition(CallbackContext callbackContext){
        if(gpsListener != null){
            locationManager.removeUpdates(gpsListener);
            callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,"SUCCESS"));
            gpsCordovaPlugin.watchGPSresult.setKeepCallback(false);

        }

    }

    private boolean hasLocationPermission()
    {
        Context context = cordova.getActivity().getApplicationContext();
        int access = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineAccess = context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);


        return (access == PackageManager.PERMISSION_GRANTED) && (fineAccess == PackageManager.PERMISSION_GRANTED);
    }


    private void unregisterListener(){

    }


    /**
     * Locaiton Listener for Provider- GPS
     */
     private class GpsListener implements LocationListener{
       private CallbackContext callbackContext;

       public GpsListener(CallbackContext callbackContext){
          this.callbackContext = callbackContext;
       }


        public void onLocationChanged(Location location) {

            try{
                JSONObject result = new JSONObject();
                result.put("latitude",location.getLatitude());
                result.put("longitude",location.getLongitude());
                result.put("source","GPS");

                //Constantly send new location to client side
                gpsCordovaPlugin.watchGPSresult = new PluginResult(PluginResult.Status.OK, result);
                gpsCordovaPlugin.watchGPSresult.setKeepCallback(true);
                this.callbackContext.sendPluginResult(gpsCordovaPlugin.watchGPSresult);

            } catch (Exception execption){
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Errro getting uptaded location " + execption.getMessage()));
            }

        }

        public void onStatusChanged(String s, int i, Bundle b) {

        }

        public void onProviderDisabled(String s) {

        }

        public void onProviderEnabled(String s) {

        }

     }


    /**
     * Locaiton Listener for Provider- NETWORK
     */
    private class NetworkGPSListener implements LocationListener{
        private CallbackContext callbackContext;

        public NetworkGPSListener(CallbackContext callbackContext){
            this.callbackContext = callbackContext;
        }


        public void onLocationChanged(Location location) {

            try{
                JSONObject result = new JSONObject();
                result.put("latitude",location.getLatitude());
                result.put("longitude",location.getLongitude());
                result.put("source","NETWORK");
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,result));
                //TODO:unregister

            } catch (Exception execption){
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Errro getting uptaded location " + execption.getMessage()));
            }

        }

        public void onStatusChanged(String s, int i, Bundle b) {

        }

        public void onProviderDisabled(String s) {

        }

        public void onProviderEnabled(String s) {

        }

    }


}

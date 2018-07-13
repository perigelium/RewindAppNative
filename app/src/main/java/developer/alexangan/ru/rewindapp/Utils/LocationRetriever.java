package developer.alexangan.ru.rewindapp.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import developer.alexangan.ru.rewindapp.Interfaces.LocationRetrievedEvents;

public class LocationRetriever implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener
{
    // LogCat tag
    //private static final String TAG = LocationRetriever.class.getSimpleName();

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    //private boolean mRequestingLocationUpdates = true;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 15000; // 30 sec
    private static int FASTEST_INTERVAL = 1000; // 1 sec
    private static int DISPLACEMENT = 0; // 1 meter
    private static int EXPIRATION_DURATION = 15000;
    private static int MAXWAITTIME = 15000;

    private Activity activity;

    private LocationRetrievedEvents callback;
    private Handler handler;
    private Runnable runnable;

    public LocationRetriever(Activity activity, LocationRetrievedEvents callback)
    {
        this.activity = activity;
        this.callback = callback;

        buildLocationRequest();

        if (checkPlayServices())
        {
            buildGoogleApiClient();
        }

        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
    }

    private synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices()
    {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS)
        {
            if (googleAPI.isUserResolvableError(result))
            {
                //Toast.makeText(context, "context device is not supported.", Toast.LENGTH_SHORT).show();
                return false;
            }

            return false;
        }

        return true;
    }

    private void buildLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        mLocationRequest.setExpirationDuration(EXPIRATION_DURATION);
        mLocationRequest.setMaxWaitTime(MAXWAITTIME);
        mLocationRequest.setNumUpdates(1);
    }

    @Override
    public void onConnected(Bundle arg0)
    {
        //if (mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        handler = new Handler();
        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                stopLocationUpdates();
                callback.onLocationReceived();
            }
        };
        handler.postDelayed(runnable, MAXWAITTIME);
    }

    private void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        //Log.i("DEBUG", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int arg0)
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        if (location != null)
        {
            mLastLocation = location;

            stopLocationUpdates();
            handler.removeCallbacks(runnable);
            callback.onLocationReceived();
        }
    }

    public Location getLastLocation()
    {
        return mLastLocation;
    }
}

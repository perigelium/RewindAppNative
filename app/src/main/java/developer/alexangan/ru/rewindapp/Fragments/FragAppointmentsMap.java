package developer.alexangan.ru.rewindapp.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import developer.alexangan.ru.rewindapp.Interfaces.AppointmentsCommunicator;
import developer.alexangan.ru.rewindapp.Interfaces.LocationRetrievedEvents;
import developer.alexangan.ru.rewindapp.Models.AppointmentInfoItem;
import developer.alexangan.ru.rewindapp.Models.AppointmentsQueryParams;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.LocationRetriever;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.API_GET_APPOINTMENTS_URL;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragAppointmentsMap extends Fragment
        implements View.OnClickListener, View.OnTouchListener, Callback,
        LocationRetrievedEvents, OnMapReadyCallback
{
    private AppointmentsCommunicator mCommunicator;
    private Activity activity;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;
    LocationRetriever locationRetriever;

    private Call callGetAppointmentsList;
    private int PERMISSION_REQUEST_CODE = 11;
    private int ENABLE_GPS_REQUEST_CODE = 12;
    Location mLastLocation;
    double latitude, longitude;
    GoogleMap gMap;
    List<AppointmentInfoItem> lAppointmentInfoItems;
    private boolean gpsIsActive;
    private TextView tvLegendAndFilter;
    private MapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (AppointmentsCommunicator) getActivity();

        requestServerDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);

        lAppointmentInfoItems = new ArrayList<>();
        gpsIsActive = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_appointments_map_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        Button btnIntornoATe = (Button) rootView.findViewById(R.id.btnIntornoATe);
        btnIntornoATe.setOnClickListener(this);

        FloatingActionButton fabListMode = (FloatingActionButton) rootView.findViewById(R.id.fabListMode);
        fabListMode.setOnClickListener(this);

        FrameLayout flLegendAndFilter = (FrameLayout) rootView.findViewById(R.id.flLegendAndFilter);
        flLegendAndFilter.setOnClickListener(this);
        flLegendAndFilter.setOnTouchListener(this);

        tvLegendAndFilter = (TextView) rootView.findViewById(R.id.tvLegendAndFilter);

        LinearLayout llOpenAppointmentsSearch = (LinearLayout) rootView.findViewById(R.id.llOpenAppointmentsSearch);
        llOpenAppointmentsSearch.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //Typeface myCustomFont = Typeface.createFromAsset(activity.getAssets(), "fonts/SF_UI_Text_Regular.ttf");

        //mapReadyWaitingDialog.show();

        ViewUtils.showToastMessage(activity, getString(R.string.MapPreparationInProgressPleaseWait));

        //new ShowmapReadyWaitingDialogTask().execute(null, null, null);

        if (isGPS_Enabled())
        {
            gpsIsActive = true;
            tvLegendAndFilter.setText(R.string.LegendAndFilter);
        }

        //Log.d("DEBUG", "started");

        // disableInput();

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                FragmentManager fm = getChildFragmentManager();
                mapFragment = (MapFragment) fm.findFragmentByTag("mapFragment");

                if (mapFragment == null)
                {
                    mapFragment = new MapFragment();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(R.id.map, mapFragment, "mapFragment");
                    ft.commit();
                    fm.executePendingTransactions();
                }

                mapFragment.getMapAsync(FragAppointmentsMap.this);
            }
        }, 100);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //searchViewReports.setQuery("", false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        //mapReadyWaitingDialog.setProgress(50);

        gMap = googleMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (lAppointmentInfoItems.size() == 0)
        {
            Realm realm = Realm.getDefaultInstance();

            realm.beginTransaction();
            RealmResults<AppointmentInfoItem> rrAppointmentInfoItems = realm.where(AppointmentInfoItem.class).findAll();
            realm.commitTransaction();

            realm.beginTransaction();
            for (AppointmentInfoItem appointmentInfoItem : rrAppointmentInfoItems)
            {
                AppointmentInfoItem appointmentInfoItemEx = realm.copyFromRealm(appointmentInfoItem);
                lAppointmentInfoItems.add(appointmentInfoItemEx);
            }
            realm.commitTransaction();
            realm.close();
        }

        //mapReadyWaitingDialog.setProgress(75);

        if (lAppointmentInfoItems.size() != 0)
        {
            addGoogleMarkers();
        }

        getCurrentCoords();
    }

    @Override
    public View getView()
    {
        return super.getView();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.onClose();
            return;
        }

        if (view.getId() == R.id.fabListMode)
        {
            boolean currentAppointmentsListMode = mSettings.getBoolean("currentAppointmentsListMode", true);
            mCommunicator.openAppointmentsListMode(lAppointmentInfoItems, currentAppointmentsListMode, FragAppointmentsMap.this);
            return;
        }

        if (view.getId() == R.id.btnIntornoATe)
        {
            addClientsPinsOnMap();
            return;
        }

        if (view.getId() == R.id.llOpenAppointmentsSearch)
        {
            mCommunicator.openAppointmentsSearch(FragAppointmentsMap.this);
            return;
        }

        if (view.getId() == R.id.flLegendAndFilter)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            mCommunicator.openLegendAndFilter(gpsIsActive);
            return;
        }
    }

    private void addClientsPinsOnMap()
    {
    }

    private boolean isGPS_Enabled()
    {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        return ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) &&
                (checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED));
    }

    private void getCurrentCoords()
    {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            this.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), ENABLE_GPS_REQUEST_CODE);
            //mCommunicator.onClose();
            return;
        }

        if (checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                String[] permissions = new String[]
                        {
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        };

                requestMultiplePermissions(permissions);
            }
        } else
        {
            requestServerDialog.show();

            locationRetriever = new LocationRetriever(activity, this);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestMultiplePermissions(String[] permissions)
    {
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Check which request we're responding to
        if (requestCode == ENABLE_GPS_REQUEST_CODE)
        {
            if (!isGPS_Enabled())
            {
                //mCommunicator.onClose();
            } else
            {
                try
                {
                    Thread.sleep(3000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                getCurrentCoords();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length >= 1)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                //disableInputAndShowProgressDialog();

                locationRetriever = new LocationRetriever(activity, this);
            }
        }
    }

    @Override
    public void onLocationReceived()
    {
        mLastLocation = locationRetriever.getLastLocation();

        if (mLastLocation != null)
        {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            mSettings.edit().putFloat("lastLatitude", (float) mLastLocation.getLatitude()).apply();
            mSettings.edit().putFloat("lastLongitude", (float) mLastLocation.getLongitude()).apply();

            // Remove when database error "not in city" is fixed
            // Ferrara
            //latitude = "44.83";
            //longitude = "11.62";

            // Palermo
/*            latitude = "38.12";
            longitude = "13.35";*/

            //LatLng curLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            LatLng curLocation = new LatLng(latitude, longitude);

            CameraPosition googlePlex = CameraPosition.builder()
                    .target(curLocation)
                    .zoom(12)
                    .bearing(0)
                    .tilt(30)
                    .build();

            gMap.addMarker(new MarkerOptions().position(curLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_user)));

            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

            if (NetworkUtils.isNetworkAvailable(activity))
            {
                if (tokenStr == null)
                {
                    requestServerDialog.dismiss();
                    alertDialogRelogin("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
                } else
                {
                    AppointmentsQueryParams appointmentsQueryParams =
                            new AppointmentsQueryParams("1", "20170119", "20170119", String.valueOf(latitude), String.valueOf(longitude), "", "", "", "");

                    NetworkUtils networkUtils = new NetworkUtils();

                    callGetAppointmentsList = networkUtils.getAppointments(this, API_GET_APPOINTMENTS_URL, appointmentsQueryParams);
                }
            }
        }
        requestServerDialog.dismiss();

        // enableInput();
    }

    private void addGoogleMarkers()
    {
        if (gMap != null)
        {
            gMap.clear();

            // Remove when database error "not in city" is fixed
            //LatLng curLocation = new LatLng(44.83, 11.62);
            //LatLng curLocation = new LatLng(38.12, 13.35);
            LatLng curLocation = new LatLng(mSettings.getFloat("lastLatitude", 45.6512f), mSettings.getFloat("lastLongitude", 12.2948f));

            CameraPosition googlePlex = CameraPosition.builder()
                    .target(curLocation)
                    .zoom(12)
                    .bearing(0)
                    .tilt(30)
                    .build();

            gMap.addMarker(new MarkerOptions().position(curLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_user)));

            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

            for (int i = 0; i < lAppointmentInfoItems.size(); i++)
            {
                AppointmentInfoItem appointmentInfoItem = lAppointmentInfoItems.get(i);

                LatLng curLatLng = new LatLng(appointmentInfoItem.getLatitude(), appointmentInfoItem.getLongitude());

                int iconResource = 0;

                if (mSettings.getBoolean("appointmentiConvergentiDisabled", false) && appointmentInfoItem.getStatus().equals("convergente"))
                {
                    continue;
                }

                if (mSettings.getBoolean("appointmentiNonConvergentiDisabled", false) && appointmentInfoItem.getStatus().equals("nonconvergente"))
                {
                    continue;
                }

                if (mSettings.getBoolean("appointmentiDeactDisabled", false) && appointmentInfoItem.getStatus().equals("deactivated"))
                {
                    continue;
                }

                if (mSettings.getBoolean("appointmentiInBorsellinoDisabled", false) && appointmentInfoItem.getStatus().equals("borsellino"))
                {
                    continue;
                }

                iconResource = setIconResource(appointmentInfoItem);

                Marker curMarker = gMap.addMarker(new MarkerOptions().position(curLatLng)
                        .title(appointmentInfoItem.getRegSociale())
                        .icon(BitmapDescriptorFactory.fromResource(iconResource)));

                curMarker.setTag(appointmentInfoItem);

                gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                {
                    @Override
                    public boolean onMarkerClick(Marker marker)
                    {
                        AppointmentInfoItem appointmentInfoItem = (AppointmentInfoItem) marker.getTag();

                        mCommunicator.onMapPinSelected(appointmentInfoItem);

                        return false;
                    }
                });
            }
        }
    }

    private int setIconResource(AppointmentInfoItem appointmentInfoItem)
    {
        int iconResource;

        if (appointmentInfoItem.getId_customer() == 0)
        {
            if (appointmentInfoItem.getStatus().equals("borsellino"))
            {
                iconResource = R.drawable.map_marker_in_borsellino_small;
            } else if (appointmentInfoItem.getStatus().equals("convergente"))
            {
                iconResource = R.drawable.map_marker_convergenti_small;
            } else if (appointmentInfoItem.getStatus().equals("nonconvergente"))
            {
                iconResource = R.drawable.map_marker_non_convergenti_small;
            } else if (appointmentInfoItem.getStatus().equals("deactivated"))
            {
                iconResource = R.drawable.map_marker_deact_small;
            } else
            {
                iconResource = R.drawable.map_marker_others_small;
            }
        } else
        {
            if (appointmentInfoItem.getStatus().equals("borsellino"))
            {
                iconResource = R.drawable.map_marker_in_borsellino_altuofianco_small;
            } else if (appointmentInfoItem.getStatus().equals("convergente"))
            {
                iconResource = R.drawable.map_marker_convergenti_altuofianco_small;
            } else if (appointmentInfoItem.getStatus().equals("nonconvergente"))
            {
                iconResource = R.drawable.map_marker_non_convergenti_altuofianco_small;
            } else if (appointmentInfoItem.getStatus().equals("deactivated"))
            {
                iconResource = R.drawable.map_marker_deact_altuofianco_small;
            } else
            {
                iconResource = R.drawable.map_marker_others_small;
            }
        }
        return iconResource;
    }

    @Override
    public void onFailure(Call call, IOException e)
    {
        if (call == callGetAppointmentsList)
        {
            requestServerDialog.dismiss();

            ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException
    {
        if (call == callGetAppointmentsList)
        {
            final String appointmentsInfoJSON = response.body().string();

            response.body().close();

            requestServerDialog.dismiss();

            Gson gson = new Gson();

            JSONArray appointmentsInfoJsonArray = null;

            try
            {
                appointmentsInfoJsonArray = new JSONArray(appointmentsInfoJSON);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            if (appointmentsInfoJsonArray != null && appointmentsInfoJsonArray.length() != 0)
            {
                fillAppointmentInfoItemsList(gson, appointmentsInfoJsonArray);
            }

            if (this.isVisible() && lAppointmentInfoItems.size() != 0)
            {
                rewriteRealmAppointmentInfoItems();

                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        addGoogleMarkers();
                    }
                });
            }
        }
    }

    private void fillAppointmentInfoItemsList(Gson gson, JSONArray appointmentsInfoJsonArray)
    {
        for (int i = 0; i < appointmentsInfoJsonArray.length(); i++)
        {
            JSONObject JsonAppointmentInfoItem = null;
            try
            {
                JsonAppointmentInfoItem = appointmentsInfoJsonArray.getJSONObject(i);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            AppointmentInfoItem appointmentInfoItem = gson.fromJson(String.valueOf(JsonAppointmentInfoItem), AppointmentInfoItem.class);

            lAppointmentInfoItems.add(appointmentInfoItem);
        }
    }

    private void rewriteRealmAppointmentInfoItems()
    {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        RealmResults<AppointmentInfoItem> rrAppointmentInfoItems = realm.where(AppointmentInfoItem.class).findAll();
        realm.commitTransaction();

        realm.beginTransaction();
        rrAppointmentInfoItems.deleteAllFromRealm();
        realm.commitTransaction();

        realm.beginTransaction();
        for (AppointmentInfoItem appointmentInfoItem : lAppointmentInfoItems)
        {
            realm.copyToRealm(appointmentInfoItem);
        }
        realm.commitTransaction();
        realm.close();
    }

    private void alertDialogRelogin(String title, String message)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle);

        builder.setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Si",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                mCommunicator.onLogoutCommand();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                alert.dismiss();
                            }
                        });

        alert = builder.create();

        alert.show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view.getId() == R.id.flLegendAndFilter)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        return false;
    }
}

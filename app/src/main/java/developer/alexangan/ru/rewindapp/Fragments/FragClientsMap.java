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
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Interfaces.LocationRetrievedEvents;
import developer.alexangan.ru.rewindapp.Models.ClientInfoItem;
import developer.alexangan.ru.rewindapp.Models.ClientPraticheInfoItem;
import developer.alexangan.ru.rewindapp.Models.ClientsQueryParams;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.LocationRetriever;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.API_GET_CLIENTS_URL;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragClientsMap extends Fragment
        implements View.OnClickListener, View.OnTouchListener, Callback,
        LocationRetrievedEvents, OnMapReadyCallback
{
    private ClientsCommunicator mCommunicator;
    private Activity activity;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;
    LocationRetriever locationRetriever;

    private Call callGetClientsList;
    private int PERMISSION_REQUEST_CODE = 11;
    private int ENABLE_GPS_REQUEST_CODE = 12;
    Location mLastLocation;
    double latitude, longitude;
    GoogleMap gMap;
    List<ClientInfoItem> lClientInfoItems;
    private boolean gpsIsActive;
    private TextView tvLegendAndFilter;
    private boolean firstStart;
    private MapFragment mapFragment;
    private Button btnLeads;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (ClientsCommunicator) getActivity();

        requestServerDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);

        lClientInfoItems = new ArrayList<>();
        gpsIsActive = true;
        firstStart = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_clients_map_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        btnLeads = (Button) rootView.findViewById(R.id.btnLeads);
        btnLeads.setOnClickListener(this);

        FrameLayout flLegendAndFilter = (FrameLayout) rootView.findViewById(R.id.flLegendAndFilter);
        flLegendAndFilter.setOnClickListener(this);
        flLegendAndFilter.setOnTouchListener(this);

        tvLegendAndFilter = (TextView) rootView.findViewById(R.id.tvLegendAndFilter);

        LinearLayout llOpenClientsSearch = (LinearLayout) rootView.findViewById(R.id.llOpenClientsSearch);
        llOpenClientsSearch.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (firstStart)
        {
            ViewUtils.showToastMessage(activity, getString(R.string.MapPreparationInProgressPleaseWait));
        }

        if (isGPS_Enabled())
        {
            gpsIsActive = true;
            tvLegendAndFilter.setText(R.string.LegendAndFilter);
        }

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

                mapFragment.getMapAsync(FragClientsMap.this);
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
        gMap = googleMap;
        //googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (lClientInfoItems.size() == 0)
        {
            Realm realm = Realm.getDefaultInstance();

            realm.beginTransaction();
            RealmResults<ClientInfoItem> rrClientInfoItems = realm.where(ClientInfoItem.class).findAll();
            realm.commitTransaction();

            realm.beginTransaction();
            for (ClientInfoItem clientInfoItem : rrClientInfoItems)
            {
                ClientInfoItem clientInfoItemEx = realm.copyFromRealm(clientInfoItem);
                lClientInfoItems.add(clientInfoItemEx);
            }
            realm.commitTransaction();
            realm.close();
        }

        if (firstStart)
        {
            if (lClientInfoItems.size() != 0)
            {
                addGoogleMarkers();
            }

            getCurrentCoords();

            firstStart = false;
        }
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

        if (view.getId() == R.id.btnLeads)
        {
            mCommunicator.openLeadsList();
            return;
        }

        if (view.getId() == R.id.llOpenClientsSearch)
        {
            mCommunicator.onOpenClientsSearch(FragClientsMap.this);
            return;
        }

        if (view.getId() == R.id.flLegendAndFilter)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            mCommunicator.onOpenLegendAndFilter(gpsIsActive);
            return;
        }
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
        if (requestCode == ENABLE_GPS_REQUEST_CODE)
        {
            if ( ! isGPS_Enabled())
            {
                ViewUtils.showToastMessage(activity, getString(R.string.LocationServiceNotEnabled));
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
            latitude = 44.83;
            longitude = 11.62;

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
                    ClientsQueryParams clientsQueryParams =
                            new ClientsQueryParams("false", "false", String.valueOf(latitude), String.valueOf(longitude), "10", "", "");

                    NetworkUtils networkUtils = new NetworkUtils();

                    callGetClientsList = networkUtils.getClients(this, API_GET_CLIENTS_URL, clientsQueryParams);
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
            LatLng curLocation = new LatLng(44.83, 11.62);
            //LatLng curLocation = new LatLng(38.12, 13.35);
            //LatLng curLocation = new LatLng(mSettings.getFloat("lastLatitude", 45.6512f), mSettings.getFloat("lastLongitude", 12.2948f));

            CameraPosition googlePlex = CameraPosition.builder()
                    .target(curLocation)
                    .zoom(12)
                    .bearing(0)
                    .tilt(30)
                    .build();

            gMap.addMarker(new MarkerOptions().position(curLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_user)));

            gMap.moveCamera(CameraUpdateFactory.newCameraPosition(googlePlex));

            for (int i = 0; i < lClientInfoItems.size(); i++)
            {
                ClientInfoItem clientInfoItem = lClientInfoItems.get(i);

                if (isClientStatusDisabled(clientInfoItem)) continue;

                LatLng curLatLng = new LatLng(clientInfoItem.getLatitude(), clientInfoItem.getLongitude());

                int iconResource = setIconResource(clientInfoItem);

                Marker curMarker = gMap.addMarker(new MarkerOptions().position(curLatLng)
                        .title(clientInfoItem.getRagione_sociale())
                        .icon(BitmapDescriptorFactory.fromResource(iconResource)));

                curMarker.setTag(clientInfoItem);

                gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                {
                    @Override
                    public boolean onMarkerClick(Marker marker)
                    {
                        ClientInfoItem clientInfoItem = (ClientInfoItem) marker.getTag();

                        mCommunicator.onMapPinSelected(clientInfoItem);

                        return false;
                    }
                });
            }
        }
    }

    private boolean isClientStatusDisabled(ClientInfoItem clientInfoItem)
    {
        if (mSettings.getBoolean("clientiConvergentiDisabled", false) && clientInfoItem.getStatus().equals("convergente"))
        {
            return true;
        }

        if (mSettings.getBoolean("clientiNonConvergentiDisabled", false) && clientInfoItem.getStatus().equals("nonconvergente"))
        {
            return true;
        }

        if (mSettings.getBoolean("clientiDeactDisabled", false) && clientInfoItem.getStatus().equals("deactivated"))
        {
            return true;
        }

        if (mSettings.getBoolean("clientiInBorsellinoDisabled", false) && clientInfoItem.getStatus().equals("borsellino"))
        {
            return true;
        }
        return false;
    }

    private int setIconResource(ClientInfoItem clientInfoItem)
    {
        int iconResource;
        
        if (clientInfoItem.getAtf_id_customer() == null)
        {
            if (clientInfoItem.getStatus().equals("borsellino"))
            {
                iconResource = R.drawable.map_marker_in_borsellino_small;
            } else if (clientInfoItem.getStatus().equals("convergente"))
            {
                iconResource = R.drawable.map_marker_convergenti_small;
            } else if (clientInfoItem.getStatus().equals("nonconvergente"))
            {
                iconResource = R.drawable.map_marker_non_convergenti_small;
            } else if (clientInfoItem.getStatus().equals("deactivated"))
            {
                iconResource = R.drawable.map_marker_deact_small;
            } else
            {
                iconResource = R.drawable.map_marker_others_small;
            }
        } else
        {
            if (clientInfoItem.getStatus().equals("borsellino"))
            {
                iconResource = R.drawable.map_marker_in_borsellino_altuofianco_small;
            } else if (clientInfoItem.getStatus().equals("convergente"))
            {
                iconResource = R.drawable.map_marker_convergenti_altuofianco_small;
            } else if (clientInfoItem.getStatus().equals("nonconvergente"))
            {
                iconResource = R.drawable.map_marker_non_convergenti_altuofianco_small;
            } else if (clientInfoItem.getStatus().equals("deactivated"))
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
        if (call == callGetClientsList)
        {
            requestServerDialog.dismiss();

            ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException
    {
        if (call == callGetClientsList)
        {
            final String clientsInfoJSON = response.body().string();

            response.body().close();

            requestServerDialog.dismiss();

            Gson gson = new Gson();

            JSONArray clientsInfoJsonArray = null;

            try
            {
                clientsInfoJsonArray = new JSONArray(clientsInfoJSON);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            if (clientsInfoJsonArray != null && clientsInfoJsonArray.length() != 0)
            {
                fillClientInfoItemsList(gson, clientsInfoJsonArray);
            }

            if (this.isVisible() && lClientInfoItems.size() != 0)
            {
                rewriteRealmClientInfoItems();

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

    private void fillClientInfoItemsList(Gson gson, JSONArray clientsInfoJsonArray)
    {
        for (int i = 0; i < clientsInfoJsonArray.length(); i++)
        {
            JSONObject JsonClientInfoItem = null;
            try
            {
                JsonClientInfoItem = clientsInfoJsonArray.getJSONObject(i);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            ClientInfoItem clientInfoItem = gson.fromJson(String.valueOf(JsonClientInfoItem), ClientInfoItem.class);

            JSONArray practiche = null;
            try
            {
                practiche = JsonClientInfoItem.getJSONArray("pratiche");
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            Type typeClientPraticheInfoItem = new TypeToken<List<ClientPraticheInfoItem>>()
            {
            }.getType();

            List<ClientPraticheInfoItem> lClientsPracticeInfoItems = gson.fromJson(String.valueOf(practiche), typeClientPraticheInfoItem);

            RealmList rl_ClientsPracticeInfoItems = new RealmList<>(lClientsPracticeInfoItems.toArray(new ClientPraticheInfoItem[lClientsPracticeInfoItems.size()]));

            clientInfoItem.setPractiche_list(rl_ClientsPracticeInfoItems);

            lClientInfoItems.add(clientInfoItem);
        }
    }

    private void rewriteRealmClientInfoItems()
    {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        RealmResults<ClientInfoItem> rrClientInfoItems = realm.where(ClientInfoItem.class).findAll();
        realm.commitTransaction();

        realm.beginTransaction();
        rrClientInfoItems.deleteAllFromRealm();
        realm.commitTransaction();

        realm.beginTransaction();
        for (ClientInfoItem clientInfoItem : lClientInfoItems)
        {
            realm.copyToRealm(clientInfoItem);
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

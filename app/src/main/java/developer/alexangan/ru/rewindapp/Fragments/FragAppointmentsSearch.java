package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import developer.alexangan.ru.rewindapp.Interfaces.AppointmentsCommunicator;
import developer.alexangan.ru.rewindapp.Models.AppointmentsSearchResultsListItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragAppointmentsSearch extends Fragment
        implements View.OnClickListener, SearchView.OnQueryTextListener, Callback
{
    private AppointmentsCommunicator mCommunicator;
    private Activity activity;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;
    private Unbinder unbinder;

    private SearchView svAppointments;
    private ImageView ivCloseSearch;
    private ImageView ivMagnifier;
    private TextView tvSearchHint;
    private Call callGetAppointmentsList;
    List<AppointmentsSearchResultsListItem> l_AppointmentsSearchResultsListItems;
    List <String> searchCriteries;
    List <String> searchStates;
    List <String> searchCriteriesDisplayed;
    private TextView tvCloseAppointmentsSearch;
    private String searchCriteriaSelected;
    private String searchStateSelected;
    private ListView searchCriteriesListView, searchResultsListView;
    private String queryString;

    //@BindViews({R.id.btnStatoChiuso, R.id.btnStatoNoInterest, R.id.btnStatoCanceled, R.id.btnStatoOutOfStrategy, R.id.btnStatoNulledOut}) List<Button> stateButtonsList;

    @OnClick({R.id.btnStatoChiuso, R.id.btnStatoNoInterest, R.id.btnStatoCanceled, R.id.btnStatoOutOfStrategy, R.id.btnStatoNulledOut})
    void onStateButtonClick(View view)
    {
        view.setAlpha(0.5f);

        switch (view.getId())
        {
            case R.id.btnStatoChiuso:
                searchStateSelected = searchStates.get(0);
                break;
            case R.id.btnStatoNoInterest:
                searchStateSelected = searchStates.get(1);
                break;
            case R.id.btnStatoCanceled:
                searchStateSelected = searchStates.get(2);
                break;
            case R.id.btnStatoOutOfStrategy:
                searchStateSelected = searchStates.get(3);
                break;
            case R.id.btnStatoNulledOut:
                searchStateSelected = searchStates.get(4);
                break;
        }

        mCommunicator.openAppointmentsSearchResults(searchStateSelected, null);

        //callGetAppointmentsList = networkUtils.getAppointmentsSearchResults(this, API_GET_CLIENTS_URL, appointmentsQueryParams);
    }

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

        l_AppointmentsSearchResultsListItems = new ArrayList<>();
        searchCriteries = new ArrayList<>();
        searchCriteriesDisplayed = new ArrayList<>();
        searchStates = new ArrayList<>();

        searchCriteries.add("ragione_sociale");
        searchCriteries.add("location");

        searchCriteriesDisplayed.add("Ragione Sociale");
        searchCriteriesDisplayed.add("Località");

        searchStates.add("Chiuso");
        searchStates.add("Non si è creato interesse");
        searchStates.add("Disdetto");
        searchStates.add("Fuori strategia");
        searchStates.add("Annullato");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_appointments_search_layout, container, false);

        tvCloseAppointmentsSearch = (TextView) rootView.findViewById(R.id.tvCloseAppointmentsSearch);
        tvCloseAppointmentsSearch.setOnClickListener(this);

        searchCriteriesListView = (ListView) rootView.findViewById(R.id.searchCriteriesListView);

        svAppointments = (SearchView) rootView.findViewById(R.id.svAppointments);

        int id = svAppointments.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);

        int searchHintBtnId = svAppointments.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        int hintTextId = svAppointments.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        int closeBtnId = svAppointments.getContext()
                .getResources()
                .getIdentifier("android:id/search_close_btn", null, null);

        ivCloseSearch = (ImageView) svAppointments.findViewById(closeBtnId);
        tvSearchHint = (TextView) svAppointments.findViewById(hintTextId);

        ivMagnifier = (ImageView) svAppointments.findViewById(searchHintBtnId);

        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Typeface myCustomFont = Typeface.createFromAsset(activity.getAssets(), "fonts/SF_UI_Text_Regular.ttf");
        tvSearchHint.setTypeface(myCustomFont);

        tvSearchHint.setTextColor(Color.BLACK);
        tvSearchHint.setHintTextColor(Color.parseColor("#ff808080"));

        ivCloseSearch.setImageResource(R.drawable.clearsearch_2);
        ivMagnifier.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

        svAppointments.setSubmitButtonEnabled(true);
        svAppointments.setOnQueryTextListener(this);

        ArrayAdapter <String> searchCriteriaListAdapter =
            new ArrayAdapter<>(activity, R.layout.appointments_search_criteries_list_row, R.id.tvSearchCriteria, searchCriteriesDisplayed);

        searchCriteriesListView.setAdapter(searchCriteriaListAdapter);

        searchCriteriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.setSelected(true);

                searchCriteriaSelected = searchCriteries.get(position);

                for (int i = 0; i < searchCriteriesListView.getChildCount(); i++)
                {
                    View childView = searchCriteriesListView.getChildAt(i);
                    childView.setBackgroundColor(Color.parseColor("#00000000"));
                    ImageView ivSearchCriteria = (ImageView) childView.findViewById(R.id.ivSearchCriteria);
                    ivSearchCriteria.setVisibility(View.GONE);
                }

                ImageView ivSearchCriteria = (ImageView) view.findViewById(R.id.ivSearchCriteria);
                ivSearchCriteria.setVisibility(View.VISIBLE);

                view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
            }
        });
    }

    @Override
    public View getView()
    {
        return super.getView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //searchViewReports.setQuery("", false);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        unbinder.unbind();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.tvCloseAppointmentsSearch)
        {
            mCommunicator.popFragmentsBackStack();
            return;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String queryString)
    {
        this.queryString = queryString;
        mSettings.edit().putString("appointmentsSearchLastQueryString", queryString).apply();

        //ViewUtils.disableSoftKeyboard(activity);

        String strQueryLower = queryString.toLowerCase();

        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialogRelogin("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                mCommunicator.openAppointmentsSearchResults(queryString, null);

/*                AppointmentsQueryParams appointmentsQueryParams = new AppointmentsQueryParams("2", "", "", "", "", "", "", searchCriteriaSelected, strQueryLower);

                NetworkUtils networkUtils = new NetworkUtils();

                requestServerDialog.show();

                callGetAppointmentsList = networkUtils.getAppointmentsSearchResults(this, API_GET_CLIENTS_URL, appointmentsQueryParams);*/
            }
        }

        return true;
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

            if (this.isVisible() && l_AppointmentsSearchResultsListItems.size() != 0)
            {
                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        mCommunicator.openAppointmentsSearchResults(queryString, l_AppointmentsSearchResultsListItems);
                        //tvSearchCriteriaSelected.requestFocus();
                        ViewUtils.disableSoftKeyboard(activity);
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

            AppointmentsSearchResultsListItem appointmentsSearchResultsListItem = gson.fromJson(String.valueOf(JsonAppointmentInfoItem), AppointmentsSearchResultsListItem.class);

            l_AppointmentsSearchResultsListItems.add(appointmentsSearchResultsListItem);
        }
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
    public boolean onQueryTextChange(String newText)
    {
/*        if (TextUtils.isEmpty(newText))
        {
            //mListView.clearTextFilter();
        } else
        {
            //mListView.setFilterText(newText.toString());
        }*/
        return false;
    }
}

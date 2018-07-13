package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
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

import developer.alexangan.ru.rewindapp.Adapters.ClientsSearchResultsListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Models.ClientInfoItem;
import developer.alexangan.ru.rewindapp.Models.ClientsQueryParams;
import developer.alexangan.ru.rewindapp.Models.ClientsSearchCriteries;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.API_GET_CLIENTS_URL;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragClientsSearch extends Fragment
        implements View.OnClickListener, View.OnTouchListener, SearchView.OnQueryTextListener, Callback
{
    private ClientsCommunicator mCommunicator;
    private Activity activity;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;

    private SearchView svClients;
    private ImageView ivCloseSearch;
    private ImageView ivMagnifier;
    private TextView tvSearchHint;
    private Call callGetClientsList;
    List<ClientInfoItem> lClientInfoItems;
/*    List <String> searchCriteries;
    List <String> searchCriteriesDisplayed;*/
    private TextView tvCloseClientsSearch;
    private String searchCriteria;
    private ListView searchCriteriesListView, searchResultsListView;
    private FrameLayout flSearchCriteriesExpandCollapse;
    private ImageView ivCaretExpandCollapse;
    private boolean searchCriteriesCollapsed;
    private TextView tvSearchCriteriaSelected;
    private ArrayMap<String, String> mapSearchCriteries;
    private String search_mode;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (ClientsCommunicator) getActivity();

        if (getArguments() != null)
        {
            search_mode = getArguments().getString("search_mode");
        }

        requestServerDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);

        lClientInfoItems = new ArrayList<>();

        mapSearchCriteries = new ClientsSearchCriteries().getMapSearchCriteries();

/*        searchCriteries = new ArrayList<>();
        searchCriteriesDisplayed = new ArrayList<>();

        searchCriteries.add("ragione_sociale");
        searchCriteries.add("partita_iva");
        searchCriteries.add("phone");
        searchCriteries.add("location");
        searchCriteries.add("mobile_phone");
        searchCriteries.add("province");

        searchCriteriesDisplayed.add("RAGIONE SOCIALE");
        searchCriteriesDisplayed.add("PARTITA IVA");
        searchCriteriesDisplayed.add("TELEFONO");
        searchCriteriesDisplayed.add("LOCALITÀ");
        searchCriteriesDisplayed.add("CELULLARE");
        searchCriteriesDisplayed.add("PROVINCIA");*/

        searchCriteriesCollapsed = true;
        searchCriteria = mSettings.getString("clientsSearchCriteria", mapSearchCriteries.keyAt(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_clients_search_layout, container, false);

        tvSearchCriteriaSelected = (TextView) rootView.findViewById(R.id.tvSearchCriteriaSelected);

        flSearchCriteriesExpandCollapse = (FrameLayout) rootView.findViewById(R.id.flSearchCriteriesExpandCollapse);
        flSearchCriteriesExpandCollapse.setOnClickListener(this);

        ivCaretExpandCollapse = (ImageView) rootView.findViewById(R.id.ivCaretExpandCollapse);

        svClients = (SearchView) rootView.findViewById(R.id.svNews);

        tvCloseClientsSearch = (TextView) rootView.findViewById(R.id.tvCloseClientsSearch);
        tvCloseClientsSearch.setOnClickListener(this);

        searchCriteriesListView = (ListView) rootView.findViewById(R.id.searchCriteriesListView);
        searchResultsListView = (ListView) rootView.findViewById(R.id.searchResultsListView);

        int id = svClients.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);

        int searchHintBtnId = svClients.getContext().getResources()
                .getIdentifier("android:id/search_mag_icon", null, null);
        int hintTextId = svClients.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        int closeBtnId = svClients.getContext()
                .getResources()
                .getIdentifier("android:id/search_close_btn", null, null);

        ivCloseSearch = (ImageView) svClients.findViewById(closeBtnId);
        tvSearchHint = (TextView) svClients.findViewById(hintTextId);

        ivMagnifier = (ImageView) svClients.findViewById(searchHintBtnId);

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

        svClients.setSubmitButtonEnabled(true);
        svClients.setOnQueryTextListener(this);

        String clientsSearchCriteriaDisplayed = mSettings.getString("clientsSearchCriteriaDisplayed", mapSearchCriteries.valueAt(0));

        tvSearchCriteriaSelected.setText(clientsSearchCriteriaDisplayed);

        List<String> mapSearchCriteriesDisplayed = new ArrayList<String>(mapSearchCriteries.values());

        ArrayAdapter searchCriteriaListAdapter =
                new ArrayAdapter(activity, R.layout.clients_search_criteries_list_row, mapSearchCriteriesDisplayed);
        //ClientsSearchCriteriaListAdapter searchCriteriaListAdapter = new ClientsSearchCriteriaListAdapter(getActivity(), R.layout.clients_search_criteries_list_row, searchCriteries);
        searchCriteriesListView.setAdapter(searchCriteriaListAdapter);

        searchCriteriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.setSelected(true);

                searchCriteria = mapSearchCriteries.keyAt(position);
                tvSearchCriteriaSelected.setText(mapSearchCriteries.valueAt(position));

                mSettings.edit().putString("clientsSearchCriteria", mapSearchCriteries.keyAt(position)).apply();
                mSettings.edit().putString("clientsSearchCriteriaDisplayed", mapSearchCriteries.valueAt(position)).apply();

                searchCriteriesListView.setVisibility(View.GONE);
                searchCriteriesCollapsed = true;
                ivCaretExpandCollapse.setImageResource(R.drawable.caret_symbol);
            }
        });

        searchCriteriesListView.setVisibility(View.GONE);

        if (lClientInfoItems.size() != 0)
        {
            fillClientsSearchResultsList();
        }
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
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.tvCloseClientsSearch)
        {
            mCommunicator.popFragmentsBackStack();
            return;
        }

        if (view.getId() == R.id.flSearchCriteriesExpandCollapse)
        {
            searchCriteriesListView.setVisibility( ! searchCriteriesCollapsed ? View.GONE : View.VISIBLE);

            ivCaretExpandCollapse.setImageResource( ! searchCriteriesCollapsed
                    ? R.drawable.caret_symbol : R.drawable.caret_upside_down);

            searchCriteriesCollapsed = ! searchCriteriesCollapsed;
            return;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String queryString)
    {
        //mSettings.edit().putString("clientsSearchLastQueryString", queryString).commit();

        //ViewUtils.disableSoftKeyboard(activity);

        String strQueryLower = queryString.toLowerCase();

        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialogRelogin("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                ClientsQueryParams clientsQueryParams = new ClientsQueryParams("true", "", "", "", "", searchCriteria, strQueryLower);

                NetworkUtils networkUtils = new NetworkUtils();

                requestServerDialog.show();

                callGetClientsList = networkUtils.getClientsSearchResults(this, API_GET_CLIENTS_URL, clientsQueryParams);
            }
        }

        return true;
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
                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        fillClientsSearchResultsList();
                        //tvSearchCriteriaSelected.requestFocus();
                        ViewUtils.disableSoftKeyboard(activity);
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

/*            JSONArray practiche = null;
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

            clientInfoItem.setPractiche_list(rl_ClientsPracticeInfoItems);*/

            lClientInfoItems.add(clientInfoItem);
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
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view.getId() == R.id.flLegendAndFilter)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        return false;
    }

    private void fillClientsSearchResultsList()
    {
        ClientsSearchResultsListAdapter searchResultsListAdapter =
                new ClientsSearchResultsListAdapter(activity, R.layout.clients_search_results_list_row, lClientInfoItems);
        searchResultsListView.setAdapter(searchResultsListAdapter);

        searchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                view.setSelected(true);
                mCommunicator.onClientSearchResultsListItemSelected(lClientInfoItems.get(position));
            }
        });
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

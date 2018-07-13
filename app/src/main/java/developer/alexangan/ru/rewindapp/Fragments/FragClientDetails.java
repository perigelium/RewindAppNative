package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import developer.alexangan.ru.rewindapp.Interfaces.ClientsCommunicator;
import developer.alexangan.ru.rewindapp.Models.ClientInfoItem;
import developer.alexangan.ru.rewindapp.Models.ClientPraticheInfoItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.MyTextUtils;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.API_GET_CLIENTS_DOCUMENTS_URL;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragClientDetails extends Fragment implements View.OnClickListener, View.OnTouchListener, Callback
{
    private ClientsCommunicator mCommunicator;
    Activity activity;
    private ClientInfoItem clientInfoItem;
    private TextView tvPartitaIva;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvMail;
    private TextView tvFax;
    private TextView tvMobilePhone;
    private TextView tvReferent;
    private TextView tvClientName;
    private LinearLayout llAltuofianco;
    private ImageView ivAltuofianco;
    private Call callGetClientDocuments;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;
    private TextView tvAttachmentsNumber;
    private ImageView ivAttachmentIcon;
    private String strPhone;
    private String strMobilePhone;
    private String strMail;
    private LinearLayout llPractices;
    private FrameLayout flReferentTitle;
    private LinearLayout llDocuments;
    private TextView tvPractices;
    private TextView tvClientAddMemo;
    private TextView tvReturnPageTitle;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (ClientsCommunicator) getActivity();

        if (getArguments() != null)
        {
            clientInfoItem = getArguments().getParcelable("clientInfoItem");
        }

        requestServerDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_client_details_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        tvClientAddMemo = (TextView) rootView.findViewById(R.id.tvClientAddMemo);
        tvClientAddMemo.setOnClickListener(this);

        tvReturnPageTitle = (TextView) rootView.findViewById(R.id.tvReturnPageTitle);

        flReferentTitle = (FrameLayout) rootView.findViewById(R.id.flReferentTitle);
        tvReferent = (TextView) rootView.findViewById(R.id.tvReferent);

        tvAttachmentsNumber = (TextView) rootView.findViewById(R.id.tvAttachmentsNumber);
        ivAttachmentIcon = (ImageView) rootView.findViewById(R.id.ivAttachmentIcon);
        tvClientName = (TextView) rootView.findViewById(R.id.tvClientName);

        tvPartitaIva = (TextView) rootView.findViewById(R.id.tvPartitaIva);
        tvAddress = (TextView) rootView.findViewById(R.id.tvAddress);
        tvMail = (TextView) rootView.findViewById(R.id.tvMail);
        tvMail.setOnClickListener(this);
        tvPhone = (TextView) rootView.findViewById(R.id.tvPhone);
        tvPhone.setOnClickListener(this);
        tvFax = (TextView) rootView.findViewById(R.id.tvFax);
        tvMobilePhone = (TextView) rootView.findViewById(R.id.tvMobilePhone);
        tvMobilePhone.setOnClickListener(this);

        llPractices = (LinearLayout) rootView.findViewById(R.id.llPractices);
        tvPractices = (TextView) rootView.findViewById(R.id.tvPractices);

        llAltuofianco = (LinearLayout) rootView.findViewById(R.id.llAltuofianco);

        llDocuments = (LinearLayout) rootView.findViewById(R.id.llDocuments);
        llDocuments.setOnClickListener(this);
        llDocuments.setOnTouchListener(this);

        ivAltuofianco = (ImageView) rootView.findViewById(R.id.ivAltuofianco);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        boolean searchMode = mSettings.getBoolean("SearchMode", false);

        if( ! searchMode)
        {
            tvReturnPageTitle.setText("Mappa");
        }

        String referent = clientInfoItem.getReferente();

        if(referent == null)
        {
            flReferentTitle.setVisibility(View.GONE);
        }
        else
        {
            tvReferent.setText(referent);
        }

        String client_name = clientInfoItem.getRagione_sociale();

        if(client_name != null)
        {
            client_name = MyTextUtils.toDisplayCase(client_name);
            tvClientName.setText(client_name);
        }

        tvPartitaIva.setText(clientInfoItem.getPartita_iva());

        String address = clientInfoItem.getAddress() + " " + clientInfoItem.getNumber() + "\n"
                + clientInfoItem.getZipcode() + " "
                + clientInfoItem.getLocation() + " "
                + "(" + clientInfoItem.getAcronym() + ")";
        
        tvAddress.setText(address);
        
        strMail = clientInfoItem.getMail();
        tvMail.setText(strMail);

        strPhone = clientInfoItem.getPhone();

        if (strPhone != null && strPhone.startsWith("9"))
        {
            strPhone = strPhone.substring(1);
        }
        tvPhone.setText(strPhone);

        tvFax.setText(clientInfoItem.getFax());

        strMobilePhone = clientInfoItem.getMobile_phone();

        if (strMobilePhone != null && strMobilePhone.startsWith("9"))
        {
            strMobilePhone = strMobilePhone.substring(1);
        }
        tvMobilePhone.setText(strMobilePhone);

        RealmList <ClientPraticheInfoItem> clientInfoPracticesList = clientInfoItem.getPractiche_list();

        if(clientInfoPracticesList == null || clientInfoPracticesList.size() == 0)
        {
            tvPractices.setTextColor(Color.parseColor("#ff808080"));
        }
        else
        {
            llPractices.setOnClickListener(this);
            llPractices.setOnTouchListener(this);
        }

        String atf_id_customer = clientInfoItem.getAtf_id_customer();
        String stato_rid = clientInfoItem.getStato_rid();

        if(atf_id_customer != null)
        {
            llAltuofianco.setOnTouchListener(this);
            llAltuofianco.setOnClickListener(this);

            if(stato_rid.equals("agganciato"))
            {
                ivAltuofianco.setImageResource(R.drawable.altuofianco_logo);
            }
            else
            {
                ivAltuofianco.setImageResource(R.drawable.altuofianco_inactive);
            }
        }
        else
        {
            ivAltuofianco.setImageResource(R.drawable.altuofianco_disabled);
        }

        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialogRelogin("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                NetworkUtils networkUtils = new NetworkUtils();

                requestServerDialog.show();

                callGetClientDocuments =
                        networkUtils.getDataForID(this, API_GET_CLIENTS_DOCUMENTS_URL, String.valueOf(clientInfoItem.getId_customer()));
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        mSettings.edit().putBoolean("mapFiltersChanged", false).apply();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.popFragmentsBackStack();
            return;
        }

        if (view.getId() == R.id.tvClientAddMemo)
        {
            mCommunicator.onClientAddMemoClicked(clientInfoItem.getId_customer(), clientInfoItem.getRagione_sociale());
            return;
        }

        if (view.getId() == R.id.tvPhone)
        {
            String phoneNumber = "tel:" + strPhone;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(phoneNumber));
            startActivity(intent);
        }

        if (view.getId() == R.id.tvMobilePhone)
        {
            String phoneNumber = "tel:" + strMobilePhone;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(phoneNumber));
            startActivity(intent);
        }

        if (view.getId() == R.id.tvMail)
        {
            String [] recipients = new String[1];
            recipients[0] = strMail;

            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, recipients);
            i.putExtra(Intent.EXTRA_SUBJECT, "Oggetto della posta");
            i.putExtra(Intent.EXTRA_TEXT, "Corpo di posta");

            try
            {
                startActivity(Intent.createChooser(i, "Invia mail..."));
            } catch (android.content.ActivityNotFoundException ex)
            {
                ViewUtils.showToastMessage(activity, getString(R.string.NoMailClientInstalled));
            }
        }

        if (view.getId() == R.id.llDocuments)
        {
            mCommunicator.onClientDocsListSelected(clientInfoItem.getId_customer());
            return;
        }

        if (view.getId() == R.id.llPractices)
        {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            List<ClientPraticheInfoItem> clientPraticheInfoItems = new ArrayList<>();
            clientPraticheInfoItems.addAll(clientInfoItem.getPractiche_list());
            realm.commitTransaction();
            realm.close();

            mCommunicator.onClientPracticesSelected(clientPraticheInfoItems);
            return;
        }

        if (view.getId() == R.id.llAltuofianco)
        {
            mCommunicator.onClientAltuofiancoDetailsSelected(clientInfoItem);
            return;
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
    public void onFailure(Call call, IOException e)
    {
        if (call == callGetClientDocuments)
        {
            requestServerDialog.dismiss();

            ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException
    {
        if (call == callGetClientDocuments)
        {
            final String clientsInfoJSON = response.body().string();

            response.body().close();

            requestServerDialog.dismiss();

            JSONArray clientsInfoJsonArray = null;

            try
            {
                clientsInfoJsonArray = new JSONArray(clientsInfoJSON);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            if(clientsInfoJsonArray != null)
            {
                ivAttachmentIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view.getId() == R.id.llDocuments || view.getId() == R.id.llPractices || view.getId() == R.id.llAltuofianco)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        return false;
    }
}

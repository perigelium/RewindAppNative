package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import developer.alexangan.ru.rewindapp.Adapters.InvitationInvoiceAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.BonusAndGareCommunicator;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.InvoiceItem;
import developer.alexangan.ru.rewindapp.Models.ItalianMonths;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.MyTextUtils;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.API_POST_INVOICE_CHECKED_URL;

public class FragInvoiceInvitation extends ListFragment implements View.OnClickListener, Callback
{
    private BonusAndGareCommunicator mCommunicator;
    private Activity activity;
/*    private Handler handler;
    private Runnable runnable;*/
    private Call callConfirmInvoiceInvitation;
    //private ProgressDialog requestServerDialog;
    AlertDialog alert;
    private ProgressDialog downloadingDialog;
    private String dateYYYYMM;
    private TreeMap<String, List<Pair<String, String>>> incomesOrExpenses;
    private LinearLayout llReturn;
    private TextView tvMonthAndYear;
    private String total;
    private TextView tvInvoiceTotal;
    private Button btnInvoiceConfirmed;
    private Button btnConfirmInvoice;
    private boolean confirmed;
    private String IDs;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (BonusAndGareCommunicator) getActivity();

        if (getArguments() != null)
        {
            dateYYYYMM = getArguments().getString("dateYYYYMM");
            total = getArguments().getString("total");
            confirmed = getArguments().getBoolean("confirmed");
        }

/*        handler = new Handler();

        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));
                //requestServerDialog.dismiss();
            }
        };*/

        downloadingDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        downloadingDialog.setTitle("");
        downloadingDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        downloadingDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_invitation_invoice_layout, container, false);

        llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);

        btnConfirmInvoice = (Button) rootView.findViewById(R.id.btnConfirmInvoice);
        btnInvoiceConfirmed = (Button) rootView.findViewById(R.id.btnInvoiceConfirmed);

        tvMonthAndYear = (TextView) rootView.findViewById(R.id.tvMonthAndYear);

        tvInvoiceTotal = (TextView) rootView.findViewById(R.id.tvInvoiceTotal);

        if (confirmed)
        {
            btnConfirmInvoice.setVisibility(View.GONE);
        } else
        {
            btnInvoiceConfirmed.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //ViewUtils.showToastMessage(activity, "This section is under construction");

/*        requestServerDialog = new ProgressDialog(activity);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);*/

        llReturn.setOnClickListener(this);

        btnConfirmInvoice.setOnClickListener(this);

        int monthNumber = Integer.valueOf(dateYYYYMM.substring(4)) - 1;

        if (monthNumber == -1)
        {
            monthNumber = 11;
        }

        String periodDisplayed = "Fattura\n" + ItalianMonths.numToLongString(monthNumber) + " " + dateYYYYMM.substring(0, 4);

        tvMonthAndYear.setText(periodDisplayed);

        total = MyTextUtils.reformatCurrencyString(total);

        tvInvoiceTotal.setText(total);

        Realm realm = Realm.getDefaultInstance();

        RealmResults<InvoiceItem> groups = realm.where(InvoiceItem.class).equalTo("period", dateYYYYMM).distinct("group");

        incomesOrExpenses = new TreeMap<>();

        IDs = "";

        for (int i = 0; i < groups.size(); i++)
        {
            RealmResults<InvoiceItem> itemsInGroup = realm.where(InvoiceItem.class).equalTo("period", dateYYYYMM).equalTo("group", groups.get(i).getGroup()).findAll();

            List<Pair<String, String>> namesAndTotals = new ArrayList<>();

            for (int j = 0; j < itemsInGroup.size(); j++)
            {
                Pair<String, String> pair = new Pair<>(itemsInGroup.get(j).getName(), itemsInGroup.get(j).getTotal());

                namesAndTotals.add(pair);
            }

            incomesOrExpenses.put(groups.get(i).getGroup(), namesAndTotals);

            if ( ! confirmed)
            {
                IDs += "," + String.valueOf(groups.get(i).getId());
            }
        }

        if ( ! confirmed)
        {
            IDs = IDs.substring(1);
        }

        realm.close();

        showInvitationInvoiceList();
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
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.popFragmentBackStack();
            return;
        }

        if (view.getId() == R.id.btnConfirmInvoice)
        {
            if (NetworkUtils.isNetworkAvailable(activity))
            {
                if (GlobalConstants.tokenStr == null)
                {
                    alertDialog("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
                } else
                {
                    downloadingDialog.show();

                    NetworkUtils networkUtils = new NetworkUtils();
                    callConfirmInvoiceInvitation = 
                            networkUtils.sendInvoiceConfirmation(this, API_POST_INVOICE_CHECKED_URL, IDs);
                }
            }

            return;
        }
    }

    @Override
    public void onFailure(Call call, IOException e)
    {
        if (call == callConfirmInvoiceInvitation)
        {
            ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

            activity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    downloadingDialog.dismiss();
                }
            });

            //handler.removeCallbacks(runnable);

            ViewUtils.showToastMessage(activity, getString(R.string.InvitationConfirmationFailed));
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException
    {
        if (call == callConfirmInvoiceInvitation)
        {
            //handler.removeCallbacks(runnable);

            final String serverAnswer = response.body().string();

            response.body().close();

            if (serverAnswer.equals("true"))
            {
                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        downloadingDialog.dismiss();

                        btnConfirmInvoice.setVisibility(View.GONE);
                        btnInvoiceConfirmed.setVisibility(View.VISIBLE);
                    }
                });

            }
        }
    }

    private void showInvitationInvoiceList()
    {
        InvitationInvoiceAdapter invitationInvoiceAdapter = new InvitationInvoiceAdapter(getActivity(), R.layout.invitation_invoice_row, incomesOrExpenses);
        setListAdapter(invitationInvoiceAdapter);
    }

    private void alertDialog(String title, String message)
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
}

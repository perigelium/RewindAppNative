package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import developer.alexangan.ru.rewindapp.Adapters.InvoiceListAdapter;
import developer.alexangan.ru.rewindapp.Interfaces.BonusAndGareCommunicator;
import developer.alexangan.ru.rewindapp.Interfaces.RetrofitAPI;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.InvoiceItem;
import developer.alexangan.ru.rewindapp.Models.InvoiceTotalItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.OnSwipeTouchListener;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import developer.alexangan.ru.rewindapp.ViewOverrides.YearPickerDialog;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

public class FragInvoicesList extends ListFragment implements View.OnClickListener, Callback<List<InvoiceItem>>
{
    private BonusAndGareCommunicator mCommunicator;
    private Activity activity;
    private Call callGetInvoicesList;
    AlertDialog alert;
    List<InvoiceItem> l_invoiceItems;
    private ProgressDialog downloadingDialog;

    private int mYear;
    private String dateYYYY;
    private TextView tvYearSelected;
    private FrameLayout flYearSlider;
    private YearPickerDialog pd;
    private boolean onInitialDownload;
    private ArrayList<InvoiceTotalItem> totals;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (BonusAndGareCommunicator) getActivity();

        downloadingDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        downloadingDialog.setTitle("");
        downloadingDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        downloadingDialog.setIndeterminate(true);

        onInitialDownload = true;
        totals = new ArrayList<InvoiceTotalItem>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_invoices_list, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        FrameLayout flListHeader = (FrameLayout) rootView.findViewById(R.id.flListHeader);
        flListHeader.setOnClickListener(this);

        flYearSlider = (FrameLayout) rootView.findViewById(R.id.flYearSlider);

        FrameLayout flPrevDate = (FrameLayout) rootView.findViewById(R.id.flPrevDate);
        flPrevDate.setOnClickListener(this);
        FrameLayout flNextDate = (FrameLayout) rootView.findViewById(R.id.flNextDate);
        flNextDate.setOnClickListener(this);

        tvYearSelected = (TextView) rootView.findViewById(R.id.tvYearSelected);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendarNow = Calendar.getInstance(); // now
        mYear = calendarNow.get(Calendar.YEAR);

        dateYYYY = String.valueOf(mYear);

        tvYearSelected.setText(dateYYYY);
        tvYearSelected.setOnClickListener(this);

        flYearSlider.setOnTouchListener(new OnSwipeTouchListener(activity)
        {
            @Override
            public void onSwipeLeft()
            {
                mYear++;

                dateYYYY = String.valueOf(mYear);
                tvYearSelected.setText(dateYYYY);

                downloadInvoicesList();
            }

            @Override
            public void onSwipeRight()
            {
                mYear--;

                dateYYYY = String.valueOf(mYear);
                tvYearSelected.setText(dateYYYY);

                downloadInvoicesList();
            }
        });

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                mYear = year;

                dateYYYY = String.valueOf(mYear);
                tvYearSelected.setText(dateYYYY);

                downloadInvoicesList();
            }
        };

        pd = new YearPickerDialog();
        pd.setListener(listener);

        if (onInitialDownload)
        {
            downloadInvoicesList();
        } else
        {
            updateInvoicesListView();
        }
    }

    private void downloadInvoicesList()
    {
        if (NetworkUtils.isNetworkAvailable(activity))
        {
            if (tokenStr == null)
            {
                alertDialog("Info", getString(R.string.OfflineModeShowLoginScreenQuestion));
            } else
            {
                totals.clear();
                updateInvoicesListView();

                downloadingDialog.show();

                final String dateYYYYMM = makeStringOf12YYYYMM(String.valueOf(mYear));

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        RetrofitAPI retrofitService = RetrofitAPI.retrofit.create(RetrofitAPI.class);

                        callGetInvoicesList =
                                retrofitService.getInvoicesList(GlobalConstants.tokenStr, dateYYYYMM);

                        callGetInvoicesList.enqueue(FragInvoicesList.this);
                    }
                }, 100);
            }
        } else
        {
            ViewUtils.showToastMessage(activity, getString(R.string.CheckInternetConnection));
        }
    }

    private String makeStringOf12YYYYMM(String strYear)
    {
        String str12YYYYMM = "";

        for (int i = 1; i < 13; i++)
        {
            String strI = String.valueOf(i);

            if (strI.length() == 1)
            {
                strI = "0" + strI;
            }

            str12YYYYMM += strYear + strI + ",";
        }

        str12YYYYMM = str12YYYYMM.substring(0, str12YYYYMM.length() - 1);
        return str12YYYYMM;
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
            mCommunicator.onClose();
        }

        if (view.getId() == R.id.flListHeader)
        {
            mCommunicator.popFragmentBackStack();
        }

        if (view.getId() == R.id.flPrevDate)
        {
            mYear--;

            dateYYYY = String.valueOf(mYear);
            tvYearSelected.setText(dateYYYY);

            downloadInvoicesList();
        }

        if (view.getId() == R.id.flNextDate)
        {
            mYear++;

            dateYYYY = String.valueOf(mYear);
            tvYearSelected.setText(dateYYYY);

            downloadInvoicesList();
        }

        if (view.getId() == R.id.tvYearSelected)
        {
            Bundle bundle = new Bundle();
            bundle.putInt("year", mYear);
            pd.setArguments(bundle);
            pd.show(getFragmentManager(), "YearPickerDialog");
        }
    }

    @Override
    public void onResponse(@NonNull Call<List<InvoiceItem>> callGetInvoicesList, @NonNull Response<List<InvoiceItem>> response)
    {
        downloadingDialog.dismiss();

        if (response.isSuccessful())
        {
            l_invoiceItems = response.body();

            if (l_invoiceItems != null && l_invoiceItems.size() != 0)
            {
                onInitialDownload = false;

                updateInvoiceItems(l_invoiceItems);

                fillTotals();

                updateInvoicesListView();

            } else
            {
                ViewUtils.showToastMessage(activity, getString(R.string.NoDataForThisYear));

                totals.clear();
            }
        } else
        {
            ViewUtils.showToastMessage(activity, getString(R.string.ServerError));

            //int statusCode = response.code();
            ResponseBody errorBody = response.errorBody();

            try
            {
                Log.d("DEBUG", errorBody.string());

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void fillTotals()
    {
        for (int i = 1; i < 13; i++)
        {
            String strI = String.valueOf(i);

            if (strI.length() == 1)
            {
                strI = "0" + strI;
            }

            String dateYYYYMM = String.valueOf(mYear) + strI;

            Realm realm = Realm.getDefaultInstance();

            realm.beginTransaction();
            RealmResults<InvoiceItem> rl_monthlyInvoiceItems = realm.where(InvoiceItem.class).equalTo("period", dateYYYYMM).findAll();
            realm.commitTransaction();
            realm.close();

            if (rl_monthlyInvoiceItems.size() != 0)
            {
                float totalMonthly = 0;

                for (InvoiceItem invoiceItemMonthly : rl_monthlyInvoiceItems)
                {
                    totalMonthly += Float.valueOf(invoiceItemMonthly.getTotal());
                }

                // if first item has checked, assumed that all items have checked
                totals.add(0, new InvoiceTotalItem(dateYYYYMM, totalMonthly, rl_monthlyInvoiceItems.get(0).getChecked()));
            }
        }
    }

    @Override
    public void onFailure(@NonNull Call<List<InvoiceItem>> call, @NonNull Throwable t)
    {
        ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

        downloadingDialog.dismiss();

        if (onInitialDownload)
        {
            mCommunicator.popFragmentBackStack();
        }
    }

    private void updateInvoiceItems(List<InvoiceItem> l_invoiceItems)
    {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<InvoiceItem> rl_invoiceItems = realm.where(InvoiceItem.class).findAll();
        realm.commitTransaction();

        realm.beginTransaction();
        rl_invoiceItems.deleteAllFromRealm();
        realm.commitTransaction();

        realm.beginTransaction();
        for (InvoiceItem invoiceItem : l_invoiceItems)
        {
            realm.copyToRealm(invoiceItem);
        }
        realm.commitTransaction();
    }

    private void updateInvoicesListView()
    {
        InvoiceListAdapter invoiceListAdapter = new InvoiceListAdapter(getActivity(), R.layout.invoices_list_row, totals);
        setListAdapter(invoiceListAdapter);

        if(totals.size() == 0)
        {
            return;
        }

        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mCommunicator.onInvoicesListItemSelected(totals.get(position).getPeriod(), String.valueOf(totals.get(position).getTotal()), totals.get(position).getChecked() != null);
            }
        });
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

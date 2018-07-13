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
import android.widget.LinearLayout;
import android.widget.TextView;

import developer.alexangan.ru.rewindapp.Interfaces.AppointmentsCommunicator;
import developer.alexangan.ru.rewindapp.Models.AppointmentInfoItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class FragAppointmentDetails extends Fragment implements View.OnClickListener, View.OnTouchListener
{
    private AppointmentsCommunicator mCommunicator;
    Activity activity;
    private AppointmentInfoItem appointmentInfoItem;
    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvMail;
    private TextView tvFax;
    private TextView tvMobilePhone;
    private TextView tvReferent;
    private TextView tvAppointmentName;
    private ProgressDialog requestServerDialog;
    AlertDialog alert;
    private String strPhone;
    private String strMobilePhone;
    private String strMail;
    private FrameLayout flReferentTitle;
    private TextView tvAppointmentAddMemo;
    private TextView tvReturnPageTitle;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (AppointmentsCommunicator) getActivity();

        if (getArguments() != null)
        {
            appointmentInfoItem = getArguments().getParcelable("appointmentInfoItem");
        }

        requestServerDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        requestServerDialog.setTitle("");
        requestServerDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        requestServerDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_appointment_details_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        tvAppointmentAddMemo = (TextView) rootView.findViewById(R.id.tvAppointmentAddMemo);
        tvAppointmentAddMemo.setOnClickListener(this);

        tvReturnPageTitle = (TextView) rootView.findViewById(R.id.tvReturnPageTitle);

        flReferentTitle = (FrameLayout) rootView.findViewById(R.id.flReferentTitle);
        tvReferent = (TextView) rootView.findViewById(R.id.tvReferent);
        
        tvAppointmentName = (TextView) rootView.findViewById(R.id.tvAppointmentName);

        tvAddress = (TextView) rootView.findViewById(R.id.tvAddress);
        tvMail = (TextView) rootView.findViewById(R.id.tvMail);
        tvMail.setOnClickListener(this);
        tvPhone = (TextView) rootView.findViewById(R.id.tvPhone);
        tvPhone.setOnClickListener(this);
        tvFax = (TextView) rootView.findViewById(R.id.tvFax);
        tvMobilePhone = (TextView) rootView.findViewById(R.id.tvMobilePhone);
        tvMobilePhone.setOnClickListener(this);

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

/*        String referent = appointmentInfoItem.getRepresentatnteLegale();

        if(referent == null)
        {
            flReferentTitle.setVisibility(View.GONE);
        }
        else
        {
            tvReferent.setText(referent);
        }

        String appointment_name = appointmentInfoItem.getRegSociale();

        if(appointment_name != null)
        {
            appointment_name = MyTextUtils.toDisplayCase(appointment_name);
            tvAppointmentName.setText(appointment_name);
        }

        String address = appointmentInfoItem.getAddress() + " " + appointmentInfoItem.getNumber() + "\n"
                + appointmentInfoItem.getZipcode() + " "
                + appointmentInfoItem.getLocation() + " "
                + "(" + appointmentInfoItem.getAcronym() + ")";
        
        tvAddress.setText(address);
        
        strMail = appointmentInfoItem.getMail();
        tvMail.setText(strMail);

        strPhone = appointmentInfoItem.getPhone();

        if (strPhone != null && strPhone.startsWith("9"))
        {
            strPhone = strPhone.substring(1);
        }
        tvPhone.setText(strPhone);

        tvFax.setText(appointmentInfoItem.getFax());

        strMobilePhone = appointmentInfoItem.getMobile_phone();

        if (strMobilePhone != null && strMobilePhone.startsWith("9"))
        {
            strMobilePhone = strMobilePhone.substring(1);
        }
        tvMobilePhone.setText(strMobilePhone);*/
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

        if (view.getId() == R.id.tvAppointmentAddMemo)
        {
            mCommunicator.onAppointmentAddMemoClicked(appointmentInfoItem.getId_customer(), FragAppointmentDetails.this);
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
        if (view.getId() == R.id.llDocuments || view.getId() == R.id.llPractices || view.getId() == R.id.llAltuofianco)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        return false;
    }
}

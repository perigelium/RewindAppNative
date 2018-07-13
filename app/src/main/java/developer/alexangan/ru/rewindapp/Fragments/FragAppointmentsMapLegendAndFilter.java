package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import developer.alexangan.ru.rewindapp.Interfaces.AppointmentsCommunicator;
import developer.alexangan.ru.rewindapp.R;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class FragAppointmentsMapLegendAndFilter extends Fragment implements View.OnClickListener
{

    private AppointmentsCommunicator mCommunicator;
    Activity activity;
    private boolean gpsIsActive;
    private CheckBox chkClientiConvergenti;
    private CheckBox chkClientiNonConvergenti;
    private CheckBox chkClientiDeact;
    private CheckBox chkClientiInBorsellino;
    private TextView tvSelectMapFiltersAllNone;
    private boolean checkAll;
    private TextView tvLegendAndFilterTitle;
    private CheckBox chkAppointmentsToday, chkAppointmentsNonEsitate, chkAppointmentsDeal, chkAppointmentsComing, chkAppointmentsPast;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (AppointmentsCommunicator) getActivity();

        if (getArguments() != null)
        {
            gpsIsActive = getArguments().getBoolean("gpsIsActive");
        }

        checkAll = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_appointments_map_legend_and_filter_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        tvLegendAndFilterTitle = (TextView) rootView.findViewById(R.id.tvLegendAndFilterTitle);

        tvSelectMapFiltersAllNone = (TextView) rootView.findViewById(R.id.tvSelectMapFiltersAllNone);
        tvSelectMapFiltersAllNone.setOnClickListener(this);

        chkAppointmentsToday = (CheckBox) rootView.findViewById(R.id.chkAppointmentToday);
        chkAppointmentsNonEsitate = (CheckBox) rootView.findViewById(R.id.chkAppointmentNonEsitate);
        chkAppointmentsDeal = (CheckBox) rootView.findViewById(R.id.chkAppointmentDeal);
        chkAppointmentsComing = (CheckBox) rootView.findViewById(R.id.chkAppointmentComing);
        chkAppointmentsPast = (CheckBox) rootView.findViewById(R.id.chkAppointmentPast);

        chkClientiConvergenti = (CheckBox) rootView.findViewById(R.id.chkClientiConvergenti);
        chkClientiNonConvergenti = (CheckBox) rootView.findViewById(R.id.chkClientiNonConvergenti);
        chkClientiDeact = (CheckBox) rootView.findViewById(R.id.chkClientiDeact);
        chkClientiInBorsellino = (CheckBox) rootView.findViewById(R.id.chkClientiInBorsellino);

        Button btnApplyMapFilter = (Button) rootView.findViewById(R.id.btnApplyMapFilter);
        btnApplyMapFilter.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (gpsIsActive)
        {
            tvLegendAndFilterTitle.setText(R.string.LegendAndFilterNoCaps);

            chkAppointmentsToday.setVisibility(View.VISIBLE);
            chkAppointmentsNonEsitate.setVisibility(View.VISIBLE);
            chkAppointmentsDeal.setVisibility(View.VISIBLE);
            chkAppointmentsComing.setVisibility(View.VISIBLE);
            chkAppointmentsPast.setVisibility(View.VISIBLE);

            chkClientiConvergenti.setVisibility(View.VISIBLE);
            chkClientiNonConvergenti.setVisibility(View.VISIBLE);
            chkClientiDeact.setVisibility(View.VISIBLE);
            chkClientiInBorsellino.setVisibility(View.VISIBLE);
        }

        if (mSettings.getBoolean("AppointmentsTodayDisabled", false))
        {
            chkAppointmentsToday.setChecked(false);
        }

        if (mSettings.getBoolean("AppointmentsNonEsitateDisabled", false))
        {
            chkAppointmentsNonEsitate.setChecked(false);
        }

        if (mSettings.getBoolean("AppointmentsDeal", false))
        {
            chkAppointmentsDeal.setChecked(false);
        }

        if (mSettings.getBoolean("AppointmentsComing", false))
        {
            chkAppointmentsComing.setChecked(false);
        }

        if (mSettings.getBoolean("AppointmentsPast", false))
        {
            chkAppointmentsPast.setChecked(false);
        }

        if (mSettings.getBoolean("clientiConvergentiDisabled", false))
        {
            chkClientiConvergenti.setChecked(false);
        }

        if (mSettings.getBoolean("clientiNonConvergentiDisabled", false))
        {
            chkClientiNonConvergenti.setChecked(false);
        }

        if (mSettings.getBoolean("clientiDeactDisabled", false))
        {
            chkClientiDeact.setChecked(false);
        }

        if (mSettings.getBoolean("clientiInBorsellinoDisabled", false))
        {
            chkClientiInBorsellino.setChecked(false);
        }

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

        if (view.getId() == R.id.tvSelectMapFiltersAllNone)
        {
            checkAll = !checkAll;

            if ( ! checkAll)
            {
                tvSelectMapFiltersAllNone.setText("Tutti");
            } else
            {
                tvSelectMapFiltersAllNone.setText("Nessuno");
            }

            chkAppointmentsToday.setChecked(checkAll);
            chkAppointmentsNonEsitate.setChecked(checkAll);
            chkAppointmentsDeal.setChecked(checkAll);
            chkAppointmentsComing.setChecked(checkAll);
            chkAppointmentsPast.setChecked(checkAll);


            chkClientiConvergenti.setChecked(checkAll);
            chkClientiNonConvergenti.setChecked(checkAll);
            chkClientiDeact.setChecked(checkAll);
            chkClientiInBorsellino.setChecked(checkAll);

            return;
        }

        if (view.getId() == R.id.btnApplyMapFilter)
        {
            mSettings.edit().putBoolean("AppointmentsTodayDisabled", !chkAppointmentsToday.isChecked()).commit();
            mSettings.edit().putBoolean("AppointmentsNonEsitateDisabled", !chkAppointmentsNonEsitate.isChecked()).commit();
            mSettings.edit().putBoolean("AppointmentsDealDisabled", !chkAppointmentsDeal.isChecked()).commit();
            mSettings.edit().putBoolean("AppointmentsComingDisabled", !chkAppointmentsComing.isChecked()).commit();
            mSettings.edit().putBoolean("AppointmentsPastDisabled", !chkAppointmentsPast.isChecked()).commit();

            mSettings.edit().putBoolean("clientiConvergentiDisabled", !chkClientiConvergenti.isChecked()).commit();
            mSettings.edit().putBoolean("clientiNonConvergentiDisabled", !chkClientiNonConvergenti.isChecked()).commit();
            mSettings.edit().putBoolean("clientiDeactDisabled", !chkClientiDeact.isChecked()).commit();
            mSettings.edit().putBoolean("clientiInBorsellinoDisabled", !chkClientiInBorsellino.isChecked()).commit();

            mSettings.edit().putBoolean("mapFiltersChanged", true).commit();

            mCommunicator.popFragmentsBackStack();
            return;
        }
    }
}

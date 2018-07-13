package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import developer.alexangan.ru.rewindapp.Interfaces.BonusAndGareCommunicator;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;

public class FragBonusAndGareMainMenu extends Fragment implements View.OnClickListener, View.OnTouchListener
{

    private BonusAndGareCommunicator mCommunicator;
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (BonusAndGareCommunicator)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_bonus_and_gare_main_menu, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        FrameLayout flBonus = (FrameLayout) rootView.findViewById(R.id.flBonus);
        FrameLayout flCallToInvoice = (FrameLayout) rootView.findViewById(R.id.flCallToInvoice);
        FrameLayout flRaceRank = (FrameLayout) rootView.findViewById(R.id.flRaceRank);

        flBonus.setOnClickListener(this);
        flCallToInvoice.setOnClickListener(this);
        flRaceRank.setOnClickListener(this);

        flBonus.setOnTouchListener(this);
        flCallToInvoice.setOnTouchListener(this);
        flRaceRank.setOnTouchListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.onClose();
            return;
        }

        if( ! NetworkUtils.isNetworkAvailable(activity))
        {
            ViewUtils.showToastMessage(activity, getString(R.string.CheckInternetConnection));
            return;
        }

        if (view.getId() == R.id.flBonus)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            mCommunicator.onBonusListSelected();
            return;
        }

        if (view.getId() == R.id.flCallToInvoice)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            mCommunicator.onInvoicesListSelected();
            return;
        }

        if (view.getId() == R.id.flRaceRank)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            mCommunicator.onRaceRankListSelected();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view.getId() == R.id.flBonus)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        if (view.getId() == R.id.flCallToInvoice)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        if (view.getId() == R.id.flRaceRank)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        return false;
    }
}

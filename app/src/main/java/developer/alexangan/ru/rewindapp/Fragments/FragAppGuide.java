package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import developer.alexangan.ru.rewindapp.Interfaces.LoginCommunicator;
import developer.alexangan.ru.rewindapp.R;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;


public class FragAppGuide extends Fragment implements View.OnClickListener
{

    private LoginCommunicator mCommunicator;
    Activity activity;
    private List<Integer> pageIds;
    private int curPagePos;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (LoginCommunicator)getActivity();

        curPagePos = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.app_guide_fragment, container, false);

        Button btnGuideNext = (Button) rootView.findViewById(R.id.btnGuideNext);
        btnGuideNext.setOnClickListener(this);

        Button btnGuideSkip = (Button) rootView.findViewById(R.id.btnGuideSkip);
        btnGuideSkip.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
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

        curPagePos = mSettings.getInt("curPagePos", 0);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mSettings.edit().putInt("curPagePos", curPagePos).apply();
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == R.id.btnGuideNext)
        {

        }

        if(view.getId() == R.id.btnGuideSkip)
        {
            mCommunicator.onGuideSkipped();
        }
    }
}

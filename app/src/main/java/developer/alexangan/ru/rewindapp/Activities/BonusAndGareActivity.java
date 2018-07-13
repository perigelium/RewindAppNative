package developer.alexangan.ru.rewindapp.Activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import developer.alexangan.ru.rewindapp.Fragments.FragBonusAndGareMainMenu;
import developer.alexangan.ru.rewindapp.Fragments.FragBonusList;
import developer.alexangan.ru.rewindapp.Fragments.FragInvoiceInvitation;
import developer.alexangan.ru.rewindapp.Fragments.FragInvoicesList;
import developer.alexangan.ru.rewindapp.Fragments.FragRaceRankDetailsList;
import developer.alexangan.ru.rewindapp.Fragments.FragRaceRankList;
import developer.alexangan.ru.rewindapp.Interfaces.BonusAndGareCommunicator;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BonusAndGareActivity extends Activity implements BonusAndGareCommunicator
{
    private FragmentManager mFragmentManager;
    FragBonusAndGareMainMenu fragBonusAndGareMainMenu;
    FragBonusList fragBonusList;
    FragInvoicesList fragInvoicesList;
    FragInvoiceInvitation fragInvoiceInvitation;
    FragRaceRankList fragRaceRanklist;
    FragRaceRankDetailsList fragRaceRankDetailslist;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bonus_and_gare_layout);

        fragBonusAndGareMainMenu = new FragBonusAndGareMainMenu();
        fragBonusList = new FragBonusList();
        fragInvoicesList = new FragInvoicesList();
        fragInvoiceInvitation = new FragInvoiceInvitation();
        fragRaceRanklist = new FragRaceRankList();
        fragRaceRankDetailslist = new FragRaceRankDetailsList();

        mFragmentManager = getFragmentManager();

        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        mFragmentTransaction.add(R.id.bonusAndGareFragContainer, fragBonusAndGareMainMenu);

        mFragmentTransaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        if (mFragmentManager.getBackStackEntryCount() == 0)
        {
            this.finish();
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onClose()
    {
        this.finish();
    }

    @Override
    public void onBonusListSelected()
    {
        if (!fragBonusList.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            mFragmentTransaction.remove(fragBonusAndGareMainMenu);
            mFragmentTransaction.add(R.id.bonusAndGareFragContainer, fragBonusList);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onInvoicesListSelected()
    {
        if (!fragInvoicesList.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            mFragmentTransaction.remove(fragBonusAndGareMainMenu);
            mFragmentTransaction.add(R.id.bonusAndGareFragContainer, fragInvoicesList);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onInvoicesListItemSelected(String dateYYYYMM, String total, boolean confirmed)
    {
        if (!fragInvoiceInvitation.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            mFragmentTransaction.remove(fragInvoicesList);

            Bundle args = fragInvoiceInvitation.getArguments() != null ? fragInvoiceInvitation.getArguments() : new Bundle();
            args.putString("dateYYYYMM", dateYYYYMM);
            args.putString("total", total);
            args.putBoolean("confirmed", confirmed);

            fragInvoiceInvitation.setArguments(args);

            mFragmentTransaction.add(R.id.bonusAndGareFragContainer, fragInvoiceInvitation);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onRaceRankListSelected()
    {
        if (!fragRaceRanklist.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            mFragmentTransaction.remove(fragBonusAndGareMainMenu);
            mFragmentTransaction.add(R.id.bonusAndGareFragContainer, fragRaceRanklist);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void onRaceRankListItemSelected(int position, String dateYYYYMM)
    {
        if (!fragRaceRankDetailslist.isAdded())
        {
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            Bundle args = fragRaceRankDetailslist.getArguments() != null ? fragRaceRankDetailslist.getArguments() : new Bundle();
            args.putString("dateYYYYMM", dateYYYYMM);
            fragRaceRankDetailslist.setArguments(args);

            mFragmentTransaction.remove(fragRaceRanklist);
            mFragmentTransaction.add(R.id.bonusAndGareFragContainer, fragRaceRankDetailslist);
            mFragmentTransaction.addToBackStack(null);

            mFragmentTransaction.commit();
        }
    }

    @Override
    public void popFragmentBackStack()
    {
        mFragmentManager.popBackStack();
    }

    @Override
    public void onLogoutCommand()
    {
        GlobalConstants.logoutInProgress = true;
        this.finish();
    }

 /*   @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            this.finish();
        }

        if (view.getId() == R.id.flBonus)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            ViewUtils.showToastMessage(this, "flBonus");
        }

        if (view.getId() == R.id.flCallToInvoice)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            ViewUtils.showToastMessage(this, "flCallToInvoice");
        }

        if (view.getId() == R.id.flRaceRank)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            ViewUtils.showToastMessage(this, "flRaceRank");
        }
    }*/

/*    @Override
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
    }*/
}

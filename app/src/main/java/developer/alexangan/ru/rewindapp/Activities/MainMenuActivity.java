package developer.alexangan.ru.rewindapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.LoginCredentials;
import developer.alexangan.ru.rewindapp.Models.ProfileInfoItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.ImageUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class MainMenuActivity extends Activity implements View.OnClickListener, View.OnTouchListener
{
    private ProfileInfoItem profileInfoItem;

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        String pathToAvatar = mSettings.getString("pathToAgentAvatar", "");

        ImageView ivAgentAvatar = (ImageView) findViewById(R.id.ivAgentAvatar);
        Bitmap bm = ImageUtils.decodeSampledBitmapFromUri(pathToAvatar, 75, 75);
        if (bm != null)
        {
            ivAgentAvatar.setImageBitmap(bm);
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ProfileInfoItem profileInfoItem = realm.where(ProfileInfoItem.class).findFirst();

        TextView tvAgentName = (TextView) findViewById(R.id.tvAgentName);

        if (profileInfoItem != null)
        {
            realm.copyFromRealm(profileInfoItem);
            this.profileInfoItem = profileInfoItem;

            String agentName = this.profileInfoItem.getName() + " " + this.profileInfoItem.getSurname();
            agentName = agentName.toUpperCase();
            tvAgentName.setText(agentName);
        }
        realm.commitTransaction();

        FrameLayout flAgentProfile = (FrameLayout) findViewById(R.id.flAgentProfile);

        FrameLayout flAppointments = (FrameLayout) findViewById(R.id.flAppointments);
        FrameLayout flClients = (FrameLayout) findViewById(R.id.flClients);
        FrameLayout flNews = (FrameLayout) findViewById(R.id.flNews);
        FrameLayout flBonusEGare = (FrameLayout) findViewById(R.id.flBonusEGare);
        FrameLayout flMessages = (FrameLayout) findViewById(R.id.flMessages);

        flAgentProfile.setOnClickListener(this);
        flAppointments.setOnClickListener(this);
        flClients.setOnClickListener(this);
        flNews.setOnClickListener(this);
        flBonusEGare.setOnClickListener(this);
        flMessages.setOnClickListener(this);

        flAppointments.setOnTouchListener(this);
        flClients.setOnTouchListener(this);
        flNews.setOnTouchListener(this);
        flBonusEGare.setOnTouchListener(this);
        flMessages.setOnTouchListener(this);

        realm.close();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (GlobalConstants.logoutInProgress)
        {
            GlobalConstants.logoutInProgress = false;

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<ProfileInfoItem> profileInfoItems = realm.where(ProfileInfoItem.class).findAll();
            profileInfoItems.deleteAllFromRealm();

            RealmResults<LoginCredentials> loginCredentialses = realm.where(LoginCredentials.class).findAll();
            loginCredentialses.deleteAllFromRealm();

            realm.commitTransaction();
            realm.close();

            this.finish();
        }
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.flAgentProfile)
        {
            Intent registerIntent = new Intent(MainMenuActivity.this, AgentProfileActivity.class);
            startActivity(registerIntent);
        }

        if (view.getId() == R.id.flAppointments)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));

            Intent registerIntent = new Intent(MainMenuActivity.this, AppointmentsActivity.class);
            startActivity(registerIntent);
        }

        if (view.getId() == R.id.flClients)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));

            Intent registerIntent = new Intent(MainMenuActivity.this, ClientsActivity.class);
            startActivity(registerIntent);

            //ViewUtils.showToastMessage(this, "Clienti");
        }

        if (view.getId() == R.id.flNews)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));

            Intent registerIntent = new Intent(MainMenuActivity.this, NewsActivity.class);
            startActivity(registerIntent);
        }

        if (view.getId() == R.id.flBonusEGare)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            Intent registerIntent = new Intent(MainMenuActivity.this, BonusAndGareActivity.class);
            startActivity(registerIntent);
        }

        if (view.getId() == R.id.flMessages)
        {
            view.setBackgroundColor(Color.parseColor("#ffffffff"));
            ViewUtils.showToastMessage(this, "Messaggi");
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view.getId() == R.id.flAppointments)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        if (view.getId() == R.id.flClients)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        if (view.getId() == R.id.flNews)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        if (view.getId() == R.id.flBonusEGare)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        if (view.getId() == R.id.flMessages)
        {
            view.setBackgroundColor(Color.parseColor("#ffcde6f9"));
        }

        return false;
    }
}

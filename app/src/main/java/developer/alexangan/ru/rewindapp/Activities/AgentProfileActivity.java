package developer.alexangan.ru.rewindapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.ProfileInfoItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.ImageUtils;
import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;

public class AgentProfileActivity extends Activity implements View.OnClickListener
{
    private ProfileInfoItem profileInfoItem;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_agent_layout);

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

        if (profileInfoItem != null)
        {
            realm.copyFromRealm(profileInfoItem);
            this.profileInfoItem = profileInfoItem;
        }
        realm.commitTransaction();

        TextView tvAgentName = (TextView) findViewById(R.id.tvAgentName);
        String agentName = this.profileInfoItem.getName() + " " + this.profileInfoItem.getSurname();
        agentName = agentName.toUpperCase();
        tvAgentName.setText(agentName);

        LinearLayout llReturn = (LinearLayout) findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        TextView btnLogout = (TextView) findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);

        TextView txtPhone = (TextView) findViewById(R.id.txtPhone);

        String phones = this.profileInfoItem.getMobile();// + " " + profileInfoItem.getPhone();

        txtPhone.setText(phones);

        TextView tvPartitaIva = (TextView) findViewById(R.id.tvPartitaIva);
        tvPartitaIva.setText(this.profileInfoItem.getVat());

        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);

        String address = this.profileInfoItem.getAddress() + " " + this.profileInfoItem.getNumber() + "\n"
                + this.profileInfoItem.getZipcode() + " "
                + this.profileInfoItem.getCity() + " "
                + "(" + this.profileInfoItem.getAcronym() + ")";

        tvAddress.setText(address);

        Button btnTermsOfUse = (Button) findViewById(R.id.btnTermsOfUse);
        btnTermsOfUse.setOnClickListener(this);

        realm.close();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            this.finish();
        }

        if (view.getId() == R.id.btnLogout)
        {
            GlobalConstants.logoutInProgress = true;
            this.finish();
        }

        if (view.getId() == R.id.btnTermsOfUse)
        {
            Intent registerIntent = new Intent(AgentProfileActivity.this, TermsOfUseActivity.class);
            startActivity(registerIntent);
        }
    }
}

package developer.alexangan.ru.rewindapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import developer.alexangan.ru.rewindapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TermsOfUseActivity extends Activity implements View.OnClickListener
{
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_use_layout);

        LinearLayout llReturn = (LinearLayout) findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        TextView tvLinkToPrivacy = (TextView) findViewById(R.id.tvLinkToPrivacy);
        TextView tvLinkToCompany = (TextView) findViewById(R.id.tvLinkToCompany);

        tvLinkToPrivacy.setOnClickListener(this);
        tvLinkToCompany.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            this.finish();
        }

        if (view.getId() == R.id.tvLinkToPrivacy)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + getString(R.string.linkToPrivacy)));
            startActivity(browserIntent);
        }

        if (view.getId() == R.id.tvLinkToCompany)
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + getString(R.string.linkToCompany)));
            startActivity(browserIntent);
        }
    }
}

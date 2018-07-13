package developer.alexangan.ru.rewindapp;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

// Created by user on 06.06.2017.

public class RewindApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/SF_UI_Text_Bold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        //GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
    }
}

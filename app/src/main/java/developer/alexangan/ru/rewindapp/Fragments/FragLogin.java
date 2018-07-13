package developer.alexangan.ru.rewindapp.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import developer.alexangan.ru.rewindapp.Interfaces.LoginCommunicator;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import developer.alexangan.ru.rewindapp.Models.LoginCredentials;
import developer.alexangan.ru.rewindapp.Models.ProfileInfoItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

import static android.os.Environment.DIRECTORY_PICTURES;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.user_id;


public class FragLogin extends Fragment implements View.OnClickListener, View.OnTouchListener, Callback
{
    Activity activity;
    Button btnLogin;
    EditText etLogin, etPassword;
    LoginCommunicator loginCommunicator;
    private boolean credentialsesFound;
    NetworkUtils networkUtils;
    String strLogin, strPassword;
    private ProgressDialog downloadingDialog;
    LinearLayout llLogin;
    boolean initialized;
    private Call callUserLogin, callAgentAvatar;
    private ImageButton ibShowPassword;
    private boolean isPasswordVisible;
    private String avatarFileName;
    private LinearLayout llLoginLogo;
    private boolean etPasswordClicked;
    private ProfileInfoItem profileInfoItem;

    public FragLogin()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        loginCommunicator = (LoginCommunicator) getActivity();

        downloadingDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        downloadingDialog.setTitle("");
        downloadingDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        downloadingDialog.setIndeterminate(true);

        initialized = false;
        isPasswordVisible = false;

        etPasswordClicked = false;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        LoginCredentials loginCredentials = realm.where(LoginCredentials.class).findFirst();
        realm.commitTransaction();

        if (loginCredentials != null)
        {
            String strLogin = loginCredentials.getLogin();
            etLogin.setText(strLogin);
            String strPassword = loginCredentials.getPassword();
            etPassword.setText(strPassword);
        }
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.login_user_fragment, container, false);

        btnLogin = (Button) rootView.findViewById(R.id.btnLogin);

        llLoginLogo = (LinearLayout) rootView.findViewById(R.id.llLoginLogo);
        llLogin = (LinearLayout) rootView.findViewById(R.id.llLogin);
        etLogin = (EditText) rootView.findViewById(R.id.etLogin);

        //flPassword = (FrameLayout) rootView.findViewById(R.id.flPassword);
        etPassword = (EditText) rootView.findViewById(R.id.etPassword);

        ibShowPassword = (ImageButton) rootView.findViewById(R.id.ibShowPassword);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        llLoginLogo.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        ibShowPassword.setOnClickListener(this);

        llLoginLogo.setOnTouchListener(this);
        etLogin.setOnTouchListener(this);
        etPassword.setOnTouchListener(this);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ProfileInfoItem profileInfoItem = realm.where(ProfileInfoItem.class).findFirst();

        if (profileInfoItem != null)
        {
            realm.copyFromRealm(profileInfoItem);
            this.profileInfoItem = profileInfoItem;
        }
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //downloadingDialog.dismiss();

        enableInput();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llLoginLogo)
        {
            ViewUtils.disableSoftKeyboard(activity);
        }

        if (view.getId() == R.id.btnLogin)
        {
            credentialsesFound = false;
            strLogin = etLogin.getText().toString();

            strPassword = etPassword.getText().toString();

            if (strPassword.length() < 4)
            {
                showToastMessage(getString(R.string.InvalidPassword));
                return;
            }

            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            RealmResults<LoginCredentials> loginCredentialses = realm.where(LoginCredentials.class).findAll();
            realm.commitTransaction();

            List<LoginCredentials> loginCredentialsesEx = new ArrayList<>();

            realm.beginTransaction();
            for (LoginCredentials loginCredentials : loginCredentialses)
            {
                LoginCredentials loginCredentialsEx = realm.copyFromRealm(loginCredentials);
                loginCredentialsesEx.add(loginCredentialsEx);
            }
            realm.commitTransaction();
            realm.close();

            for (LoginCredentials loginCredentialsX : loginCredentialsesEx)
            {
                if (strLogin.equals(loginCredentialsX.getLogin())
                        && strPassword.equals(loginCredentialsX.getPassword()))
                {
                    credentialsesFound = true;
                    user_id = loginCredentialsX.getUser_id();
                    break;
                }
            }

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("login", strLogin);
            editor.putString("password", strPassword);
            editor.apply();

            if (!NetworkUtils.isNetworkAvailable(activity))
            {
                if (credentialsesFound && this.profileInfoItem != null)
                {
                    btnLogin.setAlpha(0.4f);
                    btnLogin.setEnabled(false);

                    loginCommunicator.onLoginSucceeded();
                } else
                {
                    showToastMessage(getString(R.string.LoginFailedCheckInternetConnection));
                }
            } else
            {
                disableInputAndShowProgressDialog();

                String device_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

                networkUtils = new NetworkUtils();

                callUserLogin = networkUtils.loginRequest(this, strLogin, strPassword, device_id);
            }
        }

        if (view.getId() == R.id.ibShowPassword)
        {
            showOrHidePassword();
        }
    }

    private void showOrHidePassword()
    {
        if (isPasswordVisible)
        {
            ibShowPassword.setBackgroundResource(R.drawable.eye);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else
        {
            ibShowPassword.setBackgroundResource(R.drawable.eyedisable);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

        isPasswordVisible = !isPasswordVisible;
    }

    @Override
    public void onFailure(Call call, IOException e)
    {
        //downloadingDialog.dismiss();

        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                enableInput();
            }
        });

        if (call == callUserLogin)
        {
            showToastMessage(getString(R.string.LoginFailedCheckInternetConnection));
        }

        if (call == callAgentAvatar)
        {
            showToastMessage(getString(R.string.DownloadAvatarFailed));

            loginCommunicator.onLoginSucceeded();
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException
    {
        if (call == callUserLogin)
        {
            String loginResponse = response.body().string();

            response.body().close();

            downloadingDialog.dismiss();

            activity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    enableInput();
                }
            });

            JSONObject jsonObject;

            try
            {
                jsonObject = new JSONObject(loginResponse);
            } catch (JSONException e)
            {
                e.printStackTrace();
                return;
            }

            if ( ! jsonObject.has("error"))
            {
                if ( ! credentialsesFound && strLogin != null && strPassword != null)
                {
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    RealmResults<LoginCredentials> loginCredentialses = realm.where(LoginCredentials.class).findAll();
                    loginCredentialses.deleteAllFromRealm();
                    realm.commitTransaction();

                    int user_id = 0;

                    realm.beginTransaction();
                    LoginCredentials loginCredentials = new LoginCredentials(user_id, strLogin, strPassword);
                    realm.copyToRealm(loginCredentials);
                    realm.commitTransaction();
                    realm.close();
                }

                Gson gson = new Gson();

                ProfileInfoItem profileInfoItem = gson.fromJson(String.valueOf(jsonObject), ProfileInfoItem.class);

                if(profileInfoItem != null)
                {
                    String strMobile = profileInfoItem.getMobile();

                    if (strMobile != null && strMobile.startsWith("9"))
                    {
                        strMobile = strMobile.substring(1);
                        profileInfoItem.setMobile(strMobile);
                    }

                    this.profileInfoItem = profileInfoItem;

                    try
                    {
                        GlobalConstants.tokenStr = jsonObject.getString("token");

                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                        showToastMessage(getString(R.string.DatabaseError));
                    }

                    avatarFileName = profileInfoItem.getAvatar();
                    int id_agent = profileInfoItem.getId_agent();

                    if(avatarFileName != null && id_agent != 0)
                    {
                        String avatarUrl = GlobalConstants.USER_AVATAR_URL + id_agent + "/" + avatarFileName;

                        String imagesDirPath = null;

                        try
                        {
                            imagesDirPath = activity.getExternalFilesDir(DIRECTORY_PICTURES).getAbsolutePath();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if (imagesDirPath != null)
                        {
                            String pathToAvatar = imagesDirPath + "/" + avatarFileName;

                            mSettings.edit().putString("pathToAgentAvatar", pathToAvatar).apply();

                            File file = new File(imagesDirPath, avatarFileName);

                            if ( ! file.exists())
                            {
                                callAgentAvatar = networkUtils.downloadFile(this, avatarUrl);
                            }
                            else
                            {
                                loginCommunicator.onLoginSucceeded();
                            }
                        }
                    }

                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    RealmResults<ProfileInfoItem> profileInfoItems = realm.where(ProfileInfoItem.class).findAll();
                    profileInfoItems.deleteAllFromRealm();
                    realm.commitTransaction();

                    realm.beginTransaction();
                    realm.copyToRealm(profileInfoItem);
                    realm.commitTransaction();
                    realm.close();
                }
            } else
            {
                final String errorStr;

                try
                {
                    errorStr = jsonObject.getString("error");
                    if (errorStr.length() != 0)
                    {
                        showToastMessage(errorStr);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                showToastMessage(getString(R.string.LoginOrPasswordNotValid));
            }
        }

        if (call == callAgentAvatar)
        {
            String imagesDirPath = activity.getExternalFilesDir(DIRECTORY_PICTURES).getAbsolutePath();
            File file = new File(imagesDirPath, avatarFileName);

            if (file.exists())
            {
                file.delete();
            }

            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();
            response.body().close();

            loginCommunicator.onLoginSucceeded();
        }
    }

    private void showToastMessage(final String msg)
    {
        activity.runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableInputAndShowProgressDialog()
    {
        btnLogin.setAlpha(0.4f);
        btnLogin.setEnabled(false);

        downloadingDialog.show();
    }

    private void enableInput()
    {
        btnLogin.setAlpha(1.0f);
        btnLogin.setEnabled(true);

        downloadingDialog.dismiss();
    }

    public void setFocusOnEnterButton()
    {
        if (!etPasswordClicked)
        {
            btnLogin.getParent().requestChildFocus(btnLogin, btnLogin);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event)
    {
        if (view.getId() == R.id.etPassword)
        {
            etPasswordClicked = true;
        } else
        {
            etPasswordClicked = false;
        }
        return false;
    }
}

package developer.alexangan.ru.rewindapp.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import developer.alexangan.ru.rewindapp.Interfaces.NewsCommunicator;
import developer.alexangan.ru.rewindapp.Models.NewsItem;
import developer.alexangan.ru.rewindapp.R;
import developer.alexangan.ru.rewindapp.Utils.MyTextUtils;
import developer.alexangan.ru.rewindapp.Utils.NetworkUtils;
import developer.alexangan.ru.rewindapp.Utils.ViewUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.API_HOST_ROOT_URL;
import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.mSettings;


public class FragNewsDetailed extends Fragment implements View.OnClickListener, Callback
{

    private NewsCommunicator mCommunicator;
    Activity activity;
    private int newsItemId;
    private TextView tvNewsTitle, tvNewsSubtitle, tvNewsDate;
    private TextView tvNewsDetailedText;
/*    private Handler handler;
    private Runnable runnable;*/
    AlertDialog alert;
    private NewsItem newsItem;
    private Call callGetAttachment;
    private TextView tvAttachment;
    private Button btnRaceDetails;
    private ProgressDialog downloadingDialog;
    private String urlAttachment;
    private TextView tvNewsBodyTitle, tvNewsBodySubtitle;
    private LinearLayout llNewsBodyTitle;
    private ArrayList<NewsItem> l_newsItems;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        activity = getActivity();
        mCommunicator = (NewsCommunicator) getActivity();

        if (getArguments() != null)
        {
            newsItemId = getArguments().getInt("id");
        }

        l_newsItems = new ArrayList<>();

/*        handler = new Handler();

        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));
                downloadingDialog.dismiss();
            }
        };*/

        downloadingDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        downloadingDialog.setTitle("");
        downloadingDialog.setMessage(getString(R.string.DownloadingDataPleaseWait));
        downloadingDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_news_detailed_layout, container, false);

        LinearLayout llReturn = (LinearLayout) rootView.findViewById(R.id.llReturn);
        llReturn.setOnClickListener(this);

        tvAttachment = (TextView) rootView.findViewById(R.id.tvAttachment);
        btnRaceDetails = (Button) rootView.findViewById(R.id.btnRaceDetails);

        tvNewsTitle = (TextView) rootView.findViewById(R.id.tvNewsTitle);
        tvNewsSubtitle = (TextView) rootView.findViewById(R.id.tvNewsSubtitle);
        tvNewsBodyTitle = (TextView) rootView.findViewById(R.id.tvNewsBodyTitle);
        tvNewsBodySubtitle = (TextView) rootView.findViewById(R.id.tvNewsBodySubtitle);
        llNewsBodyTitle = (LinearLayout) rootView.findViewById(R.id.llNewsBodyTitle);
        tvNewsDate = (TextView) rootView.findViewById(R.id.tvNewsDate);
        tvNewsDetailedText = (TextView) rootView.findViewById(R.id.tvNewsDetailedText);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        RealmResults<NewsItem> rrNewsItems = realm.where(NewsItem.class).findAll();
        realm.commitTransaction();

        realm.beginTransaction();
        for (NewsItem newsItem : rrNewsItems)
        {
            NewsItem newsItemEx = realm.copyFromRealm(newsItem);
            l_newsItems.add(newsItemEx);
        }
        realm.commitTransaction();
        realm.close();

        newsItem = l_newsItems.get(newsItemId);
        //id_news = newsItem.getId_news();

        String searchString = mSettings.getString("newsSearchLastQueryString", "");
        String searchStrLower = searchString.toLowerCase();

        String strNewsTitle = newsItem.getTitle_news();
        String strNewsSubtitle = newsItem.getSubtitle_news();
        String strNewsText = newsItem.getNews();

        if (!strNewsSubtitle.contains("Periodo gara "))//newsItem.getIsRace() == 0)
        {
            btnRaceDetails.setVisibility(View.GONE);
            llNewsBodyTitle.setVisibility(View.GONE);
        } else
        {
            btnRaceDetails.setOnClickListener(this);
            strNewsText = reformatRaceNewsText(strNewsText);
        }


/*        if (strNewsTitle.toLowerCase().contains(searchStrLower))
        {
            SpannableString ssText = makeColoredString(strNewsTitle, searchStrLower);
            tvNewsTitle.setText(ssText);
        }
        else*/
        {
            tvNewsTitle.setText(strNewsTitle);
        }

/*        if (strNewsSubtitle.toLowerCase().contains(searchStrLower))
        {
            SpannableString ssText = makeColoredString(strNewsSubtitle, searchStrLower);
            tvNewsSubtitle.setText(ssText);
        }
        else*/
        {
            tvNewsSubtitle.setText(strNewsSubtitle);
        }

/*        if (strNewsText.toLowerCase().contains(searchStrLower))
        {
            SpannableString ssText = makeColoredString(strNewsText, searchStrLower);
            tvNewsDetailedText.setText(ssText);
        }
        else*/
        {
            tvNewsDetailedText.setText(strNewsText);
        }

        String datePost = newsItem.getDate_post();

        String formattedDate = MyTextUtils.reformatDateString(datePost);
        tvNewsDate.setText(formattedDate);

        if (newsItem.getIsAttach() == 0)
        {
            tvAttachment.setVisibility(View.GONE);
        } else
        {
            tvAttachment.setOnClickListener(this);
        }
    }

    private String reformatRaceNewsText(String strNewsText)
    {
        strNewsText = strNewsText.replace("\t\t\t\t\t\t\t\t", "");

        String[] strSplitted = strNewsText.split("\n\n");

        String titleAndSubtitle = strSplitted[0];
        String[] bodyTitle = titleAndSubtitle.split("H1>");

        String subTitle = bodyTitle[2];
        subTitle = subTitle.replace("<b>", "");
        subTitle = subTitle.replace("</b>", "");

        String[] bodySubtitle = titleAndSubtitle.split("b>");
        strNewsText = strNewsText.replace(titleAndSubtitle, "");

        String bodyTitleSplitted = bodyTitle[1].replace("</", "");
        String bodySubtitleSplitted = bodySubtitle[1].replace("</", "");

        SpannableStringBuilder sb = new SpannableStringBuilder(subTitle);

        StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD);

        int start = subTitle.length() - 7 - bodySubtitleSplitted.length();
        int end = subTitle.length() - 7;

        sb.setSpan(b, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        tvNewsBodyTitle.setText(bodyTitleSplitted);
        tvNewsBodySubtitle.setText(sb);

        if (Build.VERSION.SDK_INT >= 24)
        {
            strNewsText = String.valueOf(Html.fromHtml(strNewsText, Html.FROM_HTML_MODE_LEGACY));
            strNewsText = String.valueOf(Html.fromHtml(strNewsText, Html.FROM_HTML_MODE_LEGACY));
        } else
        {
            strNewsText = String.valueOf(Html.fromHtml(strNewsText));
            strNewsText = String.valueOf(Html.fromHtml(strNewsText));
        }

        int startToRemove = strNewsText.indexOf("Dettaglio delle pratiche");

        strNewsText = strNewsText.substring(0, startToRemove);

        return strNewsText;
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
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.llReturn)
        {
            mCommunicator.onDetailedNewsReturned();
            return;
        }

        if (view.getId() == R.id.tvAttachment && newsItem.getIsAttach() == 1)
        {
            if (NetworkUtils.isNetworkAvailable(activity))
            {
                urlAttachment = API_HOST_ROOT_URL + newsItem.getPathAttach();
                urlAttachment = urlAttachment.replace("..", "");
                urlAttachment = urlAttachment.replace("//", "/");
                //urlAttachment = urlAttachment.replace("update", "upload");

/*                //String urlGoogleDoc = "http://docs.google.com/gview?embedded=true&url=" + urlAttachment;
                String urlGoogleDoc = "http://drive.google.com/viewerng/viewer?embedded=true&url=" + urlAttachment;
                Uri uriGoogleDoc = Uri.parse(urlGoogleDoc);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uriGoogleDoc);
                startActivity(browserIntent);*/

                NetworkUtils networkUtils = new NetworkUtils();

                disableInputAndShowProgressDialog();

                callGetAttachment = networkUtils.downloadFile(this, urlAttachment);
            } else
            {
                ViewUtils.showToastMessage(activity, getString(R.string.AttachmentIsAccessibleOnlineOnly));
            }
            return;
        }

        if (view.getId() == R.id.btnRaceDetails)
        {
            if (NetworkUtils.isNetworkAvailable(activity))
            {
                mCommunicator.onRaceNewsDetailsReturned(newsItemId);
            } else
            {
                ViewUtils.showToastMessage(activity, getString(R.string.DetailsIsAccessibleOnlineOnly));
            }
        }
    }

    @Override
    public void onFailure(Call call, IOException e)
    {
        if (call == callGetAttachment)
        {
            ViewUtils.showToastMessage(activity, getString(R.string.ServerAnswerNotReceived));

            downloadingDialog.dismiss();

/*            activity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    downloadingDialog.dismiss();
                }
            });*/

            //handler.removeCallbacks(runnable);

            activity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    enableInput();
                }
            });
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException
    {
        if (call == callGetAttachment)
        {
            String docsDirPath = activity.getExternalFilesDir(DIRECTORY_DOCUMENTS).getAbsolutePath();

            Uri uriAttach = Uri.parse(urlAttachment);
            String attachmentFileName = uriAttach.getLastPathSegment();

            File file = new File(docsDirPath, attachmentFileName);

            if (file.exists())
            {
                Log.d("DEBUG", String.valueOf(file.length()));
                file.delete();
            }

            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();
            response.body().close();

            activity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    enableInput();
                }
            });

            // Choose application (for Android N)
/*            Intent myIntent = new Intent(Intent.ACTION_VIEW);
            myIntent.setData(Uri.fromFile(file));
            Intent j = Intent.createChooser(myIntent, getString(R.string.ChooseAppToOpenWith));
            startActivity(j);*/

            // Share application
/*            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("multipart/");
            startActivity(intent);*/

            Intent intent = getIntentWithDataAndType(uriAttach, file);

            try
            {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } catch (ActivityNotFoundException e)
            {
                activity.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(activity, R.string.NoAppForThisFileType, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @NonNull
    private Intent getIntentWithDataAndType(Uri uri, File file)
    {
        Uri url = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (url.toString().contains(".doc") || url.toString().contains(".docx"))
        {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf"))
        {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx"))
        {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx"))
        {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        }
        else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        }
        else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        }
        return intent;
    }

    private void enableInput()
    {
        tvAttachment.setAlpha(1.0f);
        tvAttachment.setEnabled(true);

        btnRaceDetails.setAlpha(1.0f);
        btnRaceDetails.setEnabled(true);

        downloadingDialog.dismiss();
    }

    private void disableInputAndShowProgressDialog()
    {
        tvAttachment.setAlpha(0.4f);
        tvAttachment.setEnabled(false);

        btnRaceDetails.setAlpha(0.4f);
        btnRaceDetails.setEnabled(false);

        downloadingDialog.show();
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

/*    @NonNull
    private SpannableString makeColoredString(String strSource, String searchString)
    {
        String sourceStrLower = strSource.toLowerCase();

        int iStart = sourceStrLower.indexOf(searchString);
        int iEnd = iStart + searchString.length();

        Spannable spannable = new SpannableString(strSource);
        SpannableString ssText = new SpannableString(spannable);
        ssText.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffd100")), iStart, iEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssText;
    }*/
}

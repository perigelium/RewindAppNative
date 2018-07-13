package developer.alexangan.ru.rewindapp.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.TimeUnit;

import developer.alexangan.ru.rewindapp.Models.AppointmentsQueryParams;
import developer.alexangan.ru.rewindapp.Models.ClientMemoItem;
import developer.alexangan.ru.rewindapp.Models.ClientsQueryParams;
import developer.alexangan.ru.rewindapp.Models.GlobalConstants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.tokenStr;

/**
 * Created by user on 12/20/2016*/

public class NetworkUtils
{
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();// && isOnline();
    }

/*    public static boolean isOnline()
    {
        try
        {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 8.8.4.4");
            ProcessWithTimeout processWithTimeout = new ProcessWithTimeout(process);
            int exitCode = processWithTimeout.waitForProcess(1500);

            if (exitCode == Integer.MIN_VALUE)
            {
                return false;
            } else
            {
                return true;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }*/

    public Call loginRequest(Callback callback, String username, String password, String device_id)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(10, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

/*        String encodedUsername = "";
        try
        {
            encodedUsername = URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }

        String encodedPassword = "";
        try
        {
            encodedPassword = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }*/

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("password", password)
                .addFormDataPart("device_id", device_id)
                .build();

        Request request = new Request.Builder()
                .url(GlobalConstants.API_LOGIN_URL)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

/*    public Call getNews(Callback callback, String apiUrl, String tokenStr, int offset, int max)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(10, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("offset", String.valueOf(offset))
                .addFormDataPart("max", String.valueOf(max))
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }*/

/*    public Call getNewsDetailed(Callback callback, String apiUrl, String tokenStr, int id_news)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(10, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("id_news", String.valueOf(id_news))
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }*/

    public Call getClients(Callback callback, String apiUrl, ClientsQueryParams clientsQueryParams)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(45, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(45, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("search", clientsQueryParams.isSearchMode())
                .addFormDataPart("lat", clientsQueryParams.getLat())
                .addFormDataPart("lng", clientsQueryParams.getLng())
                .addFormDataPart("radius", clientsQueryParams.getRadius())
                .addFormDataPart("city", clientsQueryParams.isInCity())
                //.addFormDataPart(clientsQueryParams.getSearchCriteria(), clientsQueryParams.getSearchQuery())
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call getClientsSearchResults(Callback callback, String apiUrl, ClientsQueryParams clientsQueryParams)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(45, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(45, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("search", clientsQueryParams.isSearchMode())
                .addFormDataPart(clientsQueryParams.getSearchCriteria(), clientsQueryParams.getSearchQuery())
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

/*    public Call getDataForMonth(Callback callback, String apiUrl, String periodYYYYMM)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(10, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("period", periodYYYYMM)
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }*/

    public Call getDataForID(Callback callback, String apiUrl, String clientID)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(10, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("id_customer", clientID)
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call sendInvoiceConfirmation(Callback callback, String apiUrl, String IDs)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(10, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("invoice_id", IDs)
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call sendClientMemo(Callback callback, String apiUrl, ClientMemoItem clientMemoItem)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(10, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(10, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("title", clientMemoItem.getTitle())
                .addFormDataPart("date", clientMemoItem.getDate())
                .addFormDataPart("hour", clientMemoItem.getTime())
                .addFormDataPart("description", clientMemoItem.getDescription())
                .addFormDataPart("customer_id", String.valueOf(clientMemoItem.getCustomer_id()))
                .addFormDataPart("place", clientMemoItem.getPlace())
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call downloadFile(Callback callback, String url)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(10, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(10, TimeUnit.SECONDS);
        OkHttpClient okHttpClient = defaultHttpClient.build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call callDownloadURL = okHttpClient.newCall(request);
        callDownloadURL.enqueue(callback);

        return callDownloadURL;
    }

    public Call getAppointments(Callback callback, String apiUrl, AppointmentsQueryParams appointmentsQueryParams)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(45, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(45, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("search_type", appointmentsQueryParams.getSearchType())
                .addFormDataPart("date_from", appointmentsQueryParams.getDateFrom())
                .addFormDataPart("date_to", appointmentsQueryParams.getDateTo())
                .addFormDataPart("lat", appointmentsQueryParams.getLat())
                .addFormDataPart("lng", appointmentsQueryParams.getLng())
                .addFormDataPart("status_client", appointmentsQueryParams.getStatusClient())
                .addFormDataPart("status_appointment", appointmentsQueryParams.getStatusAppointment())
                .addFormDataPart(appointmentsQueryParams.getSearchCriteria(), appointmentsQueryParams.getSearchQuery())
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }

    public Call getAppointmentsSearchResults(Callback callback, String apiUrl, AppointmentsQueryParams appointmentsQueryParams)
    {
        OkHttpClient.Builder defaultHttpClient = new OkHttpClient.Builder();
        defaultHttpClient.connectTimeout(4, TimeUnit.SECONDS);
        defaultHttpClient.readTimeout(45, TimeUnit.SECONDS);
        defaultHttpClient.writeTimeout(45, TimeUnit.SECONDS);

        OkHttpClient okHttpClient = defaultHttpClient.build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", tokenStr)
                .addFormDataPart("search", appointmentsQueryParams.getSearchType())
                .addFormDataPart(appointmentsQueryParams.getSearchCriteria(), appointmentsQueryParams.getSearchQuery())
                .build();

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);

        return call;
    }
}

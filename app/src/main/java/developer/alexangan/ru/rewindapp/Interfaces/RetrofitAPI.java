package developer.alexangan.ru.rewindapp.Interfaces;

import java.util.List;

import developer.alexangan.ru.rewindapp.Models.BonusItem;
import developer.alexangan.ru.rewindapp.Models.InvoiceItem;
import developer.alexangan.ru.rewindapp.Models.NewsItem;
import developer.alexangan.ru.rewindapp.Models.RaceRankDetailsItem;
import developer.alexangan.ru.rewindapp.Models.RaceRankItem;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static developer.alexangan.ru.rewindapp.Models.GlobalConstants.API_HOST_URL;

public interface RetrofitAPI
{
    @FormUrlEncoded
    @POST("api/getDetailNewsRace")
    Call<List<RaceRankDetailsItem>> getRaceRankNewsDetailsList(@Field("token") String tokenStr, @Field("id_news") int id_news);

    @FormUrlEncoded
    @POST("api/getListNews")
    Call<List<NewsItem>> getNewsList(@Field("token") String tokenStr, @Field("offset") int offset, @Field("max") int max);

    @FormUrlEncoded
    @POST("api/getBonuses")
    Call<List<BonusItem>> getBonusesList(@Field("token") String tokenStr, @Field("period") String periodYYYYMM);

    @FormUrlEncoded
    @POST("api/getInvoices")
    Call<List<InvoiceItem>> getInvoicesList(@Field("token") String tokenStr, @Field("period") String periodYYYYMM);

    @FormUrlEncoded
    @POST("api/getClassification")
    Call<List<RaceRankItem>> getRaceRankList(@Field("token") String tokenStr, @Field("period") String periodYYYYMM);

    @FormUrlEncoded
    @POST("api/getClassificationDetails")
    Call<List<RaceRankDetailsItem>> getRaceRankDetailsList(@Field("token") String tokenStr, @Field("period") String periodYYYYMM);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(API_HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}

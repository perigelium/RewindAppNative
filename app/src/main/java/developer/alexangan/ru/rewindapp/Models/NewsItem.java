package developer.alexangan.ru.rewindapp.Models;

import io.realm.RealmObject;


public class NewsItem extends RealmObject
{
    private int id_news;
    private String title_news;
    private String subtitle_news;
    private int isRace;
    private int isAttach;
    private int new_news;
    private String date_post;
    private String news;
    private String pathAttach;

    public int getId_news()
    {
        return id_news;
    }

    public String getTitle_news()
    {
        return title_news;
    }

    public String getSubtitle_news()
    {
        return subtitle_news;
    }

    public int getIsAttach()
    {
        return isAttach;
    }

    public int getNew_news()
    {
        return new_news;
    }

    public String getDate_post()
    {
        return date_post;
    }

    public String getNews()
    {
        return news;
    }

    public String getPathAttach()
    {
        return pathAttach;
    }

    public void setNews(String news)
    {
        this.news = news;
    }

    public void setPathAttach(String pathAttach)
    {
        this.pathAttach = pathAttach;
    }

    public int getIsRace()
    {
        return isRace;
    }
}

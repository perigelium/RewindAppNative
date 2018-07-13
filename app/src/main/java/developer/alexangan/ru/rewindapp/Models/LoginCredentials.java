package developer.alexangan.ru.rewindapp.Models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by user on 12/19/2016.
 */

public class LoginCredentials extends RealmObject
{
    @PrimaryKey
    private int user_id;

    private String login;
    private String password;

    public LoginCredentials(){};

    public LoginCredentials(int user_id, String login, String password)
    {
        this.user_id = user_id;
        this.login = login;
        this.password = password;
    }

    public String getLogin()
    {
        return login;
    }

    public String getPassword()
    {
        return password;
    }

    public int getUser_id()
    {
        return user_id;
    }
}

package developer.alexangan.ru.rewindapp.Interfaces;

/**
 * Created by user on 11/16/2016.*/


public interface LoginCommunicator
{
    void onLoginSucceeded();

    void onGuideSkipped();

    void popFragmentBackStack();
}
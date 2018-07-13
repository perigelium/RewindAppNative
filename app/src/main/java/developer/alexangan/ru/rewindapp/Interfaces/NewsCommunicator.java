package developer.alexangan.ru.rewindapp.Interfaces;


public interface NewsCommunicator
{
    void onNewsListItemSelected(int position);

    void onDetailedNewsReturned();

    void onLogoutCommand();

    void onDetailedNewsRaceReturned();

    void onRaceNewsDetailsReturned(int newsItemId);

    void onClose();
}
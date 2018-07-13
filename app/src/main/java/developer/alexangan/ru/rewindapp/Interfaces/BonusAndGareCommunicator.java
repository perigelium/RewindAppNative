package developer.alexangan.ru.rewindapp.Interfaces;

/**
 * Created by user on 17.07.2017.
 */
public interface BonusAndGareCommunicator
{

    void onClose();

    void onBonusListSelected();

    void popFragmentBackStack();

    void onLogoutCommand();

    void onInvoicesListSelected();

    void onInvoicesListItemSelected(String dateYYYYMM, String total, boolean confirmed);

    void onRaceRankListSelected();

    void onRaceRankListItemSelected(int position, String dateYYYYMM);
}

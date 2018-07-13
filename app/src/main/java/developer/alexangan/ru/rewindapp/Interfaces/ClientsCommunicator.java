package developer.alexangan.ru.rewindapp.Interfaces;


import android.app.Fragment;

import java.util.List;

import developer.alexangan.ru.rewindapp.Models.ClientDocsItem;
import developer.alexangan.ru.rewindapp.Models.ClientInfoItem;
import developer.alexangan.ru.rewindapp.Models.ClientPraticheInfoItem;
import developer.alexangan.ru.rewindapp.Models.LeadInfoItem;

public interface ClientsCommunicator
{
    void onLogoutCommand();

    void onClose();

    void onOpenLegendAndFilter(boolean gpsIsActive);

    void onMapPinSelected(ClientInfoItem clientInfoItem);

    void onClientPracticeListItemSelected(ClientPraticheInfoItem clientPraticheInfoItem);

    void onClientPracticesSelected(List<ClientPraticheInfoItem> clientPraticheInfoItems);

    void onClientAltuofiancoDetailsSelected(ClientInfoItem clientInfoItem);

    void onClientAddMemoClicked(int id_customer, String company_name);

    void onOpenClientsSearch(Fragment frag);

    void onClientSearchResultsListItemSelected(ClientInfoItem clientInfoItem);

    void onClientsLeadsListItemSelected(LeadInfoItem leadInfoItem);

    void popFragmentsBackStack();

    void openLeadsList();

    void openLeadDetails(LeadInfoItem leadInfoItem, boolean editable);

    void onClientDocsListItemSelected(ClientDocsItem clientDocsItem);

    void onClientDocsListSelected(int id_customer);
}
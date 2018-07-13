package developer.alexangan.ru.rewindapp.Interfaces;


import android.app.Fragment;

import java.util.List;

import developer.alexangan.ru.rewindapp.Fragments.FragAppointmentsListMode;
import developer.alexangan.ru.rewindapp.Models.AppointmentInfoItem;
import developer.alexangan.ru.rewindapp.Models.AppointmentsSearchResultsListItem;

public interface AppointmentsCommunicator
{
    void onLogoutCommand();

    void onClose();

    void openLegendAndFilter(boolean gpsIsActive);

    void onMapPinSelected(AppointmentInfoItem appointmentInfoItem);

    void onAppointmentAddMemoClicked(int id_customer, Fragment frag);

    void openAppointmentsSearch(Fragment frag);

    void popFragmentsBackStack();

    void openAppointmentsCalendar(List<AppointmentInfoItem> lAppointmentInfoItems, boolean dailyMode);

    void openAppointmentsListMode(List<AppointmentInfoItem> lAppointmentInfoItems, boolean dailyMode, Fragment frag);

    void openAppointmentsSearchResults(String searchQuery, List<AppointmentsSearchResultsListItem> appointmentsSearchResultsListItems);

    void onAppointmentsSearchResultsListItemSelected(AppointmentsSearchResultsListItem appointmentsSearchResultsListItem);

    void openAppointmentsNewPersonalTask();
}
package developer.alexangan.ru.rewindapp.Models;

import android.content.SharedPreferences;

public class GlobalConstants
{
    public static SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "mysettings";

    public static String tokenStr;
    public static int user_id;
    public static boolean logoutInProgress = false;

    public static String API_HOST_URL = "http://wind.monitorcrm.it/webservices_dev/index.php/";
    public static String API_LOGIN_URL = API_HOST_URL + "api/login";
    public static String USER_AVATAR_URL = "http://wind.monitorcrm.it/upload/agenti/";///upload/agenti/{id_agent}/{ProfileInfo.file_name}
    public static String API_HOST_ROOT_URL = "http://wind.monitorcrm.it/";

    public static String API_GETNEWSLIST_URL = API_HOST_URL + "api/getListNews";
    public static String API_GETDETAILEDNEWSRACE_URL = API_HOST_URL + "api/getDetailNewsRace";

    public static String API_GET_CLIENTS_URL = API_HOST_URL + "api/getClients";
    public static String API_GET_CLIENTS_DOCUMENTS_URL = API_HOST_URL + "api/getClientsDocuments";
    public static String API_SEND_CLIENT_MEMO = API_HOST_URL + "api/sendClientMemo";
    public static String API_GET_STATUSES_URL = API_HOST_URL + "api/getStatuses";
    public static String API_POST_EVENT_TO_CALENDAR = API_HOST_URL + "api/sendEvent";
    public static String API_GET_CLASIFICATION_URL = API_HOST_URL + "api/getClassification";
    public static String API_GET_CLASIFICATION_DETAILS_URL = API_HOST_URL + "api/getClassificationDetails";
    public static String API_GET_BONUSES_URL = API_HOST_URL + "api/getBonuses";
    public static String API_GET_INVOICES_URL = API_HOST_URL + "api/getInvoices";
    public static String API_GET_APPOINTMENTS_URL = API_HOST_URL + "api/getAppointments";
    public static String API_UPDATE_APPUNTAMETI_URL = API_HOST_URL + "api/updateAppuntament";
    public static String API_GET_TICKET_TYPES_URL = API_HOST_URL + "api/getTicketTypes";
    public static String API_POST_TICKET_URL = API_HOST_URL + "api/sendTicket";
    public static String API_POST_INVOICE_CHECKED_URL = API_HOST_URL + "api/sendInvoiceCheck";
    public static String API_GET_MEMO_URL = API_HOST_URL + "api/getClientMemos";
    public static String API_LOGOUT_URL = API_HOST_URL + "api/logout";

    //public static String API_LOGIN_URL = "http://bludelego.ml/rewind/index.php/api/login";

}



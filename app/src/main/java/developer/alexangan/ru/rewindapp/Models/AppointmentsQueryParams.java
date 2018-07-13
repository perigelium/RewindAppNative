package developer.alexangan.ru.rewindapp.Models;


public class AppointmentsQueryParams
    {
        private String searchType; // boolean
        private String lat; // float
        private String lng; // float
        private String statusClient;
        private String dateFrom;
        private String dateTo;
        private String statusAppointment;
        private String searchCriteria;
        private String searchQuery;

        public AppointmentsQueryParams(String searchType, String dateFrom, String dateTo, String lat, String lng,
                                       String statusClient, String statusAppointment, String searchCriteria, String searchQuery)
        {
            this.searchType = searchType;
            this.lat = lat;
            this.lng = lng;
            this.statusClient = statusClient;
            this.statusAppointment = statusAppointment;
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
            this.searchCriteria = searchCriteria;
            this.searchQuery = searchQuery;
        }

        public String getSearchType()
        {
            return searchType;
        }

        public String getLat()
        {
            return lat;
        }

        public String getLng()
        {
            return lng;
        }

        public String getSearchCriteria()
        {
            return searchCriteria;
        }

        public String getSearchQuery()
        {
            return searchQuery;
        }

        public String getDateFrom()
        {
            return dateFrom;
        }

        public String getDateTo()
        {
            return dateTo;
        }

        public String getStatusClient()
        {
            return statusClient;
        }

        public String getStatusAppointment()
        {
            return statusAppointment;
        }
    }



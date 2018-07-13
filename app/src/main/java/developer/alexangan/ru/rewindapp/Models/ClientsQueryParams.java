package developer.alexangan.ru.rewindapp.Models;


public class ClientsQueryParams
    {
        private String searchMode; // boolean
        private String lat; // float
        private String lng; // float
        private String radius; // float
        private String isInCity; // boolean
        private String searchCriteria;
        private String searchQuery;

        public ClientsQueryParams(String searchMode, String isInCity, String lat, String lng, String radius, String searchCriteria, String searchQuery)
        {
            this.searchMode = searchMode;
            this.lat = lat;
            this.lng = lng;
            this.radius = radius;
            this.isInCity = isInCity;
            this.searchCriteria = searchCriteria;
            this.searchQuery = searchQuery;
        }

        public String isSearchMode()
        {
            return searchMode;
        }

        public String getLat()
        {
            return lat;
        }

        public String getLng()
        {
            return lng;
        }

        public String getRadius()
        {
            return radius;
        }

        public String isInCity()
        {
            return isInCity;
        }

        public String getSearchCriteria()
        {
            return searchCriteria;
        }

        public String getSearchQuery()
        {
            return searchQuery;
        }
    }



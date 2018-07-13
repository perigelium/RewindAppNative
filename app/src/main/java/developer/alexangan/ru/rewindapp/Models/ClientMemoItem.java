package developer.alexangan.ru.rewindapp.Models;


public class ClientMemoItem
    {
        private int customer_id;
        private String date;
        private String time;
        private String title;
        private String description;
        private String place;

        public ClientMemoItem(int customer_id, String title, String date, String time, String place, String description)
        {
            this.customer_id = customer_id;
            this.date = date;
            this.time = time;
            this.title = title;
            this.description = description;
            this.place = place;
        }

        public int getCustomer_id()
        {
            return customer_id;
        }

        public String getDate()
        {
            return date;
        }

        public String getTime()
        {
            return time;
        }

        public String getTitle()
        {
            return title;
        }

        public String getDescription()
        {
            return description;
        }

        public String getPlace()
        {
            return place;
        }
    }



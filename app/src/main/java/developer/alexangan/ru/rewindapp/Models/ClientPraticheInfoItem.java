package developer.alexangan.ru.rewindapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

public class ClientPraticheInfoItem extends RealmObject implements Parcelable
    {
        private int id_practice;
        private int id_product;
        private String product_type;
        private String product;
        private String force_date_active;
        private String force_date_lost;
        private String quantity;
        private String flag_multiline;
        private String quantity_lost;
        private String quantity_active_force;
        private String quantity_active;
        private int id_product_old;
        private String contract_type;
        private String data_creazione;
        private String canone;
        private String serial;
        private String linee;
        private String date_insert;
        private String routing;
        private String notes;
        private int id_customer;

        protected ClientPraticheInfoItem(Parcel in)
        {
            id_practice = in.readInt();
            id_product = in.readInt();
            product_type = in.readString();
            product = in.readString();
            force_date_active = in.readString();
            force_date_lost = in.readString();
            quantity = in.readString();
            flag_multiline = in.readString();
            quantity_lost = in.readString();
            quantity_active_force = in.readString();
            quantity_active = in.readString();
            id_product_old = in.readInt();
            contract_type = in.readString();
            data_creazione = in.readString();
            canone = in.readString();
            serial = in.readString();
            linee = in.readString();
            date_insert = in.readString();
            routing = in.readString();
            notes = in.readString();
            id_customer = in.readInt();
        }

        public static final Creator<ClientPraticheInfoItem> CREATOR = new Creator<ClientPraticheInfoItem>()
        {
            @Override
            public ClientPraticheInfoItem createFromParcel(Parcel in)
            {
                return new ClientPraticheInfoItem(in);
            }

            @Override
            public ClientPraticheInfoItem[] newArray(int size)
            {
                return new ClientPraticheInfoItem[size];
            }
        };

        public ClientPraticheInfoItem()
        {
        }

        public int getId_practice()
        {
            return id_practice;
        }

        public int getId_product()
        {
            return id_product;
        }

        public String getProduct_type()
        {
            return product_type;
        }

        public String getProduct()
        {
            return product;
        }

        public String getForce_date_active()
        {
            return force_date_active;
        }

        public String getForce_date_lost()
        {
            return force_date_lost;
        }

        public String getQuantity()
        {
            return quantity;
        }

        public String getFlag_multiline()
        {
            return flag_multiline;
        }

        public String getQuantity_lost()
        {
            return quantity_lost;
        }

        public String getQuantity_active_force()
        {
            return quantity_active_force;
        }

        public String getQuantity_active()
        {
            return quantity_active;
        }

        public int getId_product_old()
        {
            return id_product_old;
        }

        public String getContract_type()
        {
            return contract_type;
        }

        public String getData_creazione()
        {
            return data_creazione;
        }

        public String getCanone()
        {
            return canone;
        }

        public String getSerial()
        {
            return serial;
        }

        public String getLinee()
        {
            return linee;
        }

        public String getDate_insert()
        {
            return date_insert;
        }

        public String getRouting()
        {
            return routing;
        }

        public String getNotes()
        {
            return notes;
        }

        public int getId_customer()
        {
            return id_customer;
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(id_practice);
            dest.writeInt(id_product);
            dest.writeString(product_type);
            dest.writeString(product);
            dest.writeString(force_date_active);
            dest.writeString(force_date_lost);
            dest.writeString(quantity);
            dest.writeString(flag_multiline);
            dest.writeString(quantity_lost);
            dest.writeString(quantity_active_force);
            dest.writeString(quantity_active);
            dest.writeInt(id_product_old);
            dest.writeString(contract_type);
            dest.writeString(data_creazione);
            dest.writeString(canone);
            dest.writeString(serial);
            dest.writeString(linee);
            dest.writeString(date_insert);
            dest.writeString(routing);
            dest.writeString(notes);
            dest.writeInt(id_customer);
        }
    }



package developer.alexangan.ru.rewindapp.Models;


import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmList;
import io.realm.RealmObject;


public class ClientInfoItem extends RealmObject implements Parcelable
    {
        private int id_customer;
        private String ragione_sociale;
        private String partita_iva;
        private String phone;
        private String location;
        private String province;
        private String address;
        private String number;
        private String zipcode;
        private String acronym;
        private String fax;
        private String referente;
        private String status;
        private double latitude;
        private double longitude;

        //altuofianco_oval info
        private String data_inserimento;
        private String tipo_contratto;
        private String codice_cliente;
        private String email_shop;
        private int punti_disponibili;
        private String stato_rid;
        private String numero_referenze;
        private String mail;
        private String mobile_phone;
        private String ref_mobile;
        private String flag_borsellino;
        private String flag_preso;
        private String date_convergence;
        private String atf_id_customer;
        private String windecare_user;
        private String windecare_password;

        RealmList<ClientPraticheInfoItem> practiche_list;

        protected ClientInfoItem(Parcel in)
        {
            id_customer = in.readInt();
            ragione_sociale = in.readString();
            partita_iva = in.readString();
            phone = in.readString();
            location = in.readString();
            province = in.readString();
            address = in.readString();
            number = in.readString();
            zipcode = in.readString();
            acronym = in.readString();
            fax = in.readString();
            referente = in.readString();
            status = in.readString();
            latitude = in.readDouble();
            longitude = in.readDouble();
            data_inserimento = in.readString();
            tipo_contratto = in.readString();
            codice_cliente = in.readString();
            email_shop = in.readString();
            punti_disponibili = in.readInt();
            stato_rid = in.readString();
            numero_referenze = in.readString();
            mail = in.readString();
            mobile_phone = in.readString();
            ref_mobile = in.readString();
            flag_borsellino = in.readString();
            flag_preso = in.readString();
            date_convergence = in.readString();
            atf_id_customer = in.readString();
            windecare_user = in.readString();
            windecare_password = in.readString();
        }

        public ClientInfoItem()
        {
        }

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeInt(id_customer);
            dest.writeString(ragione_sociale);
            dest.writeString(partita_iva);
            dest.writeString(phone);
            dest.writeString(location);
            dest.writeString(province);
            dest.writeString(address);
            dest.writeString(number);
            dest.writeString(zipcode);
            dest.writeString(acronym);
            dest.writeString(fax);
            dest.writeString(referente);
            dest.writeString(status);
            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
            dest.writeString(data_inserimento);
            dest.writeString(tipo_contratto);
            dest.writeString(codice_cliente);
            dest.writeString(email_shop);
            dest.writeInt(punti_disponibili);
            dest.writeString(stato_rid);
            dest.writeString(numero_referenze);
            dest.writeString(mail);
            dest.writeString(mobile_phone);
            dest.writeString(ref_mobile);
            dest.writeString(flag_borsellino);
            dest.writeString(flag_preso);
            dest.writeString(date_convergence);
            dest.writeString(atf_id_customer);
            dest.writeString(windecare_user);
            dest.writeString(windecare_password);
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public static final Creator<ClientInfoItem> CREATOR = new Creator<ClientInfoItem>()
        {
            @Override
            public ClientInfoItem createFromParcel(Parcel in)
            {
                return new ClientInfoItem(in);
            }

            @Override
            public ClientInfoItem[] newArray(int size)
            {
                return new ClientInfoItem[size];
            }
        };

        public int getId_customer()
        {
            return id_customer;
        }

        public String getRagione_sociale()
        {
            return ragione_sociale;
        }

        public String getPartita_iva()
        {
            return partita_iva;
        }

        public String getPhone()
        {
            return phone;
        }

        public String getLocation()
        {
            return location;
        }

        public String getProvince()
        {
            return province;
        }

        public String getAddress()
        {
            return address;
        }

        public String getNumber()
        {
            return number;
        }

        public String getZipcode()
        {
            return zipcode;
        }

        public String getAcronym()
        {
            return acronym;
        }

        public String getFax()
        {
            return fax;
        }

        public String getReferente()
        {
            return referente;
        }

        public String getStatus()
        {
            return status;
        }

        public double getLatitude()
        {
            return latitude;
        }

        public double getLongitude()
        {
            return longitude;
        }

        public String getData_inserimento()
        {
            return data_inserimento;
        }

        public String getTipo_contratto()
        {
            return tipo_contratto;
        }

        public String getCodice_cliente()
        {
            return codice_cliente;
        }

        public String getEmail_shop()
        {
            return email_shop;
        }

        public int getPunti_disponibili()
        {
            return punti_disponibili;
        }

        public String getStato_rid()
        {
            return stato_rid;
        }

        public String getNumero_referenze()
        {
            return numero_referenze;
        }

        public String getMail()
        {
            return mail;
        }

        public String getMobile_phone()
        {
            return mobile_phone;
        }

        public String getRef_mobile()
        {
            return ref_mobile;
        }

        public String getFlag_borsellino()
        {
            return flag_borsellino;
        }

        public String getFlag_preso()
        {
            return flag_preso;
        }

        public String getDate_convergence()
        {
            return date_convergence;
        }

        public String getAtf_id_customer()
        {
            return atf_id_customer;
        }

        public String getWindecare_user()
        {
            return windecare_user;
        }

        public String getWindecare_password()
        {
            return windecare_password;
        }

        public RealmList<ClientPraticheInfoItem> getPractiche_list()
        {
            return practiche_list;
        }

        public void setPractiche_list(RealmList<ClientPraticheInfoItem> practiche_list)
        {
            this.practiche_list = practiche_list;
        }
    }



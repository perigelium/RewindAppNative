package developer.alexangan.ru.rewindapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmList;
import io.realm.RealmObject;

public class AppointmentInfoItem extends RealmObject implements Parcelable
{
    private String id;
    private String appuntamentiStringHour;
    private String original_appuntamentiStringHour;
    private String appuntamentiChangedTime;
    private String regSociale;
    private String representatnteLegale;
    private String mail;
    private String phone;
    private String status;
    private String tipclient;
    private String note;
    private double latitude;
    private double longitude;
    private String city;

    private String id_Chiamata;
    private String date_chiamata;
    private String note_agent;
    private String note_chiamata;
    private String note_segretaria;
    private String motivo;
    private String category;
    private String address;
    private String civico;
    private String cap;
    private String comune;
    private String provincia;
    private String sigla;
    private String contact_name;
    private String contact_phone;
    private String contact_mail;
    private int id_customer;
    private String source;
    private String acronim;

    private String tipAppuntamenti;
    private boolean appBloccati;
    private String contattiInfo;
    private RealmList<RealmString> emailList;
    private RealmList<RealmString> phoneList;
    private String id_appto_memo;
    private String evento_pers;
    private boolean dateChanged;

    private String Id;

    public AppointmentInfoItem()
    {
    }

    protected AppointmentInfoItem(Parcel in)
    {
        id = in.readString();
        appuntamentiStringHour = in.readString();
        original_appuntamentiStringHour = in.readString();
        appuntamentiChangedTime = in.readString();
        regSociale = in.readString();
        representatnteLegale = in.readString();
        mail = in.readString();
        phone = in.readString();
        status = in.readString();
        tipclient = in.readString();
        note = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        city = in.readString();
        id_Chiamata = in.readString();
        date_chiamata = in.readString();
        note_agent = in.readString();
        note_chiamata = in.readString();
        note_segretaria = in.readString();
        motivo = in.readString();
        category = in.readString();
        address = in.readString();
        civico = in.readString();
        cap = in.readString();
        comune = in.readString();
        provincia = in.readString();
        sigla = in.readString();
        contact_name = in.readString();
        contact_phone = in.readString();
        contact_mail = in.readString();
        id_customer = in.readInt();
        source = in.readString();
        acronim = in.readString();
        tipAppuntamenti = in.readString();
        appBloccati = in.readByte() != 0;
        contattiInfo = in.readString();
        id_appto_memo = in.readString();
        evento_pers = in.readString();
        dateChanged = in.readByte() != 0;
        Id = in.readString();
    }

    public static final Creator<AppointmentInfoItem> CREATOR = new Creator<AppointmentInfoItem>()
    {
        @Override
        public AppointmentInfoItem createFromParcel(Parcel in)
        {
            return new AppointmentInfoItem(in);
        }

        @Override
        public AppointmentInfoItem[] newArray(int size)
        {
            return new AppointmentInfoItem[size];
        }
    };

    public String getId()
    {
        return id;
    }

    public String getAppuntamentiStringHour()
    {
        return appuntamentiStringHour;
    }

    public String getOriginal_appuntamentiStringHour()
    {
        return original_appuntamentiStringHour;
    }

    public String getAppuntamentiChangedTime()
    {
        return appuntamentiChangedTime;
    }

    public String getRegSociale()
    {
        return regSociale;
    }

    public String getRepresentatnteLegale()
    {
        return representatnteLegale;
    }

    public String getMail()
    {
        return mail;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getStatus()
    {
        return status;
    }

    public String getTipclient()
    {
        return tipclient;
    }

    public String getNote()
    {
        return note;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public String getCity()
    {
        return city;
    }

    public String getId_Chiamata()
    {
        return id_Chiamata;
    }

    public String getString_chiamata()
    {
        return date_chiamata;
    }

    public String getNote_agent()
    {
        return note_agent;
    }

    public String getNote_chiamata()
    {
        return note_chiamata;
    }

    public String getNote_segretaria()
    {
        return note_segretaria;
    }

    public String getMotivo()
    {
        return motivo;
    }

    public String getCategory()
    {
        return category;
    }

    public String getAddress()
    {
        return address;
    }

    public String getCivico()
    {
        return civico;
    }

    public String getCap()
    {
        return cap;
    }

    public String getComune()
    {
        return comune;
    }

    public String getProvincia()
    {
        return provincia;
    }

    public String getSigla()
    {
        return sigla;
    }

    public String getContact_name()
    {
        return contact_name;
    }

    public String getContact_phone()
    {
        return contact_phone;
    }

    public String getContact_mail()
    {
        return contact_mail;
    }

    public int getId_customer()
    {
        return id_customer;
    }

    public String getSource()
    {
        return source;
    }

    public String getAcronim()
    {
        return acronim;
    }

    public String getTipAppuntamenti()
    {
        return tipAppuntamenti;
    }

    public boolean isAppBloccati()
    {
        return appBloccati;
    }

    public String getContattiInfo()
    {
        return contattiInfo;
    }

    public RealmList<RealmString> getEmailList()
    {
        return emailList;
    }

    public RealmList<RealmString> getPhoneList()
    {
        return phoneList;
    }

    public String getId_appto_memo()
    {
        return id_appto_memo;
    }

    public String getEvento_pers()
    {
        return evento_pers;
    }

    public boolean isStringChanged()
    {
        return dateChanged;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(id);
        dest.writeString(appuntamentiStringHour);
        dest.writeString(original_appuntamentiStringHour);
        dest.writeString(appuntamentiChangedTime);
        dest.writeString(regSociale);
        dest.writeString(representatnteLegale);
        dest.writeString(mail);
        dest.writeString(phone);
        dest.writeString(status);
        dest.writeString(tipclient);
        dest.writeString(note);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(city);
        dest.writeString(id_Chiamata);
        dest.writeString(date_chiamata);
        dest.writeString(note_agent);
        dest.writeString(note_chiamata);
        dest.writeString(note_segretaria);
        dest.writeString(motivo);
        dest.writeString(category);
        dest.writeString(address);
        dest.writeString(civico);
        dest.writeString(cap);
        dest.writeString(comune);
        dest.writeString(provincia);
        dest.writeString(sigla);
        dest.writeString(contact_name);
        dest.writeString(contact_phone);
        dest.writeString(contact_mail);
        dest.writeInt(id_customer);
        dest.writeString(source);
        dest.writeString(acronim);
        dest.writeString(tipAppuntamenti);
        dest.writeByte((byte) (appBloccati ? 1 : 0));
        dest.writeString(contattiInfo);
        dest.writeString(id_appto_memo);
        dest.writeString(evento_pers);
        dest.writeByte((byte) (dateChanged ? 1 : 0));
        dest.writeString(Id);
    }
}



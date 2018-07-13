package developer.alexangan.ru.rewindapp.Models;


import io.realm.RealmObject;

public class ProfileInfoItem extends RealmObject
{
    private int id_user;
    private int id_agency;
    private int id_agent;
    private String surname;
    private String name;
    private String address;
    private String id_city;
    private String city;
    private String acronym;
    private String province;
    private String region;
    private String location;
    private String zipcode;
    private String number;
    private String phone;
    private String mobile;
    private String fax;
    private String mail;
    private String notes;
    private String role;
    private String vat;//tva
    private String login_first;
    private String login_last;
    private String date_start;
    private String date_end;
    private String profile;
    private String class_vf;
    private int is_in_top;
    private String avatar;


    public int getId_user()
    {
        return id_user;
    }

    public int getId_agency()
    {
        return id_agency;
    }

    public int getId_agent()
    {
        return id_agent;
    }

    public String getSurname()
    {
        return surname;
    }

    public String getName()
    {
        return name;
    }

    public String getAddress()
    {
        return address;
    }

    public String getNumber()
    {
        return number;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getMobile()
    {
        return mobile;
    }

    public String getFax()
    {
        return fax;
    }

    public String getMail()
    {
        return mail;
    }

    public String getNotes()
    {
        return notes;
    }

    public String getRole()
    {
        return role;
    }

    public String getVat()
    {
        return vat;
    }

    public String getLogin_first()
    {
        return login_first;
    }

    public String getLogin_last()
    {
        return login_last;
    }

    public String getDate_start()
    {
        return date_start;
    }

    public String getDate_end()
    {
        return date_end;
    }

    public String getProfile()
    {
        return profile;
    }

    public String getClass_vf()
    {
        return class_vf;
    }

    public int getIs_in_top()
    {
        return is_in_top;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getCity()
    {
        return city;
    }

    public String getProvince()
    {
        return province;
    }

    public String getRegion()
    {
        return region;
    }

    public String getLocation()
    {
        return location;
    }

    public String getAcronym()
    {
        return acronym;
    }

    public String getZipcode()
    {
        return zipcode;
    }
}



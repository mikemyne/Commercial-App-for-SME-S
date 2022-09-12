package com.example.e_commerceapp.Models;

public class User {

    private String areaofresidence, email, fullname, idnumber,phonenumber,profileimageurl,type,userid;

    public User() {
    }

    public User(String areaofresidence, String email, String fullname, String idnumber, String phonenumber, String profileimageurl, String type, String userid) {
        this.areaofresidence = areaofresidence;
        this.email = email;
        this.fullname = fullname;
        this.idnumber = idnumber;
        this.phonenumber = phonenumber;
        this.profileimageurl = profileimageurl;
        this.type = type;
        this.userid = userid;
    }

    public String getAreaofresidence() {
        return areaofresidence;
    }

    public void setAreaofresidence(String areaofresidence) {
        this.areaofresidence = areaofresidence;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getProfileimageurl() {
        return profileimageurl;
    }

    public void setProfileimageurl(String profileimageurl) {
        this.profileimageurl = profileimageurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}

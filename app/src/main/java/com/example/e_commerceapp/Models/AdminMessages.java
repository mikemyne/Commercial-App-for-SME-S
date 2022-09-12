package com.example.e_commerceapp.Models;

public class AdminMessages {

    private String date, message, senderid, profileimageurl,sendername;

    public AdminMessages() {
    }

    public AdminMessages(String date, String message, String senderid, String profileimageurl, String sendername) {
        this.date = date;
        this.message = message;
        this.senderid = senderid;
        this.profileimageurl = profileimageurl;
        this.sendername = sendername;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getProfileimageurl() {
        return profileimageurl;
    }

    public void setProfileimageurl(String profileimageurl) {
        this.profileimageurl = profileimageurl;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }
}

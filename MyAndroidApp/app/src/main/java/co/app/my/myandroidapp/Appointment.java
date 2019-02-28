package co.app.my.myandroidapp;

import java.security.Key;

/**
 * Created by geros on 11/19/2016.
 */
public class Appointment {
    String Status = "";
    String Key ="";
    String Address="";
    String Date="";
    String Place="";
    String Time="";
    String Participants="";

    public void Appointment(

    ){}

    public String getAddress(){ return Address; }
    public String getKey(){ return Key; }
    public String getStatus(){ return Status; }
    public String getPlace(){
        return Place;
    }
    public String getDate(){
        return Date;
    }
    public String getTime(){
        return Time;
    }
    public String getParticipants(){return Participants;}

    public void setAddress(String x){this.Address = x;}
}

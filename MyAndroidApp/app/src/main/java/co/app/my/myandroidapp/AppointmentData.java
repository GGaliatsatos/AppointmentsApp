package co.app.my.myandroidapp;

/**
 * Created by geros on 11/18/2016.
 */
public class AppointmentData {
    String address;
    String place;
    String date;
    String time;

    public AppointmentData(){
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public String getAddress(){
        return address;
    }

    public String getPlace(){
        return place;
    }
    public String getDate(){
        return date;
    }
    public String getTime(){
        return time;
    }
}

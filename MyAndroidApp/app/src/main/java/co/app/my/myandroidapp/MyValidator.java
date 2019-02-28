package co.app.my.myandroidapp;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by geros on 10/2/2016.
 */
public class MyValidator {

    // variable to store parsed input date



    public static boolean validateDateTime(String inputDate, String inputTime) {

        Date date = null;
        String MergedInput;
        try {
            SimpleDateFormat DateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat.setLenient(false);
            date = DateFormat.parse(inputDate);
            Log.v("message","passed day format");

        } catch (ParseException e) {
            return false;
        }//the input date format and value is valid

        Date time;
        try {
            SimpleDateFormat TimeFormat = new SimpleDateFormat("HH:mm");
            TimeFormat.setLenient(false);
            time = TimeFormat.parse(inputTime);
            Log.v("message","passed hour format");
        } catch (ParseException e) {
            return false;
        }//The input time format and value is valid
        Date DateTime;
        try {
            SimpleDateFormat DateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            DateTimeFormat.setLenient(false);
            MergedInput = inputDate + " " + inputTime;
            DateTime = DateTimeFormat.parse(MergedInput);

        } catch (ParseException e) {// Normally unreachable catch
            e.printStackTrace();
            return false;
        }


        // validate that input date and Time have not passed

        //date to calendar conversion
        Calendar validDateTime = Calendar.getInstance();
        validDateTime.setTime(DateTime);
        //get current date (info)
        Calendar today = Calendar.getInstance();
        //compare the values
        if (validDateTime.compareTo(today) <= 0)
            return false;
        else
            Log.v("message","passed everything");

        return true;
    }

    public static boolean validateAddress(String address) {
        if(address.matches("[0-9a-zA-Zα-ωΑ-Ω -'.,άέήίόύώϊϋΐΰ]*")){// δεν περναει κειμενο με τονους
            Log.v("message","valid address characters");
            return true;
        }

        Log.v("message","invalid address characters");
        return false;
    }

    public static boolean validateName(String name) {
        if(name.matches("[a-zA-ZΑ-Ω -'.]*")){
            Log.v("message","valid name characters");
            return true;
        }

        Log.v("message","invalid address characters");
        return false;
    }

    public static boolean validateEmail(String mail){
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        if (mail.matches(EMAIL_REGEX)){
            return true;

        }
        return false;
    }


    // checking if argument contains valid characters(alphabetical and numbers)
    public static boolean validateLoginCredentials(String arg1, String arg2){
        if(!arg1.matches("[0-9a-zA-Z]*"))
            return false;
        if(!arg2.matches("[0-9a-zA-Z]*"))
            return false;
        return true;
    }

}


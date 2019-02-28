package co.app.my.myandroidapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScheduleAppointment extends AppCompatActivity {

    Firebase ref;
    EditText dateEditText;
    EditText timeEditText;
    EditText addressEditText;
    EditText placeEditText;
    TextView dateTV;
    TextView TimeTV;
    TextView addressTV;
    TextView placeTV;
    private LocationManager locationManager;
    private LocationListener locationListener;

    Button getLocationButton;
    Button confirmAppointmentButton;
    ListView participantsCheckList;
    ArrayList<String> Selected;
    DatabaseHelper myDB;
    String whoAmI;
    Cursor cursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_schedule_appointment);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_menu);
        setSupportActionBar(myToolbar);


        //Initialize UI input elements
        participantsCheckList = (ListView) findViewById(R.id.CheckList);
        dateEditText = (EditText) findViewById(R.id.insert_date_EditText);
        timeEditText = (EditText) findViewById(R.id.insert_time_EditText);
        addressEditText = (EditText) findViewById(R.id.insert_address_EditText);
        placeEditText = (EditText) findViewById(R.id.insert_place_EditText);


        getLocationButton = (Button) findViewById(R.id.Get_Location_Button);
        confirmAppointmentButton = (Button) findViewById(R.id.confirm_appointment);

        // location manager init
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String address = addresses.get(0).getAddressLine(0);
                    Log.e("success", address);
                    addressEditText.setText(address);
                    try {
                        locationManager.removeUpdates(locationListener);
                        Log.e("Removed updates", "location listener un-subscribed");
                    } catch (SecurityException e) {
                        Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");

                    }


                    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Error", "location");
                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                finish();
                startActivity(i);
            }
        };


        Selected = new ArrayList<String>();
        myDB = new DatabaseHelper(this);

        cursor = myDB.WhoAmI();
        while (cursor.moveToNext()) {
            whoAmI = cursor.getString(0);
            Log.e("I am", whoAmI);
        }

        viewAllContacts();
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.

        SetListeners();
        configureButton();
        LinearLayout TapLayout;

// clickables to hide keyboard
        TapLayout = (LinearLayout) findViewById(R.id.middle);
        TapLayout.setClickable(true);
        TapLayout.setSoundEffectsEnabled(false);
        TapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeys();
            }
        });

        dateTV = (TextView) findViewById(R.id.insert_date_label);
        dateTV.setClickable(true);
        dateTV.setSoundEffectsEnabled(false);
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeys();
            }
        });

        placeTV = (TextView) findViewById(R.id.insert_place_label);
        placeTV.setClickable(true);
        placeTV.setSoundEffectsEnabled(false);
        placeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeys();
            }
        });

        addressTV = (TextView) findViewById(R.id.insert_address_label);
        addressTV.setClickable(true);
        addressTV.setSoundEffectsEnabled(false);
        addressTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeys();
            }
        });

        TimeTV = (TextView) findViewById(R.id.insert_time_label);
        TimeTV.setClickable(true);
        TimeTV.setSoundEffectsEnabled(false);
        TimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeys();
            }
        });
//end of clickables to hide keyboard


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 10:
                configureButton();
                break;
            default:
                break;
        }
    }

    private void configureButton() {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
//                        , 10);
//            }
//            return;
//        }

        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("button", "pressed");
                //noinspection MissingPermission

                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (ActivityCompat.checkSelfPermission(ScheduleAppointment.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ScheduleAppointment.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                                    , 10);
                        }
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5, locationListener);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.GPSwait), Toast.LENGTH_SHORT).show();
                }else  {
//                    if (ActivityCompat.checkSelfPermission(ScheduleAppointment.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET}, 20);
//                        }return;
//                    }
                    Log.e("internet", "searching");
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);// ALWAYS RECEIVES null...
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Networkwait), Toast.LENGTH_SHORT).show();

                }



            }
        });

    }
    public void hideKeys(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void SetListeners(){

        confirmAppointmentButton.setOnClickListener( new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 ref = new Firebase(Constants.FBroot);
                 Firebase appointmentsRef= ref.child(Constants.appointments);
                 Firebase participantsRef= ref.child(Constants.participants);
                 Firebase appointmentlistsRef = ref.child(Constants.appointmentLists);
                 String date;
                 String time;
                 String address;
                 String place;
                 String subject;
                 boolean isValid = true;
                 Log.v("message", "pressed the button");
                 date = dateEditText.getText().toString();
                 time = timeEditText.getText().toString();
                 address = addressEditText.getText().toString();
                 place = placeEditText.getText().toString();

                 if(Selected.isEmpty()|| date.isEmpty()|| time.isEmpty()|| address.isEmpty()|| place.isEmpty() ){
                     isValid=false;
                     Toast.makeText(getApplicationContext(), getResources().getString(R.string.MissingFields), Toast.LENGTH_SHORT).show();
                 }else {
                     ArrayList<String> errors = new ArrayList<String>();
                     errors.clear();
                     boolean isValidDateTime = MyValidator.validateDateTime(date, time);
                     if (isValidDateTime == true)
                         Log.v("message", "valid input");

                     else {
                         Toast.makeText(getApplicationContext(), getResources().getString(R.string.invalidDatetime), Toast.LENGTH_SHORT).show();
                         Log.v("message", "invalid input");
                         errors.add(getResources().getString(R.string.MissingDate));
                         isValid = false;
                     }


                     boolean isValidAdress = MyValidator.validateAddress(address);
                     if (isValidAdress == true) {
                         Log.v("message", "valid input");
                     } else {
                         Log.v("message", "invalid input");
                         errors.add(getResources().getString(R.string.InvalidAddress));
                         isValid = false;
                     }


                 }




                 if (isValid == true){
                     //Generate a Unique ID with push.
                     Firebase newAppointmentRef = appointmentsRef.push();
                     Map<String, String> appointment = new HashMap<String, String>();
                     appointment.put("date", date);
                     appointment.put("time",time);
                     appointment.put("address",address);
                     appointment.put("place",place);

                     //commit the new appointment record
                     newAppointmentRef.setValue(appointment);
                     // Get the unique ID generated by push()
                     String postId = newAppointmentRef.getKey();
                     //add the participants and their status
                     participantsRef = participantsRef.child(postId);
                     for(int i = 0; i < Selected.size(); i++){
                         Map<String, String> participant = new HashMap<String, String>();
                         String user = Selected.get(i);
                         participant.put("name",user);
                         participant.put("status","pending");
                         participantsRef.child(user).setValue(participant);
                         appointmentlistsRef.child(user).child(postId).setValue("pending");
                     }
                     Map<String, String> host = new HashMap<String, String>();
                     host.put("name",whoAmI);
                     host.put("status","Proposed");
                     participantsRef.child(whoAmI).setValue(host);
                     appointmentlistsRef.child(whoAmI).child(postId).setValue("Proposed");
                     Toast.makeText(getApplicationContext(), getResources().getString(R.string.AppointmentScheduled), Toast.LENGTH_SHORT).show();

                 }
             }
        });
    }

    public  void viewAllContacts(){
        Cursor res = myDB.getAllUsernames();
        if(res.getCount() == 0) {
            // show message
            showMessage(getResources().getString(R.string.contactsEmpty),getResources().getString(R.string.contactsEmptyAdvice));
            return;
        }
        //create arraylist to store each contact retrieved by the query
        ArrayAdapter<ContactSelector> adapter;
        List<ContactSelector> ContactsList=new ArrayList<ContactSelector>();


        while(res.moveToNext()){
            String SingleContact = new String();
            SingleContact = res.getString(res.getColumnIndex("USERNAME"));
            ContactsList.add(new ContactSelector(SingleContact));
        }

        adapter=new CustomAdapter(this,ContactsList);
        participantsCheckList.setAdapter(adapter);
        participantsCheckList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView label = (TextView) view.getTag(R.id.label);
                CheckBox checkbox = (CheckBox) view.getTag(R.id.check);
                String value = label.getText().toString();
                Log.v("pressed","checkbox "+ value);
                checkbox.setChecked(!checkbox.isChecked());
                if(checkbox.isChecked()){
                    Selected.add(value);
                    Log.v("added",value);
                }else{
                    Selected.remove(value);
                    Log.v("removed",value);
                }

                Log.v("message", value);

            }
        });


    }
//    private boolean isCheckedOrNot(CheckBox checkbox) {
//        if(checkbox.isChecked())
//            return true;
//        else
//            return false;
//    }


    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_contacts){
            Intent intent = new Intent(ScheduleAppointment.this, MainActivity.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.menu_action_add_contact){
            Intent intent = new Intent(ScheduleAppointment.this, AddContactActivity.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.menu_action_appointments){
            Intent intent = new Intent(ScheduleAppointment.this, AppointmentsActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_quit) {
            finish();
            this.finishAffinity();
        }else if(id == R.id.menu_action_logout){
            myDB.deleteCredentials();
            myDB.deleteAllContacts();
            Intent intent = new Intent(ScheduleAppointment.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.menu_action_swap_lang){

            Locale current = getResources().getConfiguration().locale;
            Log.e("locale ",current.toString());

            if(current.toString().equals("en_us")){

                Locale myLocale = new Locale("el");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                getResources().updateConfiguration(conf, null);
                finish();
                startActivity(new Intent(getBaseContext() , ScheduleAppointment.class));
            }else{

                Locale myLocale = new Locale("en_us");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                getResources().updateConfiguration(conf, null);
                finish();
                startActivity(new Intent(getBaseContext() , ScheduleAppointment.class));
            }

        }
        return true;
    }
}

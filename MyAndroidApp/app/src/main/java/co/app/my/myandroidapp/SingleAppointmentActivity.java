package co.app.my.myandroidapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class SingleAppointmentActivity extends AppCompatActivity {
    private Firebase ref;
    private String User = "mitsos12";
    private TextView addressTextView;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView placeTextView;
    private TextView Status;
    private ListView Invited;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_invited = new ArrayList<>();
    private Button Accept;
    private Button Decline;
    DatabaseHelper myDB;
    String whoAmI;
    Cursor cursor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_appointment);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_menu);
        setSupportActionBar(myToolbar);
        Intent intent = getIntent();
        final String id = intent.getStringExtra("appointment_id");
        addressTextView = (TextView)findViewById(R.id.address_Text);
        dateTextView = (TextView)findViewById(R.id.date_Text);
        timeTextView = (TextView)findViewById(R.id.time_Text);
        placeTextView = (TextView)findViewById(R.id.place_Text);
        Status = (TextView)findViewById(R.id.status);
        Accept = (Button)findViewById(R.id.btn_accept);
        Decline = (Button)findViewById(R.id.btn_decline);
        myDB = new DatabaseHelper(this);

        cursor = myDB.WhoAmI();
        while(cursor.moveToNext()){
            User = cursor.getString(0);
            Log.e("I am",User);
        }

        Log.e("id",id);
        Invited = (ListView)findViewById(R.id.participants);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_invited);
        Invited.setAdapter(arrayAdapter);


        ref = new Firebase(Constants.FBroot);
        Firebase appointmentListsRef = ref.child(Constants.appointmentLists).child(User).child(id);
        appointmentListsRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               Status.setText("("+dataSnapshot.getValue().toString()+")");
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {

           }
        });

        Firebase dataRef = ref.child(Constants.appointments).child(id);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppointmentData appointment = dataSnapshot.getValue(AppointmentData.class);
                addressTextView.setText(appointment.getAddress()) ;
                dateTextView.setText(appointment.getDate());
                timeTextView.setText(appointment.getTime());
                placeTextView.setText(appointment.getPlace());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Firebase participantsRef = ref.child(Constants.participants).child(id);
        participantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                for (DataSnapshot current: dataSnapshot.getChildren()){
                    Participant person = current.getValue(Participant.class);
                    String name = person.getName();
                    String status = person.getStatus();
                    String list_item = name+" ("+status+")";
                    set.add(list_item);

                }
                list_of_invited.clear();
                list_of_invited.addAll(set);
                arrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(Constants.participants).child(id).child(User).child("status").setValue("accepted");
                ref.child(Constants.appointmentLists).child(User).child(id).setValue("accepted");
                Intent intent = new Intent(SingleAppointmentActivity.this, AppointmentsActivity.class);
                finish();
                startActivity(intent);
            }
        });

        Decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(Constants.participants).child(id).child(User).child("status").setValue("declined");
                ref.child(Constants.appointmentLists).child(User).child(id).setValue(null);
                Intent intent = new Intent(SingleAppointmentActivity.this, AppointmentsActivity.class);
                finish();
                startActivity(intent);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_action_add_contact){
            Intent intent = new Intent(SingleAppointmentActivity.this, AddContactActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_contacts) {
            Intent intent = new Intent(SingleAppointmentActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_schedule_appointment) {
            Intent intent = new Intent(SingleAppointmentActivity.this, ScheduleAppointment.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.menu_action_appointments){
            Intent intent = new Intent(SingleAppointmentActivity.this, AppointmentsActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_quit) {
            finish();
            this.finishAffinity();
        }else if(id == R.id.menu_action_logout){
            myDB.deleteCredentials();
            myDB.deleteAllContacts();
            Intent intent = new Intent(SingleAppointmentActivity.this, LoginActivity.class);
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
                startActivity(new Intent(getBaseContext() , SingleAppointmentActivity.class));
            }else{

                Locale myLocale = new Locale("en_us");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                getResources().updateConfiguration(conf, null);
                finish();
                startActivity(new Intent(getBaseContext() , SingleAppointmentActivity.class));
            }

        }
        return true;
    }
}

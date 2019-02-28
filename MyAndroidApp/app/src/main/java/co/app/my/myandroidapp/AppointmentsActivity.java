package co.app.my.myandroidapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

public class AppointmentsActivity extends AppCompatActivity {
    DatabaseHelper myDB;
    String whoAmI;
    Cursor cursor;
    Firebase ref;


    private ListView appointmetsListView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_appointments = new ArrayList<>();


    //static public ArrayList<Appointment> AppointmentsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_menu);

        myDB = new DatabaseHelper(this);

        cursor = myDB.WhoAmI();
        while(cursor.moveToNext()){
            whoAmI = cursor.getString(0);
            Log.e("I am",whoAmI);
        }
        setSupportActionBar(myToolbar);
        appointmetsListView = (ListView)findViewById(R.id.my_appointments_list);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_appointments);
        appointmetsListView.setAdapter(arrayAdapter);
        ref = new Firebase(Constants.FBroot);
        Firebase appointmentlistsRef = ref.child(Constants.appointmentLists).child(whoAmI);
        appointmentlistsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()){

                    set.add(((DataSnapshot)i.next()).getKey());

                }
                list_of_appointments.clear();
                list_of_appointments.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        appointmetsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),SingleAppointmentActivity.class);
                intent.putExtra("appointment_id",((TextView)view).getText().toString() );
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_action_contacts){
            Intent intent = new Intent(AppointmentsActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_schedule_appointment) {
            Intent intent = new Intent(AppointmentsActivity.this, ScheduleAppointment.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.menu_action_logout) {
            myDB.deleteCredentials();
            myDB.deleteAllContacts();
            Intent intent = new Intent(AppointmentsActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.menu_action_add_contact){
            Intent intent = new Intent(AppointmentsActivity.this, AddContactActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_quit) {
            finish();
            this.finishAffinity();
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
                startActivity(new Intent(getBaseContext() , AppointmentsActivity.class));
            }else{

                Locale myLocale = new Locale("en_us");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                getResources().updateConfiguration(conf, null);
                finish();
                startActivity(new Intent(getBaseContext() , AppointmentsActivity.class));
            }

        }
        return true;
    }



//    public void viewAllAppointments() {
//        LayoutInflater inflaterParent = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout AllAppointments = (LinearLayout) inflaterParent.inflate(R.layout.appointments_scroll, null);
//        int k = 0;
//        if(AppointmentsList.isEmpty() == false)
//            while (Step1 == false);
//        for(Appointment object : AppointmentsList){
//            Log.e(" Inserted loop: ", "indeed");
//            Log.e(" address  ",String.valueOf(AppointmentsList.size()));
//            Appointment myAppointment = new Appointment();
//            myAppointment = AppointmentsList.get(k);
//
//
//            View SingleAppointment= inflaterParent.inflate(R.layout.appointment_field, null);
//            TextView address = (TextView)SingleAppointment.findViewById(R.id.adress_Text);
//            TextView date = (TextView)SingleAppointment.findViewById(R.id.date_Text);
//            TextView place = (TextView)SingleAppointment.findViewById(R.id.place_Text);
//            TextView time = (TextView)SingleAppointment.findViewById(R.id.time_Text);
//            TextView participants = (TextView)SingleAppointment.findViewById(R.id.participants_Text);
//            SingleAppointment.setId(k);
//            address.setText(myAppointment.Address);
//            date.setText(object.Date);
//            place.setText(object.Place);
//            time.setText(object.Time);
//            participants.setText(object.Participants);
//            AllAppointments.addView(SingleAppointment);
//            Log.e(" address ",AppointmentsList.get(k).Address );
//            k++;
//
//        }
//        my_appointments.addView(AllAppointments);
//    }




//    public void initializeFirebaseData(){
//        ref = new Firebase(Constants.FBroot);
//
//
//        final Firebase appointmentlistsRef = ref.child(Constants.appointmentLists).child(User);
//        appointmentlistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            String participants="";
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot current: dataSnapshot.getChildren()){
//
//
//                    Appointment addition = new Appointment();
//                    addition.Key = current.getKey();
//                    addition.Status = current.getValue().toString();
//
//                    Log.e(" addition: ", addition.getKey()+" "+addition.getStatus());
//                    AppointmentsList.add(addition);
//                }
//                getFirebaseData();
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
//
//    }
//
//    public void getFirebaseData() {
//        //get appointment data
//        i = 0;
//        for (Appointment item : AppointmentsList) {
//
//            Firebase appointmentsRef = ref.child(Constants.appointments).child(item.getKey());
//            appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot appointmentDataSnapshot) {
//                    Log.e(" inserted ",String.valueOf(i));
//                    AppointmentData appointment = appointmentDataSnapshot.getValue(AppointmentData.class);
//                    AppointmentsList.get(i).Address =appointment.getAddress();
//                    AppointmentsList.get(i).Place = appointment.getPlace();
//                    AppointmentsList.get(i).Date = appointment.getDate();
//                    AppointmentsList.get(i).Time = appointment.getTime();
////                            appointment = appointmentDataSnapshot.getValue(AppointmentData.class);
////                            addition.Address = appointment.getAddress();
////                            addition.Place = appointment.getPlace();
////                            addition.Date = appointment.getDate();
////                            addition.Time = appointment.getTime();
//
//                    i++;
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//
//                }
//            });
//
//
//        }
//        Step1 = true;
//        viewAllAppointments();
//    }

//            for(Appointment item2: AppointmentsList){
//                Firebase participantsRef= ref.child(Constants.participants).child(item2.getKey());
//                participantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//
//
//                    @Override
//                    public void onDataChange(DataSnapshot participantsSnapshot) {
//                            for(DataSnapshot participant : participantsSnapshot.getChildren()){
//                                Participant person = participant.getValue(Participant.class);
//                                participants += person.getName();
//                                participants += "("+person.getStatus()+") ,";
//                            }
//                            Log.e(" appointers: ",participants +"\n\n");
//                            addition.Participants = participants;
//
//                    }
//
//                    @Override
//                    public void onCancelled(FirebaseError firebaseError) {
//
//                    }
//                });}





        // get participants data

}

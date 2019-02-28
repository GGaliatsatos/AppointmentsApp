package co.app.my.myandroidapp;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DeleteContactDialogFragment.NoticeDialogListener {
    Button GoToAddContact;
    ScrollView my_contacts;
    DatabaseHelper myDB;
    String SelectedContact;
    String whoAmI;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Initialize UI input elements
        GoToAddContact = (Button)findViewById(R.id.go_to_add_contact);
        my_contacts = (ScrollView)findViewById(R.id.my_contacts);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_menu);
        setSupportActionBar(myToolbar);
        ButtonsSetListeners();
        //instance of the DB
        myDB = new DatabaseHelper(this);

        cursor = myDB.WhoAmI();
        while(cursor.moveToNext()){
            whoAmI = cursor.getString(0);
            Log.e("I am",whoAmI);
        }
        viewAllRecords();


    }

    @Override
    protected void onResume() {
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
        if(id == R.id.menu_action_add_contact){
            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_schedule_appointment) {
            Intent intent = new Intent(MainActivity.this, ScheduleAppointment.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.menu_action_appointments){
            Intent intent = new Intent(MainActivity.this, AppointmentsActivity.class);
            finish();
            startActivity(intent);
        }else if(id == R.id.menu_action_logout){
            myDB.deleteCredentials();
            myDB.deleteAllContacts();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
                startActivity(new Intent(getBaseContext() , MainActivity.class));
            }else{

                Locale myLocale = new Locale("en_us");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                getResources().updateConfiguration(conf, null);
                finish();
                startActivity(new Intent(getBaseContext() , MainActivity.class));
            }

        }
        return true;
    }

    public void ButtonsSetListeners(){
        GoToAddContact.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                        finish();
                        startActivity(intent);

                    }
                }
        );
    }

    public void viewAllRecords(){
        LayoutInflater inflaterParent = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout AllContacts = (LinearLayout) inflaterParent.inflate(R.layout.contacts_scroll, null);


        Cursor res = myDB.getAllData();
        if(res.getCount() == 0) {
            // show message
            showMessage(getResources().getString(R.string.contactsEmpty),getResources().getString(R.string.contactsEmptyAdvice));
            return;
        }
        //create arrayList to store each contact retrieved by the query
        ArrayList<Contact> contactsList = new ArrayList<Contact>();
        int i=0;
        while(res.moveToNext()){
            //store retrieved row data on an instance
            Contact contact = new Contact();
            contact.SetID(res.getString(0));
            contact.SetName(res.getString(1));
            contact.SetSurname(res.getString(2));
            contact.SetEmail(res.getString(3));
            contactsList.add(contact);
            // generate the GUI element for current instance
            View SingleContact= inflaterParent.inflate(R.layout.contact_field, null);
            final TextView username = (TextView)SingleContact.findViewById(R.id.contact_username);
            TextView name = (TextView)SingleContact.findViewById(R.id.contact_name);
            TextView surname = (TextView)SingleContact.findViewById(R.id.contact_surname);
            final TextView email = (TextView)SingleContact.findViewById(R.id.contact_email);
            SingleContact.setClickable(true);
            SingleContact.setId(i);
            username.setText(contact.USERNAME);
            name.setText(contact.Name);
            surname.setText(contact.Surname);
            email.setText(contact.Email);
            AllContacts.addView(SingleContact);
            SingleContact.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SelectedContact = username.getText().toString();
                    Log.v("MSG"," Are you sure you want to delete "+ SelectedContact );
                    Bundle bundle = new Bundle();
                    DeleteContactDialogFragment DeleteContact = new DeleteContactDialogFragment();
                    bundle.putString("message", SelectedContact );
                    DeleteContact.setArguments(bundle);
                    DeleteContact.show(getFragmentManager(),"Delete_Contact");


                    //myDB.deleteContact(email.getText().toString());
                    return  true;
                }
            });
            i++;
        }

//        StringBuffer buffer = new StringBuffer();
//        res.moveToFirst();
//        int i =0;
//        while (res.moveToNext()) {
//            buffer.append("ID:"+contactsList.get(i).ID+ "\n");
//            buffer.append("Name:"+ contactsList.get(i).Name+"\n");
//            buffer.append("Surname :"+ contactsList.get(i).Surname+"\n");
//            buffer.append("Email :"+contactsList.get(i).Email+"\n\n");
//            i++;
//        }
//
//        // Show all data
//        showMessage("Data",buffer.toString());
        my_contacts.addView(AllContacts);

    }
    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment DeleteContact) {
        Integer del;
        del = myDB.deleteContact(SelectedContact);
        // delete contact from firebase
        Firebase contactsRef = new Firebase(Constants.FBroot).child(Constants.contacts).child(whoAmI);
        contactsRef.child(SelectedContact).removeValue();

        // Refresh main activity upon erasing the record
        Intent intent = getIntent();
        finish();
        startActivity(intent);

    }


}

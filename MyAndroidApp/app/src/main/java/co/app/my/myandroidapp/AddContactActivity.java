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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.firebase.client.ValueEventListener;

import java.util.Locale;

public class AddContactActivity extends AppCompatActivity {
    Button SearchUsers;
    EditText BrowsedUser;
    Firebase ref;
    DatabaseHelper myDB;
    Cursor res;
    String whoAmI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_add_contact);
        //Initialize UI elements
        SearchUsers = (Button) findViewById(R.id.search_users_Button);
        BrowsedUser = (EditText)findViewById(R.id.search_users_EditText);
        myDB = new DatabaseHelper(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_menu);

        res = myDB.getCredentials();
        if(res.getCount()==1) {
            while(res.moveToNext()){
                whoAmI = res.getString(0);
                Log.e("I am",whoAmI);
            }
        }

        setSupportActionBar(myToolbar);
        SetUIListeners();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
//menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_action_contacts){
            Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_schedule_appointment) {
            Intent intent = new Intent(AddContactActivity.this, ScheduleAppointment.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_appointments) {
            Intent intent = new Intent(AddContactActivity.this, AppointmentsActivity.class);
            finish();
            startActivity(intent);
        }else if (id == R.id.menu_action_logout) {
            myDB.deleteCredentials();
            myDB.deleteAllContacts();
            Intent intent = new Intent(AddContactActivity.this, LoginActivity.class);
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
                startActivity(new Intent(getBaseContext() , AddContactActivity.class));
            }else{

                Locale myLocale = new Locale("en_us");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                getResources().updateConfiguration(conf, null);
                finish();
                startActivity(new Intent(getBaseContext() , AddContactActivity.class));
            }

        }
        return true;
    }



        public void SetUIListeners(){
        SearchUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Username = BrowsedUser.getText().toString();
                if(Username.equals(whoAmI)){
                    Log.v("MSG","You can't be Added as Your Contact");
                    Toast.makeText(getApplicationContext(),  getResources().getString(R.string.ErrorMe), Toast.LENGTH_SHORT).show();
                }else if(Username.matches("")){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.PleaseEnterText), Toast.LENGTH_SHORT).show();
                }else{
                    //Create a reference to the Firebase RTDB
                    ref = new Firebase(Constants.FBroot);

                    Firebase user = ref.child(Constants.users).child(Username);
                    user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null){
                                Log.v("MSG",user.name+" "+user.surname+" exists");
                                boolean success = myDB.insertContact(Username, user.name,user.surname,user.email);
                                //update user's contacts on fireBase
                                updateContactsFB(user,Username);
                                if(success){
                                    Log.v("MSG",Username+" added successfully");
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.AddedSuccessfully), Toast.LENGTH_SHORT).show();
                                }else{
                                    Log.v("MSG",Username+" is already on your Contacts List");
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.AlreadyIncluded), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }else
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoneExists), Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

            }
        });


    }
    public void updateContactsFB(final User contact, final String username){
        final Firebase myContacts = ref.child(Constants.contacts).child(whoAmI);
        myContacts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(username)){
                    //there is no record for this user
                    myContacts.child(username).setValue("");
                    Log.v("msg", "contact added successfully to Firebase");
                }else{
                    Log.v("msg", "contact exists on firebase");
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}

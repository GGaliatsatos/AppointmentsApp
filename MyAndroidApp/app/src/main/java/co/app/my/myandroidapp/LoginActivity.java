package co.app.my.myandroidapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {
    EditText UserNameInput;
    EditText PasswordInput;
    Button LoginButton;
    Button RegisterButton;
    Button ExitButton;
    Firebase ref;
    DatabaseHelper myDB;
    String UserName;
    String Password;
    boolean proceed;
    int count = 0;
    ArrayList<String> list_of_contacts =  new ArrayList<>();
    ArrayList<Contact> list_of_records =  new ArrayList<>();;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserNameInput = (EditText)findViewById(R.id.editTextUN);
        PasswordInput = (EditText)findViewById(R.id.editTextPW);
        LoginButton = (Button)findViewById(R.id.buttonLogin);
        RegisterButton = (Button)findViewById(R.id.buttonRegister);
        ExitButton = (Button)findViewById(R.id.buttonExit);
        Firebase.setAndroidContext(this);
        ref = new Firebase(Constants.FBroot);
        myDB = new DatabaseHelper(this);
        proceed = false;
        SetListeners();
        myDB.deleteCredentials();
        myDB.deleteAllContacts();



    }
    public void firebaseFunction(){
        final Firebase accountsRef = ref.child(Constants.accounts);
        accountsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(UserName)){
                    Log.v("message", UserName+" exists");
                    if(dataSnapshot.child(UserName).hasChild(Password)){
                        Log.v("message", Password+" correct");
                        //try to drag all contacts
                        receiveFBContacts();
                        //insert credentials on local table
                        myDB.insertCredentials(UserName,Password);



                    }else{
                        PasswordInput.setText("");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.InvalidCredentials), Toast.LENGTH_SHORT).show();
                    }


                }else{
                    PasswordInput.setText("");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.InvalidCredentials), Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {


            }

        });
    }
    public void SetListeners(){
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                LoginActivity.this.finishAffinity();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserName = UserNameInput.getText().toString();
                Password = PasswordInput.getText().toString();
                if(UserName.equals("") || Password.equals("")){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.InsertFields), Toast.LENGTH_SHORT).show();
                }else {
                    boolean hasValidCharacters = MyValidator.validateLoginCredentials(UserName,Password);
                    if(hasValidCharacters && !UserName.equals("") && !Password.equals("") ){
                        firebaseFunction();

                    }else{
                        Log.v("ERROR", "INVALID CHARACTERS INCLUDED");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.InvalidCharacters), Toast.LENGTH_SHORT).show();
                        PasswordInput.setText("");
                    }

                }





            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Register.class);
                finish();
                startActivity(intent);
            }
        });


    }

    public void receiveFBContacts(){
        Firebase contactsRef = ref.child(Constants.contacts).child(UserName);
        contactsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list_of_contacts.clear();
                for (DataSnapshot current: dataSnapshot.getChildren()) {
                    list_of_contacts.add(current.getKey()) ;
                    Log.e("iteration","new");
                }
                if(list_of_contacts.size() == 0){// don't try to synchronize
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }else{
                    syncFBContacts();
                }




            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        }); //
    }

    public void syncFBContacts(){

        for (String i: list_of_contacts) {
            Log.e("list item",i+"\n");
            Firebase user = ref.child(Constants.users).child(i);
            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    if (user != null){
                        String nodename = dataSnapshot.getKey();
                        Log.e("got key",nodename+"\n");
                        Log.v("MSG",user.name+" "+user.surname+" "+user.email+" exists");
                        //add to list each item
                        Contact record = new Contact();
                        record.SetEmail(user.email);
                        record.SetName(user.name);
                        record.SetSurname(user.surname);
                        record.SetID(nodename);
                        list_of_records.add(record);
                        if(list_of_contacts.size() == list_of_records.size()){// probably size() needs mutex like lock.
                            passtoSQLite();
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }



    }

    public void passtoSQLite(){
        for(Contact record :list_of_records){
            boolean success = myDB.insertContact(record.USERNAME, record.Name, record.Surname,record.Email);
            if(success){
                Log.v("MSG",record.USERNAME+" added successfully");
            }else{
                Log.v("MSG",record.USERNAME+" is already on your Contacts List");
            }
        }
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }



}

package co.app.my.myandroidapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {
    Firebase ref;
    DatabaseHelper myDB;
    String UserName;
    String Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Firebase.setAndroidContext(this);
        ref = new Firebase(Constants.FBroot);
        myDB = new DatabaseHelper(this);
        Cursor res = myDB.getCredentials();
        if(res.getCount()==1) {

            while(res.moveToNext()){
                UserName = res.getString(0);
                Password = res.getString(1);
            }
            firebaseFunction();


        }else{//clear the db stored credentials and head to login activity
            myDB.deleteCredentials();
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

    }
    public void firebaseFunction(){
        final Firebase accountsRef = ref.child(Constants.accounts);
        accountsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(UserName)&& dataSnapshot.child(UserName).hasChild(Password)){
                    Log.v("message", UserName+" exists");
                    Log.v("message", Password+" correct");
                    Intent pass = new Intent(WelcomeActivity.this, MainActivity.class);
                    finish();
                    startActivity(pass);


                }else{//clear the db stored credentials and head to login activity
                    myDB.deleteCredentials();
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);}
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {


            }

        });
    }
}

package co.app.my.myandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText UserNameInput;
    EditText PasswordInput;
    EditText RePasswordInput;
    EditText EmailInput;
    EditText NameInput;
    EditText LastNameInput;
    Button BackButton;
    Button RegisterButton;
    Button ExitButton;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        UserNameInput = (EditText)findViewById(R.id.editTextUN);
        PasswordInput = (EditText)findViewById(R.id.editTextPW);
        RePasswordInput = (EditText)findViewById(R.id.editTextReEnterPW);
        EmailInput = (EditText)findViewById(R.id.editTextEmail);
        NameInput = (EditText)findViewById(R.id.editTextName);
        LastNameInput = (EditText)findViewById(R.id.editTextLastName);
        BackButton = (Button)findViewById(R.id.buttonBack);
        RegisterButton = (Button)findViewById(R.id.buttonRegister);
        ExitButton = (Button)findViewById(R.id.buttonExit);
        Firebase.setAndroidContext(this);
        ref = new Firebase(Constants.FBroot);
        SetListeners();
    }
    public void SetListeners(){
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String UserName = UserNameInput.getText().toString();
                final String Password = PasswordInput.getText().toString();
                final String RePassword = RePasswordInput.getText().toString();
                final String Email = EmailInput.getText().toString();
                final String Name = NameInput.getText().toString();
                final String LastName = LastNameInput.getText().toString();



                boolean hasValidCharacters = MyValidator.validateLoginCredentials(UserName,Password);
                Log.v("hasValidCharacters","is : "+hasValidCharacters);
                Log.v("PW"," "+Password);
                Log.v("Re-PW"," "+RePassword);
                Log.v("here","i was"+UserName);
                ArrayList<String> errors = new ArrayList<String>();
                errors.clear();

                //Erros in form
                if(UserNameInput.getText().toString().equals("")){
                    errors.add(getResources().getString(R.string.UsernameErrorEmpty));
                }
                if(PasswordInput.getText().toString().equals("")){
                    errors.add(getResources().getString(R.string.PassErrorEmpty));
                }
                if(RePasswordInput.getText().toString().equals("")){
                    errors.add(getResources().getString(R.string.RepassErrorEmpty));
                }
                if(EmailInput.getText().toString().equals("")){
                    errors.add(getResources().getString(R.string.mailErrorEmpty));
                }else if(!MyValidator.validateEmail(EmailInput.getText().toString())){
                    errors.add(getResources().getString(R.string.invalidEmailchar));
                }
                if(NameInput.getText().toString().equals("")){
                    errors.add(getResources().getString(R.string.nameErrorEmpty));
                }else if(!MyValidator.validateName(NameInput.getText().toString())){
                    errors.add(getResources().getString(R.string.nameErrorChar));
                }
                if(LastNameInput.getText().toString().equals("")){
                    errors.add(getResources().getString(R.string.surnameErrorEmpty));
                }else if(!MyValidator.validateName(LastNameInput.getText().toString())){
                    errors.add(getResources().getString(R.string.surnameErrorChar));
                }




                if(errors.size()>0){
                    String ErrorMsg ="";
                    for(String err: errors){
                        ErrorMsg += err+".\n";
                    }
                    //inform for fields
                    Toast.makeText(getApplicationContext(), ErrorMsg, Toast.LENGTH_SHORT).show();
                    ErrorMsg = "";
                    errors.clear();

                }else{
                    if(hasValidCharacters == true && Password.equals(RePassword) ){
                        Log.v("here","i was"+UserName);
                        final Firebase accountsRef = ref.child(Constants.accounts);
                        accountsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.hasChild(UserName)){
                                    //there is no record for this user
                                    User ThisUser = new User(Email,Name,LastName);
                                    ref.child(Constants.users).child(UserName).setValue(ThisUser);
                                    accountsRef.child(UserName).child(Password).setValue("true");
                                    Log.v("msg", "account successfully created");
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.AccountCreated), Toast.LENGTH_SHORT).show();
                                    UserNameInput.setText("");
                                    PasswordInput.setText("");
                                    RePasswordInput.setText("");
                                    EmailInput.setText("");
                                    NameInput.setText("");
                                    LastNameInput.setText("");

                                }else{
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.AcountExists), Toast.LENGTH_SHORT).show();
                                    Log.v("msg", "account already exists");
                                    PasswordInput.setText("");
                                    RePasswordInput.setText("");
                                    UserNameInput.setText("");
                                }


                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                    }
                }








            }
        });
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Register.this.finishAffinity();
            }
        });

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }
}

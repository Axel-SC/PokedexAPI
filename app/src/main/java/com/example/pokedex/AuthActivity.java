package com.example.pokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.pokedex.MainActivity.ProviderType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static AuthActivity authContext;

    public AuthActivity() {
        authContext = this;
    }
    public static AuthActivity getInstance() {
        return authContext;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        setTitle("Authentication");

        mAuth = FirebaseAuth.getInstance();




        EditText emailEt = findViewById(R.id.EditTextEmail);
        EditText passwordEt = findViewById(R.id.EditTextPassword);


        Button btnSignUp = findViewById(R.id.BtnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                Log.i("PruebaId", "Email: " + email + password);
                if(!(TextUtils.isEmpty(email)) && !(TextUtils.isEmpty(password))) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                showMain(task.getResult().getUser().getEmail(), ProviderType.BASIC);
                            }else{
                                show_alert();
                            }
                        }
                    });
                }

            }
        });

        Button btnLogin = findViewById(R.id.BtnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                Log.i("PruebaId", "Email: " + email + password);
                if(!(TextUtils.isEmpty(email)) && !(TextUtils.isEmpty(password))) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                String name = user.getDisplayName();
                                String email = user.getEmail();
                               /* user.getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                    @Override
                                    public void onSuccess(GetTokenResult result) {
                                        String idToken = result.getToken();
                                        //Do whatever
                                        Log.i("TokenID", "GetTokenResult result = " + idToken);
                                    }
                                });*/
                             Log.i("UserInfo", "User: " + user+name+email);
                                showMain(task.getResult().getUser().getEmail(), ProviderType.BASIC);
                            }else{
                                show_alert();
                            }
                        }
                    });
                }

            }
        });

    }
    private void show_alert(){
        final AlertDialog diag = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Error with the authenticacion")
                .setPositiveButton("Accept", null)
                .create();
        diag.show();
    }
    public void showMain(String email, ProviderType providerType){
        Bundle extras = new Bundle();
        extras.putString("Email", email);
        extras.putString("Provider", providerType.name());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtras(extras);
        startActivity(intent);
    }



}
package com.example.PIS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Register extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    Button crear;
    EditText email, password, password2;
    CheckBox terms;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();
        crear = (Button) findViewById(R.id.buttonCrear);
        email = (EditText) findViewById(R.id.editTextUsernameRegister);
        password = (EditText) findViewById(R.id.editTextPasswordRegister);
        password2 = (EditText) findViewById(R.id.editTextPasswordRegisterSecure);
        terms = (CheckBox) findViewById(R.id.termsOfUse);

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crear();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void crear(){
        String user, passw, passw2, msj;
        boolean check;
        user = email.getText().toString();
        passw = password.getText().toString();
        passw2 = password2.getText().toString();
        check = terms.isChecked();
        if(user.equals("") || passw.equals("") || passw2.equals("")){
            msj = "Introduzca un usuario y una contraseña";
            email.requestFocus();
            password.requestFocus();
            password2.requestFocus();
            Mensaje(msj);
        }
        else if(!check){
            msj = "Acepte los términos de uso";
            terms.requestFocus();
            Mensaje(msj);
        }
        else if(!passw.equals(passw2)){
            msj = "Las contraseñas no coinciden";
            terms.requestFocus();
            Mensaje(msj);
        }
        else{
            mAuth.createUserWithEmailAndPassword(user, passw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                menuMain();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });


        }
    }

    public void Mensaje(String msj){
        Toast toast = Toast.makeText(this, msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
    private void updateUI(FirebaseUser user) {

    }
    private void menuMain(){
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
    }
}

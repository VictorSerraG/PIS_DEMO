package com.example.PIS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Register extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    Button crear;
    EditText email, password, password2;
    TextInputLayout etUser, etPassw, etPassw2;
    CheckBox terms;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("VALUES", MODE_PRIVATE);

        int theme = sharedPreferences.getInt("THEME", 1);
        switch (theme){
            case 1: setTheme(R.style.FeedActivityThemeLight);
                break;
            case 2: setTheme(R.style.FeedActivityThemeDark);
                break;
        }

        int language = sharedPreferences.getInt("LANGUAGE", 1);
        switch (language){
            case 1: setAppLocale("esp");
                break;
            case 2: setAppLocale("en");
                break;
        }

        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();
        crear = (Button) findViewById(R.id.buttonCrear);
        email = (EditText) findViewById(R.id.editTextUsernameRegister);
        password = (EditText) findViewById(R.id.editTextPasswordRegister);
        password2 = (EditText) findViewById(R.id.editTextPasswordRegisterSecure);
        terms = (CheckBox) findViewById(R.id.termsOfUse);
        etUser = (TextInputLayout) findViewById(R.id.etUserLayoutRegister);
        etPassw = (TextInputLayout) findViewById(R.id.etPasswordLayoutRegister);
        etPassw2 = (TextInputLayout) findViewById(R.id.etPasswordLayoutRegisterSecure);

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUser.setError(null);
                etPassw.setError(null);
                etPassw2.setError(null);
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
        if(user.equals("") && passw.equals("") && passw2.equals("") && !check){
            email.requestFocus();
            Mensaje(getResources().getString(R.string.errorUsuarioPassw));
        }
        else if(user.equals("") && passw.equals("") && passw2.equals("")){
            msj = getResources().getString(R.string.errorUsuario);
            email.requestFocus();
            etUser.setError(msj);
            msj = getResources().getString(R.string.errorPassw);
            etPassw.setError(msj);
            msj = getResources().getString(R.string.errorPasswSecure);
            etPassw2.setError(msj);
        }
        else if(user.equals("")) {
            msj = getResources().getString(R.string.errorUsuario);
            email.requestFocus();
            etUser.setError(msj);
        }
        else if(!isValidEmail(user)){
            msj = getResources().getString(R.string.errorEmail);
            email.requestFocus();
            etUser.setError(msj);
        }
        else if(passw.equals("")){
            msj = getResources().getString(R.string.errorPassw);
            password.requestFocus();
            etPassw.setError(msj);
        }
        else if(passw2.equals("")){
            msj = getResources().getString(R.string.errorPasswSecure);
            password2.requestFocus();
            etPassw2.setError(msj);
        }
        else if(!check){
            msj = getResources().getString(R.string.errorTerms);
            terms.requestFocus();
            Mensaje(msj);
        }
        else if(!passw.equals(passw2)){
            msj = getResources().getString(R.string.errorSamePassw);
            password.requestFocus();
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
                                menuMain(task.getResult().getUser().getEmail());
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Mensaje(getResources().getString(R.string.errorRegister));
                                updateUI(null);
                                // If sign in fails, display a message to the user.
                                try
                                {
                                    throw task.getException();
                                }
                                // If the password is not strong enough
                                catch (FirebaseAuthWeakPasswordException weakPassword)
                                {
                                    Log.d(TAG, "onComplete: weak_password");
                                    password.requestFocus();
                                    etPassw.setError(getResources().getString(R.string.errorDebil));
                                }
                                // If the email address is malformed
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                {
                                    Log.d(TAG, "onComplete: malformed_email");
                                    email.requestFocus();
                                    etUser.setError(getResources().getString(R.string.errorEmail));
                                }
                                //if the email is already in use by a different account.
                                catch (FirebaseAuthUserCollisionException existEmail)
                                {
                                    Log.d(TAG, "onComplete: exist_email");
                                    email.requestFocus();
                                    etUser.setError(getResources().getString(R.string.errorUsoEmail));
                                }
                                catch (Exception e)
                                {
                                    Log.d(TAG, "onComplete: " + e.getMessage());
                                }
                            }
                        }
                    });


        }
    }

    public void Mensaje(String msj){
        Toast toast = Toast.makeText(this, msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 32, 32);
        toast.show();
    }
    private void updateUI(FirebaseUser user) {

    }
    private void menuMain(String email){
        Intent intent = new Intent(Register.this, MainActivity.class);
        intent.putExtra("email",email);
        startActivity(intent);
    }

    private void setAppLocale(String localeCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(localeCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}

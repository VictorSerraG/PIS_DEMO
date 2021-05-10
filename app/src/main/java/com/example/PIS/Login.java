package com.example.PIS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Login extends Activity {
    Button entrar;
    EditText usuario, password;
    TextInputLayout etUser, etPassw;
    TextView registrar;
    AdaptadorBD DB;
    private static final String TAG = "EmailPassword";
    // [START declare_auth]
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

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Bold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        setContentView(R.layout.login);


        mAuth = FirebaseAuth.getInstance();

        entrar = (Button) findViewById(R.id.buttonLogin);
        usuario = (EditText) findViewById(R.id.editTextUsernameLogin);
        password = (EditText) findViewById(R.id.editTextPasswordLogin);
        registrar = (TextView) findViewById(R.id.textRegister);
        etUser = (TextInputLayout) findViewById(R.id.etUserLayoutLogin);
        etPassw = (TextInputLayout) findViewById(R.id.etPasswordLayoutLogin);

        registrar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                return true;
            }
        });

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUser.setError(null);
                etPassw.setError(null);
                logearse();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void logearse(){
        DB = new AdaptadorBD(this);
        String user, passw, msj;
        user = usuario.getText().toString();
        passw = password.getText().toString();
        if(user.equals("") && passw.equals("")){
            msj = getResources().getString(R.string.errorUsuario);
            usuario.requestFocus();
            etUser.setError(msj);
            msj = getResources().getString(R.string.errorPassw);
            etPassw.setError(msj);
        }
        else if(user.equals("")){
            msj = getResources().getString(R.string.errorUsuario);
            usuario.requestFocus();
            etUser.setError(msj);
        }else if(passw.equals("")){
            msj = getResources().getString(R.string.errorPassw);
            password.requestFocus();
            etPassw.setError(msj);
        }
        else {
            mAuth.signInWithEmailAndPassword(user, passw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                                menuMain(task.getResult().getUser().getEmail());
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Mensaje(getResources().getString(R.string.errorLogin));
                                updateUI(null);
                                try
                                {
                                    throw task.getException();
                                }
                                // ERROR_USER_DISABLED, ERROR_USER_NOT_FOUND, ERROR_USER_TOKEN_EXPIRED i ERROR_INVALID_USER_TOKEN
                                catch (FirebaseAuthInvalidUserException invalidUserException)
                                {
                                    Log.d(TAG, "onComplete: invalidUser");
                                    usuario.requestFocus();
                                    etUser.setError(getResources().getString(R.string.errorCorreoUnknown));
                                }
                                // Thrown when one or more of the credentials passed to a method fail to identify
                                catch (FirebaseAuthInvalidCredentialsException invalidCredentialsException)
                                {
                                    Log.d(TAG, "onComplete: malformed_email");
                                    usuario.requestFocus();
                                    etUser.setError(getResources().getString(R.string.errorCredencialesInv));
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
        Intent intent = new Intent(Login.this, MainActivity.class);
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
}

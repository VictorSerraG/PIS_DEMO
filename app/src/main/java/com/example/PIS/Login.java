package com.example.PIS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Locale;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Login extends Activity {
    Button entrar;
    EditText usuario, password;
    TextInputLayout etUser, etPassw;
    CheckBox recordarme;
    TextView registrar, retrievePassw;
    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean flag = false;

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        try{
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
        } catch (Exception e) {
            flag = true;
        }
        if(!flag){
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                            //Estilo
                            int theme;
                            try{
                                theme = document.getLong("estilo").intValue();
                            } catch (Exception e) {
                                theme = 0;
                            }

                            switch (theme){
                                case 0: setTheme(R.style.FeedActivityThemeLight);
                                    break;
                                case 1: setTheme(R.style.FeedActivityThemeDark);
                                    break;
                            }

                            //Idioma
                            int lang;
                            try{
                                lang = document.getLong("idioma").intValue();
                            } catch (Exception e) {
                                lang = 0;
                            }

                            switch (lang){
                                case 0: setAppLocale("esp");
                                    mAuth.setLanguageCode("es");
                                    break;
                                case 1: setAppLocale("en");
                                    mAuth.setLanguageCode("en");
                                    break;
                                case 2: setAppLocale("ca");
                                    mAuth.setLanguageCode("ca");
                                    break;
                            }

                            // Letra
                            String strletra = "";
                            int fuente;
                            try{
                                fuente = document.getLong("letra").intValue();
                            } catch (Exception e) {
                                fuente = 0;
                            }

                            switch (fuente){
                                case 0:
                                    strletra = "Roboto-Bold";
                                    break;
                                case 1:
                                    strletra = "Roboto-Medium";
                                    break;
                                case 2:
                                    strletra = "Roboto-Regular";
                                    break;
                                case 3:
                                    strletra = "Roboto-Thin";
                                    break;
                                default:
                                    break;
                            }
                            strletra = "fonts/" + strletra + ".ttf";
                            ViewPump.init(ViewPump.builder()
                                    .addInterceptor(new CalligraphyInterceptor(
                                            new CalligraphyConfig.Builder()
                                                    .setDefaultFontPath(strletra)
                                                    .setFontAttrId(R.attr.fontPath)
                                                    .build()))
                                    .build());

                            setContentView(R.layout.login);

                            boolean recordar;
                            try{
                                recordar = document.getBoolean("recordarLogin");
                            } catch (Exception e) {
                                recordar = false;
                            }

                            entrar = (Button) findViewById(R.id.buttonLogin);
                            usuario = (EditText) findViewById(R.id.editTextUsernameLogin);
                            password = (EditText) findViewById(R.id.editTextPasswordLogin);
                            registrar = (TextView) findViewById(R.id.textRegister);
                            retrievePassw = (TextView) findViewById(R.id.textOlvidoPassw);
                            etUser = (TextInputLayout) findViewById(R.id.etUserLayoutLogin);
                            etPassw = (TextInputLayout) findViewById(R.id.etPasswordLayoutLogin);
                            recordarme = (CheckBox) findViewById(R.id.remember);
                            recordarme.setChecked(recordar);

                            if(recordar){
                                String user, passw;
                                user = document.getString("user");
                                passw = document.getString("password");
                                usuario.setText(user);
                                password.setText(passw);
                            }

                            registrar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Login.this, Register.class);
                                    startActivity(intent);
                                }
                            });

                            retrievePassw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText resetMail = new EditText(v.getContext());
                                    resetMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                                    passwordResetDialog.setTitle(getResources().getString(R.string.resetPassw));
                                    passwordResetDialog.setMessage(getResources().getString(R.string.resetPasswtext));
                                    passwordResetDialog.setView(resetMail);

                                    passwordResetDialog.setPositiveButton(getResources().getString(R.string.enviar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String mail = resetMail.getText().toString();
                                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Mensaje(getResources().getString(R.string.msjEmailT));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Mensaje(getResources().getString(R.string.msjEmailF));
                                                }
                                            });
                                        }
                                    });
                                    passwordResetDialog.create().show();
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
                        } else {
                            Log.d(TAG, "No such document");
                            setTheme(R.style.FeedActivityThemeLight);
                            setContentView(R.layout.login);

                            boolean recordar;
                            try{
                                recordar = document.getBoolean("recordarLogin");
                            } catch (Exception e) {
                                recordar = false;
                            }

                            entrar = (Button) findViewById(R.id.buttonLogin);
                            usuario = (EditText) findViewById(R.id.editTextUsernameLogin);
                            password = (EditText) findViewById(R.id.editTextPasswordLogin);
                            registrar = (TextView) findViewById(R.id.textRegister);
                            retrievePassw = (TextView) findViewById(R.id.textOlvidoPassw);
                            etUser = (TextInputLayout) findViewById(R.id.etUserLayoutLogin);
                            etPassw = (TextInputLayout) findViewById(R.id.etPasswordLayoutLogin);
                            recordarme = (CheckBox) findViewById(R.id.remember);
                            recordarme.setChecked(recordar);

                            if(recordar){
                                String user, passw;
                                user = document.getString("user");
                                passw = document.getString("password");
                                usuario.setText(user);
                                password.setText(passw);
                            }

                            registrar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Login.this, Register.class);
                                    startActivity(intent);
                                }
                            });

                            retrievePassw.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EditText resetMail = new EditText(v.getContext());
                                    resetMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                                    passwordResetDialog.setTitle(getResources().getString(R.string.resetPassw));
                                    passwordResetDialog.setMessage(getResources().getString(R.string.resetPasswtext));
                                    passwordResetDialog.setView(resetMail);

                                    passwordResetDialog.setPositiveButton(getResources().getString(R.string.enviar), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String mail = resetMail.getText().toString();
                                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Mensaje(getResources().getString(R.string.msjEmailT));
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Mensaje(getResources().getString(R.string.msjEmailF));
                                                }
                                            });
                                        }
                                    });
                                    passwordResetDialog.create().show();
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
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        setTheme(R.style.FeedActivityThemeLight);

                        setContentView(R.layout.login);

                        boolean recordar = false;

                        entrar = (Button) findViewById(R.id.buttonLogin);
                        usuario = (EditText) findViewById(R.id.editTextUsernameLogin);
                        password = (EditText) findViewById(R.id.editTextPasswordLogin);
                        registrar = (TextView) findViewById(R.id.textRegister);
                        retrievePassw = (TextView) findViewById(R.id.textOlvidoPassw);
                        etUser = (TextInputLayout) findViewById(R.id.etUserLayoutLogin);
                        etPassw = (TextInputLayout) findViewById(R.id.etPasswordLayoutLogin);
                        recordarme = (CheckBox) findViewById(R.id.remember);
                        recordarme.setChecked(recordar);

                        registrar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Login.this, Register.class);
                                startActivity(intent);
                            }
                        });

                        retrievePassw.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText resetMail = new EditText(v.getContext());
                                resetMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                                passwordResetDialog.setTitle(getResources().getString(R.string.resetPassw));
                                passwordResetDialog.setMessage(getResources().getString(R.string.resetPasswtext));
                                passwordResetDialog.setView(resetMail);

                                passwordResetDialog.setPositiveButton(getResources().getString(R.string.enviar), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String mail = resetMail.getText().toString();
                                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Mensaje(getResources().getString(R.string.msjEmailT));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Mensaje(getResources().getString(R.string.msjEmailF));
                                            }
                                        });
                                    }
                                });
                                passwordResetDialog.create().show();
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
                }
            });
        }else{
            setTheme(R.style.FeedActivityThemeLight);
            setContentView(R.layout.login);

            boolean recordar = false;

            entrar = (Button) findViewById(R.id.buttonLogin);
            usuario = (EditText) findViewById(R.id.editTextUsernameLogin);
            password = (EditText) findViewById(R.id.editTextPasswordLogin);
            registrar = (TextView) findViewById(R.id.textRegister);
            retrievePassw = (TextView) findViewById(R.id.textOlvidoPassw);
            etUser = (TextInputLayout) findViewById(R.id.etUserLayoutLogin);
            etPassw = (TextInputLayout) findViewById(R.id.etPasswordLayoutLogin);
            recordarme = (CheckBox) findViewById(R.id.remember);
            recordarme.setChecked(recordar);

            registrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Login.this, Register.class);
                    startActivity(intent);
                }
            });

            retrievePassw.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText resetMail = new EditText(v.getContext());
                    resetMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                    passwordResetDialog.setTitle(getResources().getString(R.string.resetPassw));
                    passwordResetDialog.setMessage(getResources().getString(R.string.resetPasswtext));
                    passwordResetDialog.setView(resetMail);

                    passwordResetDialog.setPositiveButton(getResources().getString(R.string.enviar), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String mail = resetMail.getText().toString();
                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Mensaje(getResources().getString(R.string.msjEmailT));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Mensaje(getResources().getString(R.string.msjEmailF));
                                }
                            });
                        }
                    });
                    passwordResetDialog.create().show();
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
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void logearse(){
        String email, passw, msj;
        email = usuario.getText().toString();
        passw = password.getText().toString();
        boolean check = recordarme.isChecked();

        if(email.equals("") && passw.equals("")){
            msj = getResources().getString(R.string.errorUsuario);
            usuario.requestFocus();
            etUser.setError(msj);
            msj = getResources().getString(R.string.errorPassw);
            etPassw.setError(msj);
        }
        else if(email.equals("")){
            msj = getResources().getString(R.string.errorUsuario);
            usuario.requestFocus();
            etUser.setError(msj);
        }else if(passw.equals("")){
            msj = getResources().getString(R.string.errorPassw);
            password.requestFocus();
            etPassw.setError(msj);
        }
        else {
            mAuth.signInWithEmailAndPassword(email, passw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                if(mAuth.getCurrentUser().isEmailVerified()){
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if(check){
                                        saveUserPassw(email, passw);
                                    }else{
                                        HashMap<String, Boolean> checked = new HashMap<String, Boolean>();
                                        checked.put("recordarLogin", false);
                                        db.collection("users").document(mAuth.getCurrentUser().getEmail()).set(checked, SetOptions.merge());
                                    }
                                    menuMain(task.getResult().getUser().getEmail());
                                }else{
                                    Mensaje(getResources().getString(R.string.VerificacionEmailFailed));
                                    mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Mensaje(getResources().getString(R.string.VerificacionEmail));
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                        }
                                    });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Mensaje(getResources().getString(R.string.errorLogin));
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

    private void saveUserPassw(String user, String passw){
        HashMap<String, String> login = new HashMap<String, String>();
        login.put("user",  user);
        login.put("password",  passw);
        HashMap<String, Boolean> checked = new HashMap<String, Boolean>();
        checked.put("recordarLogin", true);
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).set(login, SetOptions.merge());
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).set(checked, SetOptions.merge());
    }

}

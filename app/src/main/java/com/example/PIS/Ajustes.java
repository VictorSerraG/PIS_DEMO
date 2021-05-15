package com.example.PIS;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Ajustes extends AppCompatActivity {
    Spinner tamaño, letra, estilo, idioma;
    Button aplicar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "Settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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

                        //Tamaño
                        int size;
                        try{
                            size = document.getLong("tamaño").intValue();
                        } catch (Exception e) {
                            size = 0;
                        }

                        setContentView(R.layout.ajustes);

                        //Spinners
                        tamaño = (Spinner) findViewById(R.id.tamaño);
                        tamaño.setSelection(size);

                        letra = (Spinner) findViewById(R.id.letra);
                        letra.setSelection(fuente);

                        estilo = (Spinner) findViewById(R.id.estilo);
                        estilo.setSelection(theme);

                        idioma = (Spinner) findViewById(R.id.idioma);
                        idioma.setSelection(lang);

                        aplicar = (Button) findViewById(R.id.buttonAplicar);

                        aplicar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveSettings();
                                Intent intent = new Intent(Ajustes.this, Ajustes.class);
                                startActivity(intent);
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                        setContentView(R.layout.ajustes);

                        //Spinners
                        tamaño = (Spinner) findViewById(R.id.tamaño);

                        letra = (Spinner) findViewById(R.id.letra);

                        estilo = (Spinner) findViewById(R.id.estilo);

                        idioma = (Spinner) findViewById(R.id.idioma);

                        aplicar = (Button) findViewById(R.id.buttonAplicar);

                        aplicar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveSettings();
                                Intent intent = new Intent(Ajustes.this, Ajustes.class);
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    setContentView(R.layout.ajustes);

                    //Spinners
                    tamaño = (Spinner) findViewById(R.id.tamaño);

                    letra = (Spinner) findViewById(R.id.letra);

                    estilo = (Spinner) findViewById(R.id.estilo);

                    idioma = (Spinner) findViewById(R.id.idioma);

                    aplicar = (Button) findViewById(R.id.buttonAplicar);

                    aplicar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveSettings();
                            Intent intent = new Intent(Ajustes.this, Ajustes.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void setAppLocale(String localeCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(localeCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    private void saveSettings(){
        HashMap<String, Integer> ajustes = new HashMap<String, Integer>();
        ajustes.put("tamaño",  tamaño.getSelectedItemPosition());
        ajustes.put("letra",  letra.getSelectedItemPosition());
        ajustes.put("estilo",  estilo.getSelectedItemPosition());
        ajustes.put("idioma",  idioma.getSelectedItemPosition());
        db.collection("users").document(mAuth.getCurrentUser().getEmail()).set(ajustes, SetOptions.merge());
    }
}

package com.example.PIS;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.webkit.CookieManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class AgregarNota extends AppCompatActivity {
    Button Add;
    EditText TITLE, CONTENT;
    String type, getTitle;
    private static final int SALIR = Menu.FIRST;
    AdaptadorBD DB;
    private static final String TAG = "AddNote";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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
                                break;
                            case 1: setAppLocale("en");
                                break;
                            case 2: setAppLocale("ca");
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

                        setContentView(R.layout.agregar_nota);

                        Add = (Button) findViewById(R.id.button_Agregar);
                        TITLE = (EditText) findViewById(R.id.editText_Titulo);
                        CONTENT = (EditText) findViewById(R.id.editText_Contenido);
                        Bundle bundle = getIntent().getExtras();

                        String content;
                        getTitle = bundle.getString("title");
                        content = bundle.getString("content");
                        type = bundle.getString("type");
                        Add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addUpdateNotes();
                            }
                        });

                        if (type.equals(("add"))) {
                            Add.setText("Afegir nota");

                        } else {
                            if (type.equals("edit")) {
                                TITLE.setText(getTitle);
                                CONTENT.setText(content);
                                Add.setText("Actualitzar nota");
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        setContentView(R.layout.agregar_nota);

                        Add = (Button) findViewById(R.id.button_Agregar);
                        TITLE = (EditText) findViewById(R.id.editText_Titulo);
                        CONTENT = (EditText) findViewById(R.id.editText_Contenido);
                        Bundle bundle = getIntent().getExtras();

                        String content;
                        getTitle = bundle.getString("title");
                        content = bundle.getString("content");
                        type = bundle.getString("type");
                        Add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addUpdateNotes();
                            }
                        });

                        if (type.equals(("add"))) {
                            Add.setText("Afegir nota");

                        } else {
                            if (type.equals("edit")) {
                                TITLE.setText(getTitle);
                                CONTENT.setText(content);
                                Add.setText("Actualitzar nota");
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    setContentView(R.layout.agregar_nota);

                    Add = (Button) findViewById(R.id.button_Agregar);
                    TITLE = (EditText) findViewById(R.id.editText_Titulo);
                    CONTENT = (EditText) findViewById(R.id.editText_Contenido);
                    Bundle bundle = getIntent().getExtras();

                    String content;
                    getTitle = bundle.getString("title");
                    content = bundle.getString("content");
                    type = bundle.getString("type");
                    Add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addUpdateNotes();
                        }
                    });

                    if (type.equals(("add"))) {
                        Add.setText("Afegir nota");

                    } else {
                        if (type.equals("edit")) {
                            TITLE.setText(getTitle);
                            CONTENT.setText(content);
                            Add.setText("Actualitzar nota");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void addUpdateNotes() {
        //basef = FirebaseDatabase.getInstance();
        DB = new AdaptadorBD(this);
        String title, content, msj;
        title = TITLE.getText().toString();
        content = CONTENT.getText().toString();
        if (type.equals("add")) {
            if (title.equals("")) {
                msj = "Ingrese un titulo";
                TITLE.requestFocus();
                Mensaje(msj);
            } else {
                if (content.equals("")) {
                    msj = "Añade contenido a la nota";
                    CONTENT.requestFocus();
                    Mensaje(msj);
                } else {
                    Cursor c = DB.getNote(title);
                    String gettitle = "";
                    if (c.moveToFirst()) {
                        do {
                            gettitle = c.getString(1);
                        } while (c.moveToNext());
                    }
                    if (gettitle.equals(title)) {
                        TITLE.requestFocus();
                        msj = "El titulo de esta nota ya existe";
                        Mensaje(msj);
                    } else {
                        DB.addNote(title, content);
                        actividad(title, content);
                    }
                }

            }
        } else {
            if (type.equals("edit")) {
                Add.setText("Update nota");
                if (title.equals("")) {
                    msj = "Añade una nota";
                    TITLE.requestFocus();
                    Mensaje(msj);
                } else {
                    if (content.equals("")) {
                        msj = "Añade contenido a la nota";
                        CONTENT.requestFocus();
                        Mensaje(msj);
                    } else {
                        DB.updateNota(title, content, getTitle);
                        actividad(title, content);
                    }
                }
            }
        }
    }


    public void Mensaje(String msj) {
        Toast toast = Toast.makeText(this, msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public void actividad(String title, String content) {
        Intent intent = new Intent(AgregarNota.this, VerNota.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
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
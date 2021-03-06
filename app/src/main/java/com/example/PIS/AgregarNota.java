package com.example.PIS;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AgregarNota extends AppCompatActivity {
    Button Add;
    EditText TITLE, CONTENT;
    String type, getTitle;
    private static final int SALIR = Menu.FIRST;
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
                            Add.setText(getResources().getString(R.string.afegir_note_b));

                        } else {
                            if (type.equals("edit")) {
                                TITLE.setText(getTitle);
                                CONTENT.setText(content);
                                Add.setText(getResources().getString(R.string.update_note_b));
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
                            Add.setText(getResources().getString(R.string.afegir_note_b));

                        } else {
                            if (type.equals("edit")) {
                                TITLE.setText(getTitle);
                                CONTENT.setText(content);
                                Add.setText(getResources().getString(R.string.update_note_b));
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
                        Add.setText(getResources().getString(R.string.afegir_note_b));

                    } else {
                        if (type.equals("edit")) {
                            TITLE.setText(getTitle);
                            CONTENT.setText(content);
                            Add.setText(getResources().getString(R.string.update_note_b));
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

        String nTitle, nContent, msj;
        nTitle = TITLE.getText().toString();
        nContent = CONTENT.getText().toString();
        if (type.equals("add")) {
            if (nTitle.equals("")) {
                msj = getResources().getString(R.string.afegir_titol);
                TITLE.requestFocus();
                Mensaje(msj);
            } else {
                if (nContent.equals("")) {
                    msj = getResources().getString(R.string.afegir_contingut);
                    CONTENT.requestFocus();
                    Mensaje(msj);
                } else {
                    DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("notes").document(nTitle);
                    Map<String,Object> note = new HashMap<>();
                    note.put("titol",nTitle);
                    note.put("content",nContent);
                    docRef.set(note, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AgregarNota.this,getResources().getString(R.string.afegida_note),Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AgregarNota.this, MainActivity.class);
                            startActivity(intent);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AgregarNota.this,getResources().getString(R.string.no_afegida_note),Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                }
            }
        } else {
            if (type.equals("edit")) {
                Add.setText(getResources().getString(R.string.update_note_b));
                if (nTitle.equals("")) {
                    msj = getResources().getString(R.string.afegir_titol);
                    TITLE.requestFocus();
                    Mensaje(msj);
                } else {
                    if (nContent.equals("")) {
                        msj = getResources().getString(R.string.afegir_contingut);
                        CONTENT.requestFocus();
                        Mensaje(msj);
                    } else {
                        CollectionReference collRef = db.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("notes");
                        collRef.document(getTitle)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });

                        Map<String,Object> note = new HashMap<>();
                        note.put("titol",nTitle);
                        note.put("content",nContent);
                                collRef.document(nTitle).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AgregarNota.this,getResources().getString(R.string.updated_note),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AgregarNota.this, MainActivity.class);
                                startActivity(intent);
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AgregarNota.this,getResources().getString(R.string.no_afegida_note),Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void setAppLocale(String localeCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(localeCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }
}
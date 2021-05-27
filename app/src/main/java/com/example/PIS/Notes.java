package com.example.PIS;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Notes extends AppCompatActivity{

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    EditText TITLE, CONTENT;
    String type, getTitle;

    @Override
    protected void onCreate(Bundle savedInstance){
        Button Add;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail());
        //docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>();

        super.onCreate(savedInstance);
        setContentView(R.layout.agregar_nota);

        CONTENT = (EditText) findViewById(R.id.editText_Contenido);
        Add = (Button) findViewById(R.id.button_Agregar);
        TITLE = (EditText) findViewById(R.id.editText_Titulo);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nTitle = TITLE.getText().toString();
                String nContent = CONTENT.getText().toString();
                //addUpdateNotes();

                DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("notes").document();
                Map<String,Object> note = new HashMap<>();
                note.put("title",nTitle);
                note.put("content",nContent);
                docRef.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Notes.this,"Nota Afegida",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Notes.this,"Error tornaho a intentar",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
                
            }
        });




    }

}

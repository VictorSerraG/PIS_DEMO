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
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;



public class MainActivity extends AppCompatActivity {
    private static final int SETTINGS = Menu.FIRST;
    private static final int ORDENAR = Menu.FIRST + 1;
    private static final int ARCHIVADOS = Menu.FIRST + 2;
    private static final int EXIST = Menu.FIRST + 3;
    private static final int DELETE = Menu.FIRST + 4;
    ListView lista;
    TextView textLista;
    FloatingActionButton add;
    AdaptadorBD DB;
    List<String> item = null;
    String getTitle;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "MAIN";

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

                        //Bundle bundle = this.getIntent().getExtras();
                        //String email = bundle.getString("email");
                        //setUp(email);
                        setContentView(R.layout.activity_main);

                        textLista = (TextView)findViewById(R.id.textView_Lista);
                        lista = (ListView)findViewById(R.id.listView_Lista);
                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getTitle = (String) lista.getItemAtPosition(position);
                                alert("list");
                            }
                        });
                        add = (FloatingActionButton) findViewById(R.id.fabAdd);
                        showNotes();

                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                actividad("add");
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document");
                        setContentView(R.layout.activity_main);

                        textLista = (TextView)findViewById(R.id.textView_Lista);
                        lista = (ListView)findViewById(R.id.listView_Lista);
                        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                getTitle = (String) lista.getItemAtPosition(position);
                                alert("list");
                            }
                        });
                        add = (FloatingActionButton) findViewById(R.id.fabAdd);
                        showNotes();

                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                actividad("add");
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    setContentView(R.layout.activity_main);

                    textLista = (TextView)findViewById(R.id.textView_Lista);
                    lista = (ListView)findViewById(R.id.listView_Lista);
                    lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            getTitle = (String) lista.getItemAtPosition(position);
                            alert("list");
                        }
                    });
                    add = (FloatingActionButton) findViewById(R.id.fabAdd);
                    showNotes();

                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            actividad("add");
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(1,SETTINGS,0,R.string.menu_settings);
        menu.add(1,ORDENAR,0,R.string.menu_ordenar);
        menu.add(1,EXIST,0,R.string.menu_cerrar);
        menu.add(1,DELETE,0,R.string.menu_borrar);
        super.onCreateOptionsMenu(menu);
        return true;
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case SETTINGS:
                Intent intent = new Intent(MainActivity.this, Ajustes.class);
                startActivity(intent);
                return true;
            case ORDENAR:
                return true;
            case DELETE:
                alert("deletes");
                return true;
            case EXIST:
                intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    
    private void showNotes(){
        DB = new AdaptadorBD(this);
        Cursor c = DB.getNotes();
        item = new ArrayList<String>();
        String title = "";
        if(c.moveToFirst() == false){
            textLista.setText(("No hi ha notes"));
        }else{
            do{
                title = c.getString(1);
                item.add(title);
            }while (c.moveToNext());
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,item);
        lista.setAdapter(adaptador);
    }

    public String getNote(){
        String type ="",content= "";

        DB = new AdaptadorBD(this);
        Cursor c = DB.getNote(getTitle);
        if(c.moveToFirst()) {

            do {
                content = c.getString(2);
            } while (c.moveToNext());
        }
        return content;
    }

    public void actividad(String act){
        String type ="",content="";
        if (act.equals("add")){
            type = "add";
            Intent intent = new Intent(MainActivity.this,AgregarNota.class);
            intent.putExtra("type",type);
            startActivity(intent);
        }else{
            if(act.equals("edit")) {
                type = "edit";
                content = getNote();
                Intent intent = new Intent(MainActivity.this, AgregarNota.class);
                intent.putExtra("type", type);
                intent.putExtra("title", getTitle);
                intent.putExtra("content", content);
                startActivity(intent);
            }else {
                if (act.equals("see")){
                    content = getNote();
                    Intent intent = new Intent(MainActivity.this, VerNota.class);
                    intent.putExtra("title", getTitle);
                    intent.putExtra("content", content);
                    startActivity(intent);
                }
            }
        }
    }

    private void alert(String f){
        AlertDialog alerta;
        alerta = new android.app.AlertDialog.Builder(this).create();
        if(f.equals("list")){
            alerta.setTitle("The title of the note: " +getTitle);
            alerta.setMessage("Quina acció vol realitzar?");
            alerta.setButton("Veure Nota", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    actividad("see");

                }
            });
            alerta.setButton2("Borrar Nota", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    delete("delete");
                    Intent intent = getIntent();
                    startActivity(intent);

                }
            });
            alerta.setButton3("Editar Nota", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    actividad("edit");

                }
            });
        }else{
            if (f.equals("deletes")){
                alerta.setTitle("Missatje de confirmació");
                alerta.setMessage("Quina acció vol realitzar?");
                alerta.setButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alerta.setButton2("Borrar Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete("deletes");
                        Intent intent = getIntent();
                        startActivity(intent);
                    }
                });
            }

        }
        alerta.show();
    }

    private void delete(String f){
        DB = new AdaptadorBD(this);
        if (f.equals("delete")){
            DB.deleteNota(getTitle);
        }else {
            if(f.equals("deletes")){
                DB.deleteNotes();
            }
        }
    }
    private void setUp(String email){

    }

    private void setAppLocale(String localeCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(localeCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }
}
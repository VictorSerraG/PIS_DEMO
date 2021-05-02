package com.example.PIS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(1,SETTINGS,0,R.string.menu_settings);
        menu.add(1,ORDENAR,0,R.string.menu_ordenar);
        menu.add(1,EXIST,0,R.string.menu_cerrar);
        menu.add(1,DELETE,0,"Borrar tots");
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
                finish();
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
}
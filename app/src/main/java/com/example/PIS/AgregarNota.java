package com.example.PIS;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class AgregarNota extends AppCompatActivity {
    Button Add;
    EditText TITLE, CONTENT;
    String type, getTitle;
    private static final int SALIR = Menu.FIRST;
    AdaptadorBD DB;
    //FirebaseDatabase basef;
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

        setContentView(R.layout.agregar_nota);

        Add = (Button) findViewById(R.id.button_Agregar);
        TITLE = (EditText) findViewById(R.id.editText_Titulo);
        CONTENT = (EditText) findViewById(R.id.editText_Contenido);
        Bundle bundle = this.getIntent().getExtras();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        menu.add(1, SALIR, 0, R.string.menu_salir);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case SALIR:
                CookieSyncManager.createInstance((this));
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                Intent intent = new Intent(AgregarNota.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void addUpdateNotes(){
        //basef = FirebaseDatabase.getInstance();
        DB = new AdaptadorBD(this);
        String title,content,msj;
        title = TITLE.getText().toString();
        content = CONTENT.getText().toString();
        if ( type.equals("add")){
            if(title.equals("")){
                msj = "Ingrese un titulo";
                TITLE.requestFocus();
                Mensaje(msj);
            }else{
                if(content.equals("")){
                    msj = "Añade contenido a la nota";
                    CONTENT.requestFocus();
                    Mensaje(msj);
                }else{
                    Cursor c = DB.getNote(title);
                    String gettitle = "";
                    if(c.moveToFirst()){
                        do {
                            gettitle = c.getString(1);
                        }while(c.moveToNext());
                    }
                    if(gettitle.equals(title)){
                        TITLE.requestFocus();
                        msj = "El titulo de esta nota ya existe";
                        Mensaje(msj);
                    }else{
                        DB.addNote(title,content);
                        actividad(title,content);
                    }
                }

            }
        }else{
            if(type.equals("edit")){
                Add.setText("Update nota");
                if (title.equals("")){
                    msj = "Añade una nota";
                    TITLE.requestFocus();
                    Mensaje(msj);
                }else{
                    if (content.equals("")){
                        msj = "Añade contenido a la nota";
                        CONTENT.requestFocus();
                        Mensaje(msj);
                    }
                    else {
                        DB.updateNota(title,content,getTitle);
                        actividad(title,content);
                    }
                }
            }
        }
    }


    public void Mensaje(String msj){
        Toast toast = Toast.makeText(this, msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
    public void actividad(String title,String content){
        Intent intent = new Intent(AgregarNota.this,VerNota.class);
        intent.putExtra("title",title);
        intent.putExtra("content",content);
        startActivity(intent);

    }
}

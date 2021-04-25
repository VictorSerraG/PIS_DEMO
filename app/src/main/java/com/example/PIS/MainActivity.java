package com.example.PIS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {
    private static final int SETTINGS = Menu.FIRST;
    private static final int ORDENAR = Menu.FIRST + 1;
    private static final int ARCHIVADOS = Menu.FIRST + 2;
    private static final int EXIST = Menu.FIRST + 3;
    ListView lista;
    TextView textLista;
    FloatingActionButton add;
    AdaptadorBD DB;
    List<String> item = null;
    String getTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLista = (TextView)findViewById(R.id.textView_Lista);
        lista = (ListView)findViewById(R.id.listView_Lista);
        add = (FloatingActionButton) findViewById(R.id.fabAdd);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actividad("add");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.add(1,SETTINGS,0,R.string.menu_settings);
        menu.add(1,ORDENAR,0,R.string.menu_ordenar);
        menu.add(1,ARCHIVADOS,0,R.string.menu_archivados);
        menu.add(1,EXIST,0,R.string.menu_cerrar);
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
            case ARCHIVADOS:
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

    public void actividad(String act){
        String type ="",content="";
        if (act.equals("add")){
            type = "add";
            Intent intent = new Intent(MainActivity.this,AgregarNota.class);
            intent.putExtra("type",type);
            startActivity(intent);
        }
    }
}
package com.example.PIS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class VerNota extends AppCompatActivity {
    private static final int EDITAR = Menu.FIRST;
    private static final int BORRAR = Menu.FIRST + 1;
    private static final int SALIR = Menu.FIRST + 2;
    String title,content;
    TextView TITLE,CONTENT;
    AdaptadorBD DB;
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

        setContentView(R.layout.ver_nota);

        Bundle bundle = this.getIntent().getExtras();

        title = bundle.getString("title");
        content = bundle.getString("content");

        TITLE = (TextView)findViewById(R.id.textView_Titulo);
        CONTENT = (TextView)findViewById(R.id.textView_Content);
        TITLE.setText(title);
        CONTENT.setText(content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu);
        menu.add(1,EDITAR,0,R.string.menu_editar);
        menu.add(2,BORRAR,0,R.string.menu_eliminar);
        menu.add(3,SALIR,0,R.string.menu_salir);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case EDITAR:
                actividad("edit");
                return true;
            case BORRAR:
                alert();
                return true;
            case SALIR:
                actividad("delete");
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void actividad(String f){
        if(f.equals("edit")) {
            String type = "edit";
            Intent intent = new Intent(VerNota.this, AgregarNota.class);
            intent.putExtra("type", type);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            startActivity(intent);
        }else{
            if(f.equals("delete")){
                CookieSyncManager.createInstance((this));
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                Intent intent = new Intent(VerNota.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void alert(){
        AlertDialog alerta;
        alerta = new AlertDialog.Builder(this).create();
        alerta.setTitle("Mensaje de confirmación");
        alerta.setMessage("¿Quiere eliminar la nota?");

        alerta.setButton("Borrar nota", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                delete();

            }
        });

        alerta.setButton2("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alerta.show();
    }

    private void delete(){
        DB = new AdaptadorBD(this);
        DB.deleteNota(title);
        actividad("delete");
    }
}

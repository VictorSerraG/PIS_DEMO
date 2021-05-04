package com.example.PIS;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Ajustes extends AppCompatActivity {
    Spinner tamaño, letra, estilo, idioma;
    Button aplicar;
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

        int language = sharedPreferences.getInt("LANGUAGE", 1);
        switch (language){
            case 1: setAppLocale("esp");
                break;
            case 2: setAppLocale("en");
                break;
        }

        setContentView(R.layout.ajustes);

        tamaño = (Spinner) findViewById(R.id.tamaño);
        ArrayAdapter<CharSequence> adapterTamaño = ArrayAdapter.createFromResource(this, R.array.tamaño, android.R.layout.simple_spinner_item);
        tamaño.setAdapter(adapterTamaño);

        letra = (Spinner) findViewById(R.id.letra);
        ArrayAdapter<CharSequence> adapterLetra = ArrayAdapter.createFromResource(this, R.array.letra, android.R.layout.simple_spinner_item);
        letra.setAdapter(adapterLetra);

        estilo = (Spinner) findViewById(R.id.estilo);
        ArrayAdapter<CharSequence> adapterEstilo = ArrayAdapter.createFromResource(this, R.array.estilo, android.R.layout.simple_spinner_item);
        estilo.setAdapter(adapterEstilo);

        idioma = (Spinner) findViewById(R.id.idioma);
        ArrayAdapter<CharSequence> adapterIdioma = ArrayAdapter.createFromResource(this, R.array.idioma, android.R.layout.simple_spinner_item);
        idioma.setAdapter(adapterIdioma);

        aplicar = (Button) findViewById(R.id.buttonAplicar);

        aplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveApply();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void saveApply(){
        int txtTamaño, txtLetra, txtEstilo, txtIdioma;
        String strletra = "";
        txtTamaño = tamaño.getSelectedItemPosition();
        txtLetra = letra.getSelectedItemPosition();
        txtEstilo = estilo.getSelectedItemPosition();
        txtIdioma = idioma.getSelectedItemPosition();

        // Letra
        switch (txtLetra){
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

        // Estilo
        if(txtEstilo == 0){
            sharedPreferences.edit().putInt("THEME",1).apply();

        }else{
            sharedPreferences.edit().putInt("THEME",2).apply();
        }

        // Idioma
        switch (txtIdioma){
            case 0:
                setAppLocale("esp");
                sharedPreferences.edit().putInt("LANGUAGE",1).apply();
                break;
            case 1:
                setAppLocale("en");
                sharedPreferences.edit().putInt("LANGUAGE",2).apply();
                break;
            default:
                break;
        }


        Intent intent = new Intent(Ajustes.this, Ajustes.class);
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

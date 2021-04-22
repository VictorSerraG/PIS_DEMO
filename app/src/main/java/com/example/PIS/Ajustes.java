package com.example.PIS;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Ajustes extends AppCompatActivity {
    Spinner tamaño, letra, estilo, idioma;
    Button aplicar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        String txtTamaño, txtLetra, txtEstilo, txtIdioma;
        txtTamaño = tamaño.getSelectedItem().toString();
        txtLetra = letra.getSelectedItem().toString();
        txtEstilo = estilo.getSelectedItem().toString();
        txtIdioma = idioma.getSelectedItem().toString();

    }
}

package com.example.PIS;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Register extends AppCompatActivity {
    Button crear;
    EditText usuario, password, password2;
    CheckBox terms;
    AdaptadorBD DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        crear = (Button) findViewById(R.id.buttonCrear);
        usuario = (EditText) findViewById(R.id.editTextUsernameRegister);
        password = (EditText) findViewById(R.id.editTextPasswordRegister);
        password2 = (EditText) findViewById(R.id.editTextPasswordRegisterSecure);
        terms = (CheckBox) findViewById(R.id.termsOfUse);

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crear();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void crear(){
        DB = new AdaptadorBD(this);
        String user, passw, passw2, msj;
        boolean check;
        user = usuario.getText().toString();
        passw = password.getText().toString();
        passw2 = password2.getText().toString();
        check = terms.isChecked();
        if(user.equals("") || passw.equals("") || passw2.equals("")){
            msj = "Introduzca un usuario y una contraseña";
            usuario.requestFocus();
            password.requestFocus();
            password2.requestFocus();
            Mensaje(msj);
        }
        else if(!check){
            msj = "Acepte los términos de uso";
            terms.requestFocus();
            Mensaje(msj);
        }
        else if(!passw.equals(passw2)){
            msj = "Las contraseñas no coinciden";
            terms.requestFocus();
            Mensaje(msj);
        }
        else{
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
        }
    }

    public void Mensaje(String msj){
        Toast toast = Toast.makeText(this, msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}

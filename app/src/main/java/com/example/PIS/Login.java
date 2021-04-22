package com.example.PIS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Login extends Activity {
    Button entrar;
    EditText usuario, password;
    TextView registrar;
    AdaptadorBD DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Roboto-Thin.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        entrar = (Button) findViewById(R.id.buttonLogin);
        usuario = (EditText) findViewById(R.id.editTextUsernameLogin);
        password = (EditText) findViewById(R.id.editTextPasswordLogin);
        registrar = (TextView) findViewById(R.id.textRegister);


        registrar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                return true;
            }
        });

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logearse();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    private void logearse(){
        DB = new AdaptadorBD(this);
        String user, passw, msj;
        user = usuario.getText().toString();
        passw = password.getText().toString();
        if(user.equals("") || passw.equals("")){
            msj = "Introduzca un usuario y una contraseña";
            usuario.requestFocus();
            password.requestFocus();
            Mensaje(msj);
        }else{
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void Mensaje(String msj){
        Toast toast = Toast.makeText(this, msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}

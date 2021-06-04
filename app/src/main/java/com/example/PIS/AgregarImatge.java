package com.example.PIS;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class AgregarImatge extends AppCompatActivity {
    private static final int REQUEST_CODE_IMG = 101;
    private ImageView imageView;
    private EditText imgTitol;
    private Button btnUpdate;
    String user_id;
    Uri imatgeUri;
    boolean imatgeAfegida = false;
    private FirebaseFirestore db;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    private static final String TAG = "AddImage";

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
                            case 2: setAppLocale("ca");
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

                        setContentView(R.layout.agregar_imatge);
                        imageView = findViewById(R.id.imatgeAgregar);
                        imgTitol = findViewById(R.id.titol_Imatge);
                        btnUpdate = findViewById(R.id.btnUpload);
                        mAuth = FirebaseAuth.getInstance();
                        db = FirebaseFirestore.getInstance();
                        user_id = mAuth.getCurrentUser().getUid();
                        storageReference = FirebaseStorage.getInstance().getReference().child(user_id);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,REQUEST_CODE_IMG);
                            }
                        });
                        btnUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String titolImatge = imgTitol.getText().toString();
                                //if (titolImatge != null && imatgeAfegida != false){
                                    pujarImatge(titolImatge);
                                //}
                            }
                        });

                    } else {
                        Log.d(TAG, "No such document");

                        setContentView(R.layout.agregar_imatge);
                        imageView = findViewById(R.id.imatgeAgregar);
                        imgTitol = findViewById(R.id.titol_Imatge);
                        btnUpdate = findViewById(R.id.btnUpload);
                        mAuth = FirebaseAuth.getInstance();
                        db = FirebaseFirestore.getInstance();
                        user_id = mAuth.getCurrentUser().getUid();
                        storageReference = FirebaseStorage.getInstance().getReference().child(user_id);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,REQUEST_CODE_IMG);
                            }
                        });
                        btnUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String titolImatge = imgTitol.getText().toString();
                                //if (titolImatge != null && imatgeAfegida != false){
                                    pujarImatge(titolImatge);
                                //}
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                    setContentView(R.layout.agregar_imatge);
                    imageView = findViewById(R.id.imatgeAgregar);
                    imgTitol = findViewById(R.id.titol_Imatge);
                    btnUpdate = findViewById(R.id.btnUpload);
                    mAuth = FirebaseAuth.getInstance();
                    db = FirebaseFirestore.getInstance();
                    user_id = mAuth.getCurrentUser().getUid();
                    storageReference = FirebaseStorage.getInstance().getReference().child(user_id);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent,REQUEST_CODE_IMG);
                        }
                    });
                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String titolImatge = imgTitol.getText().toString();
                            //if (titolImatge != null && imatgeAfegida != false){
                                pujarImatge(titolImatge);
                            //}
                        }
                    });
                }
            }
        });

    }

    private void pujarImatge(String titolImatge) {
        String msj, title, uri;
        //Drawable image;
        title = imgTitol.getText().toString();
        //image = imageView.getDrawable();
        if (title.equals("")) {
            msj = getResources().getString(R.string.afegir_titolIMG);
            imgTitol.requestFocus();
            Mensaje(msj);
        }
        else if(!imatgeAfegida){
            msj = getResources().getString(R.string.afegir_contingutIMG);
            imageView.requestFocus();
            Mensaje(msj);
        }
        else {
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("imatges").document(titolImatge);
            storageReference.child(titolImatge).child(titolImatge + ".jpg").putFile(imatgeUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(titolImatge).child(titolImatge + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap hashMap = new HashMap();
                            hashMap.put("titol", titolImatge);
                            hashMap.put("imatge", uri.toString());
                            docRef.set(hashMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AgregarImatge.this, getResources().getString(R.string.ImageSuccess), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Main_activity_img.class));
                                }
                            });
                        }
                    });
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_IMG && data!=null)
        {
            imatgeUri=data.getData();
            imatgeAfegida=true;
            imageView.setImageURI(imatgeUri);
        }
    }

    private void setAppLocale(String localeCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(localeCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    public void Mensaje(String msj) {
        Toast toast = Toast.makeText(this, msj, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}

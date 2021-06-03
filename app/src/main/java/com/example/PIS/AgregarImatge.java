package com.example.PIS;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.UserData;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_imatge);


        imageView = findViewById(R.id.imatgeAgregar);
        imgTitol = findViewById(R.id.titol_Imatge);
        btnUpdate = findViewById(R.id.btnUpload);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child("Imatges");

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
                if (titolImatge != null && imatgeAfegida != false){
                    pujarImatge(titolImatge);
                }
            }
        });


    }

    private void pujarImatge(String titolImatge) {
        CollectionReference collRef = db.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("imatges");

        storageReference.child(user_id+".jpg").putFile(imatgeUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(user_id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("titol",titolImatge);
                        hashMap.put("imatge",uri.toString());
                        collRef.document(titolImatge).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(AgregarImatge.this, "Data Successfully Uploaded!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Main_activity_img.class));
                                }else{
                                    Toast.makeText(AgregarImatge.this, "Data Not Uploaded!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });


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
}

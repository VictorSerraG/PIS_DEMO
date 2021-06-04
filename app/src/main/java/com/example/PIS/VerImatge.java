package com.example.PIS;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class VerImatge extends AppCompatActivity {
    private ImageView imageView;
    TextView textView;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_imatge);

        imageView = findViewById(R.id.imageView_preview);
        textView = findViewById(R.id.textView_img);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        CollectionReference collRef = firestore.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("imatges");

        String titol = getIntent().getStringExtra("Titol");

        DocumentReference documentReference = collRef.document(titol);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String titol = task.getResult().getString("titol");
                String url = task.getResult().getString("imatge");

                Picasso.get().load(url).into(imageView);
                textView.setText(titol);
            }
        });


    }
}

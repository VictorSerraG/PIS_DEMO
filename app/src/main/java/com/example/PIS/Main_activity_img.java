package com.example.PIS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class Main_activity_img extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
        private static final int SETTINGS = Menu.FIRST;
        private static final int ORDENAR = Menu.FIRST + 1;
        private static final int ARCHIVADOS = Menu.FIRST + 2;
        private static final int EXIST = Menu.FIRST + 3;
        private static final int DELETE = Menu.FIRST + 4;

        TextView textLista;
        FloatingActionButton add;
        DrawerLayout drawerLayout;
        FirebaseRecyclerOptions<Imatge> options;
        FirestoreRecyclerAdapter<Imatge, ImatgeViewHolder> notaAdaptador;
        ActionBarDrawerToggle toggle;
        NavigationView nav_view;
        RecyclerView noteLists;
        private FirebaseFirestore db;
        Nota notaAct;

        DatabaseReference database;
        private FirebaseAuth mAuth;
        private static final String TAG = "MAIN";
        List<String> item = null;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main_activity);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);




            db = FirebaseFirestore.getInstance();
            database = FirebaseDatabase.getInstance().getReference().child("Imatges");
            mAuth = FirebaseAuth.getInstance();
            noteLists = findViewById(R.id.notelist);
            drawerLayout = findViewById(R.id.drawer);
            nav_view = findViewById(R.id.nav_view);
            nav_view.setNavigationItemSelectedListener(this);

            toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.resetPassw,R.string.noteContent);
            drawerLayout.addDrawerListener(toggle);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();




            CarregarData("");




            View headerView = nav_view.getHeaderView(0);
            TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

            userEmail.setText(mAuth.getCurrentUser().getEmail());

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

                            //Tamaño
                            int size;
                            try{
                                size = document.getLong("tamaño").intValue();
                            } catch (Exception e) {
                                size = 0;
                            }

                            //Bundle bundle = this.getIntent().getExtras();
                            //String email = bundle.getString("email");
                            //setUp(email);


                            add = (FloatingActionButton) findViewById(R.id.fabAdd);


                            add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getApplicationContext(),AgregarImatge.class));
                                }
                            });



                        } else {
                            Log.d(TAG, "No such document");



                            add = (FloatingActionButton) findViewById(R.id.fabAdd);


                            add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(getApplicationContext(),AgregarImatge.class));;
                                }
                            });


                            View headerView = nav_view.getHeaderView(0);
                            TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

                            userEmail.setText(mAuth.getCurrentUser().getEmail());
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());



                        add = (FloatingActionButton) findViewById(R.id.fabAdd);


                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getApplicationContext(),AgregarImatge.class));
                            }
                        });


                        View headerView = nav_view.getHeaderView(0);
                        TextView userEmail = headerView.findViewById(R.id.userDisplayEmail);

                        userEmail.setText(mAuth.getCurrentUser().getEmail());

                    }
                }
            });

        }

    private void CarregarData(String data) {


        Query query=db.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("imatges").orderBy("titol", Query.Direction.DESCENDING);


        FirestoreRecyclerOptions<Imatge> allNotes = new FirestoreRecyclerOptions.Builder<Imatge>()
                .setQuery(query,Imatge.class)
                .build();

        notaAdaptador=new FirestoreRecyclerAdapter<Imatge, ImatgeViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull ImatgeViewHolder holder, final int position, @NonNull Imatge imatge) {
                holder.imatgeTitle.setText(imatge.getTitol());
                Picasso.get().load(imatge.getImatge()).into(holder.imatgeContent);
                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        Intent intent=new Intent(this,ViewActivity.class);
                        intent.putExtra("ImgKey",getRef(position).getKey());
                        startActivity(intent);
                        */

                    }
                });

            }

            @NonNull
            @Override
            public ImatgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.imatge_view_layout,parent,false);
                return new ImatgeViewHolder(v);
            }
        };
        noteLists.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteLists.setAdapter(notaAdaptador);

    }

    @Override
        public boolean onCreateOptionsMenu(Menu menu){
            getMenuInflater().inflate(R.menu.menu_main, menu);
            menu.add(1,SETTINGS,0,R.string.menu_settings);
            menu.add(1,ORDENAR,0,R.string.menu_ordenar);
            menu.add(1,EXIST,0,R.string.menu_cerrar);
            menu.add(1,DELETE,0,R.string.menu_borrar);
            super.onCreateOptionsMenu(menu);
            return true;
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            switch (id){
                case SETTINGS:
                    Intent intent = new Intent(this, Ajustes.class);
                    startActivity(intent);
                    return true;
                case ORDENAR:
                    return true;
                case DELETE:
                    alert("deletes");
                    return true;
                case EXIST:
                    intent = new Intent(this, Login.class);
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



        public void actividad(String act){
            String type ="",content="";/*
            if (act.equals("add")){
                type = "add";
                Intent intent = new Intent(this,AgregarNota.class);
                intent.putExtra("type",type);
                startActivity(intent);
            }else{
                if(act.equals("edit")) {
                    type = "edit";

                    Intent intent = new Intent(this, AgregarNota.class);
                    intent.putExtra("type", type);
                    intent.putExtra("title", notaAct.getTitol());
                    intent.putExtra("content", notaAct.getContent());
                    startActivity(intent);
                }else {
                */
                    if (act.equals("see")){

                        Intent intent = new Intent(this, VerNota.class);
                        intent.putExtra("title", notaAct.getTitol());
                        intent.putExtra("content", notaAct.getContent());
                        startActivity(intent);
                    }


        }

        private void alert(String f){
            AlertDialog alerta;
            alerta = new android.app.AlertDialog.Builder(this).create();
            if(f.equals("list")){
                alerta.setTitle("The title of the note: " + notaAct.getTitol());
                alerta.setMessage("Quina acció vol realitzar?");
                alerta.setButton("Veure Nota", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actividad("see");

                    }
                });
                alerta.setButton2("Borrar Nota", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete("delete");
                        Intent intent = getIntent();
                        startActivity(intent);

                    }
                });
                alerta.setButton3("Editar Nota", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actividad("edit");

                    }
                });
            }else{
                if (f.equals("deletes")){
                    alerta.setTitle("Missatje de confirmació");
                    alerta.setMessage("Quina acció vol realitzar?");
                    alerta.setButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    alerta.setButton2("Borrar Notes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delete("deletes");
                            Intent intent = getIntent();
                            startActivity(intent);
                        }
                    });
                }

            }
            alerta.show();
        }
        //fix
        private void delete(String f){
            DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getEmail()).collection("notes").document(notaAct.getTitol());


            docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });



        }
        private void setUp(String email){

        }

        private void setAppLocale(String localeCode) {
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.setLocale(new Locale(localeCode.toLowerCase()));
            res.updateConfiguration(conf, dm);
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){

                case R.id.notes_lay:

                    startActivity(new Intent(this, MainActivity.class));
                    break;
                case R.id.Imatges_lay:

                    startActivity(new Intent(this, MainActivity.class));

                    break;
                default:
                    Toast.makeText(this,"ComingSoon",Toast.LENGTH_SHORT);
            }
            return false;
        }

        public class ImatgeViewHolder extends RecyclerView.ViewHolder{

            TextView imatgeTitle;
            ImageView imatgeContent;
            View view;
            CardView mCardView;

            public ImatgeViewHolder(@NonNull View itemView) {
                super(itemView);

                imatgeTitle = itemView.findViewById(R.id.titles_img);
                imatgeContent = itemView.findViewById(R.id.contentimg);
                mCardView = itemView.findViewById(R.id.noteCard_img);
                view = itemView;
            }
        }

        @Override
        protected void onStart() {
            super.onStart();
            notaAdaptador.startListening();
        }

        @Override
        protected void onStop() {
            super.onStop();
            if(notaAdaptador != null) {
                notaAdaptador.stopListening();
            }
        }
    }


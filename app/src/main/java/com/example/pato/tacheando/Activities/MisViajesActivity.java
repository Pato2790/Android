package com.example.pato.tacheando.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.pato.tacheando.Classes.SharedData;
import com.example.pato.tacheando.Classes.Viajes;
import com.example.pato.tacheando.Fragments.ListaViajeFragment;
import com.example.pato.tacheando.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MisViajesActivity extends DrawerNavActivity {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    protected int tipoViajes;
    private DatabaseReference viaje;

    ArrayList<DataSnapshot> AL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mis_viajes);

        viaje = db.child("Viaje");


        tipoViajes = getIntent().getIntExtra("tipo",0);

        listarViajes(tipoViajes);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //listarViajes(tipoViajes);
    }

    public void listarViajes(int tipo) {

        Fragment newFragment = ListaViajeFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.FragmentListaMisViaje, newFragment).commit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        if (tipo == 1) { //Viajes Creados

            new Thread(new Runnable() {
                @Override
                public void run() {
                    viaje.orderByChild("IDUsuarioCreador").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            AL = new ArrayList<DataSnapshot>();


                            Iterable<DataSnapshot> IViajes = dataSnapshot.getChildren();

                            for (DataSnapshot DS : IViajes) {
                                Log.d("TAG", "Viaje");
                                AL.add(DS);
                            }

                            if (AL.size() == 0) {
                                alertNoHayViajes();
                            }

                            SharedData.bus().post(new Viajes(AL));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }
            }).start();
        }
        else { // Viajes unidps
            AL = new ArrayList<DataSnapshot>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    viaje.orderByChild("IDUsuario1").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Iterable<DataSnapshot> IViajes = dataSnapshot.getChildren();

                            for (DataSnapshot DS : IViajes) {
                                Log.d("TAG", "Viaje");
                                AL.add(DS);
                            }

                            SharedData.bus().post(new Viajes(AL));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                    viaje.orderByChild("IDUsuario2").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Iterable<DataSnapshot> IViajes = dataSnapshot.getChildren();

                            for (DataSnapshot DS : IViajes) {
                                Log.d("TAG", "Viaje");
                                AL.add(DS);
                            }

                            SharedData.bus().post(new Viajes(AL));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                    viaje.orderByChild("IDUsuario3").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Iterable<DataSnapshot> IViajes = dataSnapshot.getChildren();

                            for (DataSnapshot DS : IViajes) {
                                Log.d("TAG", "Viaje");
                                AL.add(DS);
                            }

                            if(AL.size() == 0) {
                                alertNoHayViajes();
                            }
                            SharedData.bus().post(new Viajes(AL));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }
            }).start();
        }
    }

    public void alertNoHayViajes(){
        new AlertDialog.Builder(this)
                .setTitle("Ningun viaje encontrado")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create().show();
    }
}

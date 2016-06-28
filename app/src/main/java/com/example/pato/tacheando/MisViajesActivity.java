package com.example.pato.tacheando;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MisViajesActivity extends DrawerNav {

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    protected int tipoViajes;
    private DatabaseReference Viaje;

    ArrayList<DataSnapshot> AL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_mis_viajes);

        Viaje = DB.child("Viaje");


        tipoViajes = getIntent().getIntExtra("tipo",0);

        listarViajes(tipoViajes);
    }

    public void listarViajes(int tipo) {

        Fragment newFragment = FragmentListaViaje.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.FragmentListaMisViaje, newFragment).commit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        if (tipo == 1) { //Viajes Creados

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Viaje.orderByChild("IDUsuarioCreador").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            AL = new ArrayList<DataSnapshot>();


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
                }
            }).start();
        }
        else { // Viajes unidps
            AL = new ArrayList<DataSnapshot>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Viaje.orderByChild("IDUsuario1").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Viaje.orderByChild("IDUsuario2").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    Viaje.orderByChild("IDUsuario3").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                }
            }).start();
        }
    }
}

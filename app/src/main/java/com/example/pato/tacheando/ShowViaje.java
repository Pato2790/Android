package com.example.pato.tacheando;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ShowViaje extends DrawerNav {

    private TextView tCreador,tOrigen,tDestino,cantPasajeros,tPasajero2,tPasajero3,tPasajero4;
    private Button Agregar, Abandonar;
    private int CantPas;
    private String U1,U2,U3,UC,FechaViaje,LlegoDest,NombreViaje;
    private double LatO, LatD, LongO, LongD;
    private ImageView Mapa;

    //referencias a BD Firebase

    DatabaseReference MyRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference refViaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_show_viaje);

        Mapa = (ImageView) findViewById(R.id.imagenMapa);
        tCreador = (TextView) findViewById(R.id.creador);
        tOrigen = (TextView) findViewById(R.id.origen);
        tDestino = (TextView) findViewById(R.id.destino);
        cantPasajeros = (TextView) findViewById(R.id.cantPasajeros);
        tPasajero2 = (TextView) findViewById(R.id.pasajero2);
        tPasajero3 = (TextView) findViewById(R.id.pasajero3);
        tPasajero4 = (TextView) findViewById(R.id.pasajero4);
        Agregar = (Button) findViewById(R.id.agregarse);
        Abandonar = (Button) findViewById(R.id.abandonar);

        refViaje = MyRef.child("Viaje").child(getIntent().getStringExtra("id"));

    }

    @Override
    protected void onStart() {
        super.onStart();


        refViaje.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("TAG", dataSnapshot.getValue() +"");
                if(dataSnapshot.getValue() != null) {
                    UC = dataSnapshot.child("UsuarioCreador").getValue(String.class);
                    tCreador.setText("Creador: " + UC);
                    U1 = dataSnapshot.child("Usuario1").getValue(String.class);
                    tPasajero2.setText("Pasajero 2: " + U1);
                    U2 = dataSnapshot.child("Usuario2").getValue(String.class);
                    tPasajero3.setText("Pasajero 3: " + U2);
                    U3 = dataSnapshot.child("Usuario3").getValue(String.class);
                    tPasajero4.setText("Pasajero 4: " + U3);
                    LongO = dataSnapshot.child("LongOrigen").getValue(Double.class);
                    LatO = dataSnapshot.child("LatOrigen").getValue(Double.class);
                    LongD = dataSnapshot.child("LongDestino").getValue(Double.class);
                    LatD = dataSnapshot.child("LatDestino").getValue(Double.class);

                    Ubicacion U = new Ubicacion(ShowViaje.this);

                    tOrigen.setText("Origen: " + U.getAddress(LatO, LongO));

                    tDestino.setText("Destino: " + U.getAddress(LatD, LongD));

                    CantPas = dataSnapshot.child("CantPasajeros").getValue(Integer.class);
                    cantPasajeros.setText("Cantidad de Pasajeros: " + CantPas);

                    FechaViaje = dataSnapshot.child("FechaViaje").getValue(String.class);

                    LlegoDest = dataSnapshot.child("LlegoDestino").getValue(String.class);

                    NombreViaje = dataSnapshot.child("NombreViaje").getValue(String.class);

                    Mapa.setImageResource(R.drawable.mapa);
                }
                /*else {
                    finish();
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserActual = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (CantPas < 4) {
                    if (!U1.equals(UserActual) && !U2.equals(UserActual) && !U3.equals(UserActual) && !UC.equals(UserActual)) {
                        Map<String, Object> postValues = new HashMap<>();

                        switch (CantPas) {
                            case 1: {
                                U1 = UserActual;
                                break;
                            }
                            case 2: {
                                U2 = UserActual;
                                break;
                            }
                            case 3: {
                                U3 = UserActual;
                                break;
                            }
                        }

                        CantPas++;
                        postValues.put("CantPasajeros", CantPas);
                        postValues.put("NombreViaje", NombreViaje);
                        postValues.put("LatDestino", LatD);
                        postValues.put("LatOrigen", LatO);
                        postValues.put("LongDestino", LongD);
                        postValues.put("LongOrigen", LongO);
                        postValues.put("UsuarioCreador", UC);
                        postValues.put("Usuario1", U1);
                        postValues.put("Usuario2", U2);
                        postValues.put("Usuario3", U3);
                        postValues.put("FechaViaje", FechaViaje);
                        postValues.put("LlegoDestino", LlegoDest);

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/Viaje/" + getIntent().getStringExtra("id"), postValues);

                        String Key = MyRef.child("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ViajesUnidos").push().getKey();
                        childUpdates.put("/Usuarios/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/ViajesUnidos", new HashMap<String,Object>().put(Key, getIntent().getStringExtra("id")));

                        MyRef.updateChildren(childUpdates);

                    } else {
                        alertYaEnViaje();
                    }
                } else {
                    alertViajeCompleto();
                }
            }
        });

        Abandonar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserActual = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                boolean del = false;
                if (U1.equals(UserActual) || U2.equals(UserActual) || U3.equals(UserActual) || UC.equals(UserActual)) {
                    Map<String, Object> postValues = new HashMap<>();

                    if (U1.equals(UserActual)) {
                        U1 = "Lugar Disponible";
                    } else {
                        if (U2.equals(UserActual)) {
                            U2 = "Lugar Disponible";
                        } else {
                            if (U3.equals(UserActual)) {
                                U3 = "Lugar Disponible";
                            } else {
                                if (CantPas == 1) {
                                    MyRef.child("Viaje").child(getIntent().getStringExtra("id")).removeValue();
                                    del = true;
                                } else {
                                    switch (CantPas) {
                                        case 2: {
                                            UC = U1;
                                            U1 = "Lugar Disponible";
                                            break;
                                        }
                                        case 3: {
                                            UC = U2;
                                            U2 = "Lugar Disponible";
                                            break;
                                        }
                                        case 4: {
                                            UC = U3;
                                            U3 = "Lugar Disponible";
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(del) {
                        finish();
                    }
                    else {
                        CantPas--;

                        postValues.put("CantPasajeros", CantPas);
                        postValues.put("NombreViaje", NombreViaje);
                        postValues.put("LatDestino", LatD);
                        postValues.put("LatOrigen", LatO);
                        postValues.put("LongDestino", LongD);
                        postValues.put("LongOrigen", LongO);
                        postValues.put("UsuarioCreador", UC);
                        postValues.put("Usuario1", U1);
                        postValues.put("Usuario2", U2);
                        postValues.put("Usuario3", U3);
                        postValues.put("FechaViaje", FechaViaje);
                        postValues.put("LlegoDestino", LlegoDest);

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/Viaje/" + getIntent().getStringExtra("id"), postValues);

                        MyRef.updateChildren(childUpdates);
                    }
                } else {
                    alertNoEstaEnViaje();
                }
            }
        });
    }

    public void alertYaEnViaje() {
        new AlertDialog.Builder(ShowViaje.this)
                .setTitle("Ya estas unido")
                .setMessage("Ya perteneces al viaje, no es necesario que te vuelvas a unir.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    public void alertViajeCompleto() {
        new AlertDialog.Builder(ShowViaje.this)
                .setTitle("Viaje Completo")
                .setMessage("No hay plazas disponibles para este viaje.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    public void alertNoEstaEnViaje() {
        new AlertDialog.Builder(ShowViaje.this)
                .setTitle("No es posible abandonar el viaje")
                .setMessage("No puedes abandonar el viaje porque no estas unido a él.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

}

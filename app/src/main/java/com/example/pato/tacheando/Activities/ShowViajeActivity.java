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

public class ShowViajeActivity extends DrawerNavActivity {

    private TextView tCreador,tOrigen,tDestino,cantPasajeros,tPasajero2,tPasajero3,tPasajero4;
    private Button agregarBtn, abandonarBtn;
    private int cantPas;
    private String user1,user2,user3,userC,idUser1,idUser2,idUser3,idUserC,fechaViaje,llegoDest,nombreViaje;
    private double LatO, LatD, LongO, LongD;
    private ImageView map;

    //referencias a BD Firebase

    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference refViaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.actividad_show_viaje);

        map = (ImageView) findViewById(R.id.imagenMapa);
        tCreador = (TextView) findViewById(R.id.creador);
        tOrigen = (TextView) findViewById(R.id.origen);
        tDestino = (TextView) findViewById(R.id.destino);
        cantPasajeros = (TextView) findViewById(R.id.cantPasajeros);
        tPasajero2 = (TextView) findViewById(R.id.pasajero2);
        tPasajero3 = (TextView) findViewById(R.id.pasajero3);
        tPasajero4 = (TextView) findViewById(R.id.pasajero4);
        agregarBtn = (Button) findViewById(R.id.agregarse);
        abandonarBtn = (Button) findViewById(R.id.abandonar);

        refViaje = myRef.child("Viaje").child(getIntent().getStringExtra("id"));

    }

    @Override
    protected void onStart() {
        super.onStart();


        refViaje.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("TAG", dataSnapshot.getValue() +"");
                if(dataSnapshot.getValue() != null) {
                    userC = dataSnapshot.child("UsuarioCreador").getValue(String.class);
                    idUserC = dataSnapshot.child("IDUsuarioCreador").getValue(String.class);
                    tCreador.setText("Creador: " + userC);
                    user1 = dataSnapshot.child("Usuario1").getValue(String.class);
                    idUser1 = dataSnapshot.child("IDUsuario1").getValue(String.class);
                    tPasajero2.setText("Pasajero 2: " + user1);
                    user2 = dataSnapshot.child("Usuario2").getValue(String.class);
                    idUser2 = dataSnapshot.child("IDUsuario2").getValue(String.class);
                    tPasajero3.setText("Pasajero 3: " + user2);
                    user3 = dataSnapshot.child("Usuario3").getValue(String.class);
                    idUser3 = dataSnapshot.child("IDUsuario3").getValue(String.class);
                    tPasajero4.setText("Pasajero 4: " + user3);
                    LongO = dataSnapshot.child("LongOrigen").getValue(Double.class);
                    LatO = dataSnapshot.child("LatOrigen").getValue(Double.class);
                    LongD = dataSnapshot.child("LongDestino").getValue(Double.class);
                    LatD = dataSnapshot.child("LatDestino").getValue(Double.class);

                    Ubicacion U = new Ubicacion(ShowViajeActivity.this);

                    tOrigen.setText("Origen: " + U.getAddress(LatO, LongO));

                    tDestino.setText("Destino: " + U.getAddress(LatD, LongD));

                    cantPas = dataSnapshot.child("CantPasajeros").getValue(Integer.class);
                    cantPasajeros.setText("Cantidad de Pasajeros: " + cantPas);

                    fechaViaje = dataSnapshot.child("FechaViaje").getValue(String.class);

                    llegoDest = dataSnapshot.child("LlegoDestino").getValue(String.class);

                    nombreViaje = dataSnapshot.child("NombreViaje").getValue(String.class);

                    map.setImageResource(R.drawable.mapa);
                }
                /*else {
                    finish();
                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        agregarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserActual = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String IDUserActual = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (cantPas < 4) {
                    if (!user1.equals(UserActual) && !user2.equals(UserActual) && !user3.equals(UserActual) && !userC.equals(UserActual)) {
                        Map<String, Object> postValues = new HashMap<>();

                        switch (cantPas) {
                            case 1: {
                                user1 = UserActual;
                                idUser1 = IDUserActual;
                                break;
                            }
                            case 2: {
                                user2 = UserActual;
                                idUser2 = IDUserActual;
                                break;
                            }
                            case 3: {
                                user3 = UserActual;
                                idUser3 = IDUserActual;
                                break;
                            }
                        }

                        cantPas++;
                        postValues.put("CantPasajeros", cantPas);
                        postValues.put("NombreViaje", nombreViaje);
                        postValues.put("LatDestino", LatD);
                        postValues.put("LatOrigen", LatO);
                        postValues.put("LongDestino", LongD);
                        postValues.put("LongOrigen", LongO);
                        postValues.put("UsuarioCreador", userC);
                        postValues.put("IDUsuarioCreador", idUserC);
                        postValues.put("Usuario1", user1);
                        postValues.put("IDUsuario1", idUser1);
                        postValues.put("Usuario2", user2);
                        postValues.put("IDUsuario2", idUser2);
                        postValues.put("Usuario3", user3);
                        postValues.put("IDUsuario3", idUser3);
                        postValues.put("FechaViaje", fechaViaje);
                        postValues.put("LlegoDestino", llegoDest);

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/Viaje/" + getIntent().getStringExtra("id"), postValues);

                        String Key = myRef.child("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ViajesUnidos").push().getKey();

                        HashMap<String,Object> H = new HashMap<String,Object>();
                        H.put(getIntent().getStringExtra("id"), Key);

                        childUpdates.put("/Usuarios/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/ViajesUnidos", H);

                        myRef.updateChildren(childUpdates);

                    } else {
                        alertYaEnViaje();
                    }
                } else {
                    alertViajeCompleto();
                }
            }
        });

        abandonarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserActual = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                boolean del = false;
                if (user1.equals(UserActual) || user2.equals(UserActual) || user3.equals(UserActual) || userC.equals(UserActual)) {
                    Map<String, Object> postValues = new HashMap<>();

                    if (user1.equals(UserActual)) {
                        user1 = user2; idUser1 = idUser2;
                        user2 = user3; idUser2 = idUser3;
                        user3 = "Lugar Disponible"; idUser3 = "";

                    } else {
                        if (user2.equals(UserActual)) {
                            user2 = user3; idUser2 = idUser3;
                            user3 = "Lugar Disponible"; idUser3 = "";
                        } else {
                            if (user3.equals(UserActual)) {
                                user3 = "Lugar Disponible";
                                idUser3 = "";
                            } else {
                                if (cantPas == 1) {
                                    myRef.child("Viaje").child(getIntent().getStringExtra("id")).removeValue();
                                    del = true;

                                    myRef.child("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ViajesCreados").child(getIntent().getStringExtra("id")).removeValue();

                                } else {
                                    switch (cantPas) {
                                        case 2: {
                                            userC = user1;
                                            idUserC = idUser1;
                                            user1 = "Lugar Disponible";
                                            idUser1 = "";
                                            break;
                                        }
                                        case 3: {
                                            userC = user2;
                                            idUserC = idUser2;
                                            user2 = "Lugar Disponible";
                                            idUser2 = "";
                                            break;
                                        }
                                        case 4: {
                                            userC = user3;
                                            idUserC = idUser3;
                                            user3 = "Lugar Disponible";
                                            idUser3 = "";
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
                        cantPas--;

                        postValues.put("CantPasajeros", cantPas);
                        postValues.put("NombreViaje", nombreViaje);
                        postValues.put("LatDestino", LatD);
                        postValues.put("LatOrigen", LatO);
                        postValues.put("LongDestino", LongD);
                        postValues.put("LongOrigen", LongO);
                        postValues.put("UsuarioCreador", userC);
                        postValues.put("IDUsuarioCreador", idUserC);
                        postValues.put("Usuario1", user1);
                        postValues.put("IDUsuario1", idUser1);
                        postValues.put("Usuario2", user2);
                        postValues.put("IDUsuario2", idUser2);
                        postValues.put("Usuario3", user3);
                        postValues.put("IDUsuario3", idUser3);
                        postValues.put("FechaViaje", fechaViaje);
                        postValues.put("LlegoDestino", llegoDest);

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/Viaje/" + getIntent().getStringExtra("id"), postValues);

                        myRef.child("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ViajesUnidos").child(getIntent().getStringExtra("id")).removeValue();

                        myRef.updateChildren(childUpdates);
                    }
                } else {
                    alertNoEstaEnViaje();
                }
            }
        });
    }

    public void alertYaEnViaje() {
        new AlertDialog.Builder(ShowViajeActivity.this)
                .setTitle("Ya estas unido")
                .setMessage("Ya perteneces al viaje, no es necesario que te vuelvas a unir.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    public void alertViajeCompleto() {
        new AlertDialog.Builder(ShowViajeActivity.this)
                .setTitle("Viaje Completo")
                .setMessage("No hay plazas disponibles para este viaje.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    public void alertNoEstaEnViaje() {
        new AlertDialog.Builder(ShowViajeActivity.this)
                .setTitle("No es posible abandonar el viaje")
                .setMessage("No puedes abandonar el viaje porque no estas unido a él.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

}

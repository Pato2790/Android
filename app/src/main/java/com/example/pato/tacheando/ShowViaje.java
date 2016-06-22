package com.example.pato.tacheando;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowViaje extends AppCompatActivity {

    private TextView tCreador,tOrigen,tDestino,cantPasajeros,tPasajero2,tPasajero3,tPasajero4;
    private Button Agregar;
    private int CantPas;
    private String U1,U2,U3,UC;

    //referencias a BD Firebase

    DatabaseReference MyRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference refViaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_show_viaje);

        tCreador = (TextView) findViewById(R.id.creador);
        tOrigen = (TextView) findViewById(R.id.origen);
        tDestino = (TextView) findViewById(R.id.destino);
        cantPasajeros = (TextView) findViewById(R.id.cantPasajeros);
        tPasajero2 = (TextView) findViewById(R.id.pasajero2);
        tPasajero3 = (TextView) findViewById(R.id.pasajero3);
        tPasajero4 = (TextView) findViewById(R.id.pasajero4);
        Agregar = (Button) findViewById(R.id.agregarse);

        refViaje = MyRef.child("Viaje").child(getIntent().getStringExtra("id"));

    }

    @Override
    protected void onStart() {
        super.onStart();


        refViaje.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UC = dataSnapshot.child("UsuarioCreador").getValue(String.class);
                tCreador.setText("Creador: " + UC );

                U1 = dataSnapshot.child("Usuario1").getValue(String.class);
                tPasajero2.setText("Pasajero 2: " + U1);
                U2 = dataSnapshot.child("Usuario2").getValue(String.class);
                tPasajero3.setText("Pasajero 3: " + U2);
                U3 = dataSnapshot.child("Usuario3").getValue(String.class);
                tPasajero4.setText("Pasajero 4: " + U3);
                Double longOrigen = dataSnapshot.child("LongOrigen").getValue(Double.class);
                Double latOrigen = dataSnapshot.child("LatOrigen").getValue(Double.class);
                Double longDestino = dataSnapshot.child("LongDestino").getValue(Double.class);
                Double latDestino = dataSnapshot.child("LatDestino").getValue(Double.class);

                tOrigen.setText("Origen: " + getAddress(latOrigen,longOrigen));

                tDestino.setText("Destino: " + getAddress(latDestino,longDestino));

                CantPas = dataSnapshot.child("CantPasajeros").getValue(Integer.class);
                cantPasajeros.setText("Cantidad de Pasajeros: " + CantPas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UserActual = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (CantPas < 4 && !U1.equals(UserActual) && !U2.equals(UserActual) && !U3.equals(UserActual) && !UC.equals(UserActual)) {
                    Map<String, Object> postValues = new HashMap<>();
                    postValues.put("Usuario" + (CantPas - 1), UserActual);
                    postValues.put("CantPasajeros", CantPas + 1);

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/Viajes/" + getIntent().getStringExtra("id"), postValues);

                    MyRef.updateChildren(childUpdates);
                }
                else
                {
                    new AlertDialog.Builder(getApplication())
                            .setTitle("Viaje Completo")
                            .setMessage("No hay plazas disponibles para este viaje")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            }).create().show();
                }
            }
        });
    }

    public String getAddress(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            String address = addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();

            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}

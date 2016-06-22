package com.example.pato.tacheando;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends AppCompatActivity implements FragmentBusqueda.OnFragmentInteractionListener,LocationListener{

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference Viaje;
    private FirebaseUser user;

    public String LatOrigen = "";
    public String LongOrigen ="";
    private String LatDestino = "";
    private String LongDestino ="";

    private double Distancia = 0.004;

    private TextView T1,T2,T3,T4;

    private LocationManager locationManager;
    private boolean networkOn;

    private String uid;
    private String userName;
    private String userEmail;
    private Uri userPhotoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*T1 = (TextView)findViewById(R.id.textView);
        T2 = (TextView)findViewById(R.id.textView2);
        T3 = (TextView)findViewById(R.id.textView3);
        T4 = (TextView)findViewById(R.id.textView4);*/

        //Para obtener Ubicacion
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        networkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        try
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1 , this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1 , this);

        }
        catch (SecurityException e){System.out.println("Error en el mapa");}

        getLocation();

        Fragment newFragment = FragmentBusqueda.newInstance(LongOrigen, LatOrigen);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.FragmentBusq, newFragment).commit();

        Viaje = DB.child("Viaje");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            userName = user.getDisplayName();
            userEmail = user.getEmail();
            userPhotoUrl = user.getPhotoUrl();
            uid = user.getUid();
        }

    }

   @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Metodo Listener FragmentBusqueda
    @Override
    public void BuscarAction(String Direccion) {
        obtLatLong(Direccion);

        Fragment newFragment = FragmentListaViaje.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.FragmentListaViaje, newFragment).commit();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Viaje.orderByChild("LongDestino").startAt(Double.parseDouble(LongDestino) - Distancia).endAt(Double.parseDouble(LongDestino) + Distancia).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Double LatD, LatO, LongO;

                        Iterable<DataSnapshot> IViajes = dataSnapshot.getChildren();

                        ArrayList<DataSnapshot> AL = new ArrayList<DataSnapshot>();

                        for (DataSnapshot DS: IViajes) {
                            LatD = DS.child("LatDestino").getValue(Double.class);
                            LatO = DS.child("LatOrigen").getValue(Double.class);
                            LongO = DS.child("LongOrigen").getValue(Double.class);

                            if(Double.parseDouble(LatDestino) - Distancia < LatD && Double.parseDouble(LatDestino) + Distancia > LatD )
                            {
                                if(Double.parseDouble(LatOrigen) - Distancia < LatO && Double.parseDouble(LatOrigen) + Distancia > LatO )
                                {
                                    if(Double.parseDouble(LongOrigen) - Distancia < LongO && Double.parseDouble(LongOrigen) + Distancia > LongO)
                                    {
                                        AL.add(DS);
                                    }
                                }
                            }
                        }

                        if(AL.size() == 0)
                        {
                            new AlertDialog.Builder(Main.this)
                                    .setTitle("Ningun viaje encontrado")
                                    .setMessage("¿Desea crear un nuevo viaje?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String Key = DB.child("Viaje").push().getKey();

                                            Viaje V = new Viaje(Double.parseDouble(LatDestino),Double.parseDouble(LatOrigen),Double.parseDouble(LongDestino),Double.parseDouble(LongOrigen),"No", userName);
                                            Map<String, Object> postValues = V.toMap();
                                            Map<String, Object> childUpdates = new HashMap<>();
                                            childUpdates.put("/Viaje/" + Key, postValues);
                                            DB.updateChildren(childUpdates);

                                            Intent SV = new Intent(Main.this,ShowViaje.class);
                                            SV.putExtra("id",Key);
                                            startActivity(SV);

                                        }
                                    })
                                    .setNegativeButton("Seguir Buscando", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {}
                                    }).create().show();
                        }
                        else
                        {
                            SharedData.bus().post(new Viajes(AL));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).start();

    }

    //Metodos de Google Location
    public void obtLatLong(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress,5);

            Address location=address.get(0);
            location.getLatitude();
            location.getLongitude();

            LatDestino = "" + (location.getLatitude());
            //T1.setText("LatDestino: " + LatDestino);
            LongDestino = "" + (location.getLongitude());
            //T2.setText("LongDestino: " + LongDestino);
        }
        catch (Exception e){}

    }


    private void getLocation(){
        Location lcGPS = null;
        Location lcNETWORK = null;
        if (networkOn){

            try
            {
                lcGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                lcNETWORK = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            catch (SecurityException e){}

            if(lcGPS != null) {
                LatOrigen = "" + lcGPS.getLatitude();
                //T3.setText("LatOrigen: " + LatOrigen);
                LongOrigen = "" + lcGPS.getLongitude();
                //T4.setText("LongOrigen: " + LongOrigen);
            }
            else
            {
                LatOrigen = "" + lcNETWORK.getLatitude();
                //T3.setText("LatOrigen: " + LatOrigen);
                LongOrigen = "" + lcNETWORK.getLongitude();
                //T4.setText("LongOrigen: " + LongOrigen);
            }
        }
    }

    public class Viajes {
        public ArrayList<DataSnapshot> viajes;

        public Viajes(ArrayList<DataSnapshot> v) {
            viajes = v;
        }

    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.setClass(context, Main.class);
        return in;
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}

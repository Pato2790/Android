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
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
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

public class Main extends DrawerNav implements FragmentBusqueda.OnFragmentInteractionListener, LocationListener {

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference Viaje;
    private FirebaseUser user;

    public String LatOrigen = "";
    public String LongOrigen = "";
    private String LatDestino = "";
    private String LongDestino = "";

    private double Distancia = 0.004;

    private TextView T1, T2, T3, T4;

    private LocationManager locationManager;
    private boolean networkOn;

    private String uid;
    private String userName;
    private String userEmail;
    private Uri userPhotoUrl;

    private ArrayList<DataSnapshot> AL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Para obtener Ubicacion
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        networkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!networkOn) {
            alertGps();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

        } catch (SecurityException e) {
            System.out.println("Error en el mapa");
        }

        iniciarFragmentBusqueda();

        Viaje = DB.child("Viaje");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
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
        closeKeyboard();


        if (Direccion.trim().equals("") || Direccion == null) {
            alertDireccionInvalida();
        } else if (LatOrigen.trim() == "" || LongOrigen.trim() == "") {
            alertUbicacion();
        } else {
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

                            AL = new ArrayList<DataSnapshot>();

                            for (DataSnapshot DS : IViajes) {
                                LatD = DS.child("LatDestino").getValue(Double.class);
                                LatO = DS.child("LatOrigen").getValue(Double.class);
                                LongO = DS.child("LongOrigen").getValue(Double.class);

                                if (Double.parseDouble(LatDestino) - Distancia < LatD && Double.parseDouble(LatDestino) + Distancia > LatD) {
                                    if (Double.parseDouble(LatOrigen) - Distancia < LatO && Double.parseDouble(LatOrigen) + Distancia > LatO) {
                                        if (Double.parseDouble(LongOrigen) - Distancia < LongO && Double.parseDouble(LongOrigen) + Distancia > LongO) {
                                            AL.add(DS);
                                        }
                                    }
                                }
                            }
                            SharedData.bus().post(new Viajes(AL));
                            if (AL.size() == 0) {
                                alertNoHayViajes();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }
            }).start();
        }

    }

    //Metodos de Google Location
    public void obtLatLong(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);

            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            LatDestino = "" + (location.getLatitude());
            LongDestino = "" + (location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean getLocation() {
        Location lcGPS = null;
        Location lcNETWORK = null;
        if (networkOn) {

            try {
                lcGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                lcNETWORK = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            if (lcGPS != null) {
                LatOrigen = "" + lcGPS.getLatitude();
                LongOrigen = "" + lcGPS.getLongitude();
            } else if (lcNETWORK != null){
                LatOrigen = "" + lcNETWORK.getLatitude();
                LongOrigen = "" + lcNETWORK.getLongitude();
            }
            else {
                return false;
            }
            return true;
        }
        return false;
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
        iniciarFragmentBusqueda();
    }

    @Override
    public void onProviderDisabled(String provider) {
        /*new AlertDialog.Builder(Main.this)
                .setTitle("Es necesario que active su GPS")
                .setPositiveButton("Ir a mi configuracion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).create().show();*/
    }

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Log.d("TAG","Activity result");
            networkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!networkOn) {
                alertGps();
            }
            else {
                //iniciarFragmentBusqueda();
            }
        }
    }



    protected void iniciarFragmentBusqueda() {
//        Log.d("TAG", "Iniciar Frag1");
        networkOn = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (networkOn) {
//            Log.d("TAG", "Iniciar Frag2");
            while(!getLocation());
            if(getLocation()) {
//                Log.d("TAG", "Iniciar Frag3");
                Fragment newFragment = FragmentBusqueda.newInstance(LongOrigen, LatOrigen);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.FragmentBusq, newFragment).commitAllowingStateLoss();
            }
            else {
                alertUbicacion();
            }
        }
    }

    protected void alertGps() {
        new AlertDialog.Builder(Main.this)
                .setTitle("Es necesario que active su GPS")
                .setPositiveButton("Ir a mi configuracion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                    }
                })
                .setNegativeButton("Salir de la aplicacion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false).create().show();
    }

    protected void alertUbicacion() {
        new AlertDialog.Builder(Main.this)
                .setTitle("No se ha podido determinar tu ubicacion actual")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    protected void alertDireccionInvalida() {
        new AlertDialog.Builder(Main.this)
                .setTitle("Debes ingresar una direccion valida")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    protected void alertNoHayViajes() {
        new AlertDialog.Builder(Main.this)
                .setTitle("Ningun viaje encontrado")
                .setMessage("¿Desea crear un nuevo viaje?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String Key = DB.child("Viaje").push().getKey();

                        Viaje V = new Viaje(Double.parseDouble(LatDestino), Double.parseDouble(LatOrigen), Double.parseDouble(LongDestino), Double.parseDouble(LongOrigen), "No", userName);
                        Map<String, Object> postValues = V.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/Viaje/" + Key, postValues);
                        DB.updateChildren(childUpdates);

                        Intent SV = new Intent(Main.this, ShowViaje.class);
                        SV.putExtra("id", Key);
                        startActivity(SV);

                    }
                })
                .setNegativeButton("Seguir Buscando", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    protected void closeKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public class Viajes {
        public ArrayList<DataSnapshot> viajes;

        public Viajes(ArrayList<DataSnapshot> v) {
            viajes = v;
        }

    }
}

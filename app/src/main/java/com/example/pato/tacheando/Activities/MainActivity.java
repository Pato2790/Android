package com.example.pato.tacheando;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

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

public class MainActivity extends DrawerNavActivity implements BusquedaFragment.OnFragmentInteractionListener, LocationListener {

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference viaje;
    private FirebaseUser user;

    public String latOrigen = "";
    public String longOrigen = "";
    private String latDestino = "";
    private String longDestino = "";

    private double distancia = 0.004;

    //private TextView T1, T2, T3, T4;

    private LocationManager locationManager;
    private boolean networkOn;

    private String uid;
    private String userName;
    private String userEmail;
    private Uri userPhotoUrl;

    private boolean alert;

    private ArrayList<DataSnapshot> list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        alert = false;

        checkPermissions();

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

        viaje = dbRef.child("Viaje");

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName = user.getDisplayName();
            userEmail = user.getEmail();
            userPhotoUrl = user.getPhotoUrl();
            uid = user.getUid();
        }


    }



    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Metodo Listener BusquedaFragment
    @Override
    public void BuscarAction(String Direccion) {
        closeKeyboard();

        alert = false;


        if (Direccion.trim().equals("") || Direccion == null) {
            alertDireccionInvalida();
        } else if (latOrigen.trim() == "" || longOrigen.trim() == "") {
            alertUbicacion();
        } else {
            obtLatLong(Direccion);

            Fragment newFragment = ListaViajeFragment.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.FragmentListaViaje, newFragment).commit();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    viaje.orderByChild("LongDestino").startAt(Double.parseDouble(longDestino) - distancia).endAt(Double.parseDouble(longDestino) + distancia).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Double LatD, LatO, LongO;

                            Iterable<DataSnapshot> IViajes = dataSnapshot.getChildren();

                            list = new ArrayList<DataSnapshot>();
                            String idCreador;

                            for (DataSnapshot DS : IViajes) {
                                idCreador = DS.child("IDUsuarioCreador").getValue(String.class);
                                LatD = DS.child("LatDestino").getValue(Double.class);
                                LatO = DS.child("LatOrigen").getValue(Double.class);
                                LongO = DS.child("LongOrigen").getValue(Double.class);


                                if (Double.parseDouble(latDestino) - distancia < LatD && Double.parseDouble(latDestino) + distancia > LatD) {
                                    if (Double.parseDouble(latOrigen) - distancia < LatO && Double.parseDouble(latOrigen) + distancia > LatO) {
                                        if (Double.parseDouble(longOrigen) - distancia < LongO && Double.parseDouble(longOrigen) + distancia > LongO) {
                                            if (!uid.equals(idCreador)) {
                                                list.add(DS);
                                            }
                                        }
                                    }
                                }
                            }
                            if (list.size() == 0 && !alert) {
                                alert = true;
                                alertNoHayViajes();
                            }
                            SharedData.bus().post(new Viajes(list));

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

            latDestino = "" + (location.getLatitude());
            longDestino = "" + (location.getLongitude());
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
                latOrigen = "" + lcGPS.getLatitude();
                longOrigen = "" + lcGPS.getLongitude();
            } else if (lcNETWORK != null){
                latOrigen = "" + lcNETWORK.getLatitude();
                longOrigen = "" + lcNETWORK.getLongitude();
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
        in.setClass(context, MainActivity.class);
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
        /*new AlertDialog.Builder(MainActivity.this)
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
                Fragment newFragment = BusquedaFragment.newInstance(longOrigen, latOrigen);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.FragmentBusq, newFragment).commitAllowingStateLoss();
            }
            else {
                alertUbicacion();
            }
        }
    }

    protected void alertGps() {
        new AlertDialog.Builder(MainActivity.this)
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
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("No se ha podido determinar tu ubicacion actual")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    protected void alertDireccionInvalida() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Debes ingresar una direccion valida")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    protected void alertNoHayViajes() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Ningun viaje encontrado")
                .setMessage("¿Desea crear un nuevo viaje?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                        String Key = dbRef.child("Viaje").push().getKey();

                        Viaje v = new Viaje(Double.parseDouble(latDestino), Double.parseDouble(latOrigen), Double.parseDouble(longDestino), Double.parseDouble(longOrigen), "No", userName, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Map<String, Object> postValues = v.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/Viaje/" + Key, postValues);

                        String Key2 = dbRef.child("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ViajesCreados").push().getKey();

                        HashMap<String,Object> H = new HashMap<String,Object>();
                        H.put(Key,Key2);

                        childUpdates.put("/Usuarios/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/ViajesCreados", H);

                        dbRef.updateChildren(childUpdates);

                        Intent SV = new Intent(MainActivity.this, ShowViajeActivity.class);
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

    protected void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                }
                return;
            }

        }
    }

    public void crearViaje(String direccion) {
        if (direccion.trim().equals("") || direccion == null) {
            alertDireccionInvalida();
        } else if (latOrigen.trim() == "" || longOrigen.trim() == "") {
            alertUbicacion();
        } else {
            obtLatLong(direccion);

            String Key = dbRef.child("Viaje").push().getKey();

            Viaje v = new Viaje(Double.parseDouble(latDestino), Double.parseDouble(latOrigen), Double.parseDouble(longDestino), Double.parseDouble(longOrigen), "No", userName, FirebaseAuth.getInstance().getCurrentUser().getUid());
            Map<String, Object> postValues = v.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/Viaje/" + Key, postValues);

            String Key2 = dbRef.child("Usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ViajesCreados").push().getKey();

            HashMap<String,Object> H = new HashMap<String,Object>();
            H.put(Key,Key2);

            childUpdates.put("/Usuarios/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/ViajesCreados", H);

            dbRef.updateChildren(childUpdates);

            Intent SV = new Intent(MainActivity.this, ShowViajeActivity.class);
            SV.putExtra("id", Key);
            startActivity(SV);
        }
    }

}

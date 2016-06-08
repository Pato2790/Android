package com.example.pato.tacheando;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,FragmentBusqueda.OnFragmentInteractionListener,LocationListener {

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference Viaje;

    private String LatOrigen = "";
    private String LongOrigen ="";
    private String LatDestino = "";
    private String LongDestino ="";

    private double Distancia = 0.004;

    private TextView T1,T2,T3,T4;

    private LocationManager locationManager;
    private String proveedor;
    private boolean networkOn;

    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView Autocomplete;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private TextView mPlaceDetailsText;
    private TextView mPlaceDetailsAttribution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment newFragment = FragmentBusqueda.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.FragmentBusq, newFragment).commit();

        Viaje = DB.child("Viaje");

        T1 = (TextView)findViewById(R.id.textView);
        T2 = (TextView)findViewById(R.id.textView2);
        T3 = (TextView)findViewById(R.id.textView3);
        T4 = (TextView)findViewById(R.id.textView4);

        //Para obtener Ubicacion
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        proveedor = LocationManager.GPS_PROVIDER;
        networkOn = locationManager.isProviderEnabled(proveedor);
        try
        {
            locationManager.requestLocationUpdates(proveedor, 1000, 1 , this);
        }
        catch (SecurityException e){System.out.println("Error en el mapa");}

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        Autocomplete = (AutoCompleteTextView)
                findViewById(R.id.direccion2);
        Autocomplete.setOnItemClickListener(mAutocompleteClickListener);

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        Autocomplete.setAdapter(mAdapter);

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
                final AutocompletePrediction item = mAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);


            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

                Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                        Toast.LENGTH_SHORT).show();

            }
        };


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //Metodo Listener FragmentBusqueda
    @Override
    public void BuscarAction(String Direccion) {
        obtLatLong("Avenida Colón 22, Bahía Blanca, Argentina");

        getLocation();

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
            T1.setText("LatDestino: " + LatDestino);
            LongDestino = "" + (location.getLongitude());
            T2.setText("LongDestino: " + LongDestino);
        }
        catch (Exception e){}

    }

    private void getLocation(){
        Location lc = null;
        if (networkOn){

            try
            {
                lc = locationManager.getLastKnownLocation(proveedor);
            }
            catch (SecurityException e){}

            if(lc != null){
                LatOrigen = "" + lc.getLatitude();
                T3.setText("LatOrigen: " + LatOrigen);
                LongOrigen = "" + lc.getLongitude();
                T4.setText("LongOrigen: " + LongOrigen);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            places.release();
        }
    };


}

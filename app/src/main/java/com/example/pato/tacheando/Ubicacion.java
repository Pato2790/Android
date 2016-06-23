package com.example.pato.tacheando;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pato on 22/06/2016.
 */
public class Ubicacion {

    public Context C;

    public Ubicacion(Context C)
    {
        this.C = C;
    }

    public String getAddress(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(C, Locale.getDefault());
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

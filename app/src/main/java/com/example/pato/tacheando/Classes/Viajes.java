package com.example.pato.tacheando;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/**
 * Created by fede on 6/28/2016.
 */
public class Viajes {
    public ArrayList<DataSnapshot> viajes;

    public Viajes(ArrayList<DataSnapshot> v) {
        viajes = v;
    }

}

package com.example.pato.tacheando;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pato on 05/06/2016.
 */
@IgnoreExtraProperties
public class Viaje {

    public int CantPas;
    public String FechaViaje;
    public double LatDestino;
    public double LatOrigen;
    public double LongOrigen;
    public double LongDestino;
    public String LlegoDestino;
    public String User1;
    public String User2;
    public String User3;
    public String UserCreador;
    public Map<String, Boolean> stars = new HashMap<>();

    public Viaje() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Viaje(double LatD, double LatO, double LongD, double LongO, String LD, String UC) {
        CantPas = 1;
        FechaViaje = new Date().toString();
        LatDestino = LatD;
        LatOrigen = LatO;
        LongOrigen = LongO;
        LongDestino = LongD;
        UserCreador = UC;
        LlegoDestino = LD;
        User1 = "Lugar Disponible";
        User2 = "Lugar Disponible";
        User3 = "Lugar Disponible";
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("CantPasajeros", CantPas);
        result.put("FechaViaje", FechaViaje);
        result.put("LatDestino", LatDestino);
        result.put("LatOrigen", LatOrigen);
        result.put("LongDestino", LongDestino);
        result.put("LongOrigen", LongOrigen);
        result.put("UsuarioCreador", UserCreador);
        result.put("Usuario1", User1);
        result.put("Usuario2", User2);
        result.put("Usuario3", User3);
        result.put("LlegoDestino", LlegoDestino);
        result.put("NombreViaje", "ViajeBahia");

        return result;
    }

}

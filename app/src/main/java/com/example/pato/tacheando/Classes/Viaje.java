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

    public int cantPas;
    public String fechaViaje;
    public double latDestino;
    public double latOrigen;
    public double longOrigen;
    public double longDestino;
    public String llegoDestino;
    public String user1;
    public String user2;
    public String user3;
    public String userCreador;
    public String idUser1;
    public String idUser2;
    public String idUser3;
    public String idUserCreador;

    public Map<String, Boolean> stars = new HashMap<>();

    public Viaje() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Viaje(double LatD, double LatO, double LongD, double LongO, String LD, String UC, String idUC) {
        cantPas = 1;
        fechaViaje = new Date().toString();
        latDestino = LatD;
        latOrigen = LatO;
        longOrigen = LongO;
        longDestino = LongD;
        userCreador = UC;
        llegoDestino = LD;
        user1 = "Lugar Disponible";
        user2 = "Lugar Disponible";
        user3 = "Lugar Disponible";
        idUser1 = "";
        idUser2 = "";
        idUser3 = "";
        idUserCreador = idUC;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("CantPasajeros", cantPas);
        result.put("FechaViaje", fechaViaje);
        result.put("LatDestino", latDestino);
        result.put("LatOrigen", latOrigen);
        result.put("LongDestino", longDestino);
        result.put("LongOrigen", longOrigen);
        result.put("UsuarioCreador", userCreador);
        result.put("IDUsuarioCreador", idUserCreador);
        result.put("Usuario1", user1);
        result.put("IDUsuario1", idUser1);
        result.put("Usuario2", user2);
        result.put("IDUsuario2", idUser2);
        result.put("Usuario3", user3);
        result.put("IDUsuario3", idUser3);
        result.put("LlegoDestino", llegoDestino);
        result.put("NombreViaje", "ViajeBahia");

        return result;
    }

}

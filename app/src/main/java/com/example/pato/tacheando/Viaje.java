package com.example.pato.tacheando;

/**
 * Created by Pato on 05/06/2016.
 */
public class Viaje {

    private String FechaViaje;
    private String LatDestino;
    private String LongDestino;
    private String LatOrigen;
    private String LongOrigen;
    private String Usuario1;
    private String Usuario2;
    private String Usuario3;
    private String UsuarioCreador;
    private String NombreViaje;
    private String LlegoDestino;

    public Viaje(String FV, String LatD, String LongD, String LatO, String LongO, String U1, String U2, String U3, String UC, String NV, String LD)
    {
        FechaViaje = FV; LatDestino = LatD; LongDestino = LongO; LatOrigen = LatO; LongOrigen = LongO; Usuario1 = U1; Usuario2 = U2;
        Usuario3 = U3; UsuarioCreador = UC; NombreViaje = NV; LlegoDestino = LD;
    }


}

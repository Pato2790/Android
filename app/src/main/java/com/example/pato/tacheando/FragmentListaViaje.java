package com.example.pato.tacheando;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by Pato on 07/06/2016.
 */

public class FragmentListaViaje extends Fragment {

    private ArrayList<DataSnapshot> Viajes = new ArrayList<DataSnapshot>();

    private ListView Lista;

    public static FragmentListaViaje newInstance() {
        FragmentListaViaje fragment = new FragmentListaViaje();
        return fragment;
    }

    public FragmentListaViaje() {
    }

    @Subscribe
    public void setIterable(Main.Viajes v) {
        Viajes = v.viajes;
        Listar();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedData.bus().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        SharedData.bus().unregister(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fagment_lista_viajes, container, false);
        Lista = (ListView) v.findViewById(R.id.listaViaje);

        return v;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    //Metodos Listener FragmentListaViaje
    public void Listar(){

        MyAdapter adapter = new MyAdapter(getActivity(),
                R.layout.itemviaje,
                Viajes);

        Lista.setAdapter(adapter);

        Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Intent intent = new Intent(Main.this, ShowPersona.class);
                intent.putExtra("id",ArrayPersonas.get(position).getKey());
                startActivity(intent);*/

                Intent SV = new Intent(getActivity(),ShowViaje.class);
                SV.putExtra("id",Viajes.get(position).getKey());
                startActivity(SV);
            }
        });
    }

    public class MyAdapter extends ArrayAdapter<DataSnapshot> {
        Context context;
        int layoutResourceId;
        ArrayList<DataSnapshot> data = null;

        public MyAdapter(Context context, int resource, ArrayList<DataSnapshot> objects) {
            super(context, resource, objects);

            this.layoutResourceId = resource;
            this.context = context;
            this.data = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            MyHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();

                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new MyHolder();
                holder.imgIcon = (ImageView) row.findViewById(R.id.imagenViaje);
                holder.txtDesde = (TextView) row.findViewById(R.id.desde);
                holder.txtHasta = (TextView) row.findViewById(R.id.hasta);
                holder.txtCantPas = (TextView) row.findViewById(R.id.cantPas);
                holder.txtCreador = (TextView) row.findViewById(R.id.creador);
                row.setTag(holder);
            } else {
                holder = (MyHolder) row.getTag();
            }

            Ubicacion U = new Ubicacion(getActivity());

            holder.txtHasta.setText("Hasta: " + U.getAddress(Viajes.get(position).child("LatDestino").getValue(Double.class),Viajes.get(position).child("LongDestino").getValue(Double.class)));
            holder.txtDesde.setText("Desde: " + U.getAddress(Viajes.get(position).child("LatOrigen").getValue(Double.class),Viajes.get(position).child("LongOrigen").getValue(Double.class)));
            holder.txtCreador.setText("Creador: " + Viajes.get(position).child("UsuarioCreador").getValue(String.class));
            holder.txtCantPas.setText("Cantidad Pasajeros: " + Viajes.get(position).child("CantPasajeros").getValue(Integer.class));
            holder.imgIcon.setImageResource(R.drawable.viaje);

            return row;
        }
    }

    static class MyHolder {
        ImageView imgIcon;
        TextView txtDesde;
        TextView txtHasta;
        TextView txtCantPas;
        TextView txtCreador;
    }

}

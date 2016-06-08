package com.example.pato.tacheando;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Pato on 05/06/2016.
 */
public class FragmentBusqueda extends Fragment {

    private EditText direccion;


    private OnFragmentInteractionListener mListener;

    public static FragmentBusqueda newInstance() {
        FragmentBusqueda fragment = new FragmentBusqueda();
        /*Bundle args = new Bundle();
        args.putString(LatOrigen, LatO);
        args.putString(LongOrigen, LongO);
        args.putString(LongDestino, LongD);
        args.putString(LatDestino, LatD);
        fragment.setArguments(args);*/
        return fragment;
    }

    public FragmentBusqueda(){
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_busqueda, container, false);

        direccion = (EditText) v.findViewById(R.id.direccion);

        v.findViewById(R.id.BtnBuscar).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Buscar();
            }
        });

        return v;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void Buscar()
    {
        if (mListener != null) {
            mListener.BuscarAction(direccion.getText().toString());
        }
    }

    public interface OnFragmentInteractionListener {
        void BuscarAction(String Direccion);
    }

}

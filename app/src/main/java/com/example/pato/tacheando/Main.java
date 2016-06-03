package com.example.pato.tacheando;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Main extends AppCompatActivity {

    private DatabaseReference DB = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference Viaje;
    private TextView T1, T2;
    private Button B;
    private EditText ED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Viaje = DB.child("Viaje");
        T1 = (TextView) findViewById(R.id.Nombre1);
        T2 = (TextView) findViewById(R.id.Nombre2);
        B = (Button) findViewById(R.id.guardar);
        ED = (EditText) findViewById(R.id.editText);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query Q = Viaje.orderByChild("Viaje").equalTo("ViajeBahia").getRef().orderByChild("FechaViaje").equalTo("03062016");

        Q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                T1.setText("Cantidad: " + dataSnapshot.getChildrenCount());
                Iterable<DataSnapshot> I = dataSnapshot.getChildren();
                int i=0;
                for (DataSnapshot D : I) {
                    if (i == 0) {
                        T1.setText("Creador: " + D.child("Viaje").getValue(String.class));
                    } else {
                        T2.setText("Creador: " + D.child("Viaje").getValue(String.class));
                    }
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = Viaje.push().getKey();
                Viaje.child("1").child("Viaje").setValue("ViajeBahiaaaa");
                Viaje.child("1").child("FechaViaje").setValue("03/06/2016");
            }
        });


    }
}

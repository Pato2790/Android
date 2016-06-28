package com.example.pato.tacheando;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.pato.tacheando.R.layout.activity_main;


public class DrawerNav extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseUser user;
    private ImageView imUser;
    private TextView emailUser;
    private String uid;
    private String userName;
    private String userEmail;
    private Uri userPhotoUrl;
    private  LayoutInflater inflater;
    private View vista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater = LayoutInflater.from(this);
        //vista =  (View) LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        vista=  inflater.inflate(R.layout.nav_header_main,null);
        imUser= (ImageView) vista.findViewById(R.id.imageHeader);
        emailUser =(TextView)vista.findViewById(R.id.user_email);

        NavigationView navigationView = (NavigationView) findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userName = user.getDisplayName();
            userEmail = user.getEmail();
            userPhotoUrl = user.getPhotoUrl();
            uid = user.getUid();
            imUser.setImageURI(userPhotoUrl);
            // emailUser.setText("email");
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d("ssss","sssssss");
        int id = item.getItemId();

        if (id == R.id.nav_ViajeCreado) {
            ViajeCreados();
        }
        else if (id == R.id.nav_ViajeUnido) {
            ViajeUnido();
        } else if (id == R.id.nav_Logout) {
            Log.d("ssss","sssssssss");
            Logout();
        }else if (id == R.id.nav_BuscarViaje) {
            buscarViaje();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void ViajeCreados(){

    }

    private  void ViajeUnido(){

    }

    private void Logout(){
        FirebaseAuth.getInstance().signOut();
        Intent SV = new Intent(this, LoginActivity.class);
        startActivity(SV);
    }

    private void buscarViaje(){

    }
}

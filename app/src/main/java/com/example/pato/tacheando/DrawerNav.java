package com.example.pato.tacheando;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


public class DrawerNav extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private  LayoutInflater inflater;
    private View vista;
    private NavigationView navDrawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int selectedItem, actividad;
    private FirebaseUser user;
    private ImageView imUser;
    private TextView emailUser;
    private String uid;
    private String userName;
    private String userEmail;
    private Uri userPhotoUrl;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(this instanceof Main) {
            setContentView(R.layout.activity_main);
            actividad=0;
            Log.d("sss","main");
        }else if(this instanceof ShowViaje) {
            Log.d("sss", "show");
            actividad=1;
            setContentView(R.layout.actividad_show_viaje);
        }
        else {
            Log.d("sss", "show");
            actividad=2;
            setContentView(R.layout.activity_mis_viajes);
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navDrawer = (NavigationView) findViewById(R.id.left_drawer);
        navDrawer.setNavigationItemSelectedListener(this);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        drawerToggle.syncState();

        selectedItem = savedInstanceState == null ? R.id.nav_BuscarViaje : savedInstanceState.getInt("selectedItem");

        View header = navDrawer.getHeaderView(0);
        emailUser =(TextView)header.findViewById(R.id.user_email);
        imUser= (ImageView) header.findViewById(R.id.imageHeader);

         user = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserInfo profile =  user.getProviderData().get(1);
            userName = user.getDisplayName();
            userEmail = user.getEmail();
            Uri photoUrl = profile.getPhotoUrl();
            Log.d("ssss", "" +photoUrl);
            emailUser.setText(userEmail);
            imUser.setImageURI(photoUrl);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        selectedItem = menuItem.getItemId();
        Log.d("sss","navigation");

        if (selectedItem == R.id.nav_ViajeCreado) {
            ViajeCreados();
        }
        else if (selectedItem == R.id.nav_ViajeUnido) {
            ViajeUnido();
        } else if (selectedItem == R.id.nav_Logout) {
            Log.d("ssss","logout");
            Logout();
        }else if (selectedItem == R.id.nav_BuscarViaje) {
            buscarViaje();
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Override onBackPressed method so that when a user clicks on it, it closes
     * the NavigationDrawer if it's opened not the App.
     */
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //las 3 rayitas de attiba a la derecha? tiene sentido?
        //getMenuInflater().inflate(R.menu.listaitem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if ( drawerToggle.onOptionsItemSelected(item))
            return true;
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putInt("selectedItem", selectedItem);
    }



    private void ViajeCreados(){
        if (actividad==0) {
            Intent SV = new Intent(DrawerNav.this, MisViajesActivity.class);
            SV.putExtra("tipo", 1);
            startActivity(SV);
        }
        else {
            Intent SV = new Intent(DrawerNav.this, MisViajesActivity.class);
            SV.putExtra("tipo", 1);
            startActivity(SV);
        }
    }

    private  void ViajeUnido(){
        if (actividad==0) {
            Intent SV = new Intent(DrawerNav.this, MisViajesActivity.class);
            SV.putExtra("tipo", 0);
            startActivity(SV);
        }
        else {
            Intent SV = new Intent(DrawerNav.this, MisViajesActivity.class);
            SV.putExtra("tipo", 0);
            startActivity(SV);
        }
    }

    private void Logout(){
        FirebaseAuth.getInstance().signOut();
        Intent SV = new Intent(this, LoginActivity.class);
        startActivity(SV);
    }

    private void buscarViaje(){
        if (actividad==0) {
            Intent SV = new Intent(DrawerNav.this, Main.class);
        }
        else {
            Intent SV = new Intent(DrawerNav.this, Main.class);
            startActivity(SV);
        }
    }
}
package com.example.firebaseautentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApliClient;
    private TextView txtPNombreUsuario, txtPCorreo;
    private ImageView imgPFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
//        TextView nav_user = (TextView)hView.findViewById(R.id.nav_name);
//        nav_user.setText(user);

        txtPNombreUsuario = hView.findViewById(R.id.txtPNombreUsuario);
        txtPCorreo = hView.findViewById(R.id.txtPCorreo);
        imgPFoto = hView.findViewById(R.id.imgPFoto);

//        Recibiendo autenticacion google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApliClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            Auth.GoogleSignInApi.signOut(googleApliClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if (status.isSuccess()) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(MainActivity.this, "Error al cerrar sesión", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            //
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            SharedPreferences.Editor editor = getSharedPreferences("spDatosPersonales", MODE_PRIVATE).edit();
            editor.putString("nombreCompleto", "");
            editor.putString("correo", "");

            editor.apply();
            Toast.makeText(this, "Cerrando la sesión", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApliClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            obtenerValores(result);

            //FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            //tx.replace(R.id.contenedor, new PeticionFragment());
            //tx.commit();
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {

                }
            });
        }
    }

    private void obtenerValores(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount cuenta = result.getSignInAccount();

            SharedPreferences.Editor editor = getSharedPreferences("spDatosPersonales", MODE_PRIVATE).edit();
            editor.putString("nombreCompleto", cuenta.getDisplayName());
            editor.putString("correo", cuenta.getEmail());
            editor.putString("imagen", cuenta.getPhotoUrl()==null? getString(R.string.imagen_default) : cuenta.getPhotoUrl().toString());
            editor.apply();

            //Toast.makeText(this, "Nombre: " + cuenta.getDisplayName() + " | Correo:" + cuenta.getEmail(), Toast.LENGTH_SHORT).show();
            txtPNombreUsuario.setText(cuenta.getDisplayName());
            txtPCorreo.setText(cuenta.getEmail());
            Glide.with(getApplicationContext())
                    .load(cuenta.getPhotoUrl())
                    .override(300, 240)
                    .fitCenter()
                    .centerCrop()
                    .into(imgPFoto);
            //imgPFoto.setImageURI(cuenta.getPhotoUrl());
            //Toast.makeText(this, "Nombre: " +  cuenta.getDisplayName() + " | Correo:" + cuenta.getEmail(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

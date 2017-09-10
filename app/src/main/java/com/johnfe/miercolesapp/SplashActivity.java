package com.johnfe.miercolesapp;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.felipecsl.gifimageview.library.GifImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class SplashActivity extends AppCompatActivity {

    private GifImageView gif;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    // si hay un usuario logueado lo envio al menu principal sin mostrar el splash
                    Log.d("[MainActivity]", "Actualmente se encuentra logueado: " + user.getUid());
                    Intent intent= new Intent(SplashActivity.this, MenuPrincipal.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    finish();

                } else {
                    // sino hay usuario logueado muestra el splash y luego lo envia al main
                    Log.d("", "No hay usuario logueado");
                }
                // ...
            }
        };

        gif= (GifImageView) findViewById(R.id.gif);

        //tomar el gif y pasarlos a array de bytes, luego asignarlo al GifImageView y empezar la animacion
        try {
            InputStream inputStream = getAssets().open("homero.gif");
            byte [] bytes= IOUtils.toByteArray(inputStream);
            gif.setBytes(bytes);
            gif.startAnimation();

        }catch (Exception e){


        }

        // creo un nuevo hilo, indicando  el tiempo en milisegundo qeu se tomara en mostrar el splash
        // y luego enviar a la siquiente activity que es el main
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                SplashActivity.this.finish();
            }
        },3000);

    }

    // verifico si hay un usuario logueado cuando se abre o cierra la app
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}

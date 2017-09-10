package com.johnfe.miercolesapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.johnfe.miercolesapp.model.Persona;

public class MainActivity extends AppCompatActivity {

    EditText txtUsuario;
    EditText txtClave;
    Button btnRegstrar;
    Button btnLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("[MainActivity]", "Actualmente se encuentra logueado: " + user.getUid());
                    System.out.println("Email verifiacado: "+mAuth.getCurrentUser().isEmailVerified());
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        System.out.println("Entra al  listener");
                        Intent intent= new Intent(MainActivity.this, MenuPrincipal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        finish();

                    }else{
                        System.out.println("no entra al listener");
                    }

                } else {
                    // User is signed out
                    Log.d("", "No hay usuario logueado");
                }
                // ...
            }
        };

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtClave = (EditText) findViewById(R.id.txtClave);
        btnLogin= (Button) findViewById(R.id.btnLogin);
        btnRegstrar= (Button) findViewById(R.id.btnRegistrar);



        btnRegstrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.createUserWithEmailAndPassword(txtUsuario.getText().toString().trim(), txtClave.getText().toString().trim())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                Log.d("[Creacion]", "Registro de usuario: " + task.isSuccessful() +" --- "+task.getException());

                                String mensaje="";
                                if (task.isSuccessful()) {

                                    mensaje="Usuario creado correctamente " + mAuth.getCurrentUser().getUid();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                                    DatabaseReference refUsuario = database.getReference("usuario");

                                    Persona persona = new Persona();

                                    persona.setEmail(mAuth.getCurrentUser().getEmail());
                                    persona.setFechaNacimiento("");
                                    persona.setNombre("");
                                    persona.setCelular("");
                                    persona.setApellidos("");
                                    persona.setCedula("");

                                    refUsuario.child(mAuth.getCurrentUser().getUid()).setValue(persona);

                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()){
                                                Toast.makeText(MainActivity.this,"Email de veirificación enviado :)", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(MainActivity.this,"Email de veirificación No enviado :(", Toast.LENGTH_SHORT).show();
                                            }
                                            mAuth.signOut();
                                        }
                                    });


                                }else{
                                    mensaje="Usuario no creado correctamente ";

                                }


                                Toast.makeText(MainActivity.this, mensaje,
                                        Toast.LENGTH_SHORT).show();

                            }
                        });


            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signInWithEmailAndPassword(txtUsuario.getText().toString().trim(), txtClave.getText().toString().trim())
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                Log.d("[Login]", "Usuario logueado: " + task.isSuccessful() +" ---- "+task.getException());

                                String mensaje="";
                                if (task.isSuccessful()) {
                                    mensaje="Usuario Logueado correctamente "+ mAuth.getCurrentUser().getUid();

                                    if(mAuth.getCurrentUser().isEmailVerified()){

                                       Intent intent= new Intent(MainActivity.this, MenuPrincipal.class);
                                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                       startActivity(intent);
                                       finish();





                                   }else{
                                        Toast.makeText(MainActivity.this, "Email no verificado", Toast.LENGTH_SHORT).show();

                                   }



                                }else{
                                    mensaje="Usuario no Logueado correctamente ";

                                }


                                Toast.makeText(MainActivity.this, mensaje,
                                        Toast.LENGTH_SHORT).show();

                            }
                        });

            }
        });

    }

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

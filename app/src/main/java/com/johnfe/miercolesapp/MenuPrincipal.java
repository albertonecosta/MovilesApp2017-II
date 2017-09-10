package com.johnfe.miercolesapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.johnfe.miercolesapp.model.Persona;

public class MenuPrincipal extends AppCompatActivity {

    FirebaseDatabase database;
    Button btnRegistrar;
    DatabaseReference refPersona;
    DatabaseReference refMensaje;
    TextView txtSaludo;

    Persona persona ;
    Button btnBuscar;
    Button btnCerrarSesion;
    EditText txtNombre;
    EditText txtApellido;
    EditText txtCedula;
    EditText txtCelular;
    EditText txtFechaNacimiento;
    EditText txtEmail;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
          //  mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("[MenuPrincipal]", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Intent intent= new Intent(MenuPrincipal.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                // ...
            }
        };

        persona = new Persona();
        btnRegistrar= (Button) findViewById(R.id.btnRegistrar);
        btnBuscar= (Button) findViewById(R.id.btnBuscar);
        btnCerrarSesion = (Button) findViewById(R.id.btnCerrarSesion);

        txtSaludo= (TextView) findViewById(R.id.txtSaludo);

        txtEmail= (EditText) findViewById(R.id.txtEmail);

        txtApellido= (EditText) findViewById(R.id.txtApellidos);
        txtNombre= (EditText) findViewById(R.id.txtNombre);
        txtCedula= (EditText) findViewById(R.id.txtIdentificacion);
        txtCelular= (EditText) findViewById(R.id.txtCelular);
        txtFechaNacimiento= (EditText) findViewById(R.id.txtFechaNacimiento);

        database= FirebaseDatabase.getInstance();
        refPersona = database.getReference("usuario");
        refMensaje = database.getReference("mensaje");




        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {


                persona.setApellidos(txtApellido.getText().toString().trim());
                persona.setCedula(txtCedula.getText().toString().trim());
                persona.setCelular(txtCelular.getText().toString().trim());
                persona.setFechaNacimiento(txtFechaNacimiento.getText().toString().trim());
                persona.setNombre(txtNombre.getText().toString().trim());
                persona.setEmail(txtEmail.getText().toString().trim());

                refPersona.child(mAuth.getCurrentUser().getUid()).setValue(persona);



            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refPersona.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for (DataSnapshot hijos: dataSnapshot.getChildren()){

                            System.out.println(hijos.getKey());
                            System.out.println(hijos.getValue());


                        }
                        persona= dataSnapshot.getValue(Persona.class);

                        txtEmail.setText(persona.getEmail());
                        txtApellido.setText(persona.getApellidos());
                        txtNombre.setText(persona.getNombre());
                        txtCelular.setText(persona.getCelular());
                        txtFechaNacimiento.setText(persona.getFechaNacimiento());
                        txtCedula.setText(persona.getCedula());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("[ERROR BASE DE DATOS]: "+databaseError.toString());

                    }
                });
            }
        });

        refMensaje.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated
                 String value = dataSnapshot.getValue(String.class);
                txtSaludo.setText(value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });
    }
}

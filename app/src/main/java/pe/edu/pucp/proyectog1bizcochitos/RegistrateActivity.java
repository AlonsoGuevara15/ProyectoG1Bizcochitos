package pe.edu.pucp.proyectog1bizcochitos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import pe.edu.pucp.proyectog1bizcochitos.cliente.DevicesClienteActivity;

import pe.edu.pucp.proyectog1bizcochitos.clases.Usuario;
import pe.edu.pucp.proyectog1bizcochitos.usuarioTI.DevicesTiActivity;

public class RegistrateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrate);

        //getSupportActionBar().hide();

        Intent intent = getIntent();
        String name = intent.getStringExtra("nameUser");


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        TextView textViewRegistroTitulo = findViewById(R.id.textViewRegistroTitulo);

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String displayName = currentUser.getDisplayName();
            String email = currentUser.getEmail();

            textViewRegistroTitulo.setText("Bienvenido, " + displayName);
        }

    }

    public void registro(View view) {
        EditText editTextRegistroCodigo = findViewById(R.id.editTextRegistroCodigo);
        String codigo = editTextRegistroCodigo.getText().toString();

        boolean valid = Pattern.matches("\\A\\w{8,8}\\z", codigo);
        if (codigo.isEmpty()) {
            editTextRegistroCodigo.setError("No puede estar vacío");
        } else if (codigo.length() <= 7 | codigo.length() >= 9) {
            editTextRegistroCodigo.setError("Solo puede tener 8 caracteres");
        } else if (!valid) {
            editTextRegistroCodigo.setError("Solo se permite letras y/o números");
        }


        if (!codigo.isEmpty() && codigo.length() == 8 && codigo.matches("\\A\\w{8,8}\\z")) {
            saveUser();
        }

    }

    public void saveUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        EditText editTextRegistroCodigo = findViewById(R.id.editTextRegistroCodigo);

        Usuario usuario = new Usuario();
        String nombre = currentUser.getDisplayName();
        String correo = currentUser.getEmail();
        String codigo = editTextRegistroCodigo.getText().toString();

        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setCodigo(codigo);
        usuario.setRol("Cliente");

        databaseReference.child("users").child(currentUser.getUid()).setValue(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegistrateActivity.this, "Código guardado exitosamente", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Log.d("logApp", "Error al guardar");
                    }
                });

        databaseReference.child("users").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    Log.d("infoApp", usuario.getCodigo());

                    if (usuario.getRol().equals("Cliente")) {
                        startActivity(new Intent(RegistrateActivity.this, DevicesClienteActivity.class));
                        finish();
                    } else if (usuario.getRol().equals("Admin")) {
                        startActivity(new Intent(RegistrateActivity.this, DevicesTiActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void logout(View view) {
        AuthUI instance = AuthUI.getInstance();
        instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(RegistrateActivity.this, MainActivity.class));
                finish();
            }
        });
    }

}
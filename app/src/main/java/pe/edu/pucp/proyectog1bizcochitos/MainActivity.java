package pe.edu.pucp.proyectog1bizcochitos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        validacionUsuario();

    }

    public void login(View view) {
        List<AuthUI.IdpConfig> proveedores = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        AuthMethodPickerLayout authMethodPickerLayout =
                new AuthMethodPickerLayout.Builder(R.layout.login_layout)
                        .setEmailButtonId(R.id.buttonCorreo)
                        .setGoogleButtonId(R.id.buttonGoogle)
                        .build();

        AuthUI instance = AuthUI.getInstance();
        Intent intent = instance
                .createSignInIntentBuilder()
                .setTheme(R.style.miTema)
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setAvailableProviders(proveedores)
                .build();
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//
        if (requestCode == 1 && resultCode == RESULT_OK) {
            validacionUsuario();
        }
    }

    public void validacionUsuario() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            if (currentUser.isEmailVerified()) {
                openUserAct(currentUser);
            } else {
                currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (currentUser.isEmailVerified()) {
                            openUserAct(currentUser);
                        } else {
                            Toast.makeText(MainActivity.this, "Se le envió un correo para verificar su cuenta", Toast.LENGTH_SHORT).show();
                            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("debugeo", "Correo Enviado");
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    public void openUserAct(FirebaseUser currentUser) {
        Intent intent = new Intent(MainActivity.this, RegistrateActivity.class);
        intent.putExtra("nameUser", currentUser.getDisplayName());
        startActivity(intent);
        finish();

    }
}
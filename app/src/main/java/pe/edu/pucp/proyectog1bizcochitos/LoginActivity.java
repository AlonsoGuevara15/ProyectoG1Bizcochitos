package pe.edu.pucp.proyectog1bizcochitos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        Button buttonLoginRegistrate = findViewById(R.id.buttonLoginRegistrate);
        TextView textViewLoginTitulo = findViewById(R.id.textViewLoginTitulo);
        TextInputLayout correo = findViewById(R.id.correo);
        TextInputLayout password = findViewById(R.id.password);
        Button buttonLoginIniciaSesion = findViewById(R.id.buttonLoginIniciaSesion);

        buttonLoginRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegistrateActivity.class);

                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View,String>(textViewLoginTitulo,"titulo");
                pairs[1] = new Pair<View,String>(correo,"correo");
                pairs[2] = new Pair<View,String>(password,"password");
                pairs[3] = new Pair<View,String>(buttonLoginIniciaSesion,"button_tran");
                pairs[4] = new Pair<View,String>(buttonLoginRegistrate,"login_signup");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });

    }
}
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

public class RegistrateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrate);

        getSupportActionBar().hide();

        Button buttonRegistroLogin = findViewById(R.id.buttonRegistroLogin);
        TextView textViewRegistroTitulo = findViewById(R.id.textViewRegistroTitulo);
        TextInputLayout correo = findViewById(R.id.correo);
        TextInputLayout password = findViewById(R.id.password);
        Button buttonRegistroSignup = findViewById(R.id.buttonRegistroSignup);

        buttonRegistroLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrateActivity.this,LoginActivity.class);

                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View,String>(textViewRegistroTitulo,"titulo");
                pairs[1] = new Pair<View,String>(correo,"correo");
                pairs[2] = new Pair<View,String>(password,"password");
                pairs[3] = new Pair<View,String>(buttonRegistroSignup,"button_tran");
                pairs[4] = new Pair<View,String>(buttonRegistroLogin,"login_signup");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegistrateActivity.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });

    }
}
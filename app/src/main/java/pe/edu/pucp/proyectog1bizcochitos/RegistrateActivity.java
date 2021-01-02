package pe.edu.pucp.proyectog1bizcochitos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegistrateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrate);

        getSupportActionBar().hide();

        Button buttonRegistroLogin = findViewById(R.id.buttonRegistroLogin);
        buttonRegistroLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrateActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}
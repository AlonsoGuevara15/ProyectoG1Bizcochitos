package pe.edu.pucp.proyectog1bizcochitos.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pe.edu.pucp.proyectog1bizcochitos.MainActivity;
import pe.edu.pucp.proyectog1bizcochitos.R;

public class HistoryCliente extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_cliente);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.drawer_layout3);
        navigationView = findViewById(R.id.nav_view3);
        toolbar = findViewById(R.id.toolbar3);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_hist);

        View headerView = navigationView.getHeaderView(0);
        TextView textViewMenuNombre = headerView.findViewById(R.id.textViewMenuNombre);
        TextView textViewMenuCorreo = headerView.findViewById(R.id.textViewMenuCorreo);
        textViewMenuNombre.setText(currentUser.getDisplayName());
        textViewMenuCorreo.setText(currentUser.getEmail());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_disp:
                startActivity(new Intent(HistoryCliente.this, DevicesClienteActivity.class));
                break;

            case R.id.nav_soli:
                startActivity(new Intent(HistoryCliente.this, SolicitudesCliente.class));
                break;

            case R.id.nav_logout:
                AuthUI instance = AuthUI.getInstance();
                instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(HistoryCliente.this, MainActivity.class));
                        finish();
                    }
                });
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
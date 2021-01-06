package pe.edu.pucp.proyectog1bizcochitos.usuarioTI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import pe.edu.pucp.proyectog1bizcochitos.DevicesAdapter;
import pe.edu.pucp.proyectog1bizcochitos.MainActivity;
import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Solicitud;
import pe.edu.pucp.proyectog1bizcochitos.clases.Usuario;

public class PedidosTiActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    DatabaseReference databaseReference;
    DatabaseReference refreq;
    private ArrayList<Solicitud> listarequests = new ArrayList<>();
    private RecyclerView mRecyclerView;
    ChildEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_ti);
        setACtionBarDrawer();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mRecyclerView = findViewById(R.id.recyclerReservasTI);
        refreq= databaseReference.child("requests");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    if (usuario.getRol().equals("Admin")) {

                        openListRecycler();

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    startActivity(new Intent(PedidosTiActivity.this, MainActivity.class));
                    finish();
                }
            });

        } else {
            startActivity(new Intent(PedidosTiActivity.this, MainActivity.class));
            finish();
        }



    }
    public void openListRecycler() {
        ReservasAdapter crAdapter = new ReservasAdapter(listarequests, PedidosTiActivity.this);
        mRecyclerView.setAdapter(crAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PedidosTiActivity.this));

        if (listener != null){
            refreq.removeEventListener(listener);
        }


        listener= databaseReference.child("requests").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Solicitud soli = snapshot.getValue(Solicitud.class);
                listarequests.add(soli);
                mRecyclerView.getAdapter().notifyItemInserted(listarequests.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listarequests = new ArrayList<>();
                mRecyclerView.getAdapter().notifyItemInserted(listarequests.size());
                openListRecycler();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                listarequests = new ArrayList<>();
                mRecyclerView.getAdapter().notifyItemInserted(listarequests.size());
                openListRecycler();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void openRechazarFragment(Solicitud soli) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("devices").child(soli.getDeviceid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Device device = dataSnapshot.getValue(Device.class);
                FragmentManager fm = getSupportFragmentManager();
                Fragment cFragmentOld = fm.findFragmentById(R.id.fragmentPedidosTI);
                if (cFragmentOld != null) {
                    fm.popBackStack();
                    fm.beginTransaction().remove(cFragmentOld).commit();
                }
                View view = findViewById(R.id.fragmentPedidosTI);
                view.setVisibility(View.VISIBLE);
                RechazarFragment cFragment = RechazarFragment.newInstance(soli,device);

                fm.beginTransaction()
                        .add(R.id.fragmentPedidosTI, cFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

    }




    public void setACtionBarDrawer() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.drawer_layout5);
        navigationView = findViewById(R.id.nav_view5);
        toolbar = findViewById(R.id.toolbar5);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_reserv);

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
            case R.id.nav_gest:
                startActivity(new Intent(PedidosTiActivity.this, DevicesTiActivity.class));
                break;

            case R.id.nav_logout:
                AuthUI instance = AuthUI.getInstance();
                instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(PedidosTiActivity.this, MainActivity.class));
                        finish();
                    }
                });
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            refreq.removeEventListener(listener);
        }
    }
}
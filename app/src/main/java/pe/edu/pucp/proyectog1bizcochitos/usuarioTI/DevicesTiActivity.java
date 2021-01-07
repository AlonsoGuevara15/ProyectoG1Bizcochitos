package pe.edu.pucp.proyectog1bizcochitos.usuarioTI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pe.edu.pucp.proyectog1bizcochitos.DevicesAdapter;
import pe.edu.pucp.proyectog1bizcochitos.MainActivity;
import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Usuario;

public class DevicesTiActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;


    private static final String TAG = "debugeo";
    private ArrayList<Device> listadevices = new ArrayList<>();
    private RecyclerView mRecyclerView;
    ChildEventListener listener;
    DatabaseReference databaseReference;
    DatabaseReference refdev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_ti);
        setACtionBarDrawer();
        mRecyclerView = findViewById(R.id.recyclerDevicesTI);
        FloatingActionButton mAdd = findViewById(R.id.floatingAddDevice);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DevicesTiActivity.this, NewDeviceActivity.class));
                finish();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference();
        refdev = databaseReference.child("devices");
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
                    startActivity(new Intent(DevicesTiActivity.this, MainActivity.class));
                    finish();
                }
            });

        } else {
            startActivity(new Intent(DevicesTiActivity.this, MainActivity.class));
            finish();
        }
    }

    public void openListRecycler() {
        DevicesAdapter crAdapter = new DevicesAdapter(listadevices, DevicesTiActivity.this, "Admin");
        mRecyclerView.setAdapter(crAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DevicesTiActivity.this));

        if (listener != null) {
            refdev.removeEventListener(listener);
        }


        listener = databaseReference.child("devices").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Device device = snapshot.getValue(Device.class);
                listadevices.add(device);
                mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listadevices = new ArrayList<>();
                mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());
                openListRecycler();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                listadevices = new ArrayList<>();
                mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());
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

    public void openEditFragment(String id) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("devices").child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Device device = dataSnapshot.getValue(Device.class);
                FragmentManager fm = getSupportFragmentManager();
                Fragment cFragmentOld = fm.findFragmentById(R.id.fragmentDeviceIT);
                if (cFragmentOld != null) {
                    fm.popBackStack();
                    fm.beginTransaction().remove(cFragmentOld).commit();
                }
                View view = findViewById(R.id.fragmentDeviceIT);
                view.setVisibility(View.VISIBLE);
                EditDeviceFragment cFragment = EditDeviceFragment.newInstance(device);

                fm.beginTransaction()
                        .add(R.id.fragmentDeviceIT, cFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

    }

    public void updateCampos(Device device) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refgenerado = databaseRef.child("devices").child(device.getDeviceId());
        Map<String, Object> updates = new HashMap<>();
        updates.put("marca", device.getMarca());
        updates.put("caracteristicas", device.getCaracteristicas());
        updates.put("incluye", device.getIncluye());
        updates.put("stock", device.getStock());

        refgenerado.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DevicesTiActivity.this, "Campos actualizados exitosamente", Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DevicesTiActivity.this, "No se pudo actualizar el dispositivo", Toast.LENGTH_LONG).show();
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        Fragment cFragmentOld = fm.findFragmentById(R.id.fragmentDeviceIT);
        if (cFragmentOld != null) {
            fm.popBackStack();
            fm.beginTransaction().remove(cFragmentOld).commit();
        }
        View view = findViewById(R.id.fragmentDeviceIT);
        view.setVisibility(View.GONE);

    }




    public void setACtionBarDrawer() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.drawer_layout4);
        navigationView = findViewById(R.id.nav_view4);
        toolbar = findViewById(R.id.toolbar4);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_gest);

        View headerView = navigationView.getHeaderView(0);
        TextView textViewMenuNombre = headerView.findViewById(R.id.textViewMenuNombre);
        TextView textViewMenuCorreo = headerView.findViewById(R.id.textViewMenuCorreo);
        TextView textViewMenuRol = headerView.findViewById(R.id.textViewMenuRol);
        textViewMenuNombre.setText(currentUser.getDisplayName());
        textViewMenuCorreo.setText(currentUser.getEmail());
        textViewMenuRol.setText("Usuario TI");

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
            case R.id.nav_reserv:
                startActivity(new Intent(DevicesTiActivity.this, PedidosTiActivity.class));
                finish();
                break;

            case R.id.nav_logout:
                Log.d(TAG,"CLick Cerrar SESION");
                AuthUI instance = AuthUI.getInstance();
                instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Cerro SESION");
                        startActivity(new Intent(DevicesTiActivity.this, MainActivity.class));
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
            refdev.removeEventListener(listener);
        }
    }
}
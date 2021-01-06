package pe.edu.pucp.proyectog1bizcochitos.cliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

import pe.edu.pucp.proyectog1bizcochitos.DevicesAdapter;
import pe.edu.pucp.proyectog1bizcochitos.MainActivity;
import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Solicitud;
import pe.edu.pucp.proyectog1bizcochitos.clases.Usuario;

public class DevicesClienteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private FusedLocationProviderClient fusedLocationProviderClient;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private static final String TAG = "debugeo";
    private ArrayList<Device> listadevices = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    ChildEventListener listener;
    DatabaseReference databaseReference;
    DatabaseReference refdev;
    TextView tipo;
    TextView marca;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_cliente);
        setACtionBarDrawer();
         tipo = findViewById(R.id.textTipo);
         marca = findViewById(R.id.textMarca);
        ImageButton clearsearch = findViewById(R.id.btnClearsearch);
        mRecyclerView = findViewById(R.id.recyclerDevicesClient);
        progressBar = findViewById(R.id.progressBarDeviceClient);
        progressBar.setVisibility(View.GONE);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        refdev = databaseReference.child("devices");


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        permissions();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("users").child(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    if (usuario.getRol().equals("Cliente")) {


                        openListRecycler("", "");
                        tipo.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                progressBar.setVisibility(View.VISIBLE);
                                Log.d(TAG, "escribiendo");
                                listadevices = new ArrayList<>();
                                mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());

                                String marcastr = marca.getText().toString();

                                openListRecycler(charSequence.toString(), marcastr);

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        marca.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                progressBar.setVisibility(View.VISIBLE);
                                listadevices = new ArrayList<>();
                                mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());
                                String tipostr = tipo.getText().toString();
                                openListRecycler(tipostr, charSequence.toString());

                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                        clearsearch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                listadevices = new ArrayList<>();
                                mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());
                                tipo.setText("");
                                marca.setText("");
                                openListRecycler("", "");
                            }
                        });


                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    startActivity(new Intent(DevicesClienteActivity.this, MainActivity.class));
                    finish();
                }
            });

        } else {
            startActivity(new Intent(DevicesClienteActivity.this, MainActivity.class));
            finish();
        }

    }

    public void openListRecycler(String t, String m) {
        progressBar.setVisibility(View.VISIBLE);

        DevicesAdapter crAdapter = new DevicesAdapter(listadevices, DevicesClienteActivity.this, "Cliente");
        mRecyclerView.setAdapter(crAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DevicesClienteActivity.this));

        if (listener != null) {
            refdev.removeEventListener(listener);
        }
        listener = databaseReference.child("devices").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Device device = snapshot.getValue(Device.class);
                if (device.getTipo().toLowerCase().contains(t.toLowerCase()) && device.getMarca().toLowerCase().contains(m.toLowerCase())) {
                    progressBar.setVisibility(View.VISIBLE);
                    listadevices.add(device);
                    mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listadevices = new ArrayList<>();
                mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());
                openListRecycler(tipo.getText().toString(), marca.getText().toString());

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                listadevices = new ArrayList<>();
                mRecyclerView.getAdapter().notifyItemInserted(listadevices.size());
                openListRecycler(tipo.getText().toString(), marca.getText().toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void openDetailsFragment(String id) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("devices").child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Device device = dataSnapshot.getValue(Device.class);
                FragmentManager fm = getSupportFragmentManager();
                Fragment cFragmentOld = fm.findFragmentById(R.id.fragmentDeviceClient);
                if (cFragmentOld != null) {
                    fm.popBackStack();
                    fm.beginTransaction().remove(cFragmentOld).commit();
                }
                View view = findViewById(R.id.fragmentDeviceClient);
                view.setVisibility(View.VISIBLE);
                DeviceClientFragment cFragment = DeviceClientFragment.newInstance(device);

                fm.beginTransaction()
                        .add(R.id.fragmentDeviceClient, cFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

    }

    public void openReservaFragment(String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("devices").child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Device device = dataSnapshot.getValue(Device.class);
                FragmentManager fm = getSupportFragmentManager();
                Fragment cFragmentOld = fm.findFragmentById(R.id.fragmentDeviceClient);
                if (cFragmentOld != null) {
                    fm.popBackStack();
                    fm.beginTransaction().remove(cFragmentOld).commit();
                }
                View view = findViewById(R.id.fragmentDeviceClient);
                view.setVisibility(View.VISIBLE);
                ReservaFragment cFragment = ReservaFragment.newInstance(device);
                fm.beginTransaction()
                        .add(R.id.fragmentDeviceClient, cFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });
    }

    public void saveSolicitud(Solicitud soli) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference refgenerado = databaseReference.child("requests").push();
        soli.setSolicId(refgenerado.getKey());

        refgenerado.setValue(soli).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DevicesClienteActivity.this, "Solicitud de reserva enviada", Toast.LENGTH_LONG).show();

            }
        });
        FragmentManager fm = getSupportFragmentManager();
        Fragment cFragmentOld = fm.findFragmentById(R.id.fragmentDeviceClient);
        if (cFragmentOld != null) {
            fm.popBackStack();
            fm.beginTransaction().remove(cFragmentOld).commit();
        }
        View view = findViewById(R.id.fragmentDeviceClient);
        view.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setACtionBarDrawer() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_disp);

        View headerView = navigationView.getHeaderView(0);
        TextView textViewMenuNombre = headerView.findViewById(R.id.textViewMenuNombre);
        TextView textViewMenuCorreo = headerView.findViewById(R.id.textViewMenuCorreo);
        textViewMenuNombre.setText(currentUser.getDisplayName());
        textViewMenuCorreo.setText(currentUser.getEmail());

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_soli:
                startActivity(new Intent(DevicesClienteActivity.this, SolicitudesCliente.class));
                finish();
                break;

            case R.id.nav_hist:
                startActivity(new Intent(DevicesClienteActivity.this, HistoryCliente.class));
                finish();
                break;

            case R.id.nav_logout:
                AuthUI instance = AuthUI.getInstance();
                instance.signOut(this).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(DevicesClienteActivity.this, MainActivity.class));
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


    public void permissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(DevicesClienteActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("infoApp", "Latitud: " + location.getLatitude() + " | Longitud: " + location.getLongitude());

                        }
                    }
                });
    }

    public void goGoogleMaps (View view) {
        permissions();
        startActivity(new Intent(DevicesClienteActivity.this,MapsActivity.class));
        finish();
    }
}
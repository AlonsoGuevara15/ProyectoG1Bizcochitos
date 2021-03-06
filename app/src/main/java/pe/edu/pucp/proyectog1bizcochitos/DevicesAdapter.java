package pe.edu.pucp.proyectog1bizcochitos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Solicitud;
import pe.edu.pucp.proyectog1bizcochitos.cliente.DevicesClienteActivity;
import pe.edu.pucp.proyectog1bizcochitos.usuarioTI.DevicesTiActivity;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {
    private static ArrayList<Device> lista;
    private static final String TAG = "debugeo";
    private Context contexto;
    private String rol;


    public DevicesAdapter(ArrayList<Device> l, Context contexto, String rol) {
        lista = l;
        this.contexto = contexto;
        this.rol = rol;
    }

    public DevicesAdapter() {
    }

    @NonNull
    @Override
    public DevicesAdapter.DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        DeviceViewHolder mViewHolder = new DeviceViewHolder(itemView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesAdapter.DeviceViewHolder holder, int position) {
        holder.tipodevice.setText(lista.get(position).getTipo());
        holder.marcadevice.setText(lista.get(position).getMarca());
        String ruta = "Imagenes/" + lista.get(position).getDeviceId() + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(ruta);
        Log.d(TAG,ruta);
        Glide.with(contexto).load(imageRef).into(holder.imageDevice);

        switch (rol) {
            case "Cliente":
                holder.btnDelete.setVisibility(View.GONE);
                holder.btnEdit.setVisibility(View.GONE);

                holder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((DevicesClienteActivity)contexto).openDetailsFragment(lista.get(position).getDeviceId());
                    }
                });



                break;
            case "Admin":
                holder.btnDetails.setVisibility(View.GONE);
                holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("devices").child(lista.get(position).getDeviceId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(contexto, "Dispositivo borrado exitosamente", Toast.LENGTH_LONG).show();

                                databaseReference.child("requests").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1: snapshot.getChildren()) {
                                            Solicitud solicitud = snapshot1.getValue(Solicitud.class);
                                            if (solicitud.getDeviceid().equals(lista.get(position).getDeviceId())) {
                                                databaseReference.child("requests").child(solicitud.getSolicId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                });
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(contexto, "No se pudo borrar el dispositivo", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((DevicesTiActivity)contexto).openEditFragment(lista.get(position).getDeviceId());
                    }
                });
                break;
        }


    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        public TextView tipodevice;
        public TextView marcadevice;
        public ImageButton btnDelete;
        public ImageButton btnEdit;
        public ImageButton btnDetails;
        public ImageView imageDevice;


        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tipodevice = itemView.findViewById(R.id.tipodevice);
            this.marcadevice = itemView.findViewById(R.id.marcadevice);
            this.btnDelete = itemView.findViewById(R.id.btnDelete);
            this.btnEdit = itemView.findViewById(R.id.btnEdit);
            this.btnDetails = itemView.findViewById(R.id.btnDetails);
            this.imageDevice = itemView.findViewById(R.id.imageDevice);



        }
    }

}

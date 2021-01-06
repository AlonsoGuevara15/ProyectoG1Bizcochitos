package pe.edu.pucp.proyectog1bizcochitos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Solicitud;
import pe.edu.pucp.proyectog1bizcochitos.cliente.SolicitudesCliente;

public class SolicitudesClienteAdapter extends RecyclerView.Adapter<SolicitudesClienteAdapter.SolicitudViewHolder> {

    ArrayList<Solicitud> listaSolicitudes;

    public SolicitudesClienteAdapter(ArrayList<Solicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.solicitudes_rv,parent,false);
        SolicitudViewHolder solicitudViewHolder = new SolicitudViewHolder(view);
        return solicitudViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        SolicitudViewHolder solicitudViewHolder = (SolicitudViewHolder) holder;
        Solicitud solicitud = listaSolicitudes.get(position);

        solicitudViewHolder.dispositivoRV.setText(solicitud.getDeviceid());
        solicitudViewHolder.motivoRV.setText(solicitud.getMotivo());
        solicitudViewHolder.direccionRV.setText(solicitud.getDireccion());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("devices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Device device = snapshot1.getValue(Device.class);
                    if (device!=null) {
                        if (device.getDeviceId().equals(solicitudViewHolder.dispositivoRV.getText())) {
                            solicitudViewHolder.dispositivoRV.setText(device.getTipo()+ " - " + device.getMarca());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }


    public static class SolicitudViewHolder extends RecyclerView.ViewHolder {
        public TextView dispositivoRV, motivoRV, direccionRV;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);

            this.dispositivoRV = itemView.findViewById(R.id.textViewSoliDispositivoRV);
            this.motivoRV = itemView.findViewById(R.id.textViewSoliMotivoRV);
            this.direccionRV = itemView.findViewById(R.id.textViewSoliDireccRV);

        }
    }

}

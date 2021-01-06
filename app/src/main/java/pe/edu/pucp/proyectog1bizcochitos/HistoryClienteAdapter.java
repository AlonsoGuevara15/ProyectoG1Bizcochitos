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

public class HistoryClienteAdapter extends RecyclerView.Adapter<HistoryClienteAdapter.SolicitudViewHolder>{

    ArrayList<Solicitud> listaSolicitudes;

    public HistoryClienteAdapter(ArrayList<Solicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    @NonNull
    @Override
    public SolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.historial_rv,parent,false);
        SolicitudViewHolder solicitudViewHolder = new SolicitudViewHolder(view);
        return solicitudViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitudViewHolder holder, int position) {
        SolicitudViewHolder solicitudViewHolder = (SolicitudViewHolder) holder;
        Solicitud solicitud = listaSolicitudes.get(position);

        solicitudViewHolder.dispositivoRV.setText(solicitud.getDeviceid());
        solicitudViewHolder.estadoRV.setText(solicitud.getEstado());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("devices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1: snapshot.getChildren()) {
                    Device device = snapshot1.getValue(Device.class);
                    if (device!=null) {
                        if (device.getDeviceId().equals(solicitudViewHolder.dispositivoRV.getText())) {
                            solicitudViewHolder.dispositivoRV.setText(device.getTipo() + " - " + device.getMarca());
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
        public TextView dispositivoRV, estadoRV;

        public SolicitudViewHolder(@NonNull View itemView) {
            super(itemView);

            this.dispositivoRV = itemView.findViewById(R.id.textViewHistDispositivoRV);
            this.estadoRV = itemView.findViewById(R.id.textViewHistEstadoRV);

        }
    }
}

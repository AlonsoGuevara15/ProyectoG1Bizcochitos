package pe.edu.pucp.proyectog1bizcochitos.usuarioTI;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pe.edu.pucp.proyectog1bizcochitos.R;
import pe.edu.pucp.proyectog1bizcochitos.clases.Device;
import pe.edu.pucp.proyectog1bizcochitos.clases.Solicitud;
import pe.edu.pucp.proyectog1bizcochitos.clases.Usuario;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ReservasViewHolder> {
    private static ArrayList<Solicitud> lista;
    private static final String TAG = "debugeo";
    private Context contexto;


    public ReservasAdapter(ArrayList<Solicitud> l, Context contexto) {
        lista = l;
        this.contexto = contexto;
    }

    public ReservasAdapter() {
    }

    @NonNull
    @Override
    public ReservasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedidos_ti_item, parent, false);
        return new ReservasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservasViewHolder holder, int position) {
        Solicitud soli = lista.get(position);
        holder.textViewEstadopedidoTI.setText(soli.getEstado());
        holder.textViewSoliMotivoRVti.setText("Motivo: " + soli.getMotivo());
        holder.textViewSoliDireccRVti.setText(soli.getDireccion());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        if ("Pendiente".equals(soli.getEstado())) {
            holder.textViewEstadopedidoTI.setVisibility(View.GONE);
            holder.imageButtonAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    databaseReference.child("devices").child(soli.getDeviceid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Device device = dataSnapshot.getValue(Device.class);
                            if (device!=null) {
                                if (device.getStock()>0) {
                                    Map<String, Object> updatesdev = new HashMap<>();
                                    updatesdev.put("stock", device.getStock() - 1);

                                    databaseReference.child("devices").child(device.getDeviceId()).updateChildren(updatesdev).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Map<String, Object> updateSoli = new HashMap<>();
                                            updateSoli.put("estado", "Aprobado");

                                            databaseReference.child("requests").child(soli.getSolicId()).updateChildren(updateSoli).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(contexto, "Solicitud aprobada exitosamente", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    });
                                }else {
                                    Toast.makeText(contexto, "No se pudo aceptar la solicitud por falta de stock", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(contexto, "No se pudo aceptar la solicitud porque no se pudo obtener el dispositivo", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            });

            holder.imageButtonRechazar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PedidosTiActivity) contexto).openRechazarFragment(soli);
                }
            });

        } else {
            holder.textViewEstadopedidoTI.setText(soli.getEstado());
            holder.imageButtonAceptar.setVisibility(View.GONE);
            holder.imageButtonRechazar.setVisibility(View.GONE);
        }
        Log.d(TAG, soli.getDeviceid());

        databaseReference.child("devices").child(soli.getDeviceid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Device device = dataSnapshot.getValue(Device.class);
                if (device != null) {
                    holder.textViewSoliDispositivoRVti.setText(device.getTipo() + " - " + device.getMarca());
                    databaseReference.child("users").child(soli.getUserid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            Usuario usuario = dataSnapshot.getValue(Usuario.class);
                            holder.textViewRemitenteti.setText("De: " + usuario.getNombre());
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ReservasViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewSoliDispositivoRVti;
        public TextView textViewRemitenteti;
        public TextView textViewSoliMotivoRVti;
        public TextView textViewSoliDireccRVti;
        public TextView textViewEstadopedidoTI;
        public ImageButton imageButtonRechazar;
        public ImageButton imageButtonAceptar;


        public ReservasViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewSoliDispositivoRVti = itemView.findViewById(R.id.textViewSoliDispositivoRVti);
            this.textViewRemitenteti = itemView.findViewById(R.id.textViewRemitenteti);
            this.textViewSoliMotivoRVti = itemView.findViewById(R.id.textViewSoliMotivoRVti);
            this.textViewSoliDireccRVti = itemView.findViewById(R.id.textViewSoliDireccRVti);
            this.textViewEstadopedidoTI = itemView.findViewById(R.id.textViewEstadopedidoTI);
            this.imageButtonRechazar = itemView.findViewById(R.id.imageButtonRechazar);
            this.imageButtonAceptar = itemView.findViewById(R.id.imageButtonAceptar);


        }
    }
}

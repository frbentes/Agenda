package com.frbentes.agendaac.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.frbentes.agendaac.R;
import com.frbentes.agendaac.model.dto.AppointmentDTO;
import com.frbentes.agendaac.util.DateUtil;
import com.frbentes.agendaac.view.activity.AppointmentDetailActivity;

import java.util.List;

/**
 * Created by frbentes on 16/04/17.
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private List<AppointmentDTO> appointments;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle;
        private TextView tvDescription;
        private TextView tvDate;
        private Button btnDelete;
        private AppointmentDTO appointment;

        public ViewHolder(View itemView) {
            super(itemView);

            this.tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            this.tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            this.tvDate = (TextView) itemView.findViewById(R.id.tv_date);
            this.btnDelete = (Button) itemView.findViewById(R.id.btn_remove);
            this.btnDelete.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        public void bind(AppointmentDTO appointment) {
            this.appointment = appointment;
            this.tvTitle.setText(appointment.title);
            if (!TextUtils.isEmpty(appointment.description)) {
                this.tvDescription.setVisibility(View.VISIBLE);
                this.tvDescription.setText(appointment.description);
            } else {
                this.tvDescription.setVisibility(View.GONE);
            }
            this.tvDate.setText(DateUtil.isoToDateTimeFormat(appointment.eventDate));
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            Intent intent = new Intent(context, AppointmentDetailActivity.class);
            intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_KEY, appointment.key);
            context.startActivity(intent);
        }
    }

    public AppointmentAdapter(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(appointments.get(position));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public void updateList(List<AppointmentDTO> appointments) {
        if (appointments.size() != this.appointments.size() ||
                !this.appointments.containsAll(appointments)) {
            this.appointments = appointments;
            notifyDataSetChanged();
        }
    }

    public AppointmentDTO getItem(int position) {
        return appointments.get(position);
    }

    public void removeItem(int position) {
        this.appointments.remove(position);
        notifyItemRemoved(position);
    }

}

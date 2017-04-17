package com.frbentes.agendaac.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.frbentes.agendaac.R;
import com.frbentes.agendaac.model.Appointment;
import com.frbentes.agendaac.util.DateUtil;

/**
 * Created by frbentes on 07/04/17.
 */
public class AppointmentViewHolder extends RecyclerView.ViewHolder {

    public TextView tvTitle;
    public TextView tvDescription;
    public TextView tvDate;
    public Button btnDelete;
    public RelativeLayout rlContent;

    public AppointmentViewHolder(View itemView) {
        super(itemView);

        this.tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        this.tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
        this.tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        this.btnDelete = (Button) itemView.findViewById(R.id.btn_remove);
        this.rlContent = (RelativeLayout) itemView.findViewById(R.id.rl_content);
    }

    public void bindToAppointment(Appointment appointment, View.OnClickListener deleteClickListener) {
        this.tvTitle.setText(appointment.title);
        if (!TextUtils.isEmpty(appointment.description)) {
            this.tvDescription.setVisibility(View.VISIBLE);
            this.tvDescription.setText(appointment.description);
        } else {
            this.tvDescription.setVisibility(View.GONE);
        }
        this.tvDate.setText(DateUtil.isoToDateTimeFormat(appointment.eventDate));

        this.btnDelete.setOnClickListener(deleteClickListener);
    }

}

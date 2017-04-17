package com.frbentes.agendaac.view.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.frbentes.agendaac.R;
import com.frbentes.agendaac.model.Appointment;
import com.frbentes.agendaac.util.DateUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AppointmentDetailActivity extends BaseActivity {

    public static final String EXTRA_APPOINTMENT_KEY = "appointment_key";

    private DatabaseReference mAppointmentReference;
    private ValueEventListener mAppointmentListener;
    private Appointment mAppointment;
    private String mAppointmentKey;

    private TextView tvTitle;
    private EditText edtDescription;
    private EditText edtDate;
    private EditText edtTime;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        this.toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setupToolbar();

        // Get appointment key from intent
        this.mAppointmentKey = getIntent().getStringExtra(EXTRA_APPOINTMENT_KEY);
        if (mAppointmentKey == null) {
            throw new IllegalArgumentException(getString(R.string.no_appointment_key));
        }

        this.mAppointmentReference = FirebaseDatabase.getInstance().getReference()
                .child("appointments").child(mAppointmentKey);

        this.tvTitle = (TextView) findViewById(R.id.tv_title_edit);
        this.edtDescription = (EditText) findViewById(R.id.edt_description_edit);
        this.edtDate = (EditText) findViewById(R.id.edt_date_edit);
        this.edtTime = (EditText) findViewById(R.id.edt_time_edit);

        setListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Add value event listener to the appointment
        ValueEventListener appointmentListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Appointment object and use the values to update the UI
                Appointment appointment = dataSnapshot.getValue(Appointment.class);
                tvTitle.setText(appointment.title);
                edtDescription.setText(appointment.description);
                String strDate = DateUtil.stringToDateFormat(appointment.eventDate);
                String strTime = DateUtil.stringToTimeFormat(appointment.eventDate);
                edtDate.setText(strDate);
                edtTime.setText(strTime);
                mAppointment = new Appointment(appointment.uid, appointment.title,
                        appointment.description, appointment.eventId, appointment.eventDate);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AppointmentDetailActivity.this,
                        getString(R.string.msg_fail_load_appointment), Toast.LENGTH_SHORT).show();
            }
        };
        this.mAppointmentReference.addValueEventListener(appointmentListener);

        // Keep copy of appointment listener so we can remove it when app stops
        this.mAppointmentListener = appointmentListener;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppointmentListener != null) {
            this.mAppointmentReference.removeEventListener(mAppointmentListener);
        }
    }

    private void updateAppointment() {
        Toast.makeText(this, getString(R.string.msg_updating), Toast.LENGTH_SHORT).show();
        String strDateTime = edtDate.getText().toString() + " " + edtTime.getText().toString();

        this.mAppointment.description = edtDescription.getText().toString();
        this.mAppointment.eventDate = DateUtil.stringToISOFormat(strDateTime);
        this.mAppointmentReference.setValue(mAppointment);

        FirebaseDatabase.getInstance().getReference().child("user-appointments").child(getUid())
                .child(mAppointmentKey).setValue(mAppointment);
        FirebaseDatabase.getInstance().getReference().child("appointments")
                .child(mAppointmentKey).setValue(mAppointment);

        updateAppointmentAtCalendar(mAppointment.eventId, mAppointment.title, mAppointment.description,
                edtDate.getText().toString(), edtTime.getText().toString());
        finish();
    }

    private void updateAppointmentAtCalendar(long eventId, String title, String description,
                                             String date, String time) {
        String[] partsDate = date.split("/");
        String[] partsTime = time.split(":");
        int year = Integer.parseInt(partsDate[2]);
        int month = Integer.parseInt(partsDate[1]) - 1;
        int day = Integer.parseInt(partsDate[0]);
        int hour = Integer.parseInt(partsTime[0]);
        int minutes = Integer.parseInt(partsTime[1]);

        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, hour, minutes);

        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        Intent intent = new Intent(Intent.ACTION_EDIT)
                .setData(uri)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description);
        startActivity(intent);
    }

    private void setListeners() {
        this.edtDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    edtDate.requestFocus();
                    hideKeyboard();
                    showDateDialog();
                }
                return true;
            }
        });
        this.edtTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    edtTime.requestFocus();
                    hideKeyboard();
                    showTimeDialog();
                }
                return true;
            }
        });
    }

    private void showDateDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Launch date picker dialog
        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATE_FORMAT, Locale.getDefault());
                String chosenDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                try {
                    Date date = sdf.parse(chosenDate);
                    edtDate.setText(sdf.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, year, month, day);

        // Disable past date
        dateDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dateDialog.show();
    }

    private void showTimeDialog() {
        // Get current time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Launch time picker dialog
        TimePickerDialog timeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.TIME_FORMAT, Locale.getDefault());
                String chosenTime = hourOfDay + ":" + minute;
                try {
                    Date time = sdf.parse(chosenTime);
                    edtTime.setText(sdf.format(time));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, hour, minute, true);

        timeDialog.show();
    }

    private void setupToolbar() {
        this.setSupportActionBar(this.toolbar);
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.tbar_back);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appointment_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_update:
                updateAppointment();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

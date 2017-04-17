package com.frbentes.agendaac.view.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.frbentes.agendaac.R;
import com.frbentes.agendaac.model.Appointment;
import com.frbentes.agendaac.model.User;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class NewAppointmentActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private MenuItem saveButton;

    private EditText edtTitle;
    private EditText edtDescription;
    private EditText edtDate;
    private EditText edtTime;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appointment);

        this.toolbar = (Toolbar) findViewById(R.id.new_toolbar);
        setupToolbar();

        this.mDatabase = FirebaseDatabase.getInstance().getReference();

        this.edtTitle = (EditText) findViewById(R.id.edt_title);
        this.edtDescription = (EditText) findViewById(R.id.edt_description);
        this.edtDate = (EditText) findViewById(R.id.edt_date);
        this.edtTime = (EditText) findViewById(R.id.edt_time);

        setListeners();
    }

    private void saveAppointment() {
        final String title = edtTitle.getText().toString();
        final String description = edtDescription.getText().toString();
        final String date = edtDate.getText().toString();
        final String time = edtTime.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            this.edtTitle.setError(getString(R.string.field_required));
            return;
        }

        // Date is required
        if (TextUtils.isEmpty(date)) {
            this.edtDate.setError(getString(R.string.field_required));
            return;
        }

        // Time is required
        if (TextUtils.isEmpty(time)) {
            this.edtTime.setError(getString(R.string.field_required));
            return;
        }

        // Generates an event id
        Random r = new Random();
        final long eventId = r.nextLong();

        // Disable edition
        setEditingEnabled(false);
        Toast.makeText(this, getString(R.string.msg_saving), Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        this.mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            writeNewAppointment(userId, title, description, date, time, eventId);
                            addAppointmentToCalendar(eventId, title, description, date, time);
                        } else {
                            Toast.makeText(NewAppointmentActivity.this,
                                    getString(R.string.msg_not_fetch_user), Toast.LENGTH_SHORT).show();
                        }

                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setEditingEnabled(true);
                    }
                });
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

    private void setEditingEnabled(boolean enabled) {
        this.edtTitle.setEnabled(enabled);
        this.edtDescription.setEnabled(enabled);
        this.edtDate.setEnabled(enabled);
        this.edtTime.setEnabled(enabled);
        if (enabled) {
            this.saveButton.setEnabled(true);
            this.saveButton.setIcon(R.drawable.ic_check);
        } else {
            this.saveButton.setEnabled(false);
            this.saveButton.setIcon(R.drawable.ic_check_d);
        }
    }

    private void writeNewAppointment(String userId, String title, String description, String date,
                                     String time, long eventId) {
        // Create new appointment at /user-appointments/$userid/$appointmentid and at
        // /appointments/$appointmentid simultaneously
        String key = mDatabase.child("appointments").push().getKey();

        String strDateTime = date + " " + time;
        String rememberDate = DateUtil.stringToISOFormat(strDateTime);

        Appointment appointment = new Appointment(userId, title, description, eventId, rememberDate);
        Map<String, Object> appointmentsValues = appointment.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/appointments/" + key, appointmentsValues);
        childUpdates.put("/user-appointments/" + userId + "/" + key, appointmentsValues);

        this.mDatabase.updateChildren(childUpdates);
    }

    private void addAppointmentToCalendar(long eventId, String title, String description,
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

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.Events._ID, eventId);
        startActivity(intent);
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
        getMenuInflater().inflate(R.menu.appointment_new, menu);
        this.saveButton = menu.findItem(R.id.button_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_save:
                saveAppointment();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

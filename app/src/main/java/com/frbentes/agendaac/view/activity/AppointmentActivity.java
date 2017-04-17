package com.frbentes.agendaac.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.frbentes.agendaac.R;
import com.frbentes.agendaac.model.Appointment;
import com.frbentes.agendaac.view.viewholder.AppointmentViewHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AppointmentActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Appointment, AppointmentViewHolder> mAdapter;

    private TextView tvMessage;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        this.toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setupToolbar();

        // Floating Button launches New Appointment Activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_appointment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppointmentActivity.this, NewAppointmentActivity.class));
            }
        });

        this.mDatabase = FirebaseDatabase.getInstance().getReference();

        this.tvMessage = (TextView) findViewById(R.id.tv_no_appointments_msg);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_saved_appointments);
        this.progressBar = (ProgressBar) findViewById(R.id.pb_appts);

        // Setup Layout Manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Setup Firebase Recycler Adapter with the Query
        Query query = getUserAppointments(mDatabase);
        this.mAdapter = new FirebaseRecyclerAdapter<Appointment, AppointmentViewHolder>(
                Appointment.class, R.layout.item_appointment, AppointmentViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final AppointmentViewHolder viewHolder,
                                              final Appointment model, final int position) {
                final DatabaseReference appointmentRef = getRef(position);
                final String appointmentKey = appointmentRef.getKey();

                viewHolder.rlContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Launch Appointment Detail Activity
                        Intent intent = new Intent(AppointmentActivity.this, AppointmentDetailActivity.class);
                        intent.putExtra(AppointmentDetailActivity.EXTRA_APPOINTMENT_KEY, appointmentKey);
                        startActivity(intent);
                    }
                });

                // Bind Appointment to ViewHolder, setting OnClickListener for the delete button
                viewHolder.bindToAppointment(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        DatabaseReference appointmentRef = mDatabase.child("appointments").
                                child(appointmentKey);
                        DatabaseReference userAppointmentRef = mDatabase.child("user-appointments").
                                child(model.uid).child(appointmentKey);
                        onDeleteClicked(appointmentRef, userAppointmentRef);
                    }
                });
            }
        };
        this.mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkItems(itemCount);
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkItems(itemCount);
            }
        });
        recyclerView.setAdapter(mAdapter);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                if (dataSnapshot.hasChildren()) {
                    tvMessage.setVisibility(View.GONE);
                } else {
                    tvMessage.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private Query getUserAppointments(DatabaseReference databaseReference) {
        return databaseReference.child("user-appointments").child(getUid());
    }

    private void onDeleteClicked(DatabaseReference appointmentRef, DatabaseReference userAppointmentRef) {
        appointmentRef.removeValue();
        userAppointmentRef.removeValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appointment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else if (id == R.id.button_search) {
            startActivity(new Intent(this, SearchAppointmentActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        this.setSupportActionBar(this.toolbar);
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    private void checkItems(int count) {
        if (count > 0) {
            tvMessage.setVisibility(View.GONE);
        } else {
            tvMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

}

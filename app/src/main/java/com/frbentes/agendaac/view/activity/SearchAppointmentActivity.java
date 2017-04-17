package com.frbentes.agendaac.view.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frbentes.agendaac.R;
import com.frbentes.agendaac.model.Appointment;
import com.frbentes.agendaac.model.dto.AppointmentDTO;
import com.frbentes.agendaac.view.adapter.AppointmentAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchAppointmentActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private AppointmentAdapter mAdapter;

    private TextView tvMessage;
    private Toolbar toolbar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appointment);

        this.toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setupToolbar();

        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAdapter = new AppointmentAdapter(Collections.<AppointmentDTO>emptyList());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.lst_results);
        this.tvMessage = (TextView) findViewById(R.id.tv_no_results_msg);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    private Query getAppointmentsByTitle(String title) {
        return mDatabase.child("user-appointments").child(getUid())
                .orderByChild("title")
                .startAt(title).endAt(title + "\uf8ff");
    }

    private void setupToolbar() {
        this.setSupportActionBar(this.toolbar);
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appointment_search, menu);
        this.searchView = ((SearchView) menu.findItem(R.id.search_view).getActionView());
        setupSearchView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView() {
        this.searchView.setIconifiedByDefault(false);
        this.searchView.setIconified(false);
        this.searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        this.searchView.setSubmitButtonEnabled(false);
        this.searchView.setFocusable(true);
        this.searchView.requestFocus();
        this.searchView.setQueryHint(getString(R.string.search_view_hint));
        final SearchView.SearchAutoComplete theTextArea = (SearchView.SearchAutoComplete)
                searchView.findViewById(R.id.search_src_text);
        final ImageView icon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        theTextArea.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white_color));
        theTextArea.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.search_hint_text_color));
        icon.setVisibility(View.GONE);
        icon.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent_color));
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.search_src_text);
                editText.setText("");
                searchView.setQuery("", false);
                mAdapter.updateList(Collections.<AppointmentDTO>emptyList());
            }
        });
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query = getAppointmentsByTitle(newText);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<AppointmentDTO> appointments = new ArrayList<>();
                        if (dataSnapshot.hasChildren()) {
                            tvMessage.setVisibility(View.GONE);
                            for (DataSnapshot apptDataSnapshot : dataSnapshot.getChildren()) {
                                Appointment appointment = apptDataSnapshot.getValue(Appointment.class);
                                AppointmentDTO apptDTO = new AppointmentDTO(apptDataSnapshot.getKey(),
                                        appointment.title, appointment.description, appointment.eventDate);
                                appointments.add(apptDTO);
                            }
                        } else {
                            tvMessage.setVisibility(View.VISIBLE);
                        }
                        mAdapter.updateList(appointments);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return false;
            }
        });
    }

}

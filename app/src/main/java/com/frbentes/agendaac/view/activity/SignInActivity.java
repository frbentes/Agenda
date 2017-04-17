package com.frbentes.agendaac.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.frbentes.agendaac.R;
import com.frbentes.agendaac.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText edtEmail;
    private EditText edtPassword;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setupToolbar();

        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.mAuth = FirebaseAuth.getInstance();

        // Views
        this.edtEmail = (EditText) findViewById(R.id.edt_email);
        this.edtPassword = (EditText) findViewById(R.id.edt_password);
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        Button btnSignUp = (Button) findViewById(R.id.btn_sign_up);

        // Listeners
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    private void setupToolbar() {
        this.setSupportActionBar(this.toolbar);
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_login) {
            signIn();
        } else if (i == R.id.btn_sign_up) {
            signUp();
        }
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle(R.string.sign_in_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }

    private void signUp() {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        this.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle(R.string.sign_in_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to Appointments Activity
        startActivity(new Intent(SignInActivity.this, AppointmentActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
            this.edtEmail.setError(getString(R.string.field_required));
            result = false;
        } else {
            this.edtEmail.setError(null);
        }

        if (TextUtils.isEmpty(edtPassword.getText().toString())) {
            this.edtPassword.setError(getString(R.string.field_required));
            result = false;
        } else {
            this.edtPassword.setError(null);
        }

        return result;
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        this.mDatabase.child("users").child(userId).setValue(user);
    }

}

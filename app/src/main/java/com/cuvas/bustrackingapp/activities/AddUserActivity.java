package com.cuvas.bustrackingapp.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cuvas.bustrackingapp.R;
import com.cuvas.bustrackingapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddUserActivity extends AppCompatActivity {

    TextView textView, createBtnText;
    FirebaseAuth auth;
    CardView addUserBtn;
    EditText addUserEmail, addUserPassword;
    ProgressDialog progressDialog;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_add_user);
        textView = findViewById(R.id.textView);
        createBtnText = findViewById(R.id.createBtnText);
        addUserBtn = findViewById(R.id.addUserBtn);
        addUserEmail = findViewById(R.id.addUserEmail);
        addUserPassword = findViewById(R.id.addUserPassword);

        String type = getIntent().getStringExtra("type");

        textView.setText("You are Admin \nCreate an Account for " + type);
        createBtnText.setText("Create " + type);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = addUserEmail.getText().toString();
                String password = addUserPassword.getText().toString();

                if (!email.isEmpty()) {
                    if (!password.isEmpty()) {
                        progressDialog = new ProgressDialog(AddUserActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();

                        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                User user = new User(authResult.getUser().getUid(), email, password, type);

                                db.collection("Users").document(authResult.getUser().getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddUserActivity.this, type + " account created successfully!!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(AddUserActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

    }
}
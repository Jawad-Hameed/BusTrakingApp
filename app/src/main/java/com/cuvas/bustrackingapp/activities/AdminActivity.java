package com.cuvas.bustrackingapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cuvas.bustrackingapp.R;
import com.cuvas.bustrackingapp.SharedPref;
import com.cuvas.bustrackingapp.adpater.PointListAdapter;
import com.cuvas.bustrackingapp.model.PointModel;
import com.cuvas.bustrackingapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    EditText adminEmail, adminPassword;
    CardView adminLogin;
    FirebaseAuth auth;
    FirebaseFirestore db;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_admin);

        adminEmail = findViewById(R.id.adminEmail);
        adminPassword = findViewById(R.id.adminPassword);
        adminLogin = findViewById(R.id.adminLoginBtn);


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();






        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = adminEmail.getText().toString();
                String password = adminPassword.getText().toString();

                if (!email.isEmpty()) {
                    if (!password.isEmpty()) {
                        progressDialog = new ProgressDialog(AdminActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                db.collection("Users").document(auth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        User user = value.toObject(User.class);
                                        if (user.getType().equals("Admin")){
                                            progressDialog.dismiss();
                                            SharedPref sharedPref = new SharedPref(AdminActivity.this);
                                            sharedPref.saveString("type", "Admin");
                                            Intent intent = new Intent(AdminActivity.this, AdminDashboardActivity.class);
                                            startActivity(intent);
                                        }else {
                                            Toast.makeText(AdminActivity.this, "You are not admin", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AdminActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(AdminActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }
}
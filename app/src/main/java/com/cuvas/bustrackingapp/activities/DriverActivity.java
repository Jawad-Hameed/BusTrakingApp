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

import com.cuvas.bustrackingapp.R;
import com.cuvas.bustrackingapp.SharedPref;
import com.cuvas.bustrackingapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DriverActivity extends AppCompatActivity {


    EditText driverEmail, driverPassword;
    CardView driverLoginBtn;
    FirebaseAuth auth;
    FirebaseFirestore db;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_driver);

        driverEmail = findViewById(R.id.driverEmail);
        driverPassword = findViewById(R.id.driverPassword);
        driverLoginBtn = findViewById(R.id.driverLoginBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        driverLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = driverEmail.getText().toString();
                String password = driverPassword.getText().toString();

                if (!email.isEmpty()) {
                    if (!password.isEmpty()) {
                        progressDialog = new ProgressDialog(DriverActivity.this);
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
                                        if (user.getType().equals("Driver")){
                                            progressDialog.dismiss();
                                            SharedPref sharedPref = new SharedPref(DriverActivity.this);
                                            sharedPref.saveString("type", "Driver");
                                            Intent intent = new Intent(DriverActivity.this, DriverMapActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(DriverActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(DriverActivity.this, "You are not driver", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(DriverActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(DriverActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DriverActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}
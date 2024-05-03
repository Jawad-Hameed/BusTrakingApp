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

public class StudentActivity extends AppCompatActivity {

    EditText studentEmail, studentPassword;
    CardView studentLoginBtn;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_student);

        studentEmail = findViewById(R.id.studentEmail);
        studentPassword = findViewById(R.id.studentPassword);
        studentLoginBtn = findViewById(R.id.studentLoginBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        studentLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = studentEmail.getText().toString();
                String password = studentPassword.getText().toString();

                if (!email.isEmpty()) {
                    if (!password.isEmpty()) {

                        progressDialog = new ProgressDialog(StudentActivity.this);
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
                                        if (user.getType().equals("Student")){
                                            progressDialog.dismiss();
                                            SharedPref sharedPref = new SharedPref(StudentActivity.this);
                                            sharedPref.saveString("type", "Student");
                                            Intent intent = new Intent(StudentActivity.this, StudentDashboardActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(StudentActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(StudentActivity.this, "You are not student", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(StudentActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(StudentActivity.this, "Enter your password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StudentActivity.this, "Enter your email", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}
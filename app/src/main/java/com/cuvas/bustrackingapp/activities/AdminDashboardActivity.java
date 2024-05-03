package com.cuvas.bustrackingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cuvas.bustrackingapp.R;
import com.cuvas.bustrackingapp.SharedPref;
import com.cuvas.bustrackingapp.adpater.PointListAdapter;
import com.cuvas.bustrackingapp.model.PointModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<PointModel> pointModelArrayList;
    PointListAdapter pointListAdapter;
    FirebaseFirestore db;
    Button addStudent, addDriver, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_admin_dashboard);

        addDriver = findViewById(R.id.addDiver);
        addStudent = findViewById(R.id.addStudent);
        logoutBtn = findViewById(R.id.logoutBtn);


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref sharedPref = new SharedPref(AdminDashboardActivity.this);
                sharedPref.saveString("type", "");
                Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AddUserActivity.class);
                intent.putExtra("type", "Student");
                startActivity(intent);
            }
        });

        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AddUserActivity.class);
                intent.putExtra("type", "Driver");
                startActivity(intent);
            }
        });


        recyclerView = findViewById(R.id.recyclerViewList);

        db = FirebaseFirestore.getInstance();

        pointModelArrayList = new ArrayList<>();

        pointListAdapter = new PointListAdapter(AdminDashboardActivity.this, pointModelArrayList);
        recyclerView.setAdapter(pointListAdapter);

        db.collection("Points").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                pointModelArrayList.clear();
                for (DocumentSnapshot document: value.getDocuments()){
                    PointModel pointModel = document.toObject(PointModel.class);
                    pointModelArrayList.add(pointModel);
                }
                Log.d("JAWAD", pointModelArrayList.size()+"");
            pointListAdapter.notifyDataSetChanged();
            }
        });
    }
}
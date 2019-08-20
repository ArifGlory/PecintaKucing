package myproject.pecintakucinglampung.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Dokter;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.Kelas.Slider;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.adapter.AdapterDokter;
import myproject.pecintakucinglampung.adapter.AdapterSlider;

public class KelolaDokterActivity extends AppCompatActivity {


    AdapterDokter adapter;
    RecyclerView rvMycat;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    private List<Dokter> dokterList;
    CollectionReference ref;
    FirebaseFirestore firestore;
    FloatingActionButton btnCreate;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_dokter);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("dokter");

        rvMycat = findViewById(R.id.rvMycat);
        btnCreate = findViewById(R.id.btnCreate);

        dokterList = new ArrayList<>();
        adapter = new AdapterDokter(KelolaDokterActivity.this,dokterList);

        rvMycat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvMycat.setHasFixedSize(true);
        rvMycat.setItemAnimator(new DefaultItemAnimator());
        rvMycat.setAdapter(adapter);

        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddDokterActivity.class);
                startActivity(intent);
            }
        });

        if (!SharedVariable.email.equals("admin@gmail.com")){
            btnCreate.setVisibility(View.GONE);
        }

        getDataDokter();
    }
    public void getDataDokter(){
        ref.orderBy("idDokter", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dokterList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        Dokter dokter = doc.toObject(Dokter.class);
                        dokterList.add(dokter);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("jmlDokter : ",""+dokterList.size());
                }else{
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(KelolaDokterActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(KelolaDokterActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataDokter();
    }
}

package myproject.pecintakucinglampung.activity;

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
import myproject.pecintakucinglampung.Kelas.Kesehatan;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.adapter.AdapterDokter;
import myproject.pecintakucinglampung.adapter.AdapterKesehatan;
import myproject.pecintakucinglampung.admin.AddKesehatanActivity;
import myproject.pecintakucinglampung.admin.KelolaDokterActivity;

public class ListKesehatanActivity extends AppCompatActivity {

    AdapterKesehatan adapter;
    RecyclerView rvMycat;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    private List<Kesehatan> kesehatanList;
    CollectionReference ref;
    FirebaseFirestore firestore;
    FloatingActionButton btnCreate;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kesehatan);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kesehatan");

        rvMycat = findViewById(R.id.rvMycat);
        btnCreate = findViewById(R.id.btnCreate);
        Log.d("email=",""+SharedVariable.email);

        if (SharedVariable.email.equals("admin@gmail.com")){
            btnCreate.setVisibility(View.VISIBLE);
        }

        kesehatanList = new ArrayList<>();
        adapter = new AdapterKesehatan(ListKesehatanActivity.this,kesehatanList);

        rvMycat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvMycat.setHasFixedSize(true);
        rvMycat.setItemAnimator(new DefaultItemAnimator());
        rvMycat.setAdapter(adapter);

        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        getDataKesehatan();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddKesehatanActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getDataKesehatan(){
        ref.orderBy("idKesehatan", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kesehatanList.clear();
                adapter.notifyDataSetChanged();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        Kesehatan kesehatan = doc.toObject(Kesehatan.class);
                        kesehatanList.add(kesehatan);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("jmlDokter : ",""+kesehatanList.size());
                }else{
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(ListKesehatanActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(ListKesehatanActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataKesehatan();
    }
}

package myproject.pecintakucinglampung.activity;

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
import android.widget.Toast;

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
import myproject.pecintakucinglampung.Kelas.Kucing;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.adapter.AdapterKucing;

public class MycatActivity extends AppCompatActivity {

    AdapterKucing adapter;
    RecyclerView rvMycat;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    private List<Kucing> kucingList;
    CollectionReference ref;
    FirebaseFirestore firestore;
    FloatingActionButton btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycat);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("kucing");

        rvMycat = findViewById(R.id.rvMycat);
        btnCreate = findViewById(R.id.btnCreate);

        kucingList  = new ArrayList<>();
        adapter     = new AdapterKucing(MycatActivity.this,kucingList);

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
                Intent intent = new Intent(getApplicationContext(),AddcatActivity.class);
                startActivity(intent);
            }
        });

        getKucingByPemilik(SharedVariable.userID);
    }

    public void getKucingByPemilik(final String idPemilik){

        ref.orderBy("idKucing", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                kucingList.clear();
                adapter.notifyDataSetChanged();
                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        if (doc.get("idPemilik").equals(idPemilik)){
                            Kucing kucingku = doc.toObject(Kucing.class);
                            String idKucing = doc.get("idKucing").toString();
                            kucingku.setIdKucing(idKucing);

                            kucingList.add(kucingku);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(MycatActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(MycatActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getKucingByPemilik(SharedVariable.userID);
      //  Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

      //  Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    }



}

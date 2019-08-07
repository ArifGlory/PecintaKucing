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
import android.widget.ImageView;

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
import myproject.pecintakucinglampung.Kelas.Perawatan;
import myproject.pecintakucinglampung.Kelas.SharedVariable;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.adapter.AdapterPerawatan;

public class PerawatanActivity extends AppCompatActivity {

    RecyclerView rvMycat;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    private List<Perawatan> perawatanList;
    CollectionReference ref;
    FirebaseFirestore firestore;
    FloatingActionButton btnCreate;
    AdapterPerawatan adapter;
    ImageView ivAjukanPerawat;
    private String checkIfAlreadyRegistered = "no";
    Perawatan perawatanSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perawatan);

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("perawatan");

        rvMycat = findViewById(R.id.rvMycat);
        ivAjukanPerawat = findViewById(R.id.ivAjukanPerawat);
        perawatanList   = new ArrayList<>();
        adapter         = new AdapterPerawatan(PerawatanActivity.this,perawatanList);

        rvMycat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvMycat.setHasFixedSize(true);
        rvMycat.setItemAnimator(new DefaultItemAnimator());
        rvMycat.setAdapter(adapter);

        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        ivAjukanPerawat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (perawatanList.size() > 0){
                    Intent intent = new Intent(getApplicationContext(),AjukanPerawatActivity.class);
                    intent.putExtra("check",checkIfAlreadyRegistered);
                    intent.putExtra("perawatan",perawatanSend);
                    startActivity(intent);
                }else {
                    //send dummy data
                    perawatanSend = new Perawatan("a",
                            "a",
                            "a",
                            "a","a",
                            "a",
                            "a",
                            "a"
                            );

                    Intent intent = new Intent(getApplicationContext(),AjukanPerawatActivity.class);
                    intent.putExtra("check",checkIfAlreadyRegistered);
                    intent.putExtra("perawatan",perawatanSend);
                    startActivity(intent);
                }

            }
        });

        getDataPerawat();
    }

    public void getDataPerawat(){

        ref.orderBy("idPerawatan", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                perawatanList.clear();
                adapter.notifyDataSetChanged();
                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (DocumentSnapshot doc : task.getResult()){
                        Perawatan perawatan = doc.toObject(Perawatan.class);
                        String idPerawatan  = doc.get("idPerawatan").toString();
                        String idUser       = doc.get("idUser").toString();
                        perawatan.setIdPerawatan(idPerawatan);
                        perawatanList.add(perawatan);

                        if (idUser.equals(SharedVariable.userID)){
                            checkIfAlreadyRegistered = "true";
                            perawatanSend = perawatan;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(PerawatanActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(PerawatanActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataPerawat();
    }
}

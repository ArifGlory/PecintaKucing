package myproject.pecintakucinglampung.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.admin.KelolaDokterActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    private List<Dokter> dokterList;
    CollectionReference ref;
    FirebaseFirestore firestore;
    public static android.app.AlertDialog dialog;
    MarkerOptions markerOptions;
    public Marker marker_ghost,temp_marker;
    private String idMarker = "";
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(this);
        firestore = FirebaseFirestore.getInstance();
        ref = firestore.collection("dokter");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dokterList = new ArrayList<>();
        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();

        getDataDokter();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-5.3942942, 105.2750719);
        mMap.addMarker(new MarkerOptions().position(sydney).title("lokasi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                temp_marker     = marker;
                idMarker        = temp_marker.getSnippet();

                LayoutInflater minlfater = LayoutInflater.from(MapsActivity.this);
                View v2 = minlfater.inflate(R.layout.dialog_dokter, null);
                dialog = new AlertDialog.Builder(MapsActivity.this).create();
                dialog.setView(v2);

                final TextView tvNamaDokter =  v2.findViewById(R.id.tvNamaDokter);
                final TextView tvJadwal =  v2.findViewById(R.id.tvJadwal);
                final TextView tvBidang =  v2.findViewById(R.id.tvBidang);
                final TextView tvPhone =  v2.findViewById(R.id.tvPhone);
                final TextView tvAlamat =  v2.findViewById(R.id.tvAlamat);
                final ImageView ivDokter = v2.findViewById(R.id.ivDokter);

                for (int c=0;c<dokterList.size();c++){
                   Dokter dokterku = dokterList.get(c);

                   if (dokterku.getIdDokter().equals(idMarker)){
                       index = c;
                   }
                }

                Dokter dokter = dokterList.get(index);
                 Glide.with(MapsActivity.this)
                        .load(dokter.getUrlGambar())
                        .into(ivDokter);

                tvAlamat.setText(dokter.getAlamat());
                tvNamaDokter.setText(dokter.getNama());
                tvJadwal.setText("Jadwal Praktik = "+dokter.getJadwal());
                tvBidang.setText("Bidang = "+dokter.getBidang());
                tvPhone.setText(dokter.getPhone());

                dialog.show();

                return true;
            }
        });
    }

    public void getDataDokter(){
        ref.orderBy("idDokter", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dokterList.clear();
                mMap.clear();

                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    for (final DocumentSnapshot doc : task.getResult()){
                        final Dokter dokter = doc.toObject(Dokter.class);
                        dokterList.add(dokter);
                        Double lat = Double.parseDouble(dokter.getLat());
                        Double lon = Double.parseDouble(dokter.getLon());
                        LatLng lokasiDokter = new LatLng(lat, lon);

                        markerOptions = new  MarkerOptions().position(lokasiDokter).title(dokter.getNama()).snippet(dokter.getIdDokter());
                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiDokter, 13));

                    }
                    Log.d("jmlDokter : ",""+dokterList.size());




                }else{
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(MapsActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Pengambilan data gagal")
                            .show();
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pDialogLoading.dismiss();
                new SweetAlertDialog(MapsActivity.this,SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Pengambilan data gagal")
                        .show();
                Log.d("gagalGetData:",e.toString());
            }
        });
    }
}

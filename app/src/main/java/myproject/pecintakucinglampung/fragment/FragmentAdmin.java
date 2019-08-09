package myproject.pecintakucinglampung.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import myproject.pecintakucinglampung.Kelas.Slider;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.activity.AdopsiActivity;
import myproject.pecintakucinglampung.activity.JualBeliActivity;
import myproject.pecintakucinglampung.activity.MycatActivity;
import myproject.pecintakucinglampung.activity.PerawatanActivity;
import myproject.pecintakucinglampung.adapter.AdapterSlider;
import myproject.pecintakucinglampung.admin.KelolaSliderActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAdmin extends Fragment {


    public FragmentAdmin() {
        // Required empty public constructor
    }

    CarouselView carouselView;
    int[] sampleImage = {R.drawable.cat_event1,R.drawable.cat_event2,R.drawable.cat_event3};
    TextView tvManageSlider,tvDataKucing,tvDataPengguna;

    FirebaseFirestore firestore;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference refKucing,refUser,refSlider;
    FirebaseUser fbUser;
    private FirebaseAuth fAuth;
    AdapterSlider adapter;
    private List<Slider> sliderList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_admin, container, false);
        Firebase.setAndroidContext(this.getActivity());
        FirebaseApp.initializeApp(this.getActivity());
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        refKucing = firestore.collection("kucing");
        refUser = firestore.collection("users");
        refSlider = firestore.collection("slider");

        carouselView = view.findViewById(R.id.carouselView);
        tvManageSlider = view.findViewById(R.id.tvManageSlider);
        tvDataKucing = view.findViewById(R.id.tvDataKucing);
        tvDataPengguna = view.findViewById(R.id.tvDataPengguna);

        sliderList = new ArrayList<>();
        adapter = new AdapterSlider(getActivity(),sliderList);

        pDialogLoading = new SweetAlertDialog(this.getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Menampilkan data..");
        pDialogLoading.setCancelable(false);


        tvManageSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), KelolaSliderActivity.class);
                startActivity(i);
            }
        });

        getUserCount();
        getKucingCount();
        getDataSlider();

        return view;
    }

    private void getDataSlider(){
        pDialogLoading.show();
        refSlider.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    pDialogLoading.dismiss();
                    int jumlah = task.getResult().size();
                    for (DocumentSnapshot doc : task.getResult()){
                        Slider slider = doc.toObject(Slider.class);
                        sliderList.add(slider);
                    }

                    carouselView.setImageListener(imageListener);
                    carouselView.setPageCount(sliderList.size());
                    carouselView.setImageClickListener(new ImageClickListener() {
                        @Override
                        public void onClick(int position) {
                          Slider sliderku = sliderList.get(position);
                          String url = sliderku.getLink();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        }
                    });

                }else {
                    pDialogLoading.dismiss();
                   Toast.makeText(getActivity(),"Terjadi kesalahan coba lagi nanti",Toast.LENGTH_SHORT);
                    Log.d("gagalGetData:",task.getException().toString());
                }
            }
        });
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            //imageView.setImageResource(sampleImage[position]);
            Slider slider =  sliderList.get(position);
            Glide.with(getActivity())
                    .load(slider.getUrlGambar())
                    .into(imageView);
        }
    };

    private void getUserCount(){
        refUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int jumlah  = task.getResult().size();
                    tvDataPengguna.setText(""+jumlah+" Orang");

                }
            }
        });
    }

    private void getKucingCount(){
        refKucing.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    int jumlah  = task.getResult().size();
                    tvDataKucing.setText(""+jumlah+" Ekor");

                }
            }
        });
    }




}

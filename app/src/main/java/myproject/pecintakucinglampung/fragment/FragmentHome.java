package myproject.pecintakucinglampung.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.util.List;

import myproject.pecintakucinglampung.Kelas.Slider;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.activity.AdopsiActivity;
import myproject.pecintakucinglampung.activity.JualBeliActivity;
import myproject.pecintakucinglampung.activity.MycatActivity;
import myproject.pecintakucinglampung.activity.PerawatanActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {


    public FragmentHome() {
        // Required empty public constructor
    }

    CarouselView carouselView;
    int[] sampleImage = {R.drawable.cat_event1,R.drawable.cat_event2,R.drawable.cat_event3};
    RelativeLayout rlMyCat,rlAdopsi,rlJualBeli,rlPerawatan;
    private List<Slider> sliderList;
    CollectionReference ref;
    FirebaseFirestore firestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        carouselView = view.findViewById(R.id.carouselView);
        rlMyCat = view.findViewById(R.id.rlMyCat);
        rlAdopsi = view.findViewById(R.id.rlAdopsi);
        rlJualBeli = view.findViewById(R.id.rlJualBeli);
        rlPerawatan = view.findViewById(R.id.rlPerawatan);

        carouselView.setPageCount(sampleImage.length);
        carouselView.setImageListener(imageListener);
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(getActivity(),"gambar ke = "+position,Toast.LENGTH_SHORT).show();
            }
        });

        rlMyCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MycatActivity.class);
                startActivity(intent);
            }
        });
        rlAdopsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdopsiActivity.class);
                startActivity(intent);
            }
        });
        rlJualBeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), JualBeliActivity.class);
                startActivity(intent);
            }
        });
        rlPerawatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PerawatanActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImage[position]);
        }
    };

    private void getDataSlider(){

    }


}

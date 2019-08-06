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

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.activity.MycatActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {


    public FragmentHome() {
        // Required empty public constructor
    }

    CarouselView carouselView;
    int[] sampleImage = {R.drawable.cat_event1,R.drawable.cat_event2,R.drawable.cat_event3};
    RelativeLayout rlMyCat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        carouselView = view.findViewById(R.id.carouselView);
        rlMyCat = view.findViewById(R.id.rlMyCat);

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


        return view;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImage[position]);
        }
    };


}

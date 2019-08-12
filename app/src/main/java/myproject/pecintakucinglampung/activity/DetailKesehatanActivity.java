package myproject.pecintakucinglampung.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import myproject.pecintakucinglampung.Kelas.Kesehatan;
import myproject.pecintakucinglampung.R;

public class DetailKesehatanActivity extends AppCompatActivity {

    TextView tvJudul,tvDeskripsi,tvSolusi;
    Intent intent;
    Kesehatan kesehatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kesehatan);


        intent = getIntent();
        kesehatan = (Kesehatan) intent.getSerializableExtra("kesehatan");

        tvJudul = findViewById(R.id.tvJudul);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvSolusi = findViewById(R.id.tvSolusi);

        tvJudul.setText(kesehatan.getJudul());
        tvDeskripsi.setText(kesehatan.getDeskripsi());
        tvSolusi.setText(kesehatan.getSolusi());

    }
}

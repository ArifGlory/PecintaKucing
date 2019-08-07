package myproject.pecintakucinglampung.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Perawatan;
import myproject.pecintakucinglampung.R;

public class DetailPerawatActivity extends AppCompatActivity {

    Button btnHubungi;
    TextView tvHarga,tvDeskripsi,tvNamaPerawat;
    ImageView ivPerawat;
    Perawatan perawatan;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_perawat);

        intent = getIntent();
        perawatan = (Perawatan) intent.getSerializableExtra("perawatan");

        tvHarga = findViewById(R.id.tvHarga);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvNamaPerawat = findViewById(R.id.tvNamaPerawat);
        ivPerawat = findViewById(R.id.ivPerawat);
        btnHubungi = findViewById(R.id.btnHubungi);

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        int harga = Integer.parseInt(perawatan.getHarga());

        tvHarga.setText(formatRupiah.format((double) harga) + " / hari");
        tvNamaPerawat.setText(perawatan.getNama());
        tvDeskripsi.setText(perawatan.getDeskripsi());

        Glide.with(this)
                .load(perawatan.getFoto())
                .into(ivPerawat);

        btnHubungi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (perawatan.getNope().length() == 0){
                    new SweetAlertDialog(DetailPerawatActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Nomor pemilik bermasalah,coba lagi nanti")
                            .show();
                }else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", perawatan.getNope(), null));
                    startActivity(intent);
                }
            }
        });
    }
}

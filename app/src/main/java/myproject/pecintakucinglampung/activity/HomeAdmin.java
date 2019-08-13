package myproject.pecintakucinglampung.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import cn.pedant.SweetAlert.SweetAlertDialog;
import myproject.pecintakucinglampung.Kelas.Perawatan;
import myproject.pecintakucinglampung.MainActivity;
import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.admin.ChatActivity;
import myproject.pecintakucinglampung.admin.KelolaDokterActivity;
import myproject.pecintakucinglampung.admin.KelolaSliderActivity;
import myproject.pecintakucinglampung.fragment.FragmentAdmin;

public class HomeAdmin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SweetAlertDialog pDialogLoading,pDialodInfo;
    CollectionReference ref,refPemilik;
    FirebaseFirestore firestore;
    FirebaseUser fbUser;
    private FirebaseAuth fAuth;
    FragmentAdmin fragmentAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Firebase.setAndroidContext(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
        firestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = firestore.collection("kucing");

        fragmentAdmin = new FragmentAdmin();
        goToFragment(fragmentAdmin,true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            // Handle the camera action
            fAuth.signOut();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_slider){
            Intent i = new Intent(getApplicationContext(), KelolaSliderActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_jualbeli){
            Intent i = new Intent(getApplicationContext(), JualBeliActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_adopsi){
            Intent i = new Intent(getApplicationContext(), AdopsiActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_dokter){
            Intent i = new Intent(getApplicationContext(), KelolaDokterActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_kesehatan){
            Intent i = new Intent(getApplicationContext(), ListKesehatanActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_chat){
            Intent i = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_perawat){
            Intent i = new Intent(getApplicationContext(), PerawatanActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void goToFragment(Fragment fragment, boolean isTop) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_homeAdmin, fragment);
        if (!isTop)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

package myproject.pecintakucinglampung.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;

import myproject.pecintakucinglampung.R;
import myproject.pecintakucinglampung.fragment.FragmentHome;
import myproject.pecintakucinglampung.fragment.FragmentProfil;

public class HomeActivity extends AppCompatActivity {

    FragmentHome fragmentHome;
    FragmentProfil fragmentProfil;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    goToFragment(fragmentHome,true);
                    return true;

                case R.id.navigation_profil:
                    goToFragment(fragmentProfil,true);
                    return true;

                case R.id.navigation_chat:

                    return true;
            }
            return false;
        }
    };

    void goToFragment(Fragment fragment, boolean isTop) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if (!isTop)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentHome        = new FragmentHome();
        fragmentProfil      = new FragmentProfil();

        goToFragment(fragmentHome,true);
    }

}

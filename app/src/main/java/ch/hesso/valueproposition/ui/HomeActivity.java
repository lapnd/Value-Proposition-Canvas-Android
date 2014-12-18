package ch.hesso.valueproposition.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import ch.hesso.valueproposition.R;


public class HomeActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new HomeFragment()).commit();
        }
    }
}

package net.robertx.planeteze_b07;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.robertx.planeteze_b07.ui.dashboard.HomeFragment;
import net.robertx.planeteze_b07.ui.dashboard.PastSurveyResultsFragment;
import net.robertx.planeteze_b07.ui.dashboard.ProfileFragment;

/**
 * The Dashboard activity is the main screen of the application after the user logs in.
 * It contains a bottom navigation view to switch between different fragments such as
 * Home, Profile, and Past Survey Results.
 */
public class Dashboard extends AppCompatActivity {

    /**
     * Called when the activity is first created. Initializes the UI components and fragments.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navBtnHome);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navBtnProfile) {
                replaceFragment(new ProfileFragment());
                return true;
            } else if (item.getItemId() == R.id.navBtnHome) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (item.getItemId() == R.id.navBtnCalendar) {
                replaceFragment(new PastSurveyResultsFragment());
                return true;
            }
            return false;
        });
    }
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit();
    }
}
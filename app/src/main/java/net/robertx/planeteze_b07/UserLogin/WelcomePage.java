package net.robertx.planeteze_b07.UserLogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import net.robertx.planeteze_b07.Dashboard;
import net.robertx.planeteze_b07.R;

public class WelcomePage extends AppCompatActivity {
    Button logininbutton;
    Button signinbutton;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.welcome_page);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // send logged in user to dashboard
            Intent intent = new Intent(WelcomePage.this, Dashboard.class);
            startActivity(intent);
            finish();
            return;
        }

        //sends user to sign up page
        signinbutton= findViewById(R.id.signupbutton);
        signinbutton.setOnClickListener(v -> {
            Intent intent= new Intent(WelcomePage.this, SignUpPage.class);
            startActivity(intent);
        });

        //sends user to the login page
        logininbutton = findViewById(R.id.loginbutton);
        logininbutton.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomePage.this, LoginPage.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcomepage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
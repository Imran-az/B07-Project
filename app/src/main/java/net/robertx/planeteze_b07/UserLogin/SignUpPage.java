package net.robertx.planeteze_b07.UserLogin;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import net.robertx.planeteze_b07.R;


import java.util.Objects;


public class SignUpPage extends AppCompatActivity {
    Button backbutton;
    Button register_button;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseDatabase realtimeDatabase;
    DatabaseReference databaseReference;


    EditText editTextEmail, editTextPassword, editTextFirstname, editTextLastname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);


        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.emailbox_signup);
        editTextPassword = findViewById(R.id.password_signup);
        editTextFirstname = findViewById(R.id.firstname);
        editTextLastname = findViewById(R.id.lastname);
        register_button = findViewById(R.id.signup_confirm);
        progressBar = findViewById(R.id.progressbar_signup);


        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                //checking if the inputs for email and password are empty
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(SignUpPage.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    Toast.makeText(SignUpPage.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    //gets current user
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null) {
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> emailTask) {
                                                if (emailTask.isSuccessful()){

                                                    String firstName, lastName, userID;
                                                    firstName = String.valueOf(editTextFirstname.getText());
                                                    lastName = String.valueOf(editTextLastname.getText());

                                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                                    userID = currentUser.getUid();

                                                    if (!firstName.isEmpty() && !lastName.isEmpty() && !password.isEmpty()){
                                                        User user = new User(userID, firstName, lastName, email, password);

                                                        realtimeDatabase = FirebaseDatabase.getInstance();
                                                        databaseReference = realtimeDatabase.getReference("Users");
                                                        databaseReference.child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                editTextFirstname.setText("");
                                                                editTextLastname.setText("");
                                                                editTextEmail.setText("");
                                                                editTextPassword.setText("");
                                                            }
                                                        });
                                                    }
                                                    Toast.makeText(SignUpPage.this, "User registered successfully. Please verify your email.",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(SignUpPage.this, LoginPage.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                else{
                                                    Toast.makeText(SignUpPage.this, "Email not registered",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUpPage.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });


            }
        });


        backbutton = findViewById(R.id.returnhomescreen_signup);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpPage.this, WelcomePage.class);
                startActivity(intent);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

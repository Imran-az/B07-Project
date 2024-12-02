package net.robertx.planeteze_b07.UserLogin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LoginModel {
    private final FirebaseAuth mAuth;

    public LoginModel(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public void login(String email, String password,
                      OnSuccessListener<FirebaseUser> onSuccessListener,
                      OnFailureListener onFailureListener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        if (user.isEmailVerified()) {
                            onSuccessListener.onSuccess(user);
                        } else {
                            onFailureListener.onFailure(new Exception("Verify your email before you login"));
                        }
                    } else {
                        onFailureListener.onFailure(new Exception("An unknown error occurred during login."));
                    }
                })
                .addOnFailureListener(exception -> {
                    String message = exception.getMessage();
                    if (message != null) {
                        // Match Firebase Auth error messages
                        if (message.toLowerCase().contains("password is invalid")) {
                            onFailureListener.onFailure(new Exception("Incorrect password. Please try again."));
                        } else if (message.toLowerCase().contains("no user record")) {
                            onFailureListener.onFailure(new Exception("No account found with this email."));
                        } else {
                            onFailureListener.onFailure(new Exception("Login failed: " + message));
                        }
                    } else {
                        onFailureListener.onFailure(new Exception("An unexpected error occurred during login."));
                    }
                });
    }
}


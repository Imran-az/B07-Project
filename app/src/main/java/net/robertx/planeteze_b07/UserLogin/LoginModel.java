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
                        onFailureListener.onFailure(exception);
                        return;
                    }
                    else if (message.contains("The supplied auth credential is incorrect, malformed or has expired")) {
                        onFailureListener.onFailure(new Exception("The supplied auth credential is incorrect, malformed or has expired"));
                    } else if (message.contains("The email address is badly formatted.")) {
                        onFailureListener.onFailure(new Exception("The email address is badly formatted."));
                    } else {
                        onFailureListener.onFailure(exception);
                    }
                    onFailureListener.onFailure(exception);
                });
    }
}


package net.robertx.planeteze_b07.userLogin;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * This class handles user login functionality using Firebase Authentication.
 */
public class LoginModel {
    private final FirebaseAuth mAuth;

    /**
     * Constructs a new LoginModel instance.
     *
     * @param mAuth The FirebaseAuth instance to handle authentication operations.
     */
    public LoginModel(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    /**
     * Logs in a user with the specified email and password.
     *
     * @param email             The email address of the user.
     * @param password          The password associated with the email address.
     * @param onSuccessListener A callback to handle a successful login.
     *                          The FirebaseUser object is passed to this listener.
     * @param onFailureListener A callback to handle login failure.
     *                          An Exception object with the error details is passed to this listener.
     */
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

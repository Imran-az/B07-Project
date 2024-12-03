package net.robertx.planeteze_b07.UserLogin;

import android.util.Log;

public class LoginPresenter implements LoginContract.Presenter {
    private final LoginContract.View view;
    private final LoginModel model;

    public LoginPresenter(LoginContract.View view, LoginModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onLoginButtonClicked(String email, String password) {
        if (email.isEmpty()) {
            view.showToast("Enter an email");
            return;
        }

        if (password.isEmpty()) {
            view.showToast("Enter a password");
            return;
        }

        view.showProgress();

        model.login(email, password,
                user -> {
                    view.hideProgress();
                    if (user == null) {
                        view.showToast("An unknown error occurred during login.");
                    } else {
                        view.showToast("Login Successful");
                        view.navigateToMainPage();
                    }
                },
                exception -> {
                    view.hideProgress();
                    String errorMessage = exception.getMessage();
                    if (errorMessage != null) {
                        if (errorMessage.contains("The supplied auth credential is incorrect, malformed or has expired")) {
                            view.showToast("Incorrect email or password");
                        } else if (errorMessage.contains("The email address is badly formatted.")) {
                            view.showToast("Invalid email format");
                        } else if (errorMessage.contains("Verify your email before you login")) {
                            view.showToast("Verify your email before you login");
                        } else {
                            view.showToast(errorMessage);
                        }
                    } else {
                        view.showToast("An unexpected error occurred");
                    }
                }
        );
    }


    @Override
    public void onSignUpButtonClicked() {
        view.navigateToSignUpPage();
    }

    @Override
    public void onForgotPasswordButtonClicked() {
        view.navigateToForgotPasswordPage();
    }

    @Override
    public void onBackButtonClicked() {
        view.navigateToWelcomePage();
    }
}

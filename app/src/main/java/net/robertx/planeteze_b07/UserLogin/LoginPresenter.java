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
                    view.showToast("Login Successful");
                    view.navigateToMainPage();
                },
                exception -> {
                    view.hideProgress();
                    String errorMessage = exception.getMessage();

                    if (errorMessage != null) {
                        if (errorMessage.contains("password is invalid")) {
                            view.showToast("Incorrect password. Please try again.");
                        } else if (errorMessage.contains("no user record")) {
                            view.showToast("No account found with this email.");
                        } else if (errorMessage.contains("Verify your email")) {
                            view.showToast("Verify your email before you login");
                        } else {
                            view.showToast(errorMessage); // Fallback for other exceptions
                        }
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

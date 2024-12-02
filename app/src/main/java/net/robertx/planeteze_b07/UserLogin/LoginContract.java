package net.robertx.planeteze_b07.UserLogin;

public interface LoginContract {
    interface View {
        void showProgress();
        void hideProgress();
        void showToast(String message);
        void navigateToMainPage();
        void navigateToSignUpPage();
        void navigateToForgotPasswordPage();
        void navigateToWelcomePage();
    }

    interface Presenter {
        void onLoginButtonClicked(String email, String password);
        void onSignUpButtonClicked();
        void onForgotPasswordButtonClicked();
        void onBackButtonClicked();
    }
}


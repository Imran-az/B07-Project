package net.robertx.planeteze_b07.UserLogin;

/**
 * The LoginPresenter class implements the LoginContract.Presenter interface.
 * It handles user actions and delegates tasks between the LoginContract.View and @link LoginModel.
 */
public class LoginPresenter implements LoginContract.Presenter {
    private final LoginContract.View view;
    private final LoginModel model;

    /**
     * Constructs a new LoginPresenter instance.
     *
     * @param view  The view interface to communicate with the UI.
     * @param model The model handling the login logic.
     */
    public LoginPresenter(LoginContract.View view, LoginModel model) {
        this.view = view;
        this.model = model;
    }

    /**
     * Handles the logic when the login button is clicked.
     * Validates user input and initiates the login process.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     */
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

    /**
     * Handles the logic when the sign-up button is clicked.
     * Navigates the user to the sign-up page.
     */
    @Override
    public void onSignUpButtonClicked() {
        view.navigateToSignUpPage();
    }

    /**
     * Handles the logic when the forgot password button is clicked.
     * Navigates the user to the forgot password page.
     */
    @Override
    public void onForgotPasswordButtonClicked() {
        view.navigateToForgotPasswordPage();
    }

    /**
     * Handles the logic when the back button is clicked.
     * Navigates the user to the welcome page.
     */
    @Override
    public void onBackButtonClicked() {
        view.navigateToWelcomePage();
    }
}

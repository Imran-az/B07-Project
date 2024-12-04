package net.robertx.planeteze_b07.userLogin;

/**
 * Interface defining the contract for the Login feature.
 * It contains nested interfaces for the View and Presenter components.
 */
public interface LoginContract {
    /**
     * Interface representing the View in the MVP pattern.
     * It contains methods to update the UI based on user interactions and presenter responses.
     */
    interface View {
        /**
         * Shows a progress indicator.
         */
        void showProgress();

        /**
         * Hides the progress indicator.
         */
        void hideProgress();

        /**
         * Displays a toast message.
         *
         * @param message The message to be displayed.
         */
        void showToast(String message);

        /**
         * Navigates to the main page.
         */
        void navigateToMainPage();

        /**
         * Navigates to the sign-up page.
         */
        void navigateToSignUpPage();

        /**
         * Navigates to the forgot password page.
         */
        void navigateToForgotPasswordPage();

        /**
         * Navigates to the welcome page.
         */
        void navigateToWelcomePage();
    }

    /**
     * Interface representing the Presenter in the MVP pattern.
     * It contains methods to handle user interactions and update the View.
     */
    interface Presenter {
        /**
         * Handles the login button click event.
         *
         * @param email    The user's email address.
         * @param password The user's password.
         */
        void onLoginButtonClicked(String email, String password);

        /**
         * Handles the sign-up button click event.
         */
        void onSignUpButtonClicked();

        /**
         * Handles the forgot password button click event.
         */
        void onForgotPasswordButtonClicked();

        /**
         * Handles the back button click event.
         */
        void onBackButtonClicked();
    }
}

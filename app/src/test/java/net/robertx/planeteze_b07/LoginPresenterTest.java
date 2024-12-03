package net.robertx.planeteze_b07;

import net.robertx.planeteze_b07.UserLogin.LoginContract;
import net.robertx.planeteze_b07.UserLogin.LoginModel;
import net.robertx.planeteze_b07.UserLogin.LoginPresenter;

import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    private LoginContract.View mockView;
    private LoginModel mockModel;
    private LoginPresenter presenter;

    @Before
    public void setUp() {
        mockView = mock(LoginContract.View.class);
        mockModel = mock(LoginModel.class);
        presenter = new LoginPresenter(mockView, mockModel);
    }

    @Test
    public void testLoginWithEmptyEmail() {
        presenter.onLoginButtonClicked("", "password");
        verify(mockView).showToast("Enter an email");
        verifyNoInteractions(mockModel);
    }

    @Test
    public void testLoginWithEmptyPassword() {
        presenter.onLoginButtonClicked("email@example.com", "");
        verify(mockView).showToast("Enter a password");
        verifyNoInteractions(mockModel);
    }

    @Test
    public void testLoginSuccess() {
        String email = "email@example.com";
        String password = "password";

        // Arrange
        doAnswer(invocation -> {
            OnSuccessListener<FirebaseUser> successListener = invocation.getArgument(2);
            successListener.onSuccess(mock(FirebaseUser.class));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        // Act
        presenter.onLoginButtonClicked(email, password);

        // Assert
        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("Login Successful");
        verify(mockView).navigateToMainPage();
    }

    @Test
    public void testLoginWithUnverifiedEmail() {
        String email = "unverified@example.com";
        String password = "password";

        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(3);
            onFailureListener.onFailure(new Exception("Verify your email before you login"));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("Verify your email before you login");
        verify(mockView, never()).navigateToMainPage();
    }

    @Test
    public void testSignUpNavigation() {
        presenter.onSignUpButtonClicked();
        verify(mockView).navigateToSignUpPage();
    }

    @Test
    public void testForgotPasswordNavigation() {
        presenter.onForgotPasswordButtonClicked();
        verify(mockView).navigateToForgotPasswordPage();
    }

    @Test
    public void testBackNavigation() {
        presenter.onBackButtonClicked();
        verify(mockView).navigateToWelcomePage();
    }

    @Test
    public void testLoginWithInvalidEmailFormat() {
        String email = "invalid-email";
        String password = "password";

        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(3);
            onFailureListener.onFailure(new Exception("The email address is badly formatted."));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("Invalid email format");
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        String email = "user@example.com";
        String password = "wrongpassword";

        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(3);
            onFailureListener.onFailure(new Exception("The supplied auth credential is incorrect, malformed or has expired"));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("Incorrect email or password");
    }

    @Test
    public void testLoginWithUnknownError() {
        String email = "user@example.com";
        String password = "password";

        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(3);
            onFailureListener.onFailure(new Exception("An unknown error occurred."));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("An unknown error occurred.");
    }

    @Test
    public void testLoginWithNullFirebaseUser() {
        String email = "email@example.com";
        String password = "password";

        doAnswer(invocation -> {
            OnSuccessListener<FirebaseUser> successListener = invocation.getArgument(2);
            successListener.onSuccess(null); // Simulating null user
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("An unknown error occurred during login.");
        verify(mockView, never()).navigateToMainPage();
    }

    @Test
    public void testLoginWithUnhandledErrorMessage() {
        String email = "email@example.com";
        String password = "password";

        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(3);
            onFailureListener.onFailure(new Exception("Unhandled error message"));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("Unhandled error message");
    }


    @Test
    public void testLoginWithExceptionNoMessage() {
        String email = "email@example.com";
        String password = "password";

        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(3);
            onFailureListener.onFailure(new Exception()); // Exception with no message
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("An unexpected error occurred");
    }



}



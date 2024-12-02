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
            successListener.onSuccess(mock(FirebaseUser.class)); // No need to stub isEmailVerified
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
        // Arrange
        String email = "unverified@example.com";
        String password = "password";

        doAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(3);
            onFailureListener.onFailure(new Exception("Verify your email before you login"));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        // Act
        presenter.onLoginButtonClicked(email, password);

        // Assert
        verify(mockView).showProgress();
        verify(mockView).hideProgress();
        verify(mockView).showToast("Verify your email before you login");
        verify(mockView, never()).navigateToMainPage();
    }

    @Test
    public void testLoginWithIncorrectPassword() {
        String email = "email@example.com";
        String password = "wrongpassword";

        doAnswer(invocation -> {
            OnFailureListener failureListener = invocation.getArgument(3);
            failureListener.onFailure(new Exception("The password is invalid or the user does not have a password."));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showToast("Incorrect password. Please try again.");
    }

    @Test
    public void testLoginWithNonexistentEmail() {
        String email = "nonexistent@example.com";
        String password = "password";

        doAnswer(invocation -> {
            OnFailureListener failureListener = invocation.getArgument(3);
            failureListener.onFailure(new Exception("There is no user record corresponding to this identifier."));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showToast("No account found with this email.");
    }

    @Test
    public void testLoginWithServerError() {
        String email = "email@example.com";
        String password = "password";

        doAnswer(invocation -> {
            OnFailureListener failureListener = invocation.getArgument(3);
            failureListener.onFailure(new Exception("A server error occurred. Please try again later."));
            return null;
        }).when(mockModel).login(eq(email), eq(password), any(), any());

        presenter.onLoginButtonClicked(email, password);

        verify(mockView).showToast("A server error occurred. Please try again later.");
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
}



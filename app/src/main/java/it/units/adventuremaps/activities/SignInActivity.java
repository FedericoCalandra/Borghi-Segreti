package it.units.adventuremaps.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "FIREBASE AUTH";
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                            Collections.singletonList(new AuthUI.IdpConfig.EmailBuilder().build())
                    )
                    .setIsSmartLockEnabled(false)
                    .build();
            signInLauncher.launch(signInIntent);
        } else {
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
        }
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d(TAG, "User authentication: " + user);
        } else {
            if (response == null) {
                Log.d(TAG, "User canceled authentication");
            } else{
                Log.d(TAG, "Error: " +
                        Objects.requireNonNull(response.getError())
                        .getLocalizedMessage());
            }
        }
    }

}

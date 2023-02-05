package it.units.adventuremaps.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import it.units.adventuremaps.R;

public class ActivityFirst extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_first);

        Button logInBtn = findViewById(R.id.logInButton);
        Button signInBtn = findViewById(R.id.signInButton);

        logInBtn.setOnClickListener(logInBtnClickListener);
        signInBtn.setOnClickListener(logInBtnClickListener);
    }

    private final OnClickListener logInBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent logInIntent = new Intent(getBaseContext(), SignInActivity.class);
            startActivity(logInIntent);
        }
    };
}

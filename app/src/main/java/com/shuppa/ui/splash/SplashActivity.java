package com.shuppa.ui.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.shuppa.MainActivity;
import com.shuppa.R;
import com.shuppa.data.model.User;
import com.shuppa.ui.auth.AuthChooser;
import com.shuppa.utils.Globals;
import com.shuppa.utils.Vars;

import static com.shuppa.utils.Globals.SPLASH_TIMEOUT;

public class SplashActivity extends AppCompatActivity {
    private Vars vars;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        vars = new Vars(this);

        if (vars.isLoggedIn()) {
            userUid = vars.verityApp.mAuth.getCurrentUser().getUid();
            saveCurrentUser();
        }

        //generating a unique shopping id
        vars.generateUserShoppingID();

        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_TIMEOUT);
    }

    private void saveCurrentUser() {
        vars.verityApp.db.collection(Globals.USERS)
                .document(userUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            vars.user.setPhone(user.getPhone());
                            vars.user.setEmail(user.getEmail());
                            vars.user.setName(user.getName());
                            vars.user.setAddress(user.getAddress());
                            vars.user.setImage(user.getImage());
                        }
                    }
                });
    }
}
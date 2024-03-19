package com.anasfarooq.i210813;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Firebase persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

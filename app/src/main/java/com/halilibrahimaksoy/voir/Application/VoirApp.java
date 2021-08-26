package com.halilibrahimaksoy.voir.Application;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Halo on 7.9.2015.
 */
public class VoirApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "eUwxt1n5E0WfHyeApQj9uI9dBl6pRREiNkVGA92t", "8Lez3VcIZmW4hKo50qvLrUAsF22yWtAb7G4tPzO6");


    }
}

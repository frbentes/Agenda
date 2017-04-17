package com.frbentes.agendaac;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by frbentes on 17/04/17.
 */
public class AgendaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        setupCalligraphy();
    }

    public void setupCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

}

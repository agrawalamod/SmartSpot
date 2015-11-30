package com.example.agrawalamod.smartspot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.graphics.*;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Chanchal on 11/20/2015.
 */
public class IntroScreen extends AppIntro{
    @Override
    public void init(Bundle bundle) {

        /*// Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(first_fragment);
        addSlide(second_fragment);
        addSlide(third_fragment);
        addSlide(fourth_fragment);*/

        /*setContentView(R.layout.screen_intro);*/

        String title1 = "SMARTSPOT";
        String description1 = "Your personal hotspot manager.";

        String title2 = "Create hotspot.";
        String description2 = "No need to go to setting everytime you need your hotspot." ;

        String title3 ="Save hotspots.";
        String description3 ="Want to use your hotspot at the same location again? We've got you. " ;

        String title4 = "Link account.";
        String description4 = "Link to your google accounts and used saved home/work location.";

        String title5 = "Client details";
        String description5 = "Get  detailed information about the connected devices.";

        String title6 = "Configure.";
        String description6 = "Changes SSID, set data limit, set geofences and more !";

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest
        addSlide(AppIntroFragment.newInstance(title1, description1, R.mipmap.ic_logo, Color.parseColor("#99cc00")));
        addSlide(AppIntroFragment.newInstance(title2, description2, R.mipmap.ic_launcher, Color.parseColor("#cc0052")));
        addSlide(AppIntroFragment.newInstance(title3, description3, R.mipmap.ic_save, Color.parseColor("#00aaff")));
        addSlide(AppIntroFragment.newInstance(title4, description4, R.mipmap.ic_link, Color.parseColor("#ff8000")));
        addSlide(AppIntroFragment.newInstance(title5, description5, R.mipmap.ic_clients, Color.parseColor("#e4e600")));
        addSlide(AppIntroFragment.newInstance(title6, description6, R.mipmap.ic_configure, Color.parseColor("#00b1b3")));

        setFadeAnimation();

        // OPTIONAL METHODS
        // Override bar/separator color
        /*setBarColor(Color.parseColor("#404040"));
        setSeparatorColor(Color.parseColor("#ffffff"));*/

        // Hide Skip/Done button
        showSkipButton(true);
        showDoneButton(true);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {

        System.out.print("Skip pressed!");
        Intent in = new Intent(this, GeofenceActivity.class);
        this.startActivity(in);
    }

    @Override
    public void onDonePressed() {
        onSkipPressed();
    }
}

package com.example.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.vansuita.materialabout.builder.AboutBuilder;
import com.vansuita.materialabout.views.AboutView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        AboutView view = AboutBuilder.with(this)
                .setPhoto(R.mipmap.profile_picture_round)
                .setCover(R.mipmap.profile_cover)
                .setName("Samyak Jain")
                .setSubTitle("Software Developer")
                .setBrief("IIT Kanpur | Coder | Traveller | Ski Enthusiast | Badminton | Table Tennis | MCU Fan")
                .setAppIcon(R.mipmap.ic_launcher)
                .setAppName(R.string.app_name)
//                .addGooglePlayStoreLink("8002078663318221363")
                .addGitHubLink("samyak90")
                .addEmailLink("samyak.iitk@gmail.com")
                .addFacebookLink("samyakjainsamyak")
                .addLinkedInLink("samyak-jain-6576a650")
                .addInstagramLink("samyakjain707")
//                .addWhatsappLink("Samyak", "+919559754566")
//                .addFiveStarsAction()
                .setVersionNameAsAppSubTitle()
//                .addShareAction(R.string.app_name)
                .setWrapScrollView(true)
                .setLinksAnimated(true)
                .setShowAsCard(true)
                .build();

        addContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }
}

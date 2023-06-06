package com.example.maincalendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class Invitelink extends AppCompatActivity {

    private Button btnInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitelink);

        btnInvite = findViewById(R.id.btnInvite);
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAndShareDynamicLink();
            }
        });
    }

    private void generateAndShareDynamicLink() {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setLink(Uri.parse("https://maincalendar.page.link"))
                .setDomainUriPrefix("https://maincalendar.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();
        shareDynamicLink(dynamicLinkUri);
    }

    private void shareDynamicLink(Uri dynamicLinkUri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "초대 메세지");
        intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString());
        startActivity(Intent.createChooser(intent, "초대 링크 공유"));
    }

}

package com.karrit.xoog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class referra extends AppCompatActivity {
    shared share;
    private String TAG="refferal";
    String deepLink;
    boolean referBool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referra);
        share=new shared(this);
        referBool=false;

        account_details account=new account_details(this,share.getCurrent_kid());


        deepLink=account.getReferral_link();
        if(deepLink.equals(getString(R.string.empty))){
            Log.i(TAG,"deeplink is empty");
            getRefferalLink();
            account.setReferral_link(deepLink);
            account.apply();
            deepLink=account.getReferral_link();
        }else {
            referBool=true;
        }
        ImageView close=findViewById(R.id.back);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView share=findViewById(R.id.button);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(referBool) {
                    String msg = "Hi ! I keep myself active and healthy by playing and learning many exercises and skills. I earn points and buy amazing gifts with it . Join XOOG with my referral link and earn 1000 points:"
                            + deepLink;
                    String msgHtml = String.format("<p>Hi ! I keep myself active and healthy by playing and learning many exercises and skills. I earn points and buy amazing gifts with it . Join XOOG with "
                            + "<a href=\"%s\">my referral link</a> and earn 1500 points</p>", deepLink);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    //   intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Xoog App");
                    intent.putExtra(Intent.EXTRA_TEXT, msg);
                    intent.putExtra(Intent.EXTRA_HTML_TEXT, msgHtml);
                    startActivity(Intent.createChooser(intent, "Share via"));
                }else {
                    Toast.makeText(referra.this,"Error ! Try Again Later",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void getRefferalLink(){
        String link = "https://xoog.info/?invitedby=" + share.getCurrent_kid();
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://x00g.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder("com.karrit.xoog")
                                .build())
                .buildShortDynamicLink()
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        Uri mInvitationUrl = shortDynamicLink.getShortLink();
                        Log.i(TAG,mInvitationUrl.toString());
                        deepLink=mInvitationUrl.toString();
                        referBool=true;
                        // ...
                    }
                });

    }

}

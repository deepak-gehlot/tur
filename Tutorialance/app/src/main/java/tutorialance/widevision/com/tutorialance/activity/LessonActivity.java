package tutorialance.widevision.com.tutorialance.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tutorialance.widevision.com.tutorialance.R;
import tutorialance.widevision.com.tutorialance.util.PreferenceConnector;

/**
 * Created by mercury-five on 11/01/16.
 */
public class LessonActivity extends Activity {

    TextView mTv;
    MediaPlayer mp;
    int tag =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.lesson_activity);
        mTv = (TextView) findViewById(R.id.lesson);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            String lessonDescription = b.getString("lessonDescription");
            String lessonName = b.getString("lessonName");
            setTitleLayout(lessonName);
            mTv.setText(Html.fromHtml(lessonDescription));
        }
    }

    public void clickSound() {
        if (mp == null) {
            mp = MediaPlayer.create(LessonActivity.this, R.raw.click);
        }
        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();
        }
        try {
            if (PreferenceConnector.readBoolean(LessonActivity.this, PreferenceConnector.SOUND_EFFECT, false)) {
                mp.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    void setTitleLayout(String title) {
        TextView titleTxt = (TextView) findViewById(R.id.text_title);
        titleTxt.setText(getResources().getString(R.string.app_name));
        titleTxt.setText(title);
        ImageView menuBtn = (ImageView) findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound();
                finish();
            }
        });
        menuBtn.setImageResource(R.drawable.back);
        menuBtn.setVisibility(View.VISIBLE);
    }
}

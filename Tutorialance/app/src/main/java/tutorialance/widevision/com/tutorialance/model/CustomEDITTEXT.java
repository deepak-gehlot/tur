package tutorialance.widevision.com.tutorialance.model;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by newtrainee on 2/2/15.
 */
public class CustomEDITTEXT extends EditText {


    public CustomEDITTEXT(Context context) {
        super(context);

    }

    public CustomEDITTEXT(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public CustomEDITTEXT(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface typeface = Typeface.createFromAsset(getResources().getAssets(), "/font/ProximaNova-Regular.otf");
        this.setTypeface(typeface);
    }

}

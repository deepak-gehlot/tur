package tutorialance.widevision.com.tutorialance.util;

import android.os.Environment;
import android.os.Handler;

/**
 * Created by mercury-five on 28/12/15.
 */
public class Constant {
    public static final String URL = "http://192.168.0.115:8888/joomla/index.php?option=com_tutorialance&task=getdata";
    public static final String directoryPath = Environment.getExternalStorageDirectory() + "/Tutorialance";
    public static final String FILE_NAME = "datafile";
    public static final String CHECK_UPDATE_URL = "http://192.168.0.115:8888/joomla/index.php?option=com_tutorialance&task=checkupdates";
    public static boolean buttonEnable = true;
    public static int mButtonTime = 600;

    public static String TAGFRAGMENT = "tag_fragment";

    public static void setButtonEnable() {
        buttonEnable = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonEnable = true;
            }
        }, mButtonTime);
    }

}

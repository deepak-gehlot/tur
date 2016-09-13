package tutorialance.widevision.com.tutorialance.FileUtil;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import tutorialance.widevision.com.tutorialance.util.Constant;

public class File_Reading_Writing {

    public static boolean File_Reading(String filename) {
        File direct = new File(Constant.directoryPath);
        if (!direct.exists()) {
            direct.mkdir();

        }

        File filepath = new File(Constant.directoryPath + filename);

        if (filepath.exists()) {
            return true;
        } else {
            return false;
        }

    }

    public static StringBuilder File_Reading_with_response(String filename) {

        StringBuilder offlinetext = new StringBuilder();

        File direct = new File(Constant.directoryPath);
        if (!direct.exists()) {
            direct.mkdir();

        }

        File filepath = new File(Constant.directoryPath + filename);

        if (filepath.exists()) {

            try {
                BufferedReader br = new BufferedReader(new FileReader(filepath), 8192);
                String line;

                while ((line = br.readLine()) != null) {
                    offlinetext.append(line);
//					offlinetext.append('n');
                }
                br.close();
            } catch (Exception e) {
            }

        } else {
            Log.v("File Doesn't Exists!!!", ">>>>>>>>>");
        }

        return offlinetext;

    }

    public static boolean File_Writing(String filename, String str) {
        File direct = new File(Constant.directoryPath);
        if (!direct.exists()) {
            direct.mkdir();

        }

        File filepath = new File(Constant.directoryPath + filename);


        try {
            filepath.createNewFile();
            FileOutputStream fout = new FileOutputStream(filepath);
            OutputStreamWriter fouts = new OutputStreamWriter(fout);
            fouts.write(str);
            fouts.flush();
            fouts.close();

            return true;
        } catch (Exception e2) {

            e2.printStackTrace();
            return false;

        }
    }

    public static void downloadFileFromServer(String fileURL, String fileName) {

        StatFs stat_fs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double avail_sd_space = (double) stat_fs.getAvailableBlocks() * (double) stat_fs.getBlockSize();

        double MB_Available = (avail_sd_space / 10485783);

        Log.d("MB", "" + MB_Available);
        try {

            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            int fileSize = c.getContentLength() / 1048576;

            if (MB_Available <= fileSize) {
                c.disconnect();
                return;
            }
            File f1 = new File(Constant.directoryPath);
            if (!f1.exists()) {
                f1.mkdir();
            }
            File f2 = new File(Constant.directoryPath + "/" + fileName);
            if (!f2.exists()) {
                f2.createNewFile();
            }

            try {
                FileOutputStream f = new FileOutputStream(new File(Constant.directoryPath, fileName));
                InputStream in = c.getInputStream();
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                f.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void deleteFile(String filename) {
        File file = new File(filename);

        if (file.exists()) {
            file.delete();
        }

    }

}

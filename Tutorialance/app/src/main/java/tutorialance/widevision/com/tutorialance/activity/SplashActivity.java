package tutorialance.widevision.com.tutorialance.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

import tutorialance.widevision.com.tutorialance.Database.DbHelper;
import tutorialance.widevision.com.tutorialance.FileUtil.EncryptDecryptFile;
import tutorialance.widevision.com.tutorialance.FileUtil.File_Reading_Writing;
import tutorialance.widevision.com.tutorialance.Listener.MyListener;
import tutorialance.widevision.com.tutorialance.R;
import tutorialance.widevision.com.tutorialance.SweetAlert.SweetAlertDialog;
import tutorialance.widevision.com.tutorialance.util.Constant;
import tutorialance.widevision.com.tutorialance.util.Extension;
import tutorialance.widevision.com.tutorialance.util.GifView;
import tutorialance.widevision.com.tutorialance.util.PreferenceConnector;
import tutorialance.widevision.com.tutorialance.util.ValidationTemplate;
import tutorialance.widevision.com.tutorialance.webservices.GsonClass;
import tutorialance.widevision.com.tutorialance.webservices.JsonParser;

public class SplashActivity extends Activity {
    private final static int MSG_CONTINUE = 1234;
    private final static long DELAY = 2000;
    private SweetAlertDialog pDialog;
    private DbHelper dbHelper;
    private Extension extension;
    private int UPDATE_TAG = 0;
    String fileUrl = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_activity);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading...");
        pDialog.show();
        extension = new Extension();
        dbHelper = DbHelper.getInstance();

        if (PreferenceConnector.readString(SplashActivity.this, PreferenceConnector.IS_FRIST, "0").equals("0")) {
            //  forTest();
            if (extension.executeStrategy(SplashActivity.this, "", ValidationTemplate.INTERNET)) {
                callUrl();
            } else {
                pDialog.dismiss();
                new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("No Internet connection!")
                        .show();
            }
        } else {
            if (extension.executeStrategy(SplashActivity.this, "", ValidationTemplate.INTERNET)) {
                checkUpdate();
            } else {
                pDialog.dismiss();
                new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("No Internet connection, Please Connect your device to internet.")
                        .show();
            }
        }
    }


    void callUrl() {
        JsonParser.asyncQuery(Constant.URL, new MyListener() {
            @Override
            public void onResult(String json, Exception e) {
                if (e == null) {
                    GsonClass.Update agents = null;
                    try {
                        Gson gson = new GsonBuilder().create();
                        agents = gson.fromJson(json, GsonClass.Update.class);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    if (agents.success.equals("1")) {
                        fileUrl = agents.message;
                        if (!fileUrl.startsWith("http://")) {
                            fileUrl = "http://" + fileUrl;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    new FileDownloadingTask(fileUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                } else {
                                    new FileDownloadingTask(fileUrl).execute();
                                }
                            }
                        });
                    }
                } else {
                    pDialog.dismiss();
                    new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!")
                            .show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        mHandler.removeMessages(MSG_CONTINUE);
        super.onDestroy();
    }

    private void _continue() {
        pDialog.dismiss();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CONTINUE:
                    _continue();
                    break;
            }
        }
    };


    class FileDownloadingTask extends AsyncTask<Void, Void, Void> {

        String fileUrl = "";

        FileDownloadingTask(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File_Reading_Writing.downloadFileFromServer(fileUrl, Constant.FILE_NAME);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new FileReadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new FileReadingTask().execute();
            }
        }
    }

    class FileReadingTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String json;
            try {
                json = EncryptDecryptFile.readFile(Constant.directoryPath + "/" + Constant.FILE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
                json = "";
            }
            return json;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            if (!aVoid.equals("")) {
                if (UPDATE_TAG == 0) {
                    forTest(aVoid);
                } else {
                    updateDB(aVoid);
                }
            }

            JsonParser.asyncQuery(Constant.CHECK_UPDATE_URL, new MyListener() {
                @Override
                public void onResult(String json, Exception e) {
                    if (e == null) {
                        if (!json.equals("")) {
                            GsonClass.Update agents = null;
                            try {
                                Gson gson = new GsonBuilder().create();
                                agents = gson.fromJson(json, GsonClass.Update.class);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                            if (agents.success.equals("1")) {
                                String latest = agents.latest;
                                PreferenceConnector.writeString(SplashActivity.this, PreferenceConnector.TIMESTAMP, latest);
                            }
                        }
                    }
                }
            });

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new EncryptTask(aVoid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new EncryptTask(aVoid).execute();
            }*/
        }
    }

    void checkUpdate() {
        JsonParser.asyncQuery(Constant.CHECK_UPDATE_URL, new MyListener() {
                    @Override
                    public void onResult(String json, Exception e) {
                        if (e == null) {
                            if (!json.equals("")) {
                                GsonClass.Update agents = null;
                                try {
                                    Gson gson = new GsonBuilder().create();
                                    agents = gson.fromJson(json, GsonClass.Update.class);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                                if (agents.success.equals("1")) {
                                    String latest = agents.latest;
                                    if (!latest.equals(PreferenceConnector.readString(SplashActivity.this, PreferenceConnector.TIMESTAMP, ""))) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                UPDATE_TAG = 1;
                                                callUrl();
                                            }
                                        });
                                        PreferenceConnector.writeString(SplashActivity.this, PreferenceConnector.TIMESTAMP, latest);
                                    } else {
                                        //switch to activity
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                pDialog.dismiss();
                                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                finish();
                                            }
                                        });
                                        PreferenceConnector.writeString(SplashActivity.this, PreferenceConnector.TIMESTAMP, latest);
                                    }
                                } else {
                                    //switch to activity
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pDialog.dismiss();
                                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            finish();
                                        }
                                    });
                                }
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pDialog.dismiss();
                                    new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText("Something went wrong!")
                                            .show();
                                }
                            });

                           /* startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();*/
                        }
                    }
                }
        );
    }
/*

    class EncryptTask extends AsyncTask<Void, String, String> {

        String jsonData;

        EncryptTask(String encryptedData) {
            this.jsonData = encryptedData;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String a;
            try {
                a = EncryptDecryptFile.encrypt(jsonData);
            } catch (Exception e) {
                e.printStackTrace();
                a = "";
            }
            return a;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if (!aVoid.trim().equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new WriteFileTask(aVoid).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    new WriteFileTask(aVoid).execute();
                }
            } else {
                pDialog.dismiss();
                new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        }
    }*/

  /*  class WriteFileTask extends AsyncTask<Void, Boolean, Boolean> {
        String encryptedData;

        WriteFileTask(String encryptedData) {
            this.encryptedData = encryptedData;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            boolean b = EncryptDecryptFile.saveEncryptedFile(encryptedData, Constant.FILE_NAME);
            return b;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (aVoid) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.slide_out_right, android.R.anim.slide_in_left);
                finish();
            } else {
                new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Something went wrong!")
                        .show();
            }
        }
    }*/

    /*class decryptFileTask extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String s = EncryptDecryptFile.decodeEncryptedFile(Constant.directoryPath + "/dbfile");
            return s;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            if (!aVoid.trim().equals("")) {
                GsonClass gson = getGsonFromJson(aVoid);
                if (gson.success.equals("1")) {
                    new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Done")
                            .setContentText("Done")
                            .show();
                    *//*if (gson.message != null && gson.message.size() != 0) {
                        int messageSize = gson.message.size();
                        for (int i = 0; i < messageSize; i++) {
                            //for insert Lesson in table.
                            dbHelper.insertLesson(gson.message.get(i).lessonId, gson.message.get(i).lessonTitle, gson.message.get(i).lessonDescription, 0f);
                            if (gson.message.get(0).quizList != null && gson.message.get(0).quizList.size() != 0) {
                                int quizListSize = gson.message.get(i).quizList.size();
                                for (int j = 0; j < quizListSize; j++) {
                                    GsonClass.Message message = gson.message.get(i);
                                    GsonClass.QuizList quizList = gson.message.get(i).quizList.get(j);
                                    if (gson.message.get(0).quizList.get(0).quistionList != null && gson.message.get(0).quizList.get(0).quistionList.size() != 0) {
                                        int questionListSize = gson.message.get(i).quizList.get(j).quistionList.size();
                                        int totalTime = questionListSize * 10;
                                        //for insert quiz in table.
                                        dbHelper.insertQuiz(message.lessonId, quizList.quizId, quizList.quizTitle, "" + totalTime, 0f);
                                        for (int k = 0; k < questionListSize; k++) {
                                            GsonClass.QauistionsList qauistionsList = gson.message.get(i).quizList.get(j).quistionList.get(k);
                                            //for insert questions in table.
                                            dbHelper.insertQuestion(quizList.quizId, qauistionsList.questionId, qauistionsList.questionTitle, qauistionsList.choice_a, qauistionsList.choice_b, qauistionsList.choice_c, qauistionsList.choice_d, qauistionsList.choice_correct, qauistionsList.time, "timeStamp");
                                        }
                                    }
                                }
                            }
                        }
                    }*//*
                } else {
                    new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Something went wrong!")
                            .show();
                }
            }
        }
    }*/

    private GsonClass.Message[] getGsonFromJson(String jsonResponse) {
        GsonClass.Message agents[] = null;
        try {
            Gson gson = new GsonBuilder().create();
            agents = gson.fromJson(jsonResponse, GsonClass.Message[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return agents;
    }

    void forTest(String json) {

        GsonClass.Message message[] = getGsonFromJson(json);

        PreferenceConnector.writeString(SplashActivity.this, PreferenceConnector.IS_FRIST, "1");
           /* if (gson.message != null && gson.message.size() != 0) {
                if (gson.message.get(0).quizList != null && gson.message.get(0).quizList.size() != 0) {
                    if (gson.message.get(0).quizList.get(0).quistionList != null && gson.message.get(0).quizList.get(0).quistionList.size() != 0) {

                    }
                }
            }*/
        if (message != null && message.length != 0) {
            int messageSize = message.length;
            for (int i = 0; i < messageSize; i++) {

                //for insert Lesson in table.
                dbHelper.insertLesson(message[i].lessonId, message[i].lessonTitle, message[i].lessonDescription, 0f);

                if (message[i].quizList != null && message[i].quizList.size() != 0) {
                    int quizListSize = message[i].quizList.size();
                    for (int j = 0; j < quizListSize; j++) {

                        GsonClass.Message lesson = message[i];
                        GsonClass.QuizList quizList = message[i].quizList.get(j);
                        if (message[i].quizList.get(j).quistionList != null && message[i].quizList.get(j).quistionList.size() != 0) {

                            int questionListSize = message[i].quizList.get(j).quistionList.size();
                            int totalTime = questionListSize * 10;
                            //for insert quiz in table.
                            dbHelper.insertQuiz(lesson.lessonId, quizList.quizId, quizList.quizTitle, "" + totalTime, 0f);

                            for (int k = 0; k < questionListSize; k++) {

                                GsonClass.QauistionsList qauistionsList = message[i].quizList.get(j).quistionList.get(k);

                                //for insert questions in table.
                                dbHelper.insertQuestion(lesson.lessonId, quizList.quizId, qauistionsList.questionId, qauistionsList.questionTitle, qauistionsList.choice_a, qauistionsList.choice_b, qauistionsList.choice_c, qauistionsList.choice_d, qauistionsList.choice_correct, qauistionsList.time, qauistionsList.timestamp);
                            }
                        }
                    }
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();
                File f = new File(Constant.directoryPath + "/" + Constant.FILE_NAME);
                if (f.exists()) {
                    f.delete();
                }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    private void updateDB(String json) {
        GsonClass.Message[] message = getGsonFromJson(json);
        if (message != null && message.length != 0) {
            int messageSize = message.length;
            for (int i = 0; i < messageSize; i++) {
                if (dbHelper.isLessonExist(message[i].lessonId)) {
                    //lesson id exist
                    if (message[i].quizList != null && message[i].quizList.size() != 0) {
                        int quizListSize = message[i].quizList.size();
                        for (int j = 0; j < quizListSize; j++) {
                            GsonClass.Message lesson = message[i];
                            GsonClass.QuizList quizList = message[i].quizList.get(j);
                            if (message[i].quizList.get(j).quistionList != null && message[i].quizList.get(j).quistionList.size() != 0) {

                                int questionListSize = message[i].quizList.get(j).quistionList.size();
                                int totalTime = questionListSize * 10;

                                if (dbHelper.isQuizExist(lesson.lessonId, quizList.quizId)) {
                                    //quiz id exist
                                    for (int k = 0; k < questionListSize; k++) {
                                        GsonClass.QauistionsList qauistionsList = message[i].quizList.get(j).quistionList.get(k);
                                        if (dbHelper.isQuestionExist(lesson.lessonId, quizList.quizId, qauistionsList.questionId)) {
                                            //question exist check for time stamp.
                                            if (!dbHelper.isQuestionTimeStampMatch(lesson.lessonId, quizList.quizId, qauistionsList.questionId, qauistionsList.timestamp)) {
                                                // timeStamp not match update question.
                                                dbHelper.updateQuestion(lesson.lessonId, quizList.quizId, qauistionsList.questionId, qauistionsList.questionTitle, qauistionsList.choice_a, qauistionsList.choice_b, qauistionsList.choice_c, qauistionsList.choice_d, qauistionsList.choice_correct, qauistionsList.time, qauistionsList.timestamp);
                                            } else {
                                                //timeStamp match leave as it is.
                                            }
                                        } else {
                                            //add question
                                            //for insert questions in table.
                                            dbHelper.insertQuestion(lesson.lessonId, quizList.quizId, qauistionsList.questionId, qauistionsList.questionTitle, qauistionsList.choice_a, qauistionsList.choice_b, qauistionsList.choice_c, qauistionsList.choice_d, qauistionsList.choice_correct, qauistionsList.time, qauistionsList.timestamp);
                                        }
                                    }
                                } else {
                                    //add quiz in table
                                    //for insert quiz in table.
                                    dbHelper.insertQuiz(lesson.lessonId, quizList.quizId, quizList.quizTitle, "" + totalTime, 0f);

                                    for (int k = 0; k < questionListSize; k++) {

                                        GsonClass.QauistionsList qauistionsList = message[i].quizList.get(j).quistionList.get(k);
                                        //for insert questions in table.
                                        dbHelper.insertQuestion(lesson.lessonId, quizList.quizId, qauistionsList.questionId, qauistionsList.questionTitle, qauistionsList.choice_a, qauistionsList.choice_b, qauistionsList.choice_c, qauistionsList.choice_d, qauistionsList.choice_correct, qauistionsList.time, qauistionsList.timestamp);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //add lesson in lesson table.
                    //for insert Lesson in table.
                    dbHelper.insertLesson(message[i].lessonId, message[i].lessonTitle, message[i].lessonDescription, 0f);
                    if (message[i].quizList != null && message[i].quizList.size() != 0) {
                        int quizListSize = message[i].quizList.size();
                        for (int j = 0; j < quizListSize; j++) {
                            GsonClass.Message lesson = message[i];
                            GsonClass.QuizList quizList = message[i].quizList.get(j);
                            if (message[i].quizList.get(j).quistionList != null && message[i].quizList.get(j).quistionList.size() != 0) {

                                int questionListSize = message[i].quizList.get(j).quistionList.size();
                                int totalTime = questionListSize * 10;
                                //for insert quiz in table.
                                dbHelper.insertQuiz(lesson.lessonId, quizList.quizId, quizList.quizTitle, "" + totalTime, 0f);

                                for (int k = 0; k < questionListSize; k++) {

                                    GsonClass.QauistionsList qauistionsList = message[i].quizList.get(j).quistionList.get(k);
                                    //for insert questions in table.
                                    dbHelper.insertQuestion(lesson.lessonId, quizList.quizId, qauistionsList.questionId, qauistionsList.questionTitle, qauistionsList.choice_a, qauistionsList.choice_b, qauistionsList.choice_c, qauistionsList.choice_d, qauistionsList.choice_correct, qauistionsList.time, qauistionsList.timestamp);
                                }
                            }
                        }
                    }
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }
}

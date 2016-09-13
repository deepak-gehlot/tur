package tutorialance.widevision.com.tutorialance.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import tutorialance.widevision.com.tutorialance.Bean.AnswerDataBean;
import tutorialance.widevision.com.tutorialance.Database.DbHelper;
import tutorialance.widevision.com.tutorialance.Database.QuestionTable;
import tutorialance.widevision.com.tutorialance.Database.QuizListTable;
import tutorialance.widevision.com.tutorialance.R;
import tutorialance.widevision.com.tutorialance.util.Constant;
import tutorialance.widevision.com.tutorialance.util.PreferenceConnector;

public class QuizLayoutActivity extends Activity {

    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(400);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(400);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private ViewFlipper viewFlipper;
    private CircleProgressView circleView;
    private LinearLayout next, hint, skip, close;
    private String quizId = "", totalTime = "", lessonId = "";
    private DbHelper dbHelper;
    private List<QuestionTable> questionList;
    private List<QuestionTable> qList;
    private View.OnClickListener choiceClick;
    public static ArrayList<AnswerDataBean> answerDataList;
    private int i;
    private boolean isHintUsed = false;
    private CountDownTimer countDownTimer;
    private LinearLayout numberLinear;
    private static int timeCount = 0;
    private int timeCount1 = 0;
    private ArrayList<CircleProgressView> progressViewList;
    private MediaPlayer mp;
    private MediaPlayer mpPage;
    private boolean isAnswerClick = false;
    private int answerPos = -1;
    private ArrayList<CountDownTimer> timerList = new ArrayList<>();
    private ArrayList<Integer> timeCountList = new ArrayList<>();

    void setTitleLayout() {
        TextView titleTxt = (TextView) findViewById(R.id.text_title);
        titleTxt.setText(getResources().getString(R.string.app_name));
        titleTxt.setText("Objective Questions");
    }

    public static int randInt(int min, int max) {
        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.quiz_activity);
        init();
        setTitleLayout();
        answerDataList = new ArrayList<>();
        dbHelper = DbHelper.getInstance();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            quizId = b.getString("quizId");
            totalTime = b.getString("totalTime");
            lessonId = b.getString("lessonId");
        }

        questionList = dbHelper.getQuestion(quizId, lessonId);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        /*ArrayList<String> randumNumberList = new ArrayList<>();
        Iterator iterator = qList.iterator();
        while (iterator.hasNext()) {
            int a = randInt(0, 9);
            if (!randumNumberList.contains("" + a)) {
                Log.e("", "randum number value " + a);
                randumNumberList.add("" + a);
                questionList.add(qList.get(a));
                iterator.next();
            } else {
                Log.e("", "iterator posirion else case ");
            }
        }*/

        int size = questionList.size();
        if (size != 0) {
            progressViewList = new ArrayList<>();
            for (i = 0; i < size; i++) {
                View row = layoutInflater.inflate(R.layout.quiz_row, null);

                TextView textView = new TextView(QuizLayoutActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(15, 0, 0, 0);
                textView.setTextSize(15);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.drawable.selectnotransparent);
                textView.setText("" + (i + 1));
                textView.setLayoutParams(layoutParams);
                textView.setGravity(Gravity.CENTER);
                numberLinear.addView(textView);
                TextView questionTxt = (TextView) row.findViewById(R.id.question);
                TextView txt = (TextView) row.findViewById(R.id.txt);
                TextView A_Txt = (TextView) row.findViewById(R.id.a);
                TextView B_Txt = (TextView) row.findViewById(R.id.b);
                TextView C_Txt = (TextView) row.findViewById(R.id.c);
                TextView D_Txt = (TextView) row.findViewById(R.id.d);
                final LinearLayout A_Layout = (LinearLayout) row.findViewById(R.id.choice_a);
                final LinearLayout B_Layout = (LinearLayout) row.findViewById(R.id.choice_b);
                final LinearLayout C_Layout = (LinearLayout) row.findViewById(R.id.choice_c);
                final LinearLayout D_Layout = (LinearLayout) row.findViewById(R.id.choice_d);
                int a = i + 1;
                txt.setText("" + a + "/" + size);

                A_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickSound();
                        isAnswerClick = true;
                        answerPos = 1;
                        A_Layout.setBackgroundColor(Color.GRAY);
                        B_Layout.setBackgroundColor(Color.WHITE);
                        C_Layout.setBackgroundColor(Color.WHITE);
                        D_Layout.setBackgroundColor(Color.WHITE);
                    }
                });

                B_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickSound();
                        isAnswerClick = true;
                        answerPos = 2;
                        A_Layout.setBackgroundColor(Color.WHITE);
                        B_Layout.setBackgroundColor(Color.GRAY);
                        C_Layout.setBackgroundColor(Color.WHITE);
                        D_Layout.setBackgroundColor(Color.WHITE);
                    }
                });

                C_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickSound();
                        isAnswerClick = true;
                        answerPos = 3;
                        A_Layout.setBackgroundColor(Color.WHITE);
                        B_Layout.setBackgroundColor(Color.WHITE);
                        C_Layout.setBackgroundColor(Color.GRAY);
                        D_Layout.setBackgroundColor(Color.WHITE);
                    }
                });

                D_Layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickSound();
                        isAnswerClick = true;
                        answerPos = 4;
                        A_Layout.setBackgroundColor(Color.WHITE);
                        B_Layout.setBackgroundColor(Color.WHITE);
                        C_Layout.setBackgroundColor(Color.WHITE);
                        D_Layout.setBackgroundColor(Color.GRAY);
                    }
                });

                questionTxt.setText(questionList.get(i).question);
                A_Txt.setText(questionList.get(i).choice_1);
                B_Txt.setText(questionList.get(i).choice_2);
                C_Txt.setText(questionList.get(i).choice_3);
                D_Txt.setText(questionList.get(i).choice_4);

                final CircleProgressView circleView = (CircleProgressView) row.findViewById(R.id.circleView);
                circleView.setMaxValue(Integer.parseInt(questionList.get(i).time));
                timeCountList.add(Integer.parseInt(questionList.get(i).time));
                circleView.setTextMode(TextMode.TEXT);
                circleView.setTextColor(Color.WHITE);
                circleView.setTextSize(30);
                progressViewList.add(circleView);
                createTimer(Integer.parseInt(questionList.get(i).time));

                viewFlipper.addView(row, i);
            }
            viewFlipper.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    if (!isAnswerClick) {
                        int z = viewFlipper.getDisplayedChild();
                        progressViewList.get(z).setText("" + timeCount);
                        progressViewList.get(z).setValue(timeCount);
                        numberLinear.getChildAt(z).setBackgroundResource(R.drawable.selectno);
                        if (z > 0) {
                            numberLinear.getChildAt(z - 1).setBackgroundResource(R.drawable.selectnotransparent);
                        }
                    }
                }
            });
            timeCount = timeCountList.get(0);
            timerList.get(0).start();
        } else {
            Toast.makeText(QuizLayoutActivity.this, "No question in this quiz", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void createTimer(int time) {

        countDownTimer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long l) {
                int z = viewFlipper.getDisplayedChild();
                timeCount1 = timeCount1 + 1;
                timeCount = timeCount - 1;
                progressViewList.get(z).setText("" + timeCount);
                progressViewList.get(z).setValue(timeCount);

                /*if (timeCount == Integer.parseInt(questionList.get(z).time)) {
                    countDownTimer.onFinish();
                }*/
            }

            @Override
            public void onFinish() {
                int z = viewFlipper.getDisplayedChild();
                timeCount1 = timeCount1 + 1;
                timeCount = timeCount - 1;
                progressViewList.get(z).setText("" + timeCount);
                progressViewList.get(z).setValue(timeCount1);
                showNext();
            }
        };
        timerList.add(countDownTimer);
    }

    public void pageFlip() {
        if (mpPage == null) {
            mpPage = MediaPlayer.create(QuizLayoutActivity.this, R.raw.page_flip);
        }
        if (mpPage.isPlaying()) {
            mpPage.stop();
            mpPage.reset();
        }
        try {
            if (PreferenceConnector.readBoolean(QuizLayoutActivity.this, PreferenceConnector.SOUND_EFFECT, false)) {
                mpPage.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void clickSound() {
        if (mp == null) {
            mp = MediaPlayer.create(QuizLayoutActivity.this, R.raw.click);
        }
        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();
        }
        try {
            if (PreferenceConnector.readBoolean(QuizLayoutActivity.this, PreferenceConnector.SOUND_EFFECT, false)) {
                mp.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.next:
                    if (isAnswerClick) {
                        showNext();
                    } else {
                        final Snackbar snackbar = Snackbar.make(view, "Please select an answer first.", Snackbar.LENGTH_SHORT);
                        snackbar.setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.setActionTextColor(getResources().getColor(R.color.orange));
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        snackbar.show();
                    }
                    break;

                case R.id.hint:
                    isHintUsed = true;
                    break;

                case R.id.skip:
                    isAnswerClick = false;
                    showNext();
                    break;

                case R.id.close:
                    clickSound();
                    int z = viewFlipper.getDisplayedChild();
                    timerList.get(z).cancel();
                    finish();
                    break;
            }
        }
    };

    void showNext() {

        pageFlip();
        String choice = "";
        int z1 = viewFlipper.getDisplayedChild();
        timerList.get(z1).cancel();
        if (isAnswerClick) {
            int z = viewFlipper.getDisplayedChild();
            switch (answerPos) {
                case 1:
                    choice = questionList.get(z).choice_1;
                    break;
                case 2:
                    choice = questionList.get(z).choice_2;
                    break;
                case 3:
                    choice = questionList.get(z).choice_3;
                    break;
                case 4:
                    choice = questionList.get(z).choice_4;
                    break;
            }
            AnswerDataBean answerDataBean_a = new AnswerDataBean();
            answerDataBean_a.setQuestion(questionList.get(z).question);
            answerDataBean_a.setAnswer(choice);
            answerDataBean_a.setAnswerCorrect(questionList.get(z).correctAns);
            answerDataBean_a.setHint(isHintUsed);
            answerDataBean_a.setSkip(false);
            answerDataBean_a.setLessonId(lessonId);
            answerDataBean_a.setQuizId(quizId);
            answerDataBean_a.setQuestionId(questionList.get(z).questionId);
            answerDataBean_a.setTotalTime(timeCount1);
            answerDataList.add(answerDataBean_a);
            isHintUsed = false;
            if (z == questionList.size() - 1) {
                Intent i = new Intent(QuizLayoutActivity.this, ResultFragment.class);
                startActivity(i);
                finish();
            } else {
                viewFlipper.setInAnimation(inFromRightAnimation());
                viewFlipper.setOutAnimation(outToLeftAnimation());
                progressViewList.get(z + 1).setValue(0);
                viewFlipper.showNext();
                timeCount = timeCountList.get(z1 + 1);
                timerList.get(z + 1).start();
            }
        } else {
            int i = viewFlipper.getDisplayedChild();
            AnswerDataBean answerDataBean_d = new AnswerDataBean();
            answerDataBean_d.setQuestion(questionList.get(i).question);
            answerDataBean_d.setAnswer("");
            answerDataBean_d.setAnswerCorrect(questionList.get(i).correctAns);
            answerDataBean_d.setHint(isHintUsed);
            answerDataBean_d.setSkip(true);
            answerDataBean_d.setLessonId(lessonId);
            answerDataBean_d.setQuizId(quizId);
            answerDataBean_d.setQuestionId(questionList.get(i).questionId);
            answerDataBean_d.setTotalTime(timeCount1);
            answerDataList.add(answerDataBean_d);
            isHintUsed = false;
            if (i == questionList.size() - 1) {
                Intent intent = new Intent(QuizLayoutActivity.this, ResultFragment.class);
                startActivity(intent);
                finish();
            } else {
                viewFlipper.setInAnimation(inFromRightAnimation());
                viewFlipper.setOutAnimation(outToLeftAnimation());
                progressViewList.get(i + 1).setValue(0);
                viewFlipper.showNext();
                timeCount = timeCountList.get(z1 + 1);
                timerList.get(i + 1).start();

            }
        }
        timeCount1 = 0;
        isAnswerClick = false;
        answerPos = -1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int z = viewFlipper.getDisplayedChild();
        timerList.get(z).cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    void init() {
        circleView = (CircleProgressView) findViewById(R.id.circleView);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFliper);
        next = (LinearLayout) findViewById(R.id.next);
        hint = (LinearLayout) findViewById(R.id.hint);
        skip = (LinearLayout) findViewById(R.id.skip);
        close = (LinearLayout) findViewById(R.id.close);
        numberLinear = (LinearLayout) findViewById(R.id.numberLinear);
        next.setOnClickListener(onClickListener);
        skip.setOnClickListener(onClickListener);
        hint.setOnClickListener(onClickListener);
        close.setOnClickListener(onClickListener);
    }
}

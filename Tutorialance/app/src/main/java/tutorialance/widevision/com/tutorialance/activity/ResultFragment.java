package tutorialance.widevision.com.tutorialance.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tutorialance.widevision.com.tutorialance.Bean.AnswerDataBean;
import tutorialance.widevision.com.tutorialance.Database.DbHelper;
import tutorialance.widevision.com.tutorialance.R;

public class ResultFragment extends Activity {

    private TextView marksTxt, timeTxt, shareTxt;
    private ExpandableListView listView;
    private ListAdapter listAdapter;
    private DbHelper dbHelper;
    int correct = 0;
    void setTitleLayout() {
        TextView titleTxt = (TextView) findViewById(R.id.text_title);
        titleTxt.setText(getResources().getString(R.string.app_name));
        titleTxt.setText("Results");
        ImageView menuBtn = (ImageView) findViewById(R.id.menu);
        menuBtn.setVisibility(View.VISIBLE);
        menuBtn.setImageResource(R.drawable.back);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.result_fragment);
        setTitleLayout();
        marksTxt = (TextView) findViewById(R.id.markTxt);
        timeTxt = (TextView) findViewById(R.id.timeTxt);
        shareTxt = (TextView) findViewById(R.id.shareTxt);
        listView = (ExpandableListView) findViewById(R.id.expandListView);
        listAdapter = new ListAdapter(ResultFragment.this, QuizLayoutActivity.answerDataList);
        listView.setAdapter(listAdapter);

        dbHelper = DbHelper.getInstance();

        int skip = 0;
        int incorrect = 0;
        String quizId = "";
        String lessonId = "";
        int totalNoQuestion = 0;
        int totalTimeTaken = 0;
        totalNoQuestion = QuizLayoutActivity.answerDataList.size();
        lessonId = QuizLayoutActivity.answerDataList.get(0).getLessonId();
        quizId = QuizLayoutActivity.answerDataList.get(0).getQuizId();
        for (int i = 0; i < totalNoQuestion; i++) {
            if (QuizLayoutActivity.answerDataList.get(i).isSkip()) {
                skip = skip + 1;
            } else if (QuizLayoutActivity.answerDataList.get(i).getAnswer().equals(QuizLayoutActivity.answerDataList.get(i).answerCorrect)) {
                correct = correct + 1;
            } else {
                incorrect = incorrect + 1;
            }
            totalTimeTaken = totalTimeTaken + QuizLayoutActivity.answerDataList.get(i).getTotalTime();
        }

        marksTxt.setText("" + correct + "/" + totalNoQuestion);
        timeTxt.setText("" + totalTimeTaken);

        dbHelper.insertGraphData(quizId, lessonId, totalNoQuestion, correct, incorrect, skip);
        dbHelper.updateQuizPercentage(quizId, lessonId, getPercantage(correct, totalNoQuestion));

        shareTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share("Tutorialance Grammar English, I have given test on application and my score is "
                        + correct + " URL");
            }
        });
    }

    public void share(String shareText) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "subject");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(sharingIntent, "Share Using..."));
    }

    private float getPercantage(int score, int total) {
        float per = (float) score / total;
        return per * 100;
    }

    class ListAdapter extends BaseExpandableListAdapter {

        private viewHolder holder;
        private Context context;
        private ArrayList<AnswerDataBean> resultList;
        private int previusPosition = -1;

        ListAdapter(Context context, ArrayList<AnswerDataBean> resultList) {
            this.context = context;
            this.resultList = resultList;
        }

        @Override
        public int getGroupCount() {
            return resultList.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return 1;
        }

        @Override
        public Object getGroup(int i) {
            return resultList.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return null;
        }

        @Override
        public long getGroupId(int i) {
            return 0;
        }

        @Override
        public long getChildId(int i, int i1) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.result_layout_row, viewGroup, false);
            holder = new viewHolder();
            holder.numberTxt = (TextView) view.findViewById(R.id.numberTxt);
            holder.correctImg = (ImageView) view.findViewById(R.id.correct);
            holder.inCorrectImg = (ImageView) view.findViewById(R.id.incorrect);
            holder.skipImg = (ImageView) view.findViewById(R.id.skip);

            holder.numberTxt.setText("" + (i + 1));

            if (resultList.get(i).isSkip()) {
                holder.skipImg.setVisibility(View.VISIBLE);
                holder.correctImg.setVisibility(View.GONE);
                holder.inCorrectImg.setVisibility(View.GONE);
            } else if (resultList.get(i).getAnswer().equals(resultList.get(i).getAnswerCorrect())) {
                holder.skipImg.setVisibility(View.GONE);
                holder.correctImg.setVisibility(View.VISIBLE);
                holder.inCorrectImg.setVisibility(View.GONE);
            } else {
                holder.skipImg.setVisibility(View.GONE);
                holder.correctImg.setVisibility(View.GONE);
                holder.inCorrectImg.setVisibility(View.VISIBLE);
            }

            return view;
        }

        @Override
        public View getChildView(final int i, int i1, boolean b, View view, ViewGroup viewGroup) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.view_result_row, null);
            TextView wrongTxt = (TextView) view.findViewById(R.id.wrong);
            TextView timeTxt = (TextView) view.findViewById(R.id.timeTxt);
            ImageView wrongImg = (ImageView) view.findViewById(R.id.wrongImg);
            TextView rightTxt = (TextView) view.findViewById(R.id.right);
            TextView qTxt = (TextView) view.findViewById(R.id.q);
            ImageView close = (ImageView) view.findViewById(R.id.closeResult);

            qTxt.setText(resultList.get(i).getQuestion());
            if (resultList.get(i).getAnswer().equals(resultList.get(i).getAnswerCorrect())) {
                wrongImg.setVisibility(View.GONE);
                wrongTxt.setVisibility(View.GONE);
            }

            timeTxt.setText("TimeTaken : " + resultList.get(i).getTotalTime() + " Sec.");

            rightTxt.setText(resultList.get(i).getAnswerCorrect());
            wrongTxt.setText(resultList.get(i).getAnswer());

            close.setOnClickListener(new View.OnClickListener()

                                     {
                                         @Override
                                         public void onClick(View view) {
                                             listView.collapseGroup(i);
                                         }
                                     }

            );
            return view;
        }

        /*@Override
        public void onGroupExpanded(int groupPosition) {
            //collapse the old expanded group, if not the same
            //as new group to expand
            if (groupPosition != previusPosition) {
                listView.collapseGroup(previusPosition);
            }
            super.onGroupExpanded(groupPosition);
            previusPosition = groupPosition;
        }*/

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }

        class viewHolder {
            TextView numberTxt;
            ImageView correctImg, inCorrectImg, skipImg;

        }
    }
}

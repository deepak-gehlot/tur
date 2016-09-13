package tutorialance.widevision.com.tutorialance.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import tutorialance.widevision.com.tutorialance.Bean.ColorBean;
import tutorialance.widevision.com.tutorialance.Database.DbHelper;
import tutorialance.widevision.com.tutorialance.Database.QuizListTable;
import tutorialance.widevision.com.tutorialance.Database.TableForGraph;
import tutorialance.widevision.com.tutorialance.R;
import tutorialance.widevision.com.tutorialance.util.Constant;

public class QuizFragment extends Activity {

    private GridView gridview;
    private static final ArrayList<ColorBean> colorArray = new ArrayList<>();
    private DbHelper dbHelper;
    private List<QuizListTable> quizList;
    private String lessonId = "", lessonDescription = "", lessonName = "";
    private GridAdapter gridAdapter;
    private Button beginBtn;
    private String quizId = "";
    private CardView layout;
    private XYMultipleSeriesRenderer multiRenderer;
    private XYMultipleSeriesDataset dataset;
    private XYSeries scoreSeries, attemtSeries, skippedSeries;
    private float chartTextSize;
    private GraphicalView chart;
    int height = 0;
    int tag = 0;
    int deviceHeight = 0;
    int selectedPos = 0;
    private RelativeLayout lessonRow;

    void setTitleLayout(String title) {
        TextView titleTxt = (TextView) findViewById(R.id.text_title);
        titleTxt.setText(getResources().getString(R.string.app_name));
        titleTxt.setText(title);
        ImageView menuBtn = (ImageView) findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        menuBtn.setImageResource(R.drawable.back);
        menuBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.quiz_list_fragment);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        deviceHeight = displayMetrics.heightPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (tag == 1) {
            overridePendingTransition(R.anim.push_infromright_anim, R.anim.push_out_to_left_anim);
        }

        initColorArray();

        BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(R.drawable.lessonicon);
        height = bd.getBitmap().getHeight();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        chartTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, metrics);

        dbHelper = DbHelper.getInstance();
        Bundle b = getIntent().getExtras();
        if (b != null) {
            lessonId = b.getString("lessonId");
            lessonDescription = b.getString("lessonDescription");
            lessonName = b.getString("lessonName");
            quizList = dbHelper.getQuiz(lessonId);

        }
        setTitleLayout(lessonName);
        gridview = (GridView) findViewById(R.id.gridview);
        beginBtn = (Button) findViewById(R.id.beginBtn);
        layout = (CardView) findViewById(R.id.graph_card_view);
        lessonRow = (RelativeLayout) findViewById(R.id.lesson);
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, deviceHeight / 2);
        ll.setLayoutParams(layoutParams);

        if (quizList != null && quizList.size() != 0) {
            gridAdapter = new GridAdapter(QuizFragment.this, quizList);
            gridview.setAdapter(gridAdapter);
            setGridViewHeightBasedOnChildren(gridview);
        }

        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!quizId.trim().equals("")) {
                    Intent intent = new Intent(QuizFragment.this, QuizLayoutActivity.class);
                    intent.putExtra("quizId", quizId);
                    intent.putExtra("lessonId", lessonId);
                    intent.putExtra("totalTime", "0");
                    startActivity(intent);
                    tag = 1;

                } else {
                    final Snackbar snackbar = Snackbar.make(view, "Please select quiz first.", Snackbar.LENGTH_SHORT);
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
            }
        });

        lessonRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizId = "";
                Intent i = new Intent(QuizFragment.this, LessonActivity.class);
                i.putExtra("lessonDescription", lessonDescription);
                i.putExtra("lessonName", lessonName);
                startActivity(i);
                tag = 1;
            }
        });


        try {
            if (quizId == null || quizId.equals("")) {
                quizId = quizList.get(0).quizId;
            }
        } catch (Exception e) {
            quizId = quizList.get(0).quizId;
        }
        if (quizId != null && !quizId.equals("") && lessonId != null && !lessonId.equals("")) {
            showGraph(quizId, lessonId);
        } else {
            Toast.makeText(QuizFragment.this, "No Quiz in this lesson.", Toast.LENGTH_SHORT).show();
        }
    }

    void showGraph(String quizId, String lessonId) {
        List<TableForGraph> list = dbHelper.getGraphData(quizId, lessonId);
        list.add(0, new TableForGraph());
        openChart(list);
    }

    private static void setGridViewHeightBasedOnChildren(GridView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            if ((i+1) % 2 == 0){

            }else{
                totalHeight += listItem.getMeasuredHeight();
            }

        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();

            params.height = (totalHeight + (listView.getVerticalSpacing() * (listAdapter.getCount() - 1)));

        listView.setLayoutParams(params);
    }

    void initColorArray() {
        ColorBean bean1 = new ColorBean();
        bean1.setBgColor(getResources().getColor(R.color.prev));
        bean1.setCircleColor(Color.BLACK);
        ColorBean bean2 = new ColorBean();
        bean2.setBgColor(getResources().getColor(R.color.hint));
        bean2.setCircleColor(Color.BLACK);
        ColorBean bean3 = new ColorBean();
        bean3.setBgColor(getResources().getColor(R.color.skip));
        bean3.setCircleColor(Color.BLACK);
        ColorBean bean4 = new ColorBean();
        bean4.setBgColor(getResources().getColor(R.color.close));
        bean4.setCircleColor(Color.BLACK);
        ColorBean bean5 = new ColorBean();
        bean5.setBgColor(getResources().getColor(R.color.red));
        bean5.setCircleColor(Color.BLACK);
        ColorBean bean6 = new ColorBean();
        bean6.setBgColor(getResources().getColor(R.color.orange));
        bean6.setCircleColor(Color.BLACK);
        colorArray.add(bean1);
        colorArray.add(bean2);
        colorArray.add(bean3);
        colorArray.add(bean4);
        colorArray.add(bean5);
        colorArray.add(bean6);
    }

    private class GridAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        private ViewHolder viewHolder;
        private List<QuizListTable> list;
        private RelativeLayout.LayoutParams layoutParams;

        GridAdapter(Context context, List<QuizListTable> list) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
            this.list = list;
            layoutParams = new RelativeLayout.LayoutParams(height, height + 5);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            int colorPos = position;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.quiz_grid_row, viewGroup, false);
                viewHolder = new ViewHolder();
                viewHolder.mTextView = (TextView) view.findViewById(R.id.text);
                viewHolder.mImageView = (ImageView) view.findViewById(R.id.icon);
                viewHolder.progressView = (CircleProgressView) view.findViewById(R.id.circleView);
                viewHolder.progressView.setLayoutParams(layoutParams);

                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

           /* if (position == 0) {
                viewHolder.mImageView.setImageResource(R.drawable.lessonicon);
                viewHolder.mTextView.setText("Lesson");
                viewHolder.progressView.setVisibility(View.GONE);
                viewHolder.mImageView.setVisibility(View.VISIBLE);
            } else {*/
            viewHolder.mTextView.setText("Quiz - " + (position+1));
            viewHolder.mImageView.setVisibility(View.GONE);
            viewHolder.progressView.setVisibility(View.VISIBLE);
            viewHolder.progressView.setValueAnimated(list.get(position).percentage);
          /*  }*/

            if (colorPos > 3) {
                colorPos = checkPos(colorPos);
            }
            view.setBackgroundColor(colorArray.get(colorPos).getBgColor());
            viewHolder.progressView.setFillCircleColor(colorArray.get(colorPos).getCircleColor());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* if (position == 0) {
                        quizId = "";
                        Intent i = new Intent(QuizFragment.this, LessonActivity.class);
                        i.putExtra("lessonDescription", lessonDescription);
                        i.putExtra("lessonName", lessonName);
                        startActivity(i);
                        tag = 1;
                    } else {*/
                    selectedPos = position;
                    quizId = list.get(position).quizId;
                    showGraph(quizId, lessonId);
                    notifyDataSetChanged();
                   /* }*/
                }
            });
            if (selectedPos == position) {
                view.setBackgroundColor(getResources().getColor(R.color.track_color2));
            }
            return view;
        }

        public int checkPos(int pos) {
            pos = pos - 4;
            if (pos > 3) {
                checkPos(pos);
            }
            return pos;
        }

        class ViewHolder {
            private TextView mTextView;
            private ImageView mImageView;
            private CircleProgressView progressView;
        }
    }


    @SuppressLint("ResourceAsColor")
    private void openChart(List<TableForGraph> list) {

        attemtSeries = new XYSeries(" Incorrect ");
        scoreSeries = new XYSeries(" Correct ");
        skippedSeries = new XYSeries(" Skipped ");
        dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(attemtSeries);

        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.RED);
        incomeRenderer.setPointStyle(PointStyle.CIRCLE);
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2.8f);
        incomeRenderer.setDisplayChartValues(false);
        dataset.addSeries(scoreSeries);

        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.GREEN);
        expenseRenderer.setPointStyle(PointStyle.CIRCLE);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2);
        expenseRenderer.setDisplayChartValues(false);

        dataset.addSeries(skippedSeries);

        XYSeriesRenderer skippedSeriesRenderer = new XYSeriesRenderer();
        skippedSeriesRenderer.setColor(Color.BLUE);
        skippedSeriesRenderer.setPointStyle(PointStyle.CIRCLE);
        skippedSeriesRenderer.setFillPoints(true);
        skippedSeriesRenderer.setLineWidth(1.5f);
        skippedSeriesRenderer.setDisplayChartValues(false);


        multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(10);
        multiRenderer.setYLabels(10);//
        multiRenderer.setLegendTextSize(chartTextSize);
        multiRenderer.setXTitle("Attempts\n\n");
        multiRenderer.setYTitle("Scores");

        multiRenderer.setAxisTitleTextSize(chartTextSize);

        multiRenderer.setChartTitleTextSize(chartTextSize);
        multiRenderer.setShowGrid(true);
        multiRenderer.setGridColor(Color.GRAY);
        multiRenderer.setZoomEnabled(false, false);
        multiRenderer.setPanEnabled(true, true);

        multiRenderer.setXAxisMax(10);
        multiRenderer.setYAxisMax(10);

        multiRenderer.setYLabelsPadding(8);
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});
        multiRenderer.setPanLimits(new double[]{-1, list.size() + 3, -1, 50});
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                scoreSeries.add(i, 0);
                attemtSeries.add(i, 0);
                skippedSeries.add(i, 0);
            } else {
                scoreSeries.add(i, list.get(i).correct);
                attemtSeries.add(i, list.get(i).inCorrect);
                skippedSeries.add(i, list.get(i).skip);
            }
        }

        multiRenderer.setLabelsColor(Color.BLACK);
        multiRenderer.setXLabelsColor(Color.BLACK);
        multiRenderer.setYLabelsColor(0, Color.BLACK);
        // for change background multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setBackgroundColor(Color.WHITE);
        multiRenderer.setMarginsColor(Color.WHITE);

        // for zoom buttons
        multiRenderer.setZoomButtonsVisible(false);
        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);
        multiRenderer.addSeriesRenderer(skippedSeriesRenderer);
        multiRenderer.setChartTitle("Performance Graph for Quiz - " + (selectedPos+1));
        multiRenderer.setLabelsColor(getResources().getColor(R.color.orange));
        multiRenderer.setGridColor(getResources().getColor(R.color.track_color));
        multiRenderer.setInScroll(true);

        if (chart != null) {
            layout.removeView(chart);
        }
        chart = ChartFactory.getLineChartView(QuizFragment.this, dataset, multiRenderer);
        layout.addView(chart);
    }
}

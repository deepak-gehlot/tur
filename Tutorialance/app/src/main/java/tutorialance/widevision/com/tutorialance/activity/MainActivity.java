package tutorialance.widevision.com.tutorialance.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tutorialance.widevision.com.tutorialance.Database.DbHelper;
import tutorialance.widevision.com.tutorialance.Database.LessonListTable;
import tutorialance.widevision.com.tutorialance.Database.TableForGraph;
import tutorialance.widevision.com.tutorialance.R;
import tutorialance.widevision.com.tutorialance.SweetAlert.SweetAlertDialog;
import tutorialance.widevision.com.tutorialance.model.DividerItemDecoration;
import tutorialance.widevision.com.tutorialance.util.Constant;
import tutorialance.widevision.com.tutorialance.util.PreferenceConnector;

public class MainActivity extends Activity {

    SweetAlertDialog pDialog;
    private RecyclerView recyclerView;
    private static int[] colorArray = {R.color.green, R.color.hint, R.color.skip, R.color.close, R.color.red, R.color.orange};
    private DbHelper dbHelper;
    private static ArrayList<Integer> cArray = new ArrayList<>();
    List<LessonListTable> list;
    int a = 0;
    int s = 0;
    private MediaPlayer mp;
    int width = 0;

    static int tag = 0;

    void setTitleLayout() {
        TextView titleTxt = (TextView) findViewById(R.id.text_title);
        titleTxt.setText(getResources().getString(R.string.app_name));
        ImageView menuBtn = (ImageView) findViewById(R.id.menu);
        menuBtn.setVisibility(View.VISIBLE);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    setMenuView(MainActivity.this);
                }
            }
        });
        titleTxt.setText("Home");
    }

    public void clickSound() {
        if (mp == null) {
            mp = MediaPlayer.create(MainActivity.this, R.raw.click);
        }
        if (mp.isPlaying()) {
            mp.stop();
            mp.reset();
        }
        try {
            if (PreferenceConnector.readBoolean(MainActivity.this, PreferenceConnector.SOUND_EFFECT, false)) {
                mp.start();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void setMenuView(final Activity activity) {
        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.menu_layout);
        dialog.getWindow().setLayout((width / 2), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.TOP | Gravity.RIGHT);

        final ImageView onOffBtn = (ImageView) dialog.findViewById(R.id.on_off);
        ImageView resetBtn = (ImageView) dialog.findViewById(R.id.reset);
        ImageView bugBtn = (ImageView) dialog.findViewById(R.id.reporBug);
        ImageView close = (ImageView) dialog.findViewById(R.id.menu);
        boolean sound = PreferenceConnector.readBoolean(MainActivity.this, PreferenceConnector.SOUND_EFFECT, false);
        if (sound) {
            onOffBtn.setImageResource(R.drawable.aroundon);
        } else {
            onOffBtn.setImageResource(R.drawable.aroundoff);
        }
        onOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound();
                boolean sound = PreferenceConnector.readBoolean(MainActivity.this, PreferenceConnector.SOUND_EFFECT, false);
                if (sound) {
                    onOffBtn.setImageResource(R.drawable.aroundoff);
                    PreferenceConnector.writeBoolean(MainActivity.this, PreferenceConnector.SOUND_EFFECT, false);
                } else {
                    onOffBtn.setImageResource(R.drawable.aroundon);
                    PreferenceConnector.writeBoolean(MainActivity.this, PreferenceConnector.SOUND_EFFECT, true);
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound();
                dialog.dismiss();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound();
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure")
                        .setContentText("You want to reset all data ?")
                        .setCancelText("No, cancel plz!")
                        .setConfirmText("Yes, do it!")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dbHelper.resetAll();
                                dbHelper.truncate(TableForGraph.class);
                                sweetAlertDialog.setTitleText("Done").setContentText("progress reset done.").setConfirmText("OK").showCancelButton(false).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        clickSound();
                                        sweetAlertDialog.dismiss();
                                        dialog.dismiss();
                                    }
                                }).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        clickSound();
                        sweetAlertDialog.dismiss();
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        bugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSound();
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, FeedbackActivity.class));
                tag = 1;
            }
        });

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        setContentView(R.layout.activity_main);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);

        setTitleLayout();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;

        /*final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(false);*/
        dbHelper = DbHelper.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        setupRecyclerView(recyclerView);
    }


    private void setupRecyclerView(RecyclerView recyclerView) {
        list = dbHelper.getAllLessons();
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL_LIST));
        s = list.size();
        setColor();
        if (list != null && list.size() != 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
            recyclerView.setAdapter(new RecyclerViewAdapter(MainActivity.this, list));
        }
    }


    void setColor() {
        for (int i = 0; i < colorArray.length; i++) {
            cArray.add(colorArray[i]);
        }
        a = a + 3;
        if (a < s) {
            setColor();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<LessonListTable> mValues;
        MediaPlayer mp;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final TextView mTitleText;
            public final CardView mCardView;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                mTitleText = (TextView) view.findViewById(R.id.title);
                mCardView = (CardView) view.findViewById(R.id.card);
            }
        }

        public LessonListTable getValueAt(int position) {
            return mValues.get(position);
        }

        public RecyclerViewAdapter(Context context, List<LessonListTable> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_list_row, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            int colorPos = position;

            holder.mTitleText.setBackgroundResource(cArray.get(colorPos));
            holder.mTitleText.setText(mValues.get(position).lessonName);
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Constant.buttonEnable) {
                        Constant.setButtonEnable();
                        Context context = v.getContext();
                        Intent intent = new Intent(context, QuizFragment.class);
                        intent.putExtra("lessonId", mValues.get(position).lessonId);
                        intent.putExtra("lessonDescription", mValues.get(position).lessonDescription);
                        intent.putExtra("lessonName", mValues.get(position).lessonName);
                        context.startActivity(intent);
                        tag = 1;
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

    public int checkPos(int pos) {
        pos = pos - 4;
        if (pos > 3) {
            checkPos(pos);
        }
        return pos;
    }

    @Override
    protected void onResume() {
        if (tag == 1) {
            overridePendingTransition(R.anim.push_infromright_anim, R.anim.push_out_to_left_anim);
        }
        super.onResume();
    }
}


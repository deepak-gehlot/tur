package tutorialance.widevision.com.tutorialance.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import tutorialance.widevision.com.tutorialance.R;
import tutorialance.widevision.com.tutorialance.util.Constant;
import tutorialance.widevision.com.tutorialance.util.GMailSender;

public class FeedbackActivity extends Activity {

    private EditText contentTxt;
    private Button submitBtn;
    private RadioGroup radioGroup;
    private String contentStr = "", type = "Feedback";

    void setTitleLayout() {
        TextView titleTxt = (TextView) findViewById(R.id.text_title);
        titleTxt.setText(getResources().getString(R.string.app_name));
        ImageView menuBtn = (ImageView) findViewById(R.id.menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleTxt.setText("Feedback");
        menuBtn.setImageResource(R.drawable.back);
        menuBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_out_to_left_anim, R.anim.pull_infromright_anim);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback);
        setTitleLayout();
        contentTxt = (EditText) findViewById(R.id.contentTxt);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        radioGroup = (RadioGroup) findViewById(R.id.radio);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(id);
                type = radioButton.getText().toString().trim();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.buttonEnable) {
                    Constant.setButtonEnable();
                    contentStr = contentTxt.getText().toString().trim();
                    if (!contentStr.trim().equals("")) {
                        sendEmail(type, contentStr);
                    } else {
                        final Snackbar snackbar = Snackbar.make(v, "Please enter your feedback first.", Snackbar.LENGTH_SHORT);
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
            }
        });
    }


    protected void sendEmail(String subject, String content) {

        String[] TO = {getResources().getString(R.string.send_to)};
        new Task(getResources().getString(R.string.user_name), getResources().getString(R.string.password), TO, "Tutorial (" + subject + ")", content).execute();
    }


    class Task extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        String from;
        String pass;
        String[] to; // list of recipient email addresses
        String subject;
        String body;

        Task(String from, String pass, String[] to, String subject, String body) {
            this.from = from;
            this.pass = pass;
            this.to = to;
            this.subject = subject;
            this.body = body;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(FeedbackActivity.this);
            progressDialog.setMessage("sending...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                GMailSender.sendFromGMail(from, pass, to, subject, body);
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Toast.makeText(FeedbackActivity.this, "feedback send successfully.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}

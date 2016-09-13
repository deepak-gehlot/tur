package tutorialance.widevision.com.tutorialance.Database;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mercury-five on 28/12/15.
 */
@Table(name = "QuestionTable")
public class QuestionTable extends Model {


    @Column(name = "lessonId")
    public String lessonId;

    @Column(name = "quizId")
    public String quizId;

    @Column(name = "questionId")
    public String questionId;
    @Column(name = "question")
    public String question;

    @Column(name = "choice_1")
    public String choice_1;

    @Column(name = "choice_2")
    public String choice_2;

    @Column(name = "choice_3")
    public String choice_3;

    @Column(name = "choice_4")
    public String choice_4;

    @Column(name = "correctAns")
    public String correctAns;

    @Column(name = "time")
    public String time;

    @Column(name = "time_stamp")
    public String timeStamp;

    public QuestionTable() {
        super();
    }

    public QuestionTable(String lessonId, String quizId, String questionId, String question, String choice_1, String choice_2, String choice_3, String choice_4, String correctAns, String time, String timeStamp) {
        super();
        this.lessonId = lessonId;
        this.quizId = quizId;
        this.questionId = questionId;
        this.question = question;
        this.choice_1 = choice_1;
        this.choice_2 = choice_2;
        this.choice_3 = choice_3;
        this.choice_4 = choice_4;
        this.correctAns = correctAns;
        this.time = time;
        this.timeStamp = timeStamp;
    }


}

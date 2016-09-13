package tutorialance.widevision.com.tutorialance.Database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mercury-five on 28/12/15.
 */
@Table(name = "QuizList")
public class QuizListTable extends Model {

    @Column(name = "lessonId")
    public String lessonId;

    @Column(name = "quizId")
    public String quizId;

    @Column(name = "quizName")
    public String quizName;

    @Column(name = "totalTime")
    public String totalTime;

    @Column(name = "percentage")
    public float percentage;

    public QuizListTable() {
        super();
    }

    public QuizListTable(String lessonId, String quizId, String quizName, String totalTime, float percentage) {
        super();
        this.lessonId = lessonId;
        this.quizId = quizId;
        this.quizName = quizName;
        this.percentage = percentage;
        this.totalTime = totalTime;
    }
}

package tutorialance.widevision.com.tutorialance.Database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Table for all quiz liast
 *
 * @QuizId particular quizid
 * @QuizNmae particular quiz name
 * @Percentage average percentage for quiz
 */

@Table(name = "LESSONLIST")
public class LessonListTable extends Model {

    @Column(name = "lessonId")
    public String lessonId;
    @Column(name = "lessonName")
    public String lessonName;
    @Column(name = "lessonDescription")
    public String lessonDescription;
    @Column(name = "percentage")
    public float percentage;

    public LessonListTable() {
        super();
    }

    public LessonListTable(String lessonId, String lessonName, String lessonDescription, float percentage) {
        super();
        this.lessonId = lessonId;
        this.lessonName = lessonName;
        this.percentage = percentage;
        this.lessonDescription = lessonDescription;
    }
}

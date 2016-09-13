package tutorialance.widevision.com.tutorialance.Database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mercury-five on 01/01/16.
 */
@Table(name = "GraphTable")
public class TableForGraph extends Model {

    @Column(name = "quizId")
    public String quizId;
    @Column(name = "lessonId")
    public String lessonId;
    @Column(name = "totalNoQuestion")
    public int totalNoQuestion;
    @Column(name = "correct")
    public int correct;
    @Column(name = "inCorrect")
    public int inCorrect;

    @Column(name = "skip")
    public int skip;

    public TableForGraph() {
        super();
    }

    public TableForGraph(String quizId, String lessonId, int totalNoQuestion, int correct, int inCorrect, int skip) {
        super();
        this.quizId = quizId;
        this.lessonId = lessonId;
        this.totalNoQuestion = totalNoQuestion;
        this.correct = correct;
        this.inCorrect = inCorrect;
        this.skip = skip;
    }


}

package tutorialance.widevision.com.tutorialance.Database;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mercury-five on 28/12/15.
 */
public class DbHelper {
    private static volatile DbHelper instance = null;

    // private constructor
    private DbHelper() {
    }

    public static DbHelper getInstance() {
        if (instance == null) {
            synchronized (DbHelper.class) {
                // Double check
                if (instance == null) {
                    instance = new DbHelper();
                }
            }
        }
        return instance;
    }


    public void insertLesson(String lessonId, String lessonName, String lessonDescription, float percentage) {
        LessonListTable lessonListTable = new LessonListTable(lessonId, lessonName, lessonDescription, percentage);
        lessonListTable.save();
    }


    public void insertQuiz(String lessonId, String quizId, String quizName, String totalTime, float per) {
        QuizListTable quizListTable = new QuizListTable(lessonId, quizId, quizName, totalTime, per);
        quizListTable.save();
    }

    public void insertQuestion(String lessonId, String quizId, String questionId, String question, String choice_a, String choice_b, String choice_c, String choice_d, String correct, String time, String timeStamp) {
        QuestionTable questionTable = new QuestionTable(lessonId, quizId, questionId, question, choice_a, choice_b, choice_c, choice_d, correct, time, timeStamp);
        questionTable.save();
    }

    public void insertGraphData(String quizId, String lessonId, int totalNoQuestion, int correct, int inCorrect, int skip) {
        TableForGraph tableForGraph = new TableForGraph(quizId, lessonId, totalNoQuestion, correct, inCorrect, skip);
        tableForGraph.save();
    }

    public void updateQuizPercentage(String quizId, String lessonId, float per) {
        QuizListTable item = new Select().from(QuizListTable.class).where("lessonId = ?", lessonId).where("quizId = ?", quizId).executeSingle();
        item.percentage = per;
        item.save();
    }

    public List<TableForGraph> getGraphData(String quizId, String lesssonId) {
        return new Select().from(TableForGraph.class).where("lessonId = ?", lesssonId).where("quizId = ?", quizId).execute();
    }

    public List<LessonListTable> getAllLessons() {
        return new Select().from(LessonListTable.class).execute();
    }

    public List<QuizListTable> getQuiz(String lessonId) {
        return new Select().from(QuizListTable.class).where("lessonId = ?", lessonId).execute();
    }

    public List<QuestionTable> getQuestion(String quizId, String lessonId) {
        return new Select().from(QuestionTable.class).where("quizId = ?", quizId).where("lessonId = ?", lessonId).orderBy("RANDOM()").execute();
    }


    public void resetAll() {
        List<QuizListTable> itemList = new Select().from(QuizListTable.class).execute();
        for (QuizListTable item : itemList) {
            item.percentage = 0f;
            item.save();
        }
    }


    public boolean isLessonExist(String id) {
        List<LessonListTable> item = new Select().from(LessonListTable.class).where("lessonId = ?", id).execute();
        if (item != null && item.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isQuizExist(String id, String quizId) {
        List<QuizListTable> item = new Select().from(QuizListTable.class).where("lessonId = ?", id).where("quizId = ?", quizId).execute();
        if (item != null && item.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isQuestionExist(String id, String quizId, String questionId) {
        List<QuestionTable> item = new Select().from(QuestionTable.class).where("lessonId = ?", id).where("quizId = ?", quizId).where("questionId = ?", questionId).execute();
        if (item != null && item.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isQuestionTimeStampMatch(String id, String quizId, String questionId, String timeStamp) {
        QuestionTable item = new Select().from(QuestionTable.class).where("lessonId = ?", id).where("quizId = ?", quizId).where("questionId = ?", questionId).executeSingle();
        if (item.timeStamp.equals(timeStamp)) {
            return true;
        } else {
            return false;
        }
    }


    public void updateQuestion(String lessonId, String quizId, String questionId, String question, String choice_a, String choice_b, String choice_c, String choice_d, String correct, String time, String timeStamp) {
        QuestionTable item = new Select().from(QuestionTable.class).where("quizId = ?", quizId).where("lessonId = ?", lessonId).executeSingle();
        item.quizId = quizId;
        item.questionId = questionId;
        item.question = question;
        item.choice_1 = choice_a;
        item.choice_2 = choice_b;
        item.choice_3 = choice_c;
        item.choice_4 = choice_d;
        item.correctAns = correct;
        item.time = time;
        item.timeStamp = timeStamp;
        item.save();

    }

    // Truncate fix
    public void truncate(Class<? extends Model> type) {
        TableInfo tableInfo = Cache.getTableInfo(type);
        // Not the cleanest way, but...
        ActiveAndroid.execSQL("delete from " + tableInfo.getTableName() + ";");
        ActiveAndroid.execSQL("delete from sqlite_sequence where name='" + tableInfo.getTableName() + "';");
    }
}

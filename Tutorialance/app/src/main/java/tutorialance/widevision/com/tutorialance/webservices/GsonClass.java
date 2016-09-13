package tutorialance.widevision.com.tutorialance.webservices;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class GsonClass {

    public String success;


    public ArrayList<Message> message;
    public class Update {
        public String success;
        public String latest;
        public String message;
    }

    public class Message {
        public String lessonId;
        public String lessonTitle;
        public String categoryId;
        public String lessonDescription;
        public ArrayList<QuizList> quizList;
    }

    public class QuizList {
        public ArrayList<QauistionsList> quistionList;
        public String quizId;
        public String quizTitle;
    }

    public class QauistionsList {
        public String questionId;
        public String questionTitle;
        public String choice_a;
        public String choice_b;
        public String choice_c;
        public String choice_d;
        public String choice_correct;
        public String time;
        public String timestamp;
    }

}

package net.robertx.planeteze_b07.DailySurvey;

public class MainModel {

    String question, answer, getId;

    public MainModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public MainModel(){

    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getId() {
        return getId;
    }
}

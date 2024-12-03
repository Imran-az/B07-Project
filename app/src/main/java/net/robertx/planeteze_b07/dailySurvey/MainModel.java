package net.robertx.planeteze_b07.dailySurvey;

public class MainModel {

    String question, answer, getId, CO2_answer;

    public MainModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
        //this.CO2_answer = CO2_answer;
    }

    public MainModel(){

    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setCO2_answer(String CO2_answer) {
        this.CO2_answer = CO2_answer;
    }

    public String getQuestion() {
        return question;
    }
    public String getCO2_answer() {
        return CO2_answer;
    }

    public String getAnswer() {
        return answer;
    }

    public String getId() {
        return getId;
    }
}

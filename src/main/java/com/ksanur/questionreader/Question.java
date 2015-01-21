package com.ksanur.questionreader;

public class Question {
    private boolean bonus=false;
    private boolean multipleChoice=false;
    private Category category=Category.GENERAL;

    private String question="";
    private String w;
    private String x;
    private String y;
    private String z;
    private String answer = "";

    public boolean isBonus() {
        return bonus;
    }

    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public enum Category {
        GENERAL("General Science"),
        EARTH_SPACE("Earth and Space Science"),
        LIFE("Life Science"),
        PHYSICAL("Physical Science"),
        MATH("Math"),
        CHEMISTRY("Chemistry"),
        ENERGY("Energy");

        private String name;
        private Category(String name) {
            this.name = name;
        }

        public static Category getCategory(String name) {
            for (Category c : values()) {
                if (c.getName().equals(name))
                    return c;
            }
            return null;
        }

        public String getName() {
            return name;
        }
    }
}
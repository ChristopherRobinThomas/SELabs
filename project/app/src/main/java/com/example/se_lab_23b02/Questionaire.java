package com.example.se_lab_23b02;

public class Questionaire {
    private int user_id;
    private String stemp;
    private int satisfaction;
    private int restlessnes;
    private int wellness;
    private int relaxeness;
    private int energy;
    private int tiredness;

    public Questionaire(int userId, String stemp, int satisfaction, int restlessnes, int wellness, int relaxeness, int energy, int tiredness) {
        this.user_id = userId;
        this.stemp = stemp;
        this.satisfaction = satisfaction;
        this.restlessnes = restlessnes;
        this.wellness = wellness;
        this.relaxeness = relaxeness;
        this.energy = energy;
        this.tiredness = tiredness;
    }
}

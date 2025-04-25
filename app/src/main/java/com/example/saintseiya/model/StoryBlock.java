package com.example.saintseiya.model;

import java.util.List;

public class StoryBlock {
    private String id;
    private String text;
    private List<Choice> choices;
    private String giveItem;
    private boolean gameOver;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public String getGiveItem() {
        return giveItem;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}

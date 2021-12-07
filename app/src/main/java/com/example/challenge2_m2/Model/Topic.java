package com.example.challenge2_m2.Model;

public class Topic {
    private String name;
    private final int id;
    public static int ID_COUNTER = 0;

    public Topic(String name, int id){
        this.name = name;
        this.id = id;
    }

    public Topic(String name){
        this.name = name;
        id = ID_COUNTER++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return id;
    }
}

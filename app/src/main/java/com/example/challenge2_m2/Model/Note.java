package com.example.challenge2_m2.Model;

public class Note {
    public static int ID_COUNTER = 0;
    private final int id;
    private String name;
    private String content;

    public Note(String name, String content){
        this.id = ID_COUNTER++;
        this.name = name;
        this.content = content;
    }

    public Note(String name, String content, int id){
        this.name = name;
        this.content = content;
        this.id = id;
    }

    public int getID(){ return this.id; }

    public String getContent() {
        return content;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

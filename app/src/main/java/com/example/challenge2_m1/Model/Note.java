package com.example.challenge2_m1.Model;

import androidx.fragment.app.Fragment;

public class Note {
    private String name;
    private String content;
    private String path;

    public Note(String name){
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public String getName() { return name; }

    public String getPath() { return path; }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

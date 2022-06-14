package com.example.wordcount.wordcount.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WordList {

    List<String> wordList;

    public WordList() {
    }

    public WordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public List<String> getWordList() {
        return wordList;
    }

    public void setWordList(List<String> wordList) {
        this.wordList = wordList;
    }

    public void addList(Set<String> wordList) {
        if(this.wordList == null) {
            this.wordList = new ArrayList<>();
        }
        this.wordList.addAll(wordList);
    }
}

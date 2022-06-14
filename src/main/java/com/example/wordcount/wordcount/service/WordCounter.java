package com.example.wordcount.wordcount.service;

import com.example.wordcount.wordcount.translator.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
public class WordCounter {

    private Map<String, Integer> wordList;

    private Translator translator;

    Predicate<String> isAlphabetic = s -> s.matches("^[a-zA-Z]*$");

    Consumer<String> addWordConsumer;


    @Autowired
    public WordCounter(Map<String, Integer> wordList,
                       Translator translator) {

        this.wordList = wordList;
        this.translator = translator;
    }

    public void addWord(String word) {
        if(addWordConsumer != null) {
            addWordConsumer.accept(word);
        } else {
            addWordConsumer = this::add;
            addWordConsumer.accept(word);
        }
    }

    public void add(String word) {

        Optional<String> wordOptional = Optional.ofNullable(word);
        wordOptional.ifPresentOrElse(
                s -> {
                    if(isAlphabetic.test(s)) {
                        addToMap(word);
                    } else {
                        throw new IllegalArgumentException(
                                "Sorry! Cannot add a word that contains non-alphabet characters!");
                    }
                },

                () -> {
                    throw new IllegalArgumentException(
                            "Sorry! Cannot add a word that is either null or empty!");
                }
        );

    }

    public void addToMap(String word) {
        if(this.wordList == null) {
            this.wordList = new ConcurrentHashMap<>();
        }

        if(this.wordList.containsKey(word)) {
            int wordCounter = this.wordList.get(word) + 1;
            this.wordList.put(word, wordCounter);

        } else {

            this.wordList.put(word, 1);
        }

    }

    public long count(String word){

        return this.wordList != null ? this.wordList.keySet().stream()
                .filter(key -> translator.translate(key).equalsIgnoreCase(
                        translator.translate(word)))
                .count() : 0;
    }

    public Map<String, Integer> getWordList() {
        return wordList;
    }

    public void setWordList(Map<String, Integer> wordList) {
        this.wordList = wordList;
    }

    public Translator getTranslator() {
        return translator;
    }

    public void setTranslator(Translator translator) {
        this.translator = translator;
    }


    public Consumer<String> getAddWordConsumer() {
        return addWordConsumer;
    }

    public void setAddWordConsumer(Consumer<String> addWordConsumer) {
        this.addWordConsumer = addWordConsumer;
    }


}

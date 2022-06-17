package com.example.wordcount.wordcount.service;

import com.example.wordcount.wordcount.translator.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class WordCounter {

    private Map<String, Integer> wordList;

    private Translator translator;

    private Predicate<String> isAlphabetic = s -> s.matches("^[a-zA-Z]*$");

    private Consumer<String> addWordConsumer;

    private Function<String, Long> countWordFunction;


    @Autowired
    public WordCounter(Map<String, Integer> wordList,
                       Translator translator) {

        this.wordList = wordList;
        this.translator = translator;
    }

    public void addWord(String word) {
        if(addWordConsumer == null) {
            addWordConsumer = this::add;
        }
        addWordConsumer.accept(word);
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

            this.wordList.computeIfPresent(word, (k,v)-> v+1);

        } else {

            this.wordList.put(word, 1);
        }

    }

    public long count(String word){

        if(this.wordList != null && word != null) {
            return this.wordList.entrySet().stream()
                    .filter(entry -> translator.translate(entry.getKey()).equalsIgnoreCase(
                            translator.translate(word)))
                    .mapToInt(entry -> entry.getValue())
                    .sum();

        } else {
            return 0;
        }
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

    public Long countWord(String word) {
        if(countWordFunction == null) {
            countWordFunction = this::count;
        }
        return countWordFunction.apply(word);
    }

    public Function<String, Long> getCountWordFunction() {
        return countWordFunction;
    }

    public void setCountWordFunction(Function<String, Long> countWordFunction) {
        this.countWordFunction = countWordFunction;
    }

}

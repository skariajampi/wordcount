package com.example.wordcount.wordcount.controllers;

import com.example.wordcount.wordcount.service.WordCounter;
import com.example.wordcount.wordcount.model.WordList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
public class WordCounterController {

    private WordCounter wordCounter;

    @Autowired
    public WordCounterController(WordCounter wordCounter){
        this.wordCounter = wordCounter;
    }


    @PostMapping("/wordcounter/add")
    public ResponseEntity<Void> add(
            @RequestBody WordList wordList
            ){

        System.out.println("wordList = " + wordList.getWordList());
        wordList.getWordList().forEach(w -> this.wordCounter.addWord(w));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/wordcounter/words")
    public WordList getAllWords(){

        Map<String, Integer> words = wordCounter.getWordList();
        WordList wordList = new WordList();
        wordList.addList(words.keySet());
        return wordList;
    }

    @GetMapping("/wordcounter/count/{word}")
    public Long getCount(@PathVariable String word){

        return wordCounter.count(word);
    }

}

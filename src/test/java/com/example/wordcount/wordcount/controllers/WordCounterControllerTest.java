package com.example.wordcount.wordcount.controllers;

import com.example.wordcount.wordcount.model.WordList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WordCounterControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    int randomServerPort;

    @Test
    void getAllWords() throws URISyntaxException {

        final String baseUrl = "http://localhost:"+randomServerPort+"/wordcounter/add/";
        URI uri = new URI(baseUrl);
        WordList wordList = new WordList(Arrays.asList("bread", "flower"));

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<WordList> request = new HttpEntity<>(wordList, headers);
        testRestTemplate.postForObject(uri, request, Void.class);
        WordList wordList1 = testRestTemplate.getForObject("/wordcounter/words", WordList.class);
        assertEquals(2, wordList1.getWordList().size());
    }
}
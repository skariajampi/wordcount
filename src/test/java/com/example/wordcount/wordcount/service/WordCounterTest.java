package com.example.wordcount.wordcount.service;

import com.example.wordcount.wordcount.service.WordCounter;
import com.example.wordcount.wordcount.translator.Translator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WordCounterTest {

    private WordCounter wordCounter;

    @Mock
    private Translator translator;

    Predicate<String> isAlphabetic = s -> s.matches("^[a-zA-Z]*$");

    @BeforeEach
    void setup(){

        wordCounter = new WordCounter(null, translator);
    }

    private static Stream<Arguments> alphaWordsSource() {
        return Stream.of(
                Arguments.of("flower"),
                Arguments.of("bread"),
                Arguments.of("CAPITAL"),
                Arguments.of("HIGHSTREET")
        );

    }

    @MethodSource("alphaWordsSource")
    @ParameterizedTest
    void add(String word) {
        wordCounter.add(word);
        assertEquals(1, wordCounter.getWordList().size());
    }

    @Test
    void addMultipleWords() {
        wordCounter.addWord("flower");
        wordCounter.addWord("bread");
        wordCounter.addWord("CAPITAL");

        assertEquals(3, wordCounter.getWordList().size());
    }


    private static Stream<Arguments> nonAlphaWordsSource() {
        return Stream.of(
                Arguments.of("12121"),
                Arguments.of("#@bread"),
                Arguments.of("aaa@"),
                Arguments.of("1HIGHSTREET")
        );

    }

    @MethodSource("nonAlphaWordsSource")
    @ParameterizedTest
    void addWordsWithNonAlphaCharacters(String word) {
        assertThrows(IllegalArgumentException.class, () -> wordCounter.addWord(word));
    }


    @Test
    void count() {

        when(translator.translate("flower")).thenReturn("flower");
        wordCounter.addWord("flower");

        long count = wordCounter.count("flower");

        assertEquals(1, count);
    }


    @Test
    void count_with_synonyms_in_different_languages() {

        when(translator.translate("flower")).thenReturn("flower");
        when(translator.translate("flor")).thenReturn("flower");
        when(translator.translate("blume")).thenReturn("flower");
        wordCounter.addWord("flower");
        wordCounter.addWord("flor");
        wordCounter.addWord("blume");

        long count = wordCounter.count("flower");

        assertEquals(3, count);

        verify(translator, times(4)).translate("flower");
        verify(translator).translate("flor");
        verify(translator).translate("blume");

    }


    @Test
    public void testMultiThreading() throws InterruptedException {
        int numberOfThreads = 5;
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        when(translator.translate("flower")).thenReturn("flower");
        when(translator.translate("flor")).thenReturn("flower");
        when(translator.translate("blume")).thenReturn("flower");
        when(translator.translate("blah")).thenReturn("blah");
        when(translator.translate("joe")).thenReturn("joe");
        for (int i = 0; i < numberOfThreads; i++) {
            service.submit(() -> {
                wordCounter.addWord("flower");
                wordCounter.addWord("flor");
                wordCounter.addWord("blume");
                wordCounter.addWord("blah");
                wordCounter.addWord("joe");
                latch.countDown();
            });
        }
        latch.await();

        assertEquals(3, wordCounter.count("flower"));
    }

    @Test
    void addMultipleWords_On_The_fly_Algorithm() {
        Consumer<String> addWordAlgorithm = this::addWordAlgorithm;
        wordCounter.setAddWordConsumer(addWordAlgorithm);
        wordCounter.addWord("1222");
        wordCounter.addWord("3333");
        wordCounter.addWord("@@@@@");

        assertEquals(3, wordCounter.getWordList().size());
    }

    private void addWordAlgorithm(String word) {

        Optional<String> wordOptional = Optional.ofNullable(word);
        wordOptional.ifPresentOrElse(
                s -> {
                    if(isAlphabetic.negate().test(s)) {
                        wordCounter.addToMap(word);
                    } else {
                        throw new IllegalArgumentException(
                                "Sorry! Cannot add a word that contains alphabet characters!");
                    }
                },

                () -> {
                    throw new IllegalArgumentException(
                            "Sorry! Cannot add a word that is either null or empty!");
                }
        );

    }
}
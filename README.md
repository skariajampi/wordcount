## WordCounter Microservice

### Key Points:

1. WordCounter is a Spring Boot based micro service
2. Key class is the com.example.wordcount.wordcount.service.WordCounter within the service package
    1.WordCounter has two methods:
        1.1 add (should NOT allow addition of words with non-alphabetic characters)
        1.2 count (should treat same words written in different languages as the same word, for example if
                   adding "flower", "flor" (Spanish word for flower) and "blume" (German word for flower) the counting method 
                   should return 3.  You may assume that translation of words will be done via external class 
                   provided to you called "Translator" that will have method "translate" accepting word as an 
                   argument and it will return English name for it.)
3. There are several Junit tests inside com.example.wordcount.wordcount.service.WordCounterTest
4. There is a test for Controller class using RestTemplate side com.example.wordcount.wordcount.controllers.WordCounterControllerTest
5. After starting microservice using mvn spring-boot:run, you can 
    1. Add words using http://localhost:8080/wordcounter/add. Payload will be 
    {
        "wordList": [
            "bread",
            "happy"
        ]
    }
    2. Get all words using http://localhost:8080/wordcounter/words
    3. Get count using http://localhost:8080/wordcounter/count/bread
    (Please Note:: At the moment count only works properly in unit tests due to the fact that translator can be mocked in unit tests.
    count does not work in the actual microservice due to the absence of real translator)
 6. There is a central error handler for the rest controller.
 7. com.example.wordcount.wordcount.model.WordList is used as a model in this microservice
 8. There is a Translator interface that mimics the interface of an external translator component/service
 9. This microservice can be containerized and deployed onto Kubernetes cluster or AWS ECS/Fargate for resilience
 
 


package com.eigenbaumarkt.spring_webflux.reactiveexamples;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReactiveExamplesTest {

    Person john = new Person("John", "Cleese");
    Person terry = new Person("Terry", "Jones");
    Person michael = new Person("Michael", "Palin");
    Person graham = new Person("Graham", "Chapman");

    // MONO - Tests:
    // Mono is a new, reactive Type in Spring framework 5
    // Mono is a publisher with zero or one elements in the data stream
    @Test
    public void monoTests() throws Exception {
        //create new person mono
        Mono<Person> personMonoPublisher = Mono.just(john);

        //get person object from mono publisher
        Person person = personMonoPublisher.block();

        // output name
        log.info(person.sayMyName());
    }

    @Test
    public void monoTransform() throws Exception {
        //create new person mono
        Mono<Person> personMonoPublisher = Mono.just(terry);

        PersonCommand command = personMonoPublisher
                .map(person -> { // type transformation with command-object
                    return new PersonCommand(person);
                }).block();

        log.info(command.sayMyName());
    }

    @Test(expected = NullPointerException.class)
    public void monoFilter() throws Exception {
        Mono<Person> personMonoPublisher = Mono.just(michael);

        // filter example
        Person michaelAxe = personMonoPublisher
                .filter(person -> person.getFirstName().equalsIgnoreCase("foo"))
                .block();

        log.info(michaelAxe.sayMyName()); // throws NullPointerException
    }

    // FLUX - Tests:
    // Flux is the second new, reactive Type in Spring framework 5
    // Flux is a publisher with zero or many elements in the data stream

    @Test
    public void fluxTest() throws Exception {

        Flux<Person> peoplePublisher = Flux.just(john, terry, michael, graham);

        peoplePublisher.subscribe(person -> log.info(person.sayMyName()));

    }

    @Test
    public void fluxTestFilter() throws Exception {

        Flux<Person> peoplePublisher = Flux.just(john, terry, michael, graham);

        peoplePublisher.filter(person -> person.getFirstName().equals(terry.getFirstName()))
                .subscribe(person -> log.info(person.sayMyName()));

    }

    @Test
    public void fluxTestDelayNoOutput() throws Exception {

        Flux<Person> peoplePublisher = Flux.just(john, terry, michael, graham);

        peoplePublisher.delayElements(Duration.ofSeconds(1))
                .subscribe(person -> log.info(person.sayMyName()));

        // no output: test-method terminates before output...

    }

    @Test
    public void fluxTestDelay() throws Exception {

        // to get an output, we use a CountDownLatch

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> peoplePublisher = Flux.just(john, terry, michael, graham);

        peoplePublisher.delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        // test-method will not end before the CountDownLatch is complete:
        countDownLatch.await();
    }

    @Test
    public void fluxTestFilterDelay() throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Person> peoplePublisher = Flux.just(john, terry, michael, graham);

        peoplePublisher.delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getFirstName().contains("e"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();
    }

}

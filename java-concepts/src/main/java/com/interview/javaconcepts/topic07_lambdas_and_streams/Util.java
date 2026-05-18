package com.interview.javaconcepts.topic07_lambdas_and_streams;

import java.util.function.Consumer;

public class Util {
    public static Consumer<String> println = System.out::println;

}

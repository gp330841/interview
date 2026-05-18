package com.interview.javaconcepts.topic12_tricky_programs;

import java.util.*;

public class JavaTricky {
    static void main() {

//        int[] arr printing is handled by JVM
//        [I@27716f4 - [ means arr, I means Integer class
//        int[] arr printing is handled by JVM
//        [I@27716f4 - [ means arr, I means Integer class
//        [ → array
//        I → int type
//        @... → hashcode
//        getClass().getName() + "@" + Integer.toHexString(hashCode())
        int[] arr = {1,2,3};
        System.out.println(arr);      // [I@27716f4
        System.out.println(Integer.toHexString(arr.hashCode()));

        char[] charArr = {'a', 'b'};
        System.out.println(charArr); // ab
    }
}

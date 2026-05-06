package practice;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

class Test {
     static void main() {
        System.out.println("hello");
         System.out.println((-12)%2);
         int[] arr = new int[]{1,2,3,4,5,6,7,8,9};
         int max = Arrays.stream(arr).max().getAsInt();
         System.out.println(max);
         List<Integer> list;
         ExecutorService es  = Executors.newFixedThreadPool(10);
    }
}
package com.zbw.test.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

public class ForkJoinPoolTest {

//    private static int[] data = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};



    public static void main(String[] args) {

        long[] data = Stream.iterate(1L, i -> i + 1).mapToLong(Long::longValue).limit(3000000).toArray();

        System.out.println("result=> " + calc(data));

        long start = System.currentTimeMillis();
        AccumulatorRecursiveTask task = new AccumulatorRecursiveTask(0, data.length, data);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Integer result = forkJoinPool.invoke(task);
        long end = System.currentTimeMillis();
        System.out.println("forkjoin"+(end - start));
        System.out.println("AccumulatorRecursiveTask >>" + result);

//        AccumulatorRecursiveAction action = new AccumulatorRecursiveAction(0, data.length, data);
//        forkJoinPool.invoke(action);
//        System.out.println("AccumulatorRecursiveAction >>" + AccumulatorRecursiveAction.AccumulatorHelper.getResult());
    }


    private static int calc(long[] data) {
        long start = System.currentTimeMillis();
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            result += data[i];
        }
        long end = System.currentTimeMillis();
        System.out.println("calc"+(end - start));
        return result;
    }

}
package com.example.shiro_jwt;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2019/1/17.
 */
public class Test {

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        Set<Integer> setNames = new HashSet(10);
        for (int i = 0; i < 10; i++) {
            setNames.add(i);
        }
        List<Integer> temp =  setNames.stream()
                .map(n -> n * n)
                .collect(Collectors.toList());
        System.out.println(temp);

        List<Integer> nums = Arrays.asList(1, 2, 3, 4);
        List<Integer> squareNums = nums.stream().
                map(n -> n * n).
                collect(Collectors.toList());
    }
}

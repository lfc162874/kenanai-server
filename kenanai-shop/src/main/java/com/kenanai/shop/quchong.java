package com.kenanai.shop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class quchong {
    public static void main(String[] args) {
        int factorial = factorial(5);
        System.out.println(factorial);
    }
    //5的阶乘
    public static int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }

}

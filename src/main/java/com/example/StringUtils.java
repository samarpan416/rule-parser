package com.example;

public class StringUtils {
    public static <T> boolean equalsAny(T input, T... values) {
        for (T value : values) {
            if (input.equals(value)) {
                return true;
            } else if(!input.getClass().equals(value.getClass())) {
                // checking type of input and values as well, as of now, we can pass input & values of different types
                // because compiler do not restrict it which make issue with code logic if someone mistakenly put input of other type and some/all items of values of different type
                throw new RuntimeException("Wrong type parameter passed for " + value + " of type " + value.getClass() + " while input type is " + input.getClass());
            }
        }
        return false;
    }
}

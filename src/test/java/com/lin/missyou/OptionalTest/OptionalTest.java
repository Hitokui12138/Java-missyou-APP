package com.lin.missyou.OptionalTest;

import org.junit.Test;

import java.util.Optional;
import java.util.function.Predicate;

public class OptionalTest {
    /**
     * 1. 构建Optional的三个常用方法
     */
    @Test
    public void test1(){
        //1. java.util.NoSuchElementException: No value present
        Optional<String> empty = Optional.empty();
        //empty.get();

        //2. java.lang.NullPointerException Optional.of()强制不能为null
        //Optional<String> t1 = Optional.of(null);

        //3. 不报错, 但get()取值时立即报错
        Optional<String> t2 = Optional.ofNullable(null);
        //t2.get(); //java.util.NoSuchElementException: No value present

        /**
         * 为什么get()取值时立即报错是好的机制?
         * 1. 空指针异常是一种隐藏式的错误, 只在使用时报错,溯源时非常难找具体是哪个函数, 必须调试每一个函数
         * 2. get()取值时立即报错, 则立即就能找到这个目标函数
         */
    }

    @Test
    public void test2(){
        /**
         * 一种没有意义的Optional用法
         */
        Optional<String> t2 = Optional.ofNullable(null);
        if(t2.isPresent()){
            t2.get();
        }

        /**
         * 应该考虑ifPresent(Consumer)的用法
         */
        Optional<String> t3 = Optional.ofNullable("123");
        t3.ifPresent(t-> System.out.println(t));

        /**
         * 若为空, 则赋予默认值
         * orElse(String), 但只能设置String型默认值
         */
        String s = t2.orElse("Default");
        System.out.println(s);

        /**
         * 上面那个只能设计String, 不方便
         * orElseGet(Supplier)
         */
        String s2 = t2.orElseGet(() -> new String("ABC"));
        System.out.println(s2);

        /**
         * 若为空,则报错
         * orElseThrow(Supplier)
         */
        t2.orElseThrow(() -> new RuntimeException("不能为null"));

        /**
         * Java9提供的拓展方法
         * ifPresentOrElse(), or(), stream()
         */
        //t2.ifPresentOrElse();
    }


    @Test
    public void test3(){
        /**
         * 补充几个Lambda表达式
         * consumer supplier
         * runnable 既无输入,又无输出
         * function 可以又输入,可以又输出
         * predicate 返回结果为Boolean
         * 要理解设计接口时, 为什么选择Lambda表达式作为参数
         * 类似于callback()?
         */
        Optional<String> o = Optional.ofNullable("A");
        //t.filter(predicate);
        //t.flatMap(function) map(function)
        Optional<String> s = o.map(t -> t+"B");//注意map返回结果还时Optional
        String s2 = o.map(t -> t+"B").orElse("C");//使用orElse取得Optional里面的值
        System.out.println(s2);
    }
}

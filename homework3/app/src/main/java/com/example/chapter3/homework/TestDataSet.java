package com.example.chapter3.homework;



import java.util.ArrayList;
import java.util.List;

public class TestDataSet {

    public static List<TestData> getData() {
        List<TestData> result = new ArrayList();
        result.add(new TestData("苹果","转发[视频]  ·  14：55","p1"));
        result.add(new TestData("香蕉","冲冲冲  ·  14：33","p2"));
        result.add(new TestData("橘子","我也沉了hh  ·  14：03","p3"));
        result.add(new TestData("柚子","[捂脸哭][捂脸哭]  ·  9：16","p4"));
        result.add(new TestData("西瓜 ","五角星？  ·  7：15","p5"));
        result.add(new TestData("火龙果 ","我也沉了  ·  7:13","p6"));
        result.add(new TestData("茄子 ","hhh  ·  7：00","p7"));
        result.add(new TestData("芒果 ","emmmm  ·  6：48","p8"));
        result.add(new TestData("草莓","good  ·  6：30","p9"));
        result.add(new TestData("菠萝 ","早安  ·  6：00","p10"));
        result.add(new TestData("荔枝","干啥呢  ·  3：00","p11"));
        result.add(new TestData("橙子 ","晚安  ·  2：00","p12"));
        return result;
    }

}


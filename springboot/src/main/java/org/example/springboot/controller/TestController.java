package org.example.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOError;

@Slf4j
@RestController
@RequestMapping("Test")
public class TestController {

    @GetMapping("/debug")
    public void Test(@RequestParam Integer index,@RequestParam Integer count) {
        // 简单的测试方法，可以在这里打断点
        log.info("测试开始，index:{}", index);
        if(index==1){
            log.debug("测试1开始");
            String message = "Hello Debug";
            System.out.println(message);

            // 计算示例
            int a = 10;
            int b = 20;
            int result =add(a,b);
            log.debug("测试1结束");
            log.info("结果: " + result);
        }else if(index==2){
            log.debug("测试2开始");
            loopTest(count);
            log.debug("测试2结束");
        } else if (index==3){
            log.debug("测试3开始");
            threadTest();
            log.debug("测试3结束");
        }  else {
            log.warn("非法参数:index:{}", index);

        }

        log.info("测试结束，index:{}", index);




    }
    public int add(int a, int b){
        return a+b;
    }


    public void loopTest(Integer count) {
        // 循环测试，方便观察变量变化
        for (int i = 0; i < count; i++) {
            System.out.println("循环次数: " + i);
        }
    }

    public void threadTest()  {
        new Thread(()->{
            System.out.println("线程1-1");
            System.out.println("线程1-2");
            System.out.println("线程1-3");
        }).start();

        new Thread(()->{
            System.out.println("线程2-1");
            System.out.println("线程2-2");
            System.out.println("线程2-3");
        }).start();
    }
}

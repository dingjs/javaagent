package com.thunisoft.agent.test;

import com.thunisoft.agent.ConfigUtils;
import com.thunisoft.agent.log.ExecuteLogUtils;

public class TestPerformance {

    public static void main(String[] args) {
        // dingjsh commented in 20150730 模拟测试千万次或更多调用，记录执行时间，这样可大概估出对性能的影响
        String configFileName = "";
        ConfigUtils.initProperties(configFileName);
        ExecuteLogUtils.init();
        int classCount = 1000;
        String[] strArr = new String[classCount];
        for (int index = 0; index < classCount; index++) {
            strArr[index] = "" + index;
        }
        long start = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < classCount; j++) {
                String str = strArr[j];
                ExecuteLogUtils.log(str, str, start, 5);
            }
        }
        System.out.println(System.currentTimeMillis() - start);
    }

}

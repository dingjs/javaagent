package com.wenshuo.agent.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import com.tdunning.math.stats.Centroid;
import com.tdunning.math.stats.Dist;
import com.tdunning.math.stats.MergingDigest;
import com.tdunning.math.stats.ScaleFunction;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.wenshuo.agent.javassist.ClassPool;
import com.wenshuo.agent.javassist.CtClass;
import com.wenshuo.agent.javassist.CtMethod;
import com.wenshuo.agent.javassist.Modifier;
import com.wenshuo.agent.javassist.NotFoundException;

public class TestCtMethod {
    
    private static List<String> staticMethodNames = Arrays.asList("privateStatic","protectedStatic","publicStatic","defaultStatic");
    
    @Test
    public void staticMethodTest() throws NotFoundException{
        ClassPool cp = ClassPool.getDefault();
        CtClass ctClass = cp.get("com.wenshuo.agent.test.TestCtMethod");
        CtMethod[] ctMethods = ctClass.getDeclaredMethods();
        for(CtMethod ctMethod : ctMethods){
            String methodName = ctMethod.getName();
            if(staticMethodNames.contains(methodName)){
                Assert.assertTrue(methodName+" is static method", Modifier.isStatic(ctMethod.getModifiers()));
            }
        }
        System.out.println("staticMethodTest is successful");
    }

    private static void privateStatic(){
        System.out.println("I'm private and static");
    }
    
    protected static void protectedStatic(){
        System.out.println("I'm private and static");
    }
    
    public static void publicStatic(){
        System.out.println("I'm public and static");
    }
    static void defaultStatic(){
        System.out.println("I'm default and static");
    }


    /**
     * 100个线程并发测试，每个线程往100个不同的Digest中添加5000000个随机数据[0, 100]，并计算耗时<br/>
     * 内存限制：-Xms50m -Xmx50m
     *
     * 测试结论：从统计值来看，统计结果基本符合预期，并且耗时基本在800ms~1000ms，未发生内存溢出，初步评估可以加入到javaagent方法耗时统计中并且对性能影响在可控范围内<br/>
     * 部分统计结果如下：<br/>
     *        MIN	  10th pct	  20th pct	  25th pct	  50th pct	  75th pct	  90th pct	  95th pct	  99th pct	       MAX	        耗时	<br/>
     * ==========	==========	==========	==========	==========	==========	==========	==========	==========	==========	==========	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       942	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       869	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       939	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       895	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       942	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       845	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       887	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       898	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       911	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       815	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       762	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       893	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       933	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       866	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       863	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       920	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       830	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	        99	       100	       870	<br/>
     *          0	        10	        20	        25	        50	        75	        90	        95	       100	       100	       913  <br/>
     *
     *
     * @return void
     * @date 2024/9/5 下午6:41
     **/
    @Test
    @Ignore
    public void testMethodPctCounter() {
        int compression = 150;
        int factor = 5;

        final int M = 100;
        final List<MergingDigest> mds = new ArrayList<MergingDigest>(M);
        final long[] counts = new long[M];
        for (int i = 0; i < M; ++i) {
            mds.add(new MergingDigest(compression, (factor + 1) * compression, compression));
            counts[i] = 0;
        }

        // Fill all digests with random values (0~100).
        final Random random = new Random();

        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                100,
                100,
                0,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(1000),
                new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 5000000; ++i) {
            executorService.execute((new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < M; ++j) {
                        MergingDigest md = mds.get(j);
                        synchronized (md) {
                            int data = random.nextInt(101);
                            long start = System.currentTimeMillis();
                            md.add(data);
                            counts[j] = counts[j] + (System.currentTimeMillis() - start);
                        }
                    }
                }
            }));
        }
        while (true) {
            if (executorService.getActiveCount() != 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        executorService.shutdown();

        // Output
        double[] qArr = new double[]{0.1, 0.2, 0.25, 0.5, 0.75, 0.90, 0.95, 0.99};
        System.out.printf("%10s\t", "MIN");
        for (int i = 0 ; i < qArr.length; ++i) {
            System.out.printf("%4sth pct\t", (int)(qArr[i] * 100));
        }
        System.out.printf("%10s\t", "MAX");
        System.out.printf("%10s\t", "耗时");
        System.out.println();
        for (int i = 0 ; i < 11; ++i) {
            System.out.print("==========\t");
        }
        System.out.println();


        for (int i = 0; i < mds.size(); ++i) {
            MergingDigest md = mds.get(i);
            System.out.printf("%10.0f\t", md.getMin());
            long start = System.currentTimeMillis();
            for (double q : qArr) {
                System.out.printf("%10.0f\t", md.quantile(q));
            }
            counts[i] = counts[i] + (System.currentTimeMillis() - start);
            System.out.printf("%10.0f\t", md.getMax());
            System.out.printf("%10d\t", counts[i]);
            System.out.println();
        }

    }

}

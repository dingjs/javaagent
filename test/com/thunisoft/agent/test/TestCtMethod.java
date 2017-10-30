package com.thunisoft.agent.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.thunisoft.agent.javassist.ClassPool;
import com.thunisoft.agent.javassist.CtClass;
import com.thunisoft.agent.javassist.CtMethod;
import com.thunisoft.agent.javassist.Modifier;
import com.thunisoft.agent.javassist.NotFoundException;

public class TestCtMethod {
    
    private static List<String> staticMethodNames = Arrays.asList("privateStatic","protectedStatic","publicStatic","defaultStatic");
    
    @Test
    public void staticMethodTest() throws NotFoundException{
        ClassPool cp = ClassPool.getDefault();
        CtClass ctClass = cp.get("com.thunisoft.agent.test.TestCtMethod");
        CtMethod[] ctMethods = ctClass.getDeclaredMethods();
        for(CtMethod ctMethod : ctMethods){
            String methodName = ctMethod.getName();
            if(staticMethodNames.contains(methodName)){
                Assert.assertTrue(methodName+" is static method", Modifier.isStatic(ctMethod.getModifiers()));
            }
        }
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
    
    
    
}

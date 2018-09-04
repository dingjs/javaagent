/*
 * @(#)PojoDetector.java	2016-5-7 下午04:18:14
 * javaagent
 * Copyright 2016 wenshuo, Inc. All rights reserved.
 * wenshuo PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.wenshuo.agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.wenshuo.agent.javassist.CtMethod;

/**
 * PojoDetector
 * @author dingjsh
 * @time 2016-5-7下午04:18:14
 */
public class PojoDetector {
    
//    private static final String[] ignoreMthods = new String[]{"toString","equals","hashCode","toJSONString"};
    
    
    
    public static Set<String> getPojoMethodNames(CtMethod[] methods){
        Set<String> pojoMethods = Collections.emptySet();
        if(null != methods && methods.length>0){
            pojoMethods = new HashSet<String>();
            Map<String,String> propName2MethodName = new HashMap<String,String>();
            for(CtMethod method : methods){
                String methodName = method.getName();
                if(isSetMethod(methodName) || isGetMethod(methodName)){
                    String propName = getPojoPropertyName(methodName);
                    if(propName2MethodName.containsKey(propName)){
                        pojoMethods.add(methodName);
                        pojoMethods.add(propName2MethodName.get(propName));
                    }else{
                        propName2MethodName.put(propName, methodName);
                    }
                }
            }
        }
        return pojoMethods;
    }
    
    
    
    private static boolean isGetMethod(String methodName){
        return methodName.startsWith("get") || methodName.startsWith("is");
    }
    
    private static boolean isSetMethod(String methodName){
        return methodName.startsWith("set");
    }
    
    private static String getPojoPropertyName(String methodName){
        if(methodName.startsWith("get") || methodName.startsWith("set")){
            return methodName.substring(3);
        }else if(methodName.startsWith("is")){
            return methodName.substring(2);
        }
        return "";
    }
    

}

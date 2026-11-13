package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    /**
     * 前置通知，在通知中对公共字段进行填充
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {//joinPoint参数表示当前切点所对应的连接点
        log.info("正在对公共字段进行填充...");
        //获取当前拦截到的方法的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature(); // 获取当前拦截到的方法的签名
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象
        OperationType operationType = autoFill.value();//获取注解对象中的数据库操作类型
        //获取当前拦截方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        Object object = args[0];//约定参数(对象)位置为第一个

        //准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据数据库操作类型，使用反射为实体对象中的公共字段设置值
        if (operationType == OperationType.INSERT){
            try {
                Method setCreateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(object, now);
                setCreateUser.invoke(object, currentId);
                setUpdateTime.invoke(object, now);
                setUpdateUser.invoke(object, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(operationType == OperationType.UPDATE)
            try {
                Method setUpdateTime = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = object.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(object, now);
                setUpdateUser.invoke(object, currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}

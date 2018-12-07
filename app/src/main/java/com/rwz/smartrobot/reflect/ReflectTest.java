package com.rwz.smartrobot.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectTest {

    private static final Integer TEST_INT = 1024;

    public static void main(String[] args) {

        try {
            modifyIntField();
            modifyBoolField();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void modifyIntField() throws NoSuchFieldException, IllegalAccessException {
        Field field = Student.class.getDeclaredField("TEST_INT");
        //忽略访问权限
        field.setAccessible(true);
        //忽略static修饰符【原理不明白】
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers()&~Modifier.FINAL);
        field.set(null, 512);
        System.out.println("ReflectTest,modifyIntField : " + Student.getTestInt());
    }

    private static void modifyBoolField() throws NoSuchFieldException, IllegalAccessException {
        Field field = Student.class.getDeclaredField("TEST_BOOL");
        //忽略访问权限
        field.setAccessible(true);
        //忽略static修饰符【原理不明白】
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers()&~Modifier.FINAL);

        field.set(null, false);
        System.out.println("ReflectTest, modifyBoolField : " + Student.getTestBool());
    }

}

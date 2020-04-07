package com.example.xposettesthook;


import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Hook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("handleLoadPackage 執行！");
        if (lpparam.packageName.contentEquals("com.example.xposedtest")) {
            XposedBridge.log("開始 hook 測試");
            XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("處理 setText 方法前");
                            param.args[0] = "我是被 Xposed 修改的";
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("處理 setText 方法後");
                        }
                    });

            /**
             * Hook private 資料練習
             */
            Class<?> MainActivityClass = null;
            MainActivityClass = lpparam.classLoader.loadClass("com.example.xposedtest.MainActivity");
            final Class<?> finalMainActivityClass = MainActivityClass;
            XposedHelpers.findAndHookMethod("com.example.xposedtest.MainActivity", lpparam.classLoader,
                    "onResume", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("Hook private field 練習");
                            XposedBridge.log("Class name = " + finalMainActivityClass.getName());
                            Field field = finalMainActivityClass.getDeclaredField("privateStrTest");
                            // 一定要設定 Accessible = true 才能存取 private
                            field.setAccessible(true);
                            XposedBridge.log("privateStrTest 方法之前 = " + field.get(param.thisObject));
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("Hook private field 練習");
                            XposedBridge.log("Class name = " + finalMainActivityClass.getName());
                            Field field = finalMainActivityClass.getDeclaredField("privateStrTest");
                            field.setAccessible(true);
                            XposedBridge.log("privateStrTest 方法之後 = " + field.get(param.thisObject));
                        }
                    });

            /**
             * Hook List<custom class> 資料練習
             */
            XposedHelpers.findAndHookMethod("com.example.xposedtest.MainActivity", lpparam.classLoader,
                    "setCustomBeanList", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("Hook List<custom class> 練習");
                            Field field = finalMainActivityClass.getDeclaredField("customBeans");
                            // 一定要設定 Accessible = true 才能存取 private
                            field.setAccessible(true);
                            XposedBridge.log("setCustomBeanList 方法之前 = " + field.get(param.thisObject));
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("Hook List<custom class> 練習");
                            Field field = finalMainActivityClass.getDeclaredField("customBeans");
                            field.setAccessible(true);
//                            Class<?> CustomBean = lpparam.classLoader.loadClass("com.example.xposedtest.CustomBean");
                            ParameterizedType pt = (ParameterizedType) field.getGenericType();
                            Type[] listActualTypeArguments = pt.getActualTypeArguments();
                            XposedBridge.log("listActualTypeArguments length =" + listActualTypeArguments.length);
                            for (int i = 0; i < listActualTypeArguments.length; i++) {
                                XposedBridge.log("listActualTypeArguments 方法之後 = " + listActualTypeArguments[i]);
                                Class<?> CustomBean = (Class<?>) listActualTypeArguments[i];
                                Field[] fields = CustomBean.getFields();
                                for (Field f :fields){
                                    XposedBridge.log("他的屬性有什麼呢 = "+f.getName());
                                }
//                                Field a = CustomBean.getField("a");
//                                a.setAccessible(true);
//                                String aStr = (String) a.get(param.thisObject);
//                                XposedBridge.log("a string = " + aStr);
                            }

                        }
                    });

            XposedHelpers.findAndHookMethod("com.example.xposedtest.LoginActivity", lpparam.classLoader,
                    "verifyLogin", String.class, String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("處理 verifyLogin 方法前");
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            XposedBridge.log("處理 verifyLogin 方法後");
                            param.setResult(true);
                        }
                    });
        }
    }
}

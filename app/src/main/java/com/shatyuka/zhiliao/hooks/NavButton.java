package com.shatyuka.zhiliao.hooks;

import android.view.View;


import com.shatyuka.zhiliao.Helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class NavButton implements IHook {


    static Field Tab_tabView;
    static int index;

    @Override
    public String getName() {
        return "隐藏导航栏按钮";
    }

    @Override
    public void init(ClassLoader classLoader) throws Throwable {

        Class<?> tabLayoutTabClass = classLoader.loadClass("com.google.android.material.tabs.TabLayout");

        Tab_tabView = tabLayoutTabClass.getField("view");
    }

    @Override
    public void hook() throws Throwable {
        if (Helper.prefs.getBoolean("switch_mainswitch", false) && (
                Helper.prefs.getBoolean("switch_vipnav", false) ||
                Helper.prefs.getBoolean("switch_videonav", false) ||
                Helper.prefs.getBoolean("switch_friendnav", false) ||
                Helper.prefs.getBoolean("switch_panelnav", false))) {

            XposedBridge.hookAllMethods(Tab_tabView.getDeclaringClass(), "newTab", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int[] keepPositions = {1, 8};  
                    // 如果应该隐藏该Tab，设置其视图为不可见
                    if (!contains(keepPositions, index++)) {
                        View tabView = (View) Tab_tabView.get(param.getResult());
                            tabView.setVisibility(View.GONE);  // 隐藏视图
                    }
                }
                private boolean contains(int[] array, int value) {
                    for (int i : array) {
                        if (i == value) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }
}

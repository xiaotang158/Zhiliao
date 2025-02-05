package com.shatyuka.zhiliao.hooks;

import android.view.View;

import com.shatyuka.zhiliao.Helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class NavButton implements IHook {
    static Class<?> BottomNavMenuView;
    static Class<?> IMenuItem;

    static Method getItemId;

    static Field Tab_tabView;
    static Field Tab_text;
    static Field Tab_position;

    @Override
    public String getName() {
        return "隐藏导航栏按钮";
    }

    @Override
    public void init(ClassLoader classLoader) throws Throwable {
        BottomNavMenuView = classLoader.loadClass("com.zhihu.android.bottomnav.core.BottomNavMenuView");

        Class<?> tabLayoutTabClass = classLoader.loadClass("com.google.android.material.tabs.TabLayout$Tab");
        IMenuItem = Arrays.stream(BottomNavMenuView.getDeclaredMethods())
                .filter(method -> method.getReturnType() == tabLayoutTabClass)
                .map(method -> method.getParameterTypes()[0]).findFirst().get();

        getItemId = Arrays.stream(IMenuItem.getDeclaredMethods())
                .filter(method -> method.getReturnType() == String.class).findFirst().get();

        Tab_tabView = tabLayoutTabClass.getField("view");
        Tab_text = tabLayoutTabClass.getField("text");
        Tab_position = tabLayoutTabClass.getField("position");
    }

    @Override
    public void hook() throws Throwable {
        if (Helper.prefs.getBoolean("switch_mainswitch", false) && (
                Helper.prefs.getBoolean("switch_vipnav", false) ||
                Helper.prefs.getBoolean("switch_videonav", false) ||
                Helper.prefs.getBoolean("switch_friendnav", false) ||
                Helper.prefs.getBoolean("switch_panelnav", false))) {

            XposedBridge.hookMethod(Helper.getMethodByParameterTypes(BottomNavMenuView, IMenuItem), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    boolean shouldHide = false;

                    String txt = (String) Tab_text.get(param.getResult());  // 获取当前Tab的文本
                    int position = (int) Tab_position.get(param.getResult());  // 获取当前Tab的位置
                    int[] keepPositions = {0, 4};  // 需要保持不变的位置

                    // 如果txt是"推荐"或"热榜"，或position是keepPositions中的一个，则不隐藏
                    if ("推荐".equals(txt) || "热榜".equals(txt) || (txt==null&&contains(keepPositions, position))) {
                        shouldHide = false;  // 不隐藏
                    } else {
                        shouldHide = true;  // 隐藏
                    }

                    // 如果应该隐藏该Tab，设置其视图为不可见
                    if (shouldHide) {
                        View tabView = (View) Tab_tabView.get(param.getResult());
                        if (tabView != null) {
                            tabView.setVisibility(View.GONE);  // 隐藏视图
                        }
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

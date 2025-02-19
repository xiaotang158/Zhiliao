package com.shatyuka.zhiliao.hooks;

import android.view.View;
import android.os.Handler;
import android.os.Looper;
import com.shatyuka.zhiliao.Helper;
import java.lang.reflect.Field;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class NavButton implements IHook {
    static Class<?> BottomNavMenuView;
    static Field Tab_tabView;
    static int index = 0;

    @Override
    public String getName() {
        return "隐藏导航栏按钮";
    }

    @Override
    public void init(ClassLoader classLoader) throws Throwable {
        // 获取 TabLayout$Tab 的 view 字段
        Class<?> tabLayoutTabClass = classLoader.loadClass("com.google.android.material.tabs.TabLayout$Tab");
        BottomNavMenuView = classLoader.loadClass("com.google.android.material.tabs.TabLayout");
        Tab_tabView = tabLayoutTabClass.getDeclaredField("view");
        Tab_tabView.setAccessible(true);
    }

    @Override
    public void hook() throws Throwable {
        // 条件检查：判断是否启用导航栏按钮隐藏功能
        if (Helper.prefs.getBoolean("switch_mainswitch", false) &&
            (Helper.prefs.getBoolean("switch_vipnav", false) ||
            Helper.prefs.getBoolean("switch_videonav", false) ||
            Helper.prefs.getBoolean("switch_friendnav", false) ||
            Helper.prefs.getBoolean("switch_panelnav", false))) {

            XposedBridge.hookAllMethods(BottomNavMenuView, "newTab", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int[] keepPositions = {0,2,3,7};  // 保留的 Tab 位置
                    // 判断 Tab 的位置，是否需要隐藏
                    if (!contains(keepPositions, index++)) {
                        // 获取 Tab 的视图并隐藏
                        View tabView = (View) Tab_tabView.get(param.getResult());
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tabView.setVisibility(View.GONE);  // 隐藏 Tab
                            }
                        }, 1);
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

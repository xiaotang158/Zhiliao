package com.shatyuka.zhiliao.hooks;

import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class VIPBanner implements IHook {
     static Class<?> MoreHybridView;
     static Class<?> ZHRecyclerView;

    @Override
    public String getName() {
        return "隐藏会员卡片";
    }

    @Override
    public void init(ClassLoader classLoader) throws Throwable {
        MoreHybridView = classLoader.loadClass("com.zhihu.android.app.ui.fragment.more.more.widget.MoreHybridView");
        ZHRecyclerView = classLoader.loadClass("com.zhihu.android.base.widget.ZHRecyclerView");
    }

    @Override
    public void hook() throws Throwable {
        if (Helper.prefs != null &&
            Helper.prefs.getBoolean("switch_mainswitch", false) &&
            Helper.prefs.getBoolean("switch_vipbanner", false)) {

            
                XposedBridge.hookAllMethods(MoreHybridView, "onAttachedToWindow", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        ((View) param.thisObject).setVisibility(View.GONE);
                    }
                });
            

            
                XposedBridge.hookAllMethods(ZHRecyclerView, "onAttachedToWindow", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        ((View) param.thisObject).setVisibility(View.GONE);
                    }
                });
            
        }
    }
}

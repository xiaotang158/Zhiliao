package com.shatyuka.zhiliao.hooks;

import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import com.shatyuka.zhiliao.Helper;


public class VIPBanner implements IHook {
     static Class<?> MoreHybridView;
     static Class<?> ZHRecyclerView;
     boolean MoreHybridStat = false;
     boolean ZHRecyclerStat = false;

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
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                         
                    if (MoreHybridStat) return; // 只执行一次
                         
                    View view = (View) param.getResult();
                                         XposedBridge.log("[Zhiliao] " + view);

                    int viewId = view.getId();

                    if (viewId != View.NO_ID && viewId != 1) { // 过滤无效 ID
                         String resourceName = view.getContext().getResources().getResourceEntryName(viewId);

                        if ("hybrid_layout".equals(resourceName)) { // 判断 ID
                            view.setVisibility(View.GONE); // 隐藏 View
                            MoreHybridStat = true; // 避免重复执行
                        }
                    }
                
                    }
                });
            

            
                XposedBridge.hookAllMethods(ZHRecyclerView, "onAttachedToWindow", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (ZHRecyclerStat) return; // 只执行一次

                    View view = (View) param.getResult();
                                         XposedBridge.log("[Zhiliao] " + view);
                    int viewId = view.getId();

                    if (viewId != View.NO_ID && viewId != 1) { // 过滤无效 ID
                        String resourceName = view.getContext().getResources().getResourceEntryName(viewId);

                        if ("function_panel".equals(resourceName)) { // 判断 ID
                            view.setVisibility(View.GONE); // 隐藏 View
                            ZHRecyclerStat = true; // 避免重复执行
                        }
                    }
                
                    }
                });
            
        }
    }
}

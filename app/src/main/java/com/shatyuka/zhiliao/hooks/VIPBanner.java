package com.shatyuka.zhiliao.hooks;

import android.content.res.Resources;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import com.shatyuka.zhiliao.Helper;

public class VIPBanner implements IHook {
     static Class<?> MoreHybridView;
     static Class<?> ZHRecyclerView;


     boolean MoreHybridStat = true;
     boolean ZHRecyclerStat = true;

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
        if (Helper.prefs.getBoolean("switch_mainswitch", false) &&
            Helper.prefs.getBoolean("switch_vipbanner", false)) {
            
       XposedBridge.hookAllMethods(MoreHybridView, "onFinishInflate", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (MoreHybridStat) {
                        View view = (View) param.thisObject;
                        view.setVisibility(View.GONE);
                        MoreHybridStat = false;
                    }
                }
            });

       XposedBridge.hookAllMethods(ZHRecyclerView, "onScrollChanged", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (ZHRecyclerStat) {
                        View view = (View) param.thisObject;
                        try {
                            String name = view.getContext().getResources().getResourceEntryName(view.getId());
                            if ("function_panel".equals(name) ||
                                "new_function_panel".equals(name) ||
                                "common_items".equals(name) ||
                                "new_common_items".equals(name)) {
                                
                                view.setVisibility(View.GONE);
                                ZHRecyclerStat = false;
                            }
                        } catch (Resources.NotFoundException ignored) {}
                    }
                }
            });
        }
    }
}

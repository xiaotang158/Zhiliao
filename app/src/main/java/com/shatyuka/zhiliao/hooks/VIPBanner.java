package com.shatyuka.zhiliao.hooks;

import android.content.res.Resources;
import java.lang.reflect.Method;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import com.shatyuka.zhiliao.Helper;

public class VIPBanner implements IHook {
     static Class<?> MoreHybridView;
    // static Class<?> ZHRecyclerView;
     static Method MonAttachedToWindow;
    // static Method ZonAttachedToWindow;

     boolean MoreHybridStat = true;
     boolean ZHRecyclerStat = true;

    @Override
    public String getName() {

        return "隐藏会员卡片";
    }

    @Override
    public void init(ClassLoader classLoader) throws Throwable {
        MoreHybridView = classLoader.loadClass("com.zhihu.android.app.ui.fragment.more.more.widget.MoreHybridView");
       // ZHRecyclerView = classLoader.loadClass("com.zhihu.android.base.widget.ZHRecyclerView");
  

      
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

       // XposedBridge.hookAllMethods(ZHRecyclerView, "onViewAdded", new XC_MethodHook() {

       //          @Override
       //          protected void afterHookedMethod(MethodHookParam param) throws Throwable {
       //                                                XposedBridge.log("[Zhiliao3] " + param);

       //              super.afterHookedMethod(param);
       //              if (ZHRecyclerStat && param.thisObject.getClass().getName().equals(ZHRecyclerView.getName())) {
       //                                                                             XposedBridge.log("[Zhiliao4] " + param.thisObject);

       //                  View view = (View) param.thisObject;
       //                  try {
       //                      String name = view.getContext().getResources().getResourceEntryName(view.getId());
       //                      if ("function_panel".equals(name) ||
       //                          "new_function_panel".equals(name) ||
       //                          "common_items".equals(name) ||
       //                          "new_common_items".equals(name)) {
                                
       //                          view.setVisibility(View.GONE);
       //                          ZHRecyclerStat = false;
       //                      }
       //                  } catch (Resources.NotFoundException ignored) {}
       //              }
       //          }
       //      });
        }
    }
}

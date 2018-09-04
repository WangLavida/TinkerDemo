package com.demo.tinkerdemo;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.demo.tinkerdemo.tinker.Log.MyLogImp;
import com.demo.tinkerdemo.tinker.reporter.SampleLoadReporter;
import com.demo.tinkerdemo.tinker.reporter.SamplePatchListener;
import com.demo.tinkerdemo.tinker.reporter.SamplePatchReporter;
import com.demo.tinkerdemo.tinker.service.SampleResultService;
import com.demo.tinkerdemo.tinker.util.TinkerManager;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.listener.PatchListener;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.LoadReporter;
import com.tencent.tinker.lib.reporter.PatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by W.J on 2018/9/4.
 */

/**
 * 使用DefaultLifeCycle注解生成Application（这种方式是Tinker官方推荐的）
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = ".MyApplication",// application类名。只能用字符串，这个MyApplication文件是不存在的，但可以在AndroidManifest.xml的application标签上使用（name）
        flags = ShareConstants.TINKER_ENABLE_ALL,// tinkerFlags
        loaderClass = "com.tencent.tinker.loader.TinkerLoader",//loaderClassName, 我们这里使用默认即可!（可不写）
        loadVerifyFlag = false)//tinkerLoadVerifyFlag
public class TinkerApplication extends DefaultApplicationLike {
    private Application mApplication;
    private Context mContext;
    private Tinker mTinker;

    public TinkerApplication(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    // 固定写法
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // 可以将之前自定义的Application中onCreate()方法所执行的操作搬到这里...
//    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        mApplication = getApplication();
        mContext = getApplication();
        initTinker(base);
        // 可以将之前自定义的Application中onCreate()方法所执行的操作搬到这里...
    }

    private void initTinker(Context base) {
        // tinker需要你开启MultiDex
        MultiDex.install(base);

        TinkerManager.setTinkerApplicationLike(this);
        // 设置全局异常捕获
        TinkerManager.initFastCrashProtect();
        //开启升级重试功能（在安装Tinker之前设置）
        TinkerManager.setUpgradeRetryEnable(true);
        //设置Tinker日志输出类
        TinkerInstaller.setLogIml(new MyLogImp());
        //安装Tinker(在加载完multiDex之后，否则你需要将com.tencent.tinker.**手动放到main dex中)
        TinkerManager.installTinker(this);
        //or you can just use DefaultLoadReporter
        LoadReporter loadReporter = new SampleLoadReporter(base);
//or you can just use DefaultPatchReporter
        PatchReporter patchReporter = new SamplePatchReporter(base);
//or you can just use DefaultPatchListener
        PatchListener patchListener = new SamplePatchListener(base);
//you can set your own upgrade patch if you need
        AbstractPatch upgradePatchProcessor = new UpgradePatch();
        TinkerInstaller.install(TinkerApplication.this,
                loadReporter, patchReporter, patchListener,
                SampleResultService.class, upgradePatchProcessor);
        mTinker = Tinker.with(getApplication());
    }

}

package com.polyvrnvoddemo;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.List;

/**
 * @author df
 * @create 2019/2/20
 * @Describe
 */
public class PolyvRNVodPluginPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        return Arrays.<NativeModule>asList(new PolyvRNVodConfigModule(reactContext), new PolyvRNVodDownloadModule(reactContext));
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {

        return Arrays.<ViewManager>asList(
                new PolyvVodPlayer()
        );
    }
}

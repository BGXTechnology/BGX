package com.bgx;

import android.support.multidex.MultiDexApplication;

import com.MakeInAppPurchase.MakeInAppPurchasePackage;
import com.RNFetchBlob.RNFetchBlobPackage;
import com.chilkat.GetPublicPrivateKeyPackage;
import com.chirag.RNMail.RNMail;
import com.evollu.react.fcm.FIRMessagingPackage;
import com.facebook.react.ReactApplication;
import com.lock.LockTaskServicePackage;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.imagepicker.ImagePickerPackage;
import com.oblador.vectoricons.VectorIconsPackage;
import com.reactcommunity.rnlanguages.RNLanguagesPackage;
import com.setting.CheckCameraAndPhotoHelperPackage;

import org.reactnative.camera.RNCameraPackage;

import java.util.Arrays;
import java.util.List;

import cl.json.RNSharePackage;
import cl.json.ShareApplication;
import ui.fileselector.RNFileSelectorPackage;

public class MainApplication extends MultiDexApplication implements ReactApplication, ShareApplication {

    private final ReactNativeHost mReactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            return Arrays.<ReactPackage>asList(
                    new MainReactPackage(),
                    new RNLanguagesPackage(),
                    new RNSharePackage(),
                    new FIRMessagingPackage(),
                    new RNMail(),
                    new RNFileSelectorPackage(),
                    new RNCameraPackage(),
                    new ImagePickerPackage(),
                    new RNFetchBlobPackage(),
                    new VectorIconsPackage(),
                    new GetPublicPrivateKeyPackage(),
                    new CheckCameraAndPhotoHelperPackage(),
                    new MakeInAppPurchasePackage(),
                    new LockTaskServicePackage()
            );
        }

        @Override
        protected String getJSMainModuleName() {
            return "index";
        }
    };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, /* native exopackage */ false);
    }

    @Override
    public String getFileProviderAuthority() {
        return "com.bgx.test.provider";
    }


}

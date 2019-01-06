package com.MakeInAppPurchase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;

public class MakeInAppPurchaseModule extends ReactContextBaseJavaModule implements LifecycleEventListener, ActivityEventListener {

    private BillingProcessor mBillingProcessor;
    private final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjifzHQJ6GoDVKs89kRbyztxRMm68xDanZ8I1KYed4QSZOvpSasuUWBVLlGwiHngaIuMZgzAG+QH5qDUiHhzBrtxxILIGQBup4KfV7Ygs7fAcy/We6AeXUYSrTQ4mRTKzGhz3xdn9MqHOS1UKtPGK+4A1OkRk9ypCpmZnl7zGhc7vzoWbEFZTccU4v4vfxifeBnRkSFd7R4yTtIvJ9h3Vj3z3WgMTRPbqjlYFvFnoOcGYlKfRSoOaZxYWUAzQbRS2zGA+Lb3DL69Z3cvYYJdPMj30H3oTyZpFv+yAp6Rl2fthseMQgI8EnpVQOVtgScqXBMLb5RCDUhFmoqeEwgf59QIDAQAB";
    private Callback mCallback;
    public MakeInAppPurchaseModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addLifecycleEventListener(this);
        reactContext.addActivityEventListener(this);
        mBillingProcessor = new BillingProcessor(reactContext, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                mCallback.invoke(new Gson().toJson(details));
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
//                callbackError.invoke(error.getCause());
            }

            @Override
            public void onBillingInitialized() {

            }

            @Override
            public void onPurchaseHistoryRestored() {
//                for(String sku : bp.listOwnedProducts())
//                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
//                for(String sku : bp.listOwnedSubscriptions())
//                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
            }
        });
        mBillingProcessor.initialize();
    }

    @Override
    public String getName() {
        return "MakeInAppPurchase";
    }

    @ReactMethod
    public void makeTransaction(final Callback callback) {
        Log.e("makeTransaction : ", "zoooooooo");
        mCallback = callback;
        if (!BillingProcessor.isIabServiceAvailable(getCurrentActivity())) {
//            callbackError.invoke("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
            Log.e("makeTransaction : ", "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
            return;
        }
        mBillingProcessor.purchase(getCurrentActivity(), "bgx_test_number_1");
    }


    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {

    }

    @Override
    public void onHostDestroy() {
        if (mBillingProcessor != null)
            mBillingProcessor.release();
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (mBillingProcessor != null) {
            mBillingProcessor.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}

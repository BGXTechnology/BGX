package com.chilkat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;

import com.chilkatsoft.CkByteData;
import com.chilkatsoft.CkCrypt2;
import com.chilkatsoft.CkEcc;
import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkPrivateKey;
import com.chilkatsoft.CkPrng;
import com.chilkatsoft.CkPublicKey;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class GetPublicPrivateKeyModule extends ReactContextBaseJavaModule {
    private final String TAG = GetPublicPrivateKeyModule.class.getName();
    private final String KEY_CHILKAT = "AKHVAT.CB4112019_nu3k6EN2no4h";
    private CkPublicKey mPubKey;
    private CkPrivateKey mPrivKey;
    private String mFormatJson = "{\"address_to\": \"%s\", \"coin_code\": \"%s\", \"reason\": \"%s\", \"tx_payload\": %s}";
    private String mFormatJsonMake = "{\"address_from\": \"%s\", \"address_to\": \"%s\", \"coin_code\": \"%s\", \"tx_payload\": %s}";

    public GetPublicPrivateKeyModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "GetPublicPrivateKey";
    }


    @ReactMethod
    public void getPublicPrivateKey(Callback enterKey) {
        CkGlobal glob = new CkGlobal();
        boolean success1 = glob.UnlockBundle(KEY_CHILKAT);
        if (success1 != true) {
            Log.e(TAG, glob.lastErrorText());
            return;
        }
        //  The LastErrorText can be examined in the success case to see if it was unlocked in
        //  trial more, or with a purchased unlock code.
        Log.e(TAG, glob.lastErrorText());

        CkPrng fortuna = new CkPrng();
        String entropy = fortuna.getEntropy(32, "base64");
        boolean success = fortuna.AddEntropy(entropy, "base64");
        CkEcc ecc = new CkEcc();
        //  Generate a random ECC private key on the secp256r1 curve.
        //  Chilkat also supports other curves, such as secp384r1, secp521r1, and secp256k1.
        mPrivKey = ecc.GenEccKey("secp256k1", fortuna);
        if (mPrivKey == null) {
            Log.e(TAG, ecc.lastErrorText());
            enterKey.invoke("", "");
            return;
        }

        //  Now get the public key part from the private key;.
        //  (A public key is actually a subset of the private key.)
        mPubKey = mPrivKey.GetPublicKey();
        String publicKey = mPubKey.getPkcs1ENC("base64");


        String priKey = mPrivKey.getPkcs1Pem().replace("-----BEGIN PRIVATE KEY-----", "");
        priKey = priKey.replace("-----END PRIVATE KEY-----", "");

        CkCrypt2 ckCrypt2 = new CkCrypt2();
        ckCrypt2.put_HashAlgorithm("sha256");
        ckCrypt2.put_Charset("utf-8");
        ckCrypt2.put_EncodingMode("base64url");
        String publicKeyHashed = ckCrypt2.hashStringENC(publicKey);

        enterKey.invoke(priKey.trim().toString(), publicKey, publicKeyHashed);
        //  The public key PEM looks like this:
        Log.e(TAG, "finished.");

    }

    @ReactMethod
    public void getHashPublicKey(String publicKey, Callback callback) {
        CkGlobal glob = new CkGlobal();
        boolean success1 = glob.UnlockBundle(KEY_CHILKAT);
        if (success1 != true) {
            Log.e(TAG, glob.lastErrorText());
            return;
        }
        CkCrypt2 ckCrypt2 = new CkCrypt2();
        ckCrypt2.put_HashAlgorithm("sha256");
        ckCrypt2.put_Charset("utf-8");
        ckCrypt2.put_EncodingMode("base64url");
        String publicKeyHashed = ckCrypt2.hashStringENC(publicKey);

        callback.invoke(publicKeyHashed);
    }

    @ReactMethod
    public void saveFile(String passPem, String nameFile, String privateKey, Callback callback) {
        CkGlobal glob = new CkGlobal();
        boolean success1 = glob.UnlockBundle(KEY_CHILKAT);
        if (success1 != true) {
            Log.e(TAG, glob.lastErrorText());
            callback.invoke("", glob.lastErrorText());
        }
        File folder = new File(Environment.getExternalStorageDirectory() + "/bgx");
        try {
            folder.mkdir();
            File myFile = new File(folder.getAbsolutePath() + "/" + nameFile);
            myFile.createNewFile();
            mPrivKey = new CkPrivateKey();
            CkByteData ckByteData = new CkByteData();
            ckByteData.appendEncoded(privateKey, "base64");
            mPrivKey = new CkPrivateKey();
            boolean success = mPrivKey.LoadPkcs1(ckByteData);
            if (success != true) {
                Log.e(TAG, mPrivKey.lastErrorText());
                callback.invoke("", mPrivKey.lastErrorText());
                return;
            }
            success = mPrivKey.SavePkcs8EncryptedFile(passPem, myFile.getAbsolutePath());
            if (success != true) {
                Log.e(TAG, mPrivKey.lastErrorText());
            }
            callback.invoke(myFile.getAbsolutePath(), mPrivKey.lastErrorText());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @ReactMethod
    public void loadFilePrivateKey(String keyPem, String pathFile, Callback callback, Callback error) {
        CkGlobal glob = new CkGlobal();
        boolean success1 = glob.UnlockBundle(KEY_CHILKAT);
        if (success1 != true) {
            Log.e(TAG, glob.lastErrorText());
            error.invoke(glob.lastErrorText());
        }
        mPrivKey = new CkPrivateKey();
        boolean success = mPrivKey.LoadPkcs8EncryptedFile(pathFile, keyPem);
        if (success != true) {
            Log.e(TAG, mPrivKey.lastErrorText());
            error.invoke(mPrivKey.lastErrorText());
            return;
        }
        String priKey = mPrivKey.getPkcs1Pem().replace("-----BEGIN PRIVATE KEY-----", "");
        priKey = priKey.replace("-----END PRIVATE KEY-----", "");

        mPubKey = mPrivKey.GetPublicKey();
        String publicKey = mPubKey.getPkcs1ENC("base64");

        CkCrypt2 ckCrypt2 = new CkCrypt2();
        ckCrypt2.put_HashAlgorithm("sha256");
        ckCrypt2.put_Charset("utf-8");
        ckCrypt2.put_EncodingMode("base64url");
        String publicKeyHashed = ckCrypt2.hashStringENC(publicKey);

        Log.e("loadFilePrivateKey ", priKey);
        Log.e("loadFilePrivateKey ", publicKey);
        Log.e("loadFilePrivateKey ", publicKeyHashed);
        callback.invoke(priKey, publicKey, publicKeyHashed);
    }

    @ReactMethod
    public void loadStringPrivateKey(String privateKey, Callback callback, Callback error) {
        CkGlobal glob = new CkGlobal();
        boolean success1 = glob.UnlockBundle(KEY_CHILKAT);
        if (success1 != true) {
            Log.e(TAG, glob.lastErrorText());
            error.invoke(glob.lastErrorText());
            return;
        }
//        mPrivKey = new CkPrivateKey();
//        CkStringBuilder sbPem = new CkStringBuilder();
//        boolean bCrlf = true;
//        sbPem.Clear();
//        String[] priKeys = privateKey.split("\r\n");
//        sbPem.AppendLine("-----BEGIN PRIVATE KEY-----", bCrlf);
//        sbPem.AppendLine(priKeys[0], bCrlf);
//        sbPem.AppendLine(priKeys[1], bCrlf);
//        sbPem.AppendLine("-----END PRIVATE KEY-----", bCrlf);
//        boolean success = mPrivKey.LoadPem(sbPem.getAsString());
//        if (success != true) {
//            Log.e(TAG, mPrivKey.lastErrorText());
//            error.invoke(mPrivKey.lastErrorText());
//            return;
//        }
        mPrivKey = new CkPrivateKey();
        CkByteData ckByteData = new CkByteData();
        ckByteData.appendEncoded(privateKey, "base64");
        boolean success = mPrivKey.LoadPkcs1(ckByteData);
        if (success != true) {
            Log.e(TAG, mPrivKey.lastErrorText());
            error.invoke(mPrivKey.lastErrorText());
            return;
        }

        Log.e(TAG, mPrivKey.GetPublicKey().getPkcs1ENC("base64"));
        mPubKey = mPrivKey.GetPublicKey();
        String publicKey = mPubKey.getPkcs1ENC("base64");

        CkCrypt2 ckCrypt2 = new CkCrypt2();
        ckCrypt2.put_HashAlgorithm("sha256");
        ckCrypt2.put_Charset("utf-8");
        ckCrypt2.put_EncodingMode("base64url");
        String publicKeyHashed = ckCrypt2.hashStringENC(publicKey);

        callback.invoke(privateKey, publicKey, publicKeyHashed);
    }

    @ReactMethod
    public void getSignedPayload(String addressTo, String payload,
                                 String nameCard, String reason,
                                 String privateKey,
                                 Callback callback, Callback error) {
        CkGlobal glob = new CkGlobal();
        boolean success1 = glob.UnlockBundle(KEY_CHILKAT);
        if (success1 != true) {
            Log.e(TAG, glob.lastErrorText());
            error.invoke(glob.lastErrorText());
        }
        CkEcc ecdsa = new CkEcc();
        CkCrypt2 crypt = new CkCrypt2();
        crypt.put_HashAlgorithm("sha256");
        crypt.put_Charset("utf-8");
        crypt.put_EncodingMode("base64");
        Log.e("getSignedPayload", String.format(mFormatJson, addressTo, nameCard, reason, payload));
        String sha256Hash = crypt.hashStringENC(String.format(mFormatJson, addressTo, nameCard, reason, payload));
        CkByteData ckByteData = new CkByteData();
        ckByteData.appendEncoded(privateKey, "base64");
        mPrivKey = new CkPrivateKey();
        boolean success = mPrivKey.LoadPkcs1(ckByteData);
        if (success != true) {
            Log.e(TAG, mPrivKey.lastErrorText());
            error.invoke(mPrivKey.lastErrorText());
            return;
        }
        String signPayload = ecdsa.signHashENC(sha256Hash, "base64", mPrivKey, new CkPrng());
        Log.e("signPayload", signPayload);
        callback.invoke(signPayload);
    }

    @ReactMethod
    public void getMakeSignedPayload(String addressFrom, String addressTo,
                                     String nameCard, String payload,
                                     String privateKey,
                                     Callback callback, Callback error) {
        CkGlobal glob = new CkGlobal();
        boolean success1 = glob.UnlockBundle(KEY_CHILKAT);
        if (success1 != true) {
            Log.e(TAG, glob.lastErrorText());
            error.invoke(glob.lastErrorText());
        }
        CkEcc ecdsa = new CkEcc();
        CkCrypt2 crypt = new CkCrypt2();
        crypt.put_HashAlgorithm("sha256");
        crypt.put_Charset("utf-8");
        crypt.put_EncodingMode("base64");

        String sha256Hash = crypt.hashStringENC(String.format(mFormatJsonMake, addressFrom, addressTo, nameCard, payload));

        CkByteData ckByteData = new CkByteData();
        ckByteData.appendEncoded(privateKey, "base64");
        mPrivKey = new CkPrivateKey();
        boolean success = mPrivKey.LoadPkcs1(ckByteData);
        if (success != true) {
            Log.e(TAG, mPrivKey.lastErrorText());
            error.invoke(mPrivKey.lastErrorText());
            return;
        }
        String signPayload = ecdsa.signHashENC(sha256Hash, "base64", mPrivKey, new CkPrng());
        callback.invoke(signPayload);
    }

    @ReactMethod
    public void checkExitFile(String nameFile, Callback callback) {
        File file = new File(Environment.getExternalStorageDirectory() + "/bgx/" + nameFile);
        callback.invoke(file.exists());
    }


    @ReactMethod
    public void checkEmailDevice(Callback callback) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getCurrentActivity()).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                callback.invoke(true);
                return;
            }
        }
        callback.invoke(false);
    }


    static {
        System.loadLibrary("chilkat");
        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }
}

package com.bgx;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.chilkatsoft.CkEcc;
import com.chilkatsoft.CkGlobal;
import com.chilkatsoft.CkPrivateKey;
import com.chilkatsoft.CkPrng;
import com.chilkatsoft.CkPublicKey;

import java.io.File;
import java.io.IOException;


public class TestActivity extends AppCompatActivity {
    private final String TAG = TestActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        CkGlobal glob = new CkGlobal();
        boolean success1 = glob.UnlockBundle("Anything for 30-day trial");
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
        CkPrivateKey privKey = ecc.GenEccKey("secp256k1", fortuna);
        if (privKey == null) {
            Log.e(TAG, ecc.lastErrorText());
            return;
        }

        //  Save the private key to PKCS8 encrypted PEM
        //  (The private key can be saved in a variety of different formats. See the online reference documentation.)
        Log.e(TAG, privKey.toString());
        File myFile = new File(Environment.getExternalStorageDirectory() + "/eccPrivKey.pem");
        try {
            myFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        success = privKey.SavePkcs8EncryptedPemFile("pemPassword", myFile.getAbsolutePath());
        if (success != true) {
            Log.e(TAG, privKey.lastErrorText());
        }

        //  The private key PEM looks like this:

        //  	-----BEGIN ENCRYPTED PRIVATE KEY-----
        //  	MIHFMEAGCSqGSIb3DQEFDTAzMBsGCSqGSIb3DQEFDDAOBAhUmn+1/lwCIwICCAAw
        //  	FAYIKoZIhvcNAwcECPlyNXAXZO+oBIGAIvxvTENXJWbrCwSjh0QNxLecBotUpfI5
        //  	auOonLmwVMyt1ahMmNyVo/D+pnKQSE41Fg8fApM0DCDKZUOVCCcK1qirIsMPQkGp
        //  	klPJbvrQRVYgzBtU31uNB5y2wqhrIeepal1HXBvbkUK0nfJMbcdz/XAEIQu2HhTD
        //  	t6LMScPQld4=
        //  	-----END ENCRYPTED PRIVATE KEY-----

        //  Now get the public key part from the private key.
        //  (A public key is actually a subset of the private key.)
        CkPublicKey pubKey = privKey.GetPublicKey();

        //  Save the public key to a PEM file.
        //  (The public key can be saved in a variety of different formats. See the online reference documentation.)
        //  For ECC keys, the 1st argument (bPreferPkcs1) is ignored and unused.
        boolean bPreferPkcs1 = false;
        File eccPubKey = new File(Environment.getExternalStorageDirectory() + "/eccPubKey.pem");
        try {
            eccPubKey.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        success = pubKey.SavePemFile(bPreferPkcs1, eccPubKey.getAbsolutePath());

        //  The public key PEM looks like this:

        //  	-----BEGIN PUBLIC KEY-----
        //  	MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAA
        //  	AAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA////
        //  	///////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSd
        //  	NgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5
        //  	RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA
        //  	//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABGdOH8uM6SfX3mdV+TR0mWp2
        //  	gfVRPlxWxwhOiowuNByBxmQz7ZR4CJY1XcN2zkUo5pzW73ZhGwgd2XTOvqPqG40=
        //  	-----END PUBLIC KEY-----
        //
        Log.e(TAG, "finished.");

    }

    static {
        System.loadLibrary("chilkat");

        // Note: If the incorrect library name is passed to System.loadLibrary,
        // then you will see the following error message at application startup:
        //"The application <your-application-name> has stopped unexpectedly. Please try again."
    }
}


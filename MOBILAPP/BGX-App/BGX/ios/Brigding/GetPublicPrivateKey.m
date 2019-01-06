//
//  GetPublicPrivateKey.m
//  BGX
//
//  Created by THAI LE QUANG on 9/6/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <React/RCTViewManager.h>
#import <React/RCTLog.h>
#import <React/UIView+React.h>

#import "CkoPrng.h"
#import "CkoEcc.h"
#import "CkoPrivateKey.h"
#import "CkoPublicKey.h"
#import "CkoGlobal.h"
#import "CkoCrypt2.h"
#import "CkoStringBuilder.h"
#import <MessageUI/MessageUI.h>
#import "CkoBinData.h"

@interface GetPublicPrivateKey : RCTViewManager
@property (nonatomic,strong)CkoPrivateKey   *privKey;
@property (nonatomic,strong)CkoPublicKey    *pubKey;
@property (nonatomic,strong)CkoCrypt2       *ckoCrypt2;
@property (nonatomic,strong)CkoEcc          *ecdsa;
@end

@implementation GetPublicPrivateKey

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(getPublicPrivateKey:(RCTResponseSenderBlock)callback)
{
  CkoGlobal *glob = [[CkoGlobal alloc] init];
  BOOL success = [glob UnlockBundle: @"AKHVAT.CB4112019_nu3k6EN2no4h"];
  if (success != YES) {
    NSLog(@"%@",glob.LastErrorText);
    return;
  }
  
  CkoPrng *fortuna = [[CkoPrng alloc] init];
  NSString *entropy = [fortuna GetEntropy: [NSNumber numberWithInt: 32] encoding: @"base64"];
  success = [fortuna AddEntropy: entropy encoding: @"base64"];
  
  CkoEcc *ecc = [[CkoEcc alloc] init];
  
  self.privKey = [ecc GenEccKey: @"secp256k1" prng: fortuna];
  if (self.privKey == nil ) {
    NSLog(@"%@",ecc.LastErrorText);
    return;
  }
  
  NSString *privateKeyString = [self.privKey GetPkcs1Pem];
  
  privateKeyString = [privateKeyString stringByReplacingOccurrencesOfString:@"-----BEGIN PRIVATE KEY-----" withString:@""];
  privateKeyString = [privateKeyString stringByReplacingOccurrencesOfString:@"-----END PRIVATE KEY-----" withString:@""];
  privateKeyString = [privateKeyString stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
  NSLog(@"%@",privateKeyString);
  
  //  Now get the public key part from the private key.
  //  (A public key is actually a subset of the private key.)
  self.pubKey = [self.privKey GetPublicKey];
  //    NSLog(@"%@",[pubKey GetXml]);
  
  NSString *publicKeyString = [self.pubKey GetPkcs1ENC:@"base64"];
  NSLog(@"%@", publicKeyString);
  
  self.ckoCrypt2 = [[CkoCrypt2 alloc] init];
  self.ckoCrypt2.HashAlgorithm = @"sha256";
  self.ckoCrypt2.Charset = @"utf-8";
  self.ckoCrypt2.EncodingMode = @"base64url";
  NSString *publicKeyHashed = [self.ckoCrypt2 HashStringENC:publicKeyString];
  
  callback(@[privateKeyString, publicKeyString, publicKeyHashed]);
}

RCT_EXPORT_METHOD(saveFile:(NSString *)passPem nameFile:(NSString*)nameFile privateKey: (NSString *)privateKey callback:(RCTResponseSenderBlock)callback)
{
  CkoGlobal *glob = [[CkoGlobal alloc] init];
  BOOL success = [glob UnlockBundle: @"AKHVAT.CB4112019_nu3k6EN2no4h"];
  if (success != YES) {
    NSLog(@"%@",glob.LastErrorText);
    return;
  }
 
  NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
  NSString *documentsDirectory = [paths objectAtIndex:0];
  NSString *filePath = [documentsDirectory stringByAppendingPathComponent:nameFile];
  
  self.privKey = [[CkoPrivateKey alloc] init];
  CkoBinData *binData = [[CkoBinData alloc] init];
  [binData AppendEncoded:privateKey encoding:@"base64"];
  success = [self.privKey LoadPkcs1:[binData GetBinary]];
  if (success != YES) {
    NSLog(@"%@",self.privKey.LastErrorText);
    callback(@[self.privKey.LastErrorText]);
    return;
  }
  //  Save the private key to encrypted binary PKCS8
  success = [self.privKey SavePkcs8EncryptedFile:passPem path: filePath];
  if (success != YES) {
    NSLog(@"%@", self.privKey.LastErrorText);
  }
  callback(@[filePath, self.privKey.LastErrorText]);
}

RCT_EXPORT_METHOD(loadStringPrivateKey: (NSString *)privateKey callback:(RCTResponseSenderBlock)callback error:(RCTResponseSenderBlock)error)
{
  CkoGlobal *glob = [[CkoGlobal alloc] init];
  BOOL success = [glob UnlockBundle: @"AKHVAT.CB4112019_nu3k6EN2no4h"];
  if (success != YES) {
    NSLog(@"%@",glob.LastErrorText);
    return;
  }
//  CkoStringBuilder *sbPem = [[CkoStringBuilder alloc] init];
//  BOOL bCrlf = YES;
//  NSArray *priKeys = [privateKey componentsSeparatedByString:@"\r\n"];
//
//  [sbPem Clear];
//  [sbPem AppendLine: @"-----BEGIN PRIVATE KEY-----" crlf: bCrlf];
//  [sbPem AppendLine: [priKeys objectAtIndex:0] crlf: bCrlf];
//  [sbPem AppendLine: [priKeys objectAtIndex:1] crlf: bCrlf];
//  [sbPem AppendLine: @"-----END PRIVATE KEY-----" crlf: bCrlf];
//  success = [self.privKey LoadPem: [sbPem GetAsString]];
//  if (success != YES) {
//    NSLog(@"%@",self.privKey.LastErrorText);
//    error(@[self.privKey.LastErrorText]);
//    return;
//  }
  self.privKey = [[CkoPrivateKey alloc] init];
  CkoBinData *binData = [[CkoBinData alloc] init];
  [binData AppendEncoded:privateKey encoding:@"base64"];
  success = [self.privKey LoadPkcs1:[binData GetBinary]];
  if (success != YES) {
    NSLog(@"%@",self.privKey.LastErrorText);
    error(@[self.privKey.LastErrorText]);
    return;
  }
  
  self.pubKey = [self.privKey GetPublicKey];
  //    NSLog(@"%@",[pubKey GetXml]);
  
  NSString *publicKeyString = [self.pubKey GetPkcs1ENC:@"base64"];
  NSLog(@"%@", publicKeyString);
  
  self.ckoCrypt2 = [[CkoCrypt2 alloc] init];
  self.ckoCrypt2.HashAlgorithm = @"sha256";
  self.ckoCrypt2.Charset = @"utf-8";
  self.ckoCrypt2.EncodingMode = @"base64url";
  NSString *publicKeyHashed = [self.ckoCrypt2 HashStringENC:publicKeyString];
  
  callback(@[privateKey, publicKeyString, publicKeyHashed]);
  
}

RCT_EXPORT_METHOD(getSignedPayload: (NSString *)addressTo payload:(NSString *)payload nameCard:(NSString *)nameCard reason:(NSString *)reason privateKey:(NSString *)privateKey  callback:(RCTResponseSenderBlock)callback  callbackError:(RCTResponseSenderBlock)callbackError)
{
  CkoGlobal *glob = [[CkoGlobal alloc] init];
  BOOL success = [glob UnlockBundle: @"AKHVAT.CB4112019_nu3k6EN2no4h"];
  if (success != YES) {
    NSLog(@"%@",glob.LastErrorText);
    return;
  }

  self.privKey = [[CkoPrivateKey alloc] init];
  CkoBinData *binData = [[CkoBinData alloc] init];
  [binData AppendEncoded:privateKey encoding:@"base64"];
  success = [self.privKey LoadPkcs1:[binData GetBinary]];
  if (success != YES) {
    NSLog(@"%@",self.privKey.LastErrorText);
    callbackError(@[self.privKey.LastErrorText]);
    return;
  }
  CkoPrng *fortuna = [[CkoPrng alloc] init];
  self.ecdsa = [[CkoEcc alloc] init];
  self.ckoCrypt2 = [[CkoCrypt2 alloc] init];
  self.ckoCrypt2.HashAlgorithm = @"sha256";
  self.ckoCrypt2.Charset = @"utf-8";
  self.ckoCrypt2.EncodingMode = @"base64";
  
  NSString *template = @"{\"address_to\": \"%@\", \"coin_code\": \"%@\", \"reason\": \"%@\", \"tx_payload\": %@}";
  NSString *json = [NSString stringWithFormat:template,addressTo, nameCard, reason, payload];
  
  NSString *sha256Hash = [self.ckoCrypt2 HashStringENC:json];
  NSString *signPlayload = [self.ecdsa SignHashENC:sha256Hash encoding:@"base64" privkey:self.privKey prng:(CkoPrng *)fortuna];
  callback(@[signPlayload]);
  
}

RCT_EXPORT_METHOD(getMakeSignedPayload: (NSString *)addressFrom addressTo:(NSString *)addressTo nameCard:(NSString *)nameCard payload:(NSString *)payload privateKey:(NSString *)privateKey  callback:(RCTResponseSenderBlock)callback  callbackError:(RCTResponseSenderBlock)callbackError)
{
  CkoGlobal *glob = [[CkoGlobal alloc] init];
  BOOL success = [glob UnlockBundle: @"AKHVAT.CB4112019_nu3k6EN2no4h"];
  if (success != YES) {
    NSLog(@"%@",glob.LastErrorText);
    return;
  }
  
  self.privKey = [[CkoPrivateKey alloc] init];
  CkoBinData *binData = [[CkoBinData alloc] init];
  [binData AppendEncoded:privateKey encoding:@"base64"];
  success = [self.privKey LoadPkcs1:[binData GetBinary]];
  if (success != YES) {
    NSLog(@"%@",self.privKey.LastErrorText);
    callbackError(@[self.privKey.LastErrorText]);
    return;
  }
  CkoPrng *fortuna = [[CkoPrng alloc] init];
  self.ecdsa = [[CkoEcc alloc] init];
  self.ckoCrypt2 = [[CkoCrypt2 alloc] init];
  self.ckoCrypt2.HashAlgorithm = @"sha256";
  self.ckoCrypt2.Charset = @"utf-8";
  self.ckoCrypt2.EncodingMode = @"base64";
  
  NSString *template = @"{\"address_from\": \"%@\", \"address_to\": \"%@\", \"coin_code\": \"%@\", \"tx_payload\": %@}";
  NSString *json = [NSString stringWithFormat:template,addressFrom, addressTo, nameCard, payload];
  
  NSString *sha256Hash = [self.ckoCrypt2 HashStringENC:json];
  NSString *signPlayload = [self.ecdsa SignHashENC:sha256Hash encoding:@"base64" privkey:self.privKey prng:(CkoPrng *)fortuna];
  callback(@[signPlayload]);
  
}


RCT_EXPORT_METHOD(getHashPublicKey: (NSString *)publicKey callback:(RCTResponseSenderBlock)callback)
{
  NSLog(@"%@" , publicKey);
  CkoGlobal *glob = [[CkoGlobal alloc] init];
  BOOL success = [glob UnlockBundle: @"AKHVAT.CB4112019_nu3k6EN2no4h"];
  if (success != YES) {
    NSLog(@"%@",glob.LastErrorText);
    return;
  }
  
  self.ckoCrypt2 = [[CkoCrypt2 alloc] init];
  self.ckoCrypt2.HashAlgorithm = @"sha256";
  self.ckoCrypt2.Charset = @"utf-8";
  self.ckoCrypt2.EncodingMode = @"base64url";
  NSString *publicKeyHashed = [self.ckoCrypt2 HashStringENC:publicKey];
  callback(@[publicKeyHashed]);
}

RCT_EXPORT_METHOD(checkEmailDevice:(RCTResponseSenderBlock)callback)
{
  if ([MFMailComposeViewController canSendMail]){
    callback(@[@"true"]);
      return;
  }else{
    callback(@[@"false"]);
  }
}
//- (void)generateKey {
//  CkoGlobal *glob = [[CkoGlobal alloc] init];
//  BOOL success = [glob UnlockBundle: @"Anything for 30-day trial"];
//  if (success != YES) {
//    NSLog(@"%@",glob.LastErrorText);
//    return;
//  }
//
//  //  The LastErrorText can be examined in the success case to see if it was unlocked in
//  //  trial more, or with a purchased unlock code.
//  NSLog(@"%@",glob.LastErrorText);
//
//  CkoPrng *fortuna = [[CkoPrng alloc] init];
//  NSString *entropy = [fortuna GetEntropy: [NSNumber numberWithInt: 32] encoding: @"base64"];
//  success = [fortuna AddEntropy: entropy encoding: @"base64"];
//
//  CkoEcc *ecc = [[CkoEcc alloc] init];
//
//  //  Generate a random ECC private key on the secp256r1 curve.
//  //  Chilkat also supports other curves, such as secp384r1, secp521r1, and secp256k1.
//
//  CkoPrivateKey *privKey = [ecc GenEccKey: @"secp256r1" prng: fortuna];
//  NSLog(@"%@",[privKey GetPkcs1Pem]);
//  if (privKey == nil ) {
//    NSLog(@"%@",ecc.LastErrorText);
//    return;
//  }
//
//  //  Save the private key to PKCS8 encrypted PEM
//  //  (The private key can be saved in a variety of different formats. See the online reference documentation.)
//  success = [privKey SavePkcs8EncryptedPemFile: @"pemPassword" path: @"qa_output/eccPrivKey.pem"];
//  if (success != YES) {
//    NSLog(@"%@",privKey.LastErrorText);
//  }
//
//  //  The private key PEM looks like this:
//
//  //    -----BEGIN ENCRYPTED PRIVATE KEY-----
//  //    MIHFMEAGCSqGSIb3DQEFDTAzMBsGCSqGSIb3DQEFDDAOBAhUmn+1/lwCIwICCAAw
//  //    FAYIKoZIhvcNAwcECPlyNXAXZO+oBIGAIvxvTENXJWbrCwSjh0QNxLecBotUpfI5
//  //    auOonLmwVMyt1ahMmNyVo/D+pnKQSE41Fg8fApM0DCDKZUOVCCcK1qirIsMPQkGp
//  //    klPJbvrQRVYgzBtU31uNB5y2wqhrIeepal1HXBvbkUK0nfJMbcdz/XAEIQu2HhTD
//  //    t6LMScPQld4=
//  //    -----END ENCRYPTED PRIVATE KEY-----
//
//  //  Now get the public key part from the private key.
//  //  (A public key is actually a subset of the private key.)
//  CkoPublicKey *pubKey = [privKey GetPublicKey];
//  NSLog(@"%@",[pubKey GetXml]);
//  NSLog(@"%@",[pubKey GetPkcs1ENC:@"base64"]);
//  //  Save the public key to a PEM file.
//  //  (The public key can be saved in a variety of different formats. See the online reference documentation.)
//  //  For ECC keys, the 1st argument (bPreferPkcs1) is ignored and unused.
//  BOOL bPreferPkcs1 = NO;
//  success = [pubKey SavePemFile: bPreferPkcs1 path: @"qa_output/eccPubKey.pem"];
//}

@end

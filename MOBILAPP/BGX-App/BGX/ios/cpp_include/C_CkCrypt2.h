// This is a generated source file for Chilkat version 9.5.0.73
#ifndef _C_CkCrypt2_H
#define _C_CkCrypt2_H
#include "chilkatDefs.h"

#include "Chilkat_C.h"


CK_VISIBLE_PUBLIC void CkCrypt2_setAbortCheck(HCkCrypt2 cHandle, BOOL (*fnAbortCheck)(void));
CK_VISIBLE_PUBLIC void CkCrypt2_setPercentDone(HCkCrypt2 cHandle, BOOL (*fnPercentDone)(int pctDone));
CK_VISIBLE_PUBLIC void CkCrypt2_setProgressInfo(HCkCrypt2 cHandle, void (*fnProgressInfo)(const char *name, const char *value));
CK_VISIBLE_PUBLIC void CkCrypt2_setTaskCompleted(HCkCrypt2 cHandle, void (*fnTaskCompleted)(HCkTask hTask));

CK_VISIBLE_PUBLIC HCkCrypt2 CkCrypt2_Create(void);
CK_VISIBLE_PUBLIC void CkCrypt2_Dispose(HCkCrypt2 handle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getAbortCurrent(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putAbortCurrent(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC int CkCrypt2_getBCryptWorkFactor(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putBCryptWorkFactor(HCkCrypt2 cHandle, int newVal);
CK_VISIBLE_PUBLIC int CkCrypt2_getBlockSize(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getCadesEnabled(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putCadesEnabled(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getCadesSigPolicyHash(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putCadesSigPolicyHash(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_cadesSigPolicyHash(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getCadesSigPolicyId(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putCadesSigPolicyId(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_cadesSigPolicyId(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getCadesSigPolicyUri(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putCadesSigPolicyUri(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_cadesSigPolicyUri(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getCharset(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putCharset(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_charset(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getCipherMode(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putCipherMode(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_cipherMode(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getCompressionAlgorithm(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putCompressionAlgorithm(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_compressionAlgorithm(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getCryptAlgorithm(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putCryptAlgorithm(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_cryptAlgorithm(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getDebugLogFilePath(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putDebugLogFilePath(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_debugLogFilePath(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getEncodingMode(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putEncodingMode(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_encodingMode(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getFirstChunk(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putFirstChunk(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getHashAlgorithm(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putHashAlgorithm(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_hashAlgorithm(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC int CkCrypt2_getHavalRounds(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putHavalRounds(HCkCrypt2 cHandle, int newVal);
CK_VISIBLE_PUBLIC int CkCrypt2_getHeartbeatMs(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putHeartbeatMs(HCkCrypt2 cHandle, int newVal);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getIncludeCertChain(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putIncludeCertChain(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC int CkCrypt2_getInitialCount(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putInitialCount(HCkCrypt2 cHandle, int newVal);
CK_VISIBLE_PUBLIC int CkCrypt2_getIterationCount(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putIterationCount(HCkCrypt2 cHandle, int newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getIV(HCkCrypt2 cHandle, HCkByteData retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putIV(HCkCrypt2 cHandle, HCkByteData newVal);
CK_VISIBLE_PUBLIC int CkCrypt2_getKeyLength(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putKeyLength(HCkCrypt2 cHandle, int newVal);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getLastChunk(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putLastChunk(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getLastErrorHtml(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const char *CkCrypt2_lastErrorHtml(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getLastErrorText(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const char *CkCrypt2_lastErrorText(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getLastErrorXml(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const char *CkCrypt2_lastErrorXml(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getLastMethodSuccess(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putLastMethodSuccess(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getMacAlgorithm(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putMacAlgorithm(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_macAlgorithm(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC int CkCrypt2_getNumSignerCerts(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getOaepHash(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putOaepHash(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_oaepHash(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getOaepMgfHash(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putOaepMgfHash(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_oaepMgfHash(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getOaepPadding(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putOaepPadding(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC int CkCrypt2_getPaddingScheme(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putPaddingScheme(HCkCrypt2 cHandle, int newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getPbesAlgorithm(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putPbesAlgorithm(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_pbesAlgorithm(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getPbesPassword(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putPbesPassword(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_pbesPassword(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getPkcs7CryptAlg(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putPkcs7CryptAlg(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_pkcs7CryptAlg(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC int CkCrypt2_getRc2EffectiveKeyLength(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putRc2EffectiveKeyLength(HCkCrypt2 cHandle, int newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getSalt(HCkCrypt2 cHandle, HCkByteData retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putSalt(HCkCrypt2 cHandle, HCkByteData newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getSecretKey(HCkCrypt2 cHandle, HCkByteData retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putSecretKey(HCkCrypt2 cHandle, HCkByteData newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getSigningAlg(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putSigningAlg(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_signingAlg(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getUtf8(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putUtf8(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getUuFilename(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putUuFilename(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_uuFilename(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_getUuMode(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void CkCrypt2_putUuMode(HCkCrypt2 cHandle, const char *newVal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_uuMode(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_getVerboseLogging(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_putVerboseLogging(HCkCrypt2 cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkCrypt2_getVersion(HCkCrypt2 cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const char *CkCrypt2_version(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_AddEncryptCert(HCkCrypt2 cHandle, HCkCert cert);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_AddPfxSourceData(HCkCrypt2 cHandle, HCkByteData pfxBytes, const char *pfxPassword);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_AddPfxSourceFile(HCkCrypt2 cHandle, const char *pfxFilePath, const char *pfxPassword);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_AesKeyUnwrap(HCkCrypt2 cHandle, const char *kek, const char *wrappedKeyData, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_aesKeyUnwrap(HCkCrypt2 cHandle, const char *kek, const char *wrappedKeyData, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_AesKeyWrap(HCkCrypt2 cHandle, const char *kek, const char *keyData, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_aesKeyWrap(HCkCrypt2 cHandle, const char *kek, const char *keyData, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_BCryptHash(HCkCrypt2 cHandle, const char *password, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_bCryptHash(HCkCrypt2 cHandle, const char *password);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_BCryptVerify(HCkCrypt2 cHandle, const char *password, const char *bcryptHash);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_BytesToString(HCkCrypt2 cHandle, HCkByteData inData, const char *charset, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_bytesToString(HCkCrypt2 cHandle, HCkByteData inData, const char *charset);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_ByteSwap4321(HCkCrypt2 cHandle, HCkByteData data, HCkByteData outBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CkDecryptFile(HCkCrypt2 cHandle, const char *srcFile, const char *destFile);
CK_VISIBLE_PUBLIC HCkTask CkCrypt2_CkDecryptFileAsync(HCkCrypt2 cHandle, const char *srcFile, const char *destFile);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CkEncryptFile(HCkCrypt2 cHandle, const char *srcFile, const char *destFile);
CK_VISIBLE_PUBLIC HCkTask CkCrypt2_CkEncryptFileAsync(HCkCrypt2 cHandle, const char *srcFile, const char *destFile);
CK_VISIBLE_PUBLIC void CkCrypt2_ClearEncryptCerts(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CompressBytes(HCkCrypt2 cHandle, HCkByteData data, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CompressBytesENC(HCkCrypt2 cHandle, HCkByteData data, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_compressBytesENC(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CompressString(HCkCrypt2 cHandle, const char *str, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CompressStringENC(HCkCrypt2 cHandle, const char *str, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_compressStringENC(HCkCrypt2 cHandle, const char *str);
CK_VISIBLE_PUBLIC unsigned long CkCrypt2_CrcBytes(HCkCrypt2 cHandle, const char *crcAlg, HCkByteData byteData);
CK_VISIBLE_PUBLIC unsigned long CkCrypt2_CrcFile(HCkCrypt2 cHandle, const char *crcAlg, const char *path);
CK_VISIBLE_PUBLIC HCkTask CkCrypt2_CrcFileAsync(HCkCrypt2 cHandle, const char *crcAlg, const char *path);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CreateDetachedSignature(HCkCrypt2 cHandle, const char *inFilePath, const char *sigFilePath);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CreateP7M(HCkCrypt2 cHandle, const char *inFilename, const char *p7mPath);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_CreateP7S(HCkCrypt2 cHandle, const char *inFilename, const char *p7sPath);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_Decode(HCkCrypt2 cHandle, const char *str, const char *encoding, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecodeString(HCkCrypt2 cHandle, const char *inStr, const char *charset, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_decodeString(HCkCrypt2 cHandle, const char *inStr, const char *charset, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptBd(HCkCrypt2 cHandle, HCkBinData bd);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptBytes(HCkCrypt2 cHandle, HCkByteData data, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptBytesENC(HCkCrypt2 cHandle, const char *str, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptEncoded(HCkCrypt2 cHandle, const char *encodedEncryptedData, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_decryptEncoded(HCkCrypt2 cHandle, const char *encodedEncryptedData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptSb(HCkCrypt2 cHandle, HCkBinData bdIn, HCkStringBuilder sbOut);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptSecureENC(HCkCrypt2 cHandle, const char *cipherText, HCkSecureString secureStr);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptStream(HCkCrypt2 cHandle, HCkStream strm);
CK_VISIBLE_PUBLIC HCkTask CkCrypt2_DecryptStreamAsync(HCkCrypt2 cHandle, HCkStream strm);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptString(HCkCrypt2 cHandle, HCkByteData data, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_decryptString(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_DecryptStringENC(HCkCrypt2 cHandle, const char *str, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_decryptStringENC(HCkCrypt2 cHandle, const char *str);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_Encode(HCkCrypt2 cHandle, HCkByteData byteData, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_encode(HCkCrypt2 cHandle, HCkByteData byteData, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncodeBytes(HCkCrypt2 cHandle, const unsigned char *pByteData, unsigned long szByteData, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_encodeBytes(HCkCrypt2 cHandle, const unsigned char *pByteData, unsigned long szByteData, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncodeString(HCkCrypt2 cHandle, const char *strToEncode, const char *charsetName, const char *toEncodingName, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_encodeString(HCkCrypt2 cHandle, const char *strToEncode, const char *charsetName, const char *toEncodingName);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptBd(HCkCrypt2 cHandle, HCkBinData bd);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptBytes(HCkCrypt2 cHandle, HCkByteData data, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptBytesENC(HCkCrypt2 cHandle, HCkByteData data, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_encryptBytesENC(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptEncoded(HCkCrypt2 cHandle, const char *str, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_encryptEncoded(HCkCrypt2 cHandle, const char *str);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptSb(HCkCrypt2 cHandle, HCkStringBuilder sbIn, HCkBinData bdOut);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptSecureENC(HCkCrypt2 cHandle, HCkSecureString secureStr, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_encryptSecureENC(HCkCrypt2 cHandle, HCkSecureString secureStr);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptStream(HCkCrypt2 cHandle, HCkStream strm);
CK_VISIBLE_PUBLIC HCkTask CkCrypt2_EncryptStreamAsync(HCkCrypt2 cHandle, HCkStream strm);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptString(HCkCrypt2 cHandle, const char *str, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_EncryptStringENC(HCkCrypt2 cHandle, const char *str, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_encryptStringENC(HCkCrypt2 cHandle, const char *str);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GenEncodedSecretKey(HCkCrypt2 cHandle, const char *password, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_genEncodedSecretKey(HCkCrypt2 cHandle, const char *password, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GenerateSecretKey(HCkCrypt2 cHandle, const char *password, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GenerateUuid(HCkCrypt2 cHandle, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_generateUuid(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GenRandomBytesENC(HCkCrypt2 cHandle, int numBytes, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_genRandomBytesENC(HCkCrypt2 cHandle, int numBytes);
CK_VISIBLE_PUBLIC HCkCert CkCrypt2_GetDecryptCert(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GetEncodedAad(HCkCrypt2 cHandle, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_getEncodedAad(HCkCrypt2 cHandle, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GetEncodedAuthTag(HCkCrypt2 cHandle, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_getEncodedAuthTag(HCkCrypt2 cHandle, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GetEncodedIV(HCkCrypt2 cHandle, const char *encoding, HCkString outIV);
CK_VISIBLE_PUBLIC const char *CkCrypt2_getEncodedIV(HCkCrypt2 cHandle, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GetEncodedKey(HCkCrypt2 cHandle, const char *encoding, HCkString outKey);
CK_VISIBLE_PUBLIC const char *CkCrypt2_getEncodedKey(HCkCrypt2 cHandle, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GetEncodedSalt(HCkCrypt2 cHandle, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_getEncodedSalt(HCkCrypt2 cHandle, const char *encoding);
CK_VISIBLE_PUBLIC HCkCert CkCrypt2_GetLastCert(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GetSignatureSigningTime(HCkCrypt2 cHandle, int index, SYSTEMTIME *outSysTime);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_GetSignatureSigningTimeStr(HCkCrypt2 cHandle, int index, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_getSignatureSigningTimeStr(HCkCrypt2 cHandle, int index);
CK_VISIBLE_PUBLIC HCkCert CkCrypt2_GetSignerCert(HCkCrypt2 cHandle, int index);
CK_VISIBLE_PUBLIC HCkCertChain CkCrypt2_GetSignerCertChain(HCkCrypt2 cHandle, int index);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashBdENC(HCkCrypt2 cHandle, HCkBinData bd, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_hashBdENC(HCkCrypt2 cHandle, HCkBinData bd);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashBeginBytes(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashBeginString(HCkCrypt2 cHandle, const char *strData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashBytes(HCkCrypt2 cHandle, HCkByteData data, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashBytesENC(HCkCrypt2 cHandle, HCkByteData data, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_hashBytesENC(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashFile(HCkCrypt2 cHandle, const char *path, HCkByteData outBytes);
CK_VISIBLE_PUBLIC HCkTask CkCrypt2_HashFileAsync(HCkCrypt2 cHandle, const char *path);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashFileENC(HCkCrypt2 cHandle, const char *path, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_hashFileENC(HCkCrypt2 cHandle, const char *path);
CK_VISIBLE_PUBLIC HCkTask CkCrypt2_HashFileENCAsync(HCkCrypt2 cHandle, const char *path);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashFinal(HCkCrypt2 cHandle, HCkByteData outBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashFinalENC(HCkCrypt2 cHandle, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_hashFinalENC(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashMoreBytes(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashMoreString(HCkCrypt2 cHandle, const char *strData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashString(HCkCrypt2 cHandle, const char *str, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HashStringENC(HCkCrypt2 cHandle, const char *str, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_hashStringENC(HCkCrypt2 cHandle, const char *str);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HasSignatureSigningTime(HCkCrypt2 cHandle, int index);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HmacBytes(HCkCrypt2 cHandle, HCkByteData inBytes, HCkByteData outHmac);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HmacBytesENC(HCkCrypt2 cHandle, HCkByteData inBytes, HCkString outEncodedHmac);
CK_VISIBLE_PUBLIC const char *CkCrypt2_hmacBytesENC(HCkCrypt2 cHandle, HCkByteData inBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HmacString(HCkCrypt2 cHandle, const char *inText, HCkByteData outHmac);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_HmacStringENC(HCkCrypt2 cHandle, const char *inText, HCkString outEncodedHmac);
CK_VISIBLE_PUBLIC const char *CkCrypt2_hmacStringENC(HCkCrypt2 cHandle, const char *inText);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_InflateBytes(HCkCrypt2 cHandle, HCkByteData data, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_InflateBytesENC(HCkCrypt2 cHandle, const char *str, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_InflateString(HCkCrypt2 cHandle, HCkByteData data, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_inflateString(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_InflateStringENC(HCkCrypt2 cHandle, const char *str, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_inflateStringENC(HCkCrypt2 cHandle, const char *str);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_IsUnlocked(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC HCkJsonObject CkCrypt2_LastJsonData(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_MacBdENC(HCkCrypt2 cHandle, HCkBinData bd, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_macBdENC(HCkCrypt2 cHandle, HCkBinData bd);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_MacBytes(HCkCrypt2 cHandle, HCkByteData inBytes, HCkByteData outBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_MacBytesENC(HCkCrypt2 cHandle, HCkByteData inBytes, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_macBytesENC(HCkCrypt2 cHandle, HCkByteData inBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_MacString(HCkCrypt2 cHandle, const char *inText, HCkByteData outBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_MacStringENC(HCkCrypt2 cHandle, const char *inText, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_macStringENC(HCkCrypt2 cHandle, const char *inText);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_MySqlAesDecrypt(HCkCrypt2 cHandle, const char *strEncryptedHex, const char *strPassword, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_mySqlAesDecrypt(HCkCrypt2 cHandle, const char *strEncryptedHex, const char *strPassword);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_MySqlAesEncrypt(HCkCrypt2 cHandle, const char *strData, const char *strPassword, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_mySqlAesEncrypt(HCkCrypt2 cHandle, const char *strData, const char *strPassword);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueSignBd(HCkCrypt2 cHandle, HCkBinData bd);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueSignBytes(HCkCrypt2 cHandle, HCkByteData data, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueSignBytesENC(HCkCrypt2 cHandle, HCkByteData data, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_opaqueSignBytesENC(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueSignString(HCkCrypt2 cHandle, const char *str, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueSignStringENC(HCkCrypt2 cHandle, const char *str, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_opaqueSignStringENC(HCkCrypt2 cHandle, const char *str);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueVerifyBd(HCkCrypt2 cHandle, HCkBinData bd);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueVerifyBytes(HCkCrypt2 cHandle, HCkByteData p7s, HCkByteData outOriginal);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueVerifyBytesENC(HCkCrypt2 cHandle, const char *p7s, HCkByteData outOriginal);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueVerifyString(HCkCrypt2 cHandle, HCkByteData p7s, HCkString outOriginal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_opaqueVerifyString(HCkCrypt2 cHandle, HCkByteData p7s);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_OpaqueVerifyStringENC(HCkCrypt2 cHandle, const char *p7s, HCkString outOriginal);
CK_VISIBLE_PUBLIC const char *CkCrypt2_opaqueVerifyStringENC(HCkCrypt2 cHandle, const char *p7s);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_Pbkdf1(HCkCrypt2 cHandle, const char *password, const char *charset, const char *hashAlg, const char *salt, int iterationCount, int outputKeyBitLen, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_pbkdf1(HCkCrypt2 cHandle, const char *password, const char *charset, const char *hashAlg, const char *salt, int iterationCount, int outputKeyBitLen, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_Pbkdf2(HCkCrypt2 cHandle, const char *password, const char *charset, const char *hashAlg, const char *salt, int iterationCount, int outputKeyBitLen, const char *encoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_pbkdf2(HCkCrypt2 cHandle, const char *password, const char *charset, const char *hashAlg, const char *salt, int iterationCount, int outputKeyBitLen, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_Pkcs7ExtractDigest(HCkCrypt2 cHandle, int signerIndex, const char *pkcs7, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_pkcs7ExtractDigest(HCkCrypt2 cHandle, int signerIndex, const char *pkcs7);
CK_VISIBLE_PUBLIC void CkCrypt2_RandomizeIV(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC void CkCrypt2_RandomizeKey(HCkCrypt2 cHandle);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_ReadFile(HCkCrypt2 cHandle, const char *filename, HCkByteData outBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_ReEncode(HCkCrypt2 cHandle, const char *encodedData, const char *fromEncoding, const char *toEncoding, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_reEncode(HCkCrypt2 cHandle, const char *encodedData, const char *fromEncoding, const char *toEncoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SaveLastError(HCkCrypt2 cHandle, const char *path);
#if defined(CK_CSP_INCLUDED)
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetCSP(HCkCrypt2 cHandle, HCkCsp csp);
#endif
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetDecryptCert(HCkCrypt2 cHandle, HCkCert cert);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetDecryptCert2(HCkCrypt2 cHandle, HCkCert cert, HCkPrivateKey key);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetEncodedAad(HCkCrypt2 cHandle, const char *aadStr, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetEncodedAuthTag(HCkCrypt2 cHandle, const char *authTagStr, const char *encoding);
CK_VISIBLE_PUBLIC void CkCrypt2_SetEncodedIV(HCkCrypt2 cHandle, const char *ivStr, const char *encoding);
CK_VISIBLE_PUBLIC void CkCrypt2_SetEncodedKey(HCkCrypt2 cHandle, const char *keyStr, const char *encoding);
CK_VISIBLE_PUBLIC void CkCrypt2_SetEncodedSalt(HCkCrypt2 cHandle, const char *saltStr, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetEncryptCert(HCkCrypt2 cHandle, HCkCert cert);
CK_VISIBLE_PUBLIC void CkCrypt2_SetHmacKeyBytes(HCkCrypt2 cHandle, HCkByteData keyBytes);
CK_VISIBLE_PUBLIC void CkCrypt2_SetHmacKeyEncoded(HCkCrypt2 cHandle, const char *key, const char *encoding);
CK_VISIBLE_PUBLIC void CkCrypt2_SetHmacKeyString(HCkCrypt2 cHandle, const char *key);
CK_VISIBLE_PUBLIC void CkCrypt2_SetIV(HCkCrypt2 cHandle, const unsigned char *pByteData, unsigned long szByteData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetMacKeyBytes(HCkCrypt2 cHandle, HCkByteData keyBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetMacKeyEncoded(HCkCrypt2 cHandle, const char *key, const char *encoding);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetMacKeyString(HCkCrypt2 cHandle, const char *key);
CK_VISIBLE_PUBLIC void CkCrypt2_SetSecretKey(HCkCrypt2 cHandle, const unsigned char *pByteData, unsigned long szByteData);
CK_VISIBLE_PUBLIC void CkCrypt2_SetSecretKeyViaPassword(HCkCrypt2 cHandle, const char *password);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetSigningCert(HCkCrypt2 cHandle, HCkCert cert);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetSigningCert2(HCkCrypt2 cHandle, HCkCert cert, HCkPrivateKey privateKey);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SetVerifyCert(HCkCrypt2 cHandle, HCkCert cert);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SignBdENC(HCkCrypt2 cHandle, HCkBinData dataToSign, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_signBdENC(HCkCrypt2 cHandle, HCkBinData dataToSign);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SignBytes(HCkCrypt2 cHandle, HCkByteData data, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SignBytesENC(HCkCrypt2 cHandle, HCkByteData data, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_signBytesENC(HCkCrypt2 cHandle, HCkByteData data);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SignSbENC(HCkCrypt2 cHandle, HCkStringBuilder sb, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_signSbENC(HCkCrypt2 cHandle, HCkStringBuilder sb);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SignString(HCkCrypt2 cHandle, const char *str, HCkByteData outData);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_SignStringENC(HCkCrypt2 cHandle, const char *str, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_signStringENC(HCkCrypt2 cHandle, const char *str);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_StringToBytes(HCkCrypt2 cHandle, const char *inStr, const char *charset, HCkByteData outBytes);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_TrimEndingWith(HCkCrypt2 cHandle, const char *inStr, const char *ending, HCkString outStr);
CK_VISIBLE_PUBLIC const char *CkCrypt2_trimEndingWith(HCkCrypt2 cHandle, const char *inStr, const char *ending);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_UnlockComponent(HCkCrypt2 cHandle, const char *unlockCode);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_UseCertVault(HCkCrypt2 cHandle, HCkXmlCertVault vault);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifyBdENC(HCkCrypt2 cHandle, HCkBinData data, const char *encodedSig);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifyBytes(HCkCrypt2 cHandle, HCkByteData data, HCkByteData sig);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifyBytesENC(HCkCrypt2 cHandle, HCkByteData data, const char *encodedSig);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifyDetachedSignature(HCkCrypt2 cHandle, const char *inFilename, const char *p7sFilename);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifyP7M(HCkCrypt2 cHandle, const char *p7mPath, const char *destPath);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifyP7S(HCkCrypt2 cHandle, const char *inFilename, const char *p7sFilename);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifySbENC(HCkCrypt2 cHandle, HCkStringBuilder sb, const char *encodedSig);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifyString(HCkCrypt2 cHandle, const char *str, HCkByteData sig);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_VerifyStringENC(HCkCrypt2 cHandle, const char *str, const char *encodedSig);
CK_VISIBLE_PUBLIC BOOL CkCrypt2_WriteFile(HCkCrypt2 cHandle, const char *filename, HCkByteData fileData);
#endif

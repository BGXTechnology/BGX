// This is a generated source file for Chilkat version 9.5.0.73
#ifndef _C_CkJwtWH
#define _C_CkJwtWH
#include "chilkatDefs.h"

#include "Chilkat_C.h"


CK_VISIBLE_PUBLIC HCkJwtW CkJwtW_Create(void);
CK_VISIBLE_PUBLIC void CkJwtW_Dispose(HCkJwtW handle);
CK_VISIBLE_PUBLIC BOOL CkJwtW_getAutoCompact(HCkJwtW cHandle);
CK_VISIBLE_PUBLIC void  CkJwtW_putAutoCompact(HCkJwtW cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkJwtW_getDebugLogFilePath(HCkJwtW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void  CkJwtW_putDebugLogFilePath(HCkJwtW cHandle, const wchar_t *newVal);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_getDebugLogFilePath(HCkJwtW cHandle);
CK_VISIBLE_PUBLIC void CkJwtW_getLastErrorHtml(HCkJwtW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_getLastErrorHtml(HCkJwtW cHandle);
CK_VISIBLE_PUBLIC void CkJwtW_getLastErrorText(HCkJwtW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_getLastErrorText(HCkJwtW cHandle);
CK_VISIBLE_PUBLIC void CkJwtW_getLastErrorXml(HCkJwtW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_getLastErrorXml(HCkJwtW cHandle);
CK_VISIBLE_PUBLIC BOOL CkJwtW_getLastMethodSuccess(HCkJwtW cHandle);
CK_VISIBLE_PUBLIC void  CkJwtW_putLastMethodSuccess(HCkJwtW cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC BOOL CkJwtW_getVerboseLogging(HCkJwtW cHandle);
CK_VISIBLE_PUBLIC void  CkJwtW_putVerboseLogging(HCkJwtW cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkJwtW_getVersion(HCkJwtW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_getVersion(HCkJwtW cHandle);
CK_VISIBLE_PUBLIC BOOL CkJwtW_CreateJwt(HCkJwtW cHandle, const wchar_t *header, const wchar_t *payload, const wchar_t *password, HCkString outStr);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_createJwt(HCkJwtW cHandle, const wchar_t *header, const wchar_t *payload, const wchar_t *password);
CK_VISIBLE_PUBLIC BOOL CkJwtW_CreateJwtPk(HCkJwtW cHandle, const wchar_t *header, const wchar_t *payload, HCkPrivateKeyW key, HCkString outStr);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_createJwtPk(HCkJwtW cHandle, const wchar_t *header, const wchar_t *payload, HCkPrivateKeyW key);
CK_VISIBLE_PUBLIC int CkJwtW_GenNumericDate(HCkJwtW cHandle, int numSecOffset);
CK_VISIBLE_PUBLIC BOOL CkJwtW_GetHeader(HCkJwtW cHandle, const wchar_t *token, HCkString outStr);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_getHeader(HCkJwtW cHandle, const wchar_t *token);
CK_VISIBLE_PUBLIC BOOL CkJwtW_GetPayload(HCkJwtW cHandle, const wchar_t *token, HCkString outStr);
CK_VISIBLE_PUBLIC const wchar_t *CkJwtW_getPayload(HCkJwtW cHandle, const wchar_t *token);
CK_VISIBLE_PUBLIC BOOL CkJwtW_IsTimeValid(HCkJwtW cHandle, const wchar_t *jwt, int leeway);
CK_VISIBLE_PUBLIC BOOL CkJwtW_SaveLastError(HCkJwtW cHandle, const wchar_t *path);
CK_VISIBLE_PUBLIC BOOL CkJwtW_VerifyJwt(HCkJwtW cHandle, const wchar_t *token, const wchar_t *password);
CK_VISIBLE_PUBLIC BOOL CkJwtW_VerifyJwtPk(HCkJwtW cHandle, const wchar_t *token, HCkPublicKeyW key);
#endif
// This is a generated source file for Chilkat version 9.5.0.73
#ifndef _C_CkAuthAzureADWH
#define _C_CkAuthAzureADWH
#include "chilkatDefs.h"

#include "Chilkat_C.h"


CK_VISIBLE_PUBLIC void CkAuthAzureADW_setAbortCheck(HCkAuthAzureADW cHandle, BOOL (*fnAbortCheck)(void));
CK_VISIBLE_PUBLIC void CkAuthAzureADW_setPercentDone(HCkAuthAzureADW cHandle, BOOL (*fnPercentDone)(int pctDone));
CK_VISIBLE_PUBLIC void CkAuthAzureADW_setProgressInfo(HCkAuthAzureADW cHandle, void (*fnProgressInfo)(const wchar_t *name, const wchar_t *value));
CK_VISIBLE_PUBLIC void CkAuthAzureADW_setTaskCompleted(HCkAuthAzureADW cHandle, void (*fnTaskCompleted)(HCkTaskW hTask));

CK_VISIBLE_PUBLIC HCkAuthAzureADW CkAuthAzureADW_Create(void);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_Dispose(HCkAuthAzureADW handle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getAccessToken(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void  CkAuthAzureADW_putAccessToken(HCkAuthAzureADW cHandle, const wchar_t *newVal);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getAccessToken(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getClientId(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void  CkAuthAzureADW_putClientId(HCkAuthAzureADW cHandle, const wchar_t *newVal);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getClientId(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getClientSecret(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void  CkAuthAzureADW_putClientSecret(HCkAuthAzureADW cHandle, const wchar_t *newVal);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getClientSecret(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getDebugLogFilePath(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void  CkAuthAzureADW_putDebugLogFilePath(HCkAuthAzureADW cHandle, const wchar_t *newVal);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getDebugLogFilePath(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getLastErrorHtml(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getLastErrorHtml(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getLastErrorText(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getLastErrorText(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getLastErrorXml(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getLastErrorXml(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC BOOL CkAuthAzureADW_getLastMethodSuccess(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void  CkAuthAzureADW_putLastMethodSuccess(HCkAuthAzureADW cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC int CkAuthAzureADW_getNumSecondsRemaining(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getResource(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void  CkAuthAzureADW_putResource(HCkAuthAzureADW cHandle, const wchar_t *newVal);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getResource(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getTenantId(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void  CkAuthAzureADW_putTenantId(HCkAuthAzureADW cHandle, const wchar_t *newVal);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getTenantId(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC BOOL CkAuthAzureADW_getValid(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC BOOL CkAuthAzureADW_getVerboseLogging(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC void  CkAuthAzureADW_putVerboseLogging(HCkAuthAzureADW cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkAuthAzureADW_getVersion(HCkAuthAzureADW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthAzureADW_getVersion(HCkAuthAzureADW cHandle);
CK_VISIBLE_PUBLIC BOOL CkAuthAzureADW_ObtainAccessToken(HCkAuthAzureADW cHandle, HCkSocketW connection);
CK_VISIBLE_PUBLIC HCkTaskW CkAuthAzureADW_ObtainAccessTokenAsync(HCkAuthAzureADW cHandle, HCkSocketW connection);
CK_VISIBLE_PUBLIC BOOL CkAuthAzureADW_SaveLastError(HCkAuthAzureADW cHandle, const wchar_t *path);
#endif

// This is a generated source file for Chilkat version 9.5.0.73
#ifndef _C_CkAuthUtilWH
#define _C_CkAuthUtilWH
#include "chilkatDefs.h"

#include "Chilkat_C.h"


CK_VISIBLE_PUBLIC HCkAuthUtilW CkAuthUtilW_Create(void);
CK_VISIBLE_PUBLIC void CkAuthUtilW_Dispose(HCkAuthUtilW handle);
CK_VISIBLE_PUBLIC void CkAuthUtilW_getDebugLogFilePath(HCkAuthUtilW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC void  CkAuthUtilW_putDebugLogFilePath(HCkAuthUtilW cHandle, const wchar_t *newVal);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthUtilW_getDebugLogFilePath(HCkAuthUtilW cHandle);
CK_VISIBLE_PUBLIC void CkAuthUtilW_getLastErrorHtml(HCkAuthUtilW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthUtilW_getLastErrorHtml(HCkAuthUtilW cHandle);
CK_VISIBLE_PUBLIC void CkAuthUtilW_getLastErrorText(HCkAuthUtilW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthUtilW_getLastErrorText(HCkAuthUtilW cHandle);
CK_VISIBLE_PUBLIC void CkAuthUtilW_getLastErrorXml(HCkAuthUtilW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthUtilW_getLastErrorXml(HCkAuthUtilW cHandle);
CK_VISIBLE_PUBLIC BOOL CkAuthUtilW_getLastMethodSuccess(HCkAuthUtilW cHandle);
CK_VISIBLE_PUBLIC void  CkAuthUtilW_putLastMethodSuccess(HCkAuthUtilW cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC BOOL CkAuthUtilW_getVerboseLogging(HCkAuthUtilW cHandle);
CK_VISIBLE_PUBLIC void  CkAuthUtilW_putVerboseLogging(HCkAuthUtilW cHandle, BOOL newVal);
CK_VISIBLE_PUBLIC void CkAuthUtilW_getVersion(HCkAuthUtilW cHandle, HCkString retval);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthUtilW_getVersion(HCkAuthUtilW cHandle);
CK_VISIBLE_PUBLIC BOOL CkAuthUtilW_SaveLastError(HCkAuthUtilW cHandle, const wchar_t *path);
CK_VISIBLE_PUBLIC BOOL CkAuthUtilW_WalmartSignature(HCkAuthUtilW cHandle, const wchar_t *requestUrl, const wchar_t *consumerId, const wchar_t *privateKey, const wchar_t *requestMethod, HCkString outStr);
CK_VISIBLE_PUBLIC const wchar_t *CkAuthUtilW_walmartSignature(HCkAuthUtilW cHandle, const wchar_t *requestUrl, const wchar_t *consumerId, const wchar_t *privateKey, const wchar_t *requestMethod);
#endif

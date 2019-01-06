//
//  CheckCameraAndPhotoHelper.m
//  BGX
//
//  Created by THAI LE QUANG on 9/21/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <React/RCTViewManager.h>
#import <React/RCTLog.h>
#import <React/UIView+React.h>
#import <AVFoundation/AVFoundation.h>
#import "Photos/Photos.h"

@interface CheckCameraAndPhotoHelper : RCTViewManager

@end

@implementation CheckCameraAndPhotoHelper


RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(checkIsCameraEnable:(RCTResponseSenderBlock)callback)
{
  AVAuthorizationStatus authStatus = [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
  
  switch (authStatus)
  {
    case AVAuthorizationStatusNotDetermined:
      callback(@[@"1"]);
      break;
      
    case AVAuthorizationStatusRestricted:
      callback(@[@"2"]);
      break;
    case AVAuthorizationStatusDenied:
      callback(@[@"3"]);
      break;
    case AVAuthorizationStatusAuthorized:
      callback(@[@"4"]);
      break;
  }
}

RCT_EXPORT_METHOD(checkIsPhotoEnable:(RCTResponseSenderBlock)callback)
{
  
  PHAuthorizationStatus status = [PHPhotoLibrary authorizationStatus];
  
  switch (status)
  {
    case PHAuthorizationStatusNotDetermined:
      callback(@[@"1"]);
      break;
      
    case PHAuthorizationStatusRestricted:
      callback(@[@"2"]);
      break;
    case PHAuthorizationStatusDenied:
      callback(@[@"3"]);
      break;
    case PHAuthorizationStatusAuthorized:
      callback(@[@"4"]);
      break;
      
  }
}

RCT_EXPORT_METHOD(goToCameraPhotoSetting:(RCTResponseSenderBlock)callback)
{
  [[UIApplication sharedApplication] openURL:[NSURL URLWithString:UIApplicationOpenSettingsURLString]];
  callback(@[@"true"]);
}

@end

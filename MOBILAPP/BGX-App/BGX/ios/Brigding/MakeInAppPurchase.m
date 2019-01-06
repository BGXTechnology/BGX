//
//  MakeInAppPurchase.m
//  BGX
//
//  Created by THAI LE QUANG on 11/5/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <React/RCTViewManager.h>
#import <React/RCTLog.h>
#import <React/UIView+React.h>
#import "RageIAPHelper.h"

@interface MakeInAppPurchase : RCTViewManager {
  SKProduct *tokenProduct;
  RCTResponseSenderBlock _callBack;
}


@end


@implementation MakeInAppPurchase

RCT_EXPORT_MODULE()

- (id)init
{
  self = [super init];
  if (self) {
    // Initialization code here.
  }
  
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(receiveTestNotification:)
                                               name:@"makeTransaction"
                                             object:nil];
  
  return self;
}

RCT_EXPORT_METHOD(makeTransaction:(RCTResponseSenderBlock)callback)
{
  _callBack = callback;
  [[RageIAPHelper sharedInstance] requestProductsWithCompletionHandler:^(BOOL success, NSArray *products) {
    if (success) {
      NSLog(@"123");
      if ([products count] > 0) {
        tokenProduct = [products objectAtIndex:0];
        [[RageIAPHelper sharedInstance] buyProduct:tokenProduct];
      }
    }
  }];
}

- (void) receiveTestNotification:(NSNotification *) notification {
  
  NSDictionary *userInfo = notification.userInfo;
  NSString *status = [userInfo objectForKey:@"status"];
  NSLog(@"%@", status);
  
  _callBack(@[status]);
}

@end


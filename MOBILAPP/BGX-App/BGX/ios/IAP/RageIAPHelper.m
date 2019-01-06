//
//  RageIAPHelper.m
//  BGX
//
//  Created by THAI LE QUANG on 10/23/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "RageIAPHelper.h"

@implementation RageIAPHelper

+ (RageIAPHelper *)sharedInstance {
  static dispatch_once_t once;
  static RageIAPHelper * sharedInstance;
  dispatch_once(&once, ^{
    NSSet * productIdentifiers = [NSSet setWithObjects:
                                  @"com.titan.BGX.token",
                                  nil];
    sharedInstance = [[self alloc] initWithProductIdentifiers:productIdentifiers];
  });
  return sharedInstance;
}

@end

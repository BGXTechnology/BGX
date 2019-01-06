/**
 * Copyright (c) 2015-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

#import <UIKit/UIKit.h>
#import <StoreKit/StoreKit.h>

@import UserNotifications;
@interface AppDelegate : UIResponder <UIApplicationDelegate, UNUserNotificationCenterDelegate, SKProductsRequestDelegate, SKPaymentTransactionObserver> {
//    SKProduct *tokenProduct;
}

@property (nonatomic, strong) UIWindow *window;

@end

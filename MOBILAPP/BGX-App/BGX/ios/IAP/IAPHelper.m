//
//  IAPHelper.m
//  BGX
//
//  Created by THAI LE QUANG on 10/23/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "IAPHelper.h"

NSString *const IAPHelperProductPurchasedNotification = @"IAPHelperProductPurchasedNotification";

@interface IAPHelper () <SKProductsRequestDelegate, SKPaymentTransactionObserver>
@end

@implementation IAPHelper
{
  SKProductsRequest * _productsRequest;
  RequestProductsCompletionHandler _completionHandler;
  
  NSSet * _productIdentifiers;
  NSMutableSet * _purchasedProductIdentifiers;
}

- (id)initWithProductIdentifiers:(NSSet *)productIdentifiers {
  
  if ((self = [super init])) {
    
    // Store product identifiers
    _productIdentifiers = productIdentifiers;
    
    // Check for previously purchased products
    _purchasedProductIdentifiers = [NSMutableSet set];
    for (NSString * productIdentifier in _productIdentifiers) {
      BOOL productPurchased = [[NSUserDefaults standardUserDefaults] boolForKey:productIdentifier];
      if (productPurchased) {
        [_purchasedProductIdentifiers addObject:productIdentifier];
        NSLog(@"Previously purchased: %@", productIdentifier);
      } else {
        NSLog(@"Not purchased: %@", productIdentifier);
      }
    }
    
    // Add self as transaction observer
    [[SKPaymentQueue defaultQueue] addTransactionObserver:self];
    
  }
  return self;
}

- (void)requestProductsWithCompletionHandler:(RequestProductsCompletionHandler)completionHandler {
  
  // 1
  _completionHandler = [completionHandler copy];
  
  // 2
  _productsRequest = [[SKProductsRequest alloc] initWithProductIdentifiers:_productIdentifiers];
  _productsRequest.delegate = self;
  [_productsRequest start];
  
}

- (BOOL)productPurchased:(NSString *)productIdentifier {
  return [_purchasedProductIdentifiers containsObject:productIdentifier];
}

- (void)buyProduct:(SKProduct *)product {
  
  NSLog(@"Buying %@...", product.productIdentifier);
  
  SKPayment * payment = [SKPayment paymentWithProduct:product];
  [[SKPaymentQueue defaultQueue] addPayment:payment];
  
}

//- (void)validateReceiptForTransaction:(SKPaymentTransaction *)transaction {
//  VerificationController * verifier = [VerificationController sharedInstance];
//  [verifier verifyPurchase:transaction completionHandler:^(BOOL success) {
//    if (success) {
//      NSLog(@"Successfully verified receipt!");
//      [self provideContentForProductIdentifier:transaction.payment.productIdentifier];
//    } else {
//      NSLog(@"Failed to validate receipt.");
//      [[SKPaymentQueue defaultQueue] finishTransaction: transaction];
//    }
//  }];
//}

#pragma mark - SKProductsRequestDelegate

- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response {
  
  NSLog(@"Loaded list of products...");
  _productsRequest = nil;
  
  NSArray * skProducts = response.products;
  for (SKProduct * skProduct in skProducts) {
    NSLog(@"Found product: %@ %@ %0.2f",
          skProduct.productIdentifier,
          skProduct.localizedTitle,
          skProduct.price.floatValue);
  }
  
  if (_completionHandler == nil) {
    return;
  }
  
  _completionHandler(YES, skProducts);
  _completionHandler = nil;
  
}

- (void)request:(SKRequest *)request didFailWithError:(NSError *)error {
  
  NSLog(@"Failed to load list of products.");
  _productsRequest = nil;
  
  _completionHandler(NO, nil);
  _completionHandler = nil;
  
}

#pragma mark SKPaymentTransactionOBserver

- (void)paymentQueue:(SKPaymentQueue *)queue updatedTransactions:(NSArray *)transactions
{
  for (SKPaymentTransaction * transaction in transactions) {
    switch (transaction.transactionState)
    {
      case SKPaymentTransactionStatePurchased:
        [self completeTransaction:transaction];
        break;
      case SKPaymentTransactionStateFailed:
        [self failedTransaction:transaction];
        break;
      case SKPaymentTransactionStateRestored:
        [self restoreTransaction:transaction];
      default:
        break;
    }
  };
}

- (void)completeTransaction:(SKPaymentTransaction *)transaction {
  NSLog(@"completeTransaction...");
  
  NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"1", @"status", nil];
  [[NSNotificationCenter defaultCenter] postNotificationName:@"makeTransaction" object:nil userInfo:dict];
//  [self validateReceiptForTransaction:transaction];
  [[SKPaymentQueue defaultQueue] finishTransaction:transaction];
}

- (void)restoreTransaction:(SKPaymentTransaction *)transaction {
  NSLog(@"restoreTransaction...");
  
//  [self validateReceiptForTransaction:transaction];
  [[SKPaymentQueue defaultQueue] finishTransaction:transaction];
}

- (void)failedTransaction:(SKPaymentTransaction *)transaction {
  
  NSDictionary *dict = [NSDictionary dictionaryWithObjectsAndKeys:@"0", @"status", nil];
  [[NSNotificationCenter defaultCenter] postNotificationName:@"makeTransaction" object:nil userInfo:dict];
  NSLog(@"failedTransaction...");
  if (transaction.error.code != SKErrorPaymentCancelled)
  {
    NSLog(@"Transaction error: %@", transaction.error.localizedDescription);
  }
  
  [[SKPaymentQueue defaultQueue] finishTransaction: transaction];
}

- (void)provideContentForProductIdentifier:(NSString *)productIdentifier {
  
  if ([productIdentifier isEqualToString:@"com.titan.BGX.token"]) {
    
//    int currentValue = [[NSUserDefaults standardUserDefaults] integerForKey:@"com.razeware.inapprage.randomrageface"];
//    currentValue += 5;
//    [[NSUserDefaults standardUserDefaults] setInteger:currentValue forKey:@"com.razeware.inapprage.randomrageface"];
//    [[NSUserDefaults standardUserDefaults] synchronize];
    
  } else {
    [_purchasedProductIdentifiers addObject:productIdentifier];
    [[NSUserDefaults standardUserDefaults] setBool:YES forKey:productIdentifier];
    [[NSUserDefaults standardUserDefaults] synchronize];
  }
  
  [[NSNotificationCenter defaultCenter] postNotificationName:IAPHelperProductPurchasedNotification object:productIdentifier userInfo:nil];
  
}

- (void)restoreCompletedTransactions {
  [[SKPaymentQueue defaultQueue] restoreCompletedTransactions];
}

@end



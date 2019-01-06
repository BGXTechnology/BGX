<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Pushnotification {
  private $_fireBaseiOSKey = 'AAAARHo7qKg:APA91bE8fcEfH1uhQiP0dq2oYn9bp-YWsv11yGr_gcjz6FeM6Fzvl-Bkb8YeQB0sh4s9UUvh5_EqLl0xiKwk81y4WEJhTf_FJGcOD73k532ndv5fFsHGhB5LDKeSped4P-OomfeKQaFp';
  private $_fireBaseAndroidKey = 'AAAAUCvx_7w:APA91bFC3MpoD2eTYbTw4MF6tpbDpk-76JgglO_aD7mSEaVxqSYea9f1tzHsCYF83L17xgnCWjm7Lh0LSRgRHs9Rpa8EeKpfLbTOjfZbNnHix3hXoHhXAFSzjD9oqkhlqLVhzSAYTUWq';

  /**
   * $type = 1: android
   * $type = 2: iOS
  */
  public function push($tokens, $message, $type = 1, $notification = null){
    $url = 'https://fcm.googleapis.com/fcm/send';
    $fields = array(
      'registration_ids' => $tokens,
      'data' => $message,
      'content_available' => true,
      'priority' => 'high'
    );

    if($notification !== null){
      $fields['notification'] = $notification;
    }

    if($type == 2){
      $firebase_server_key = $this->_fireBaseiOSKey;
    }else{
      $firebase_server_key = $this->_fireBaseAndroidKey;
    }

    $headers = array(
      'Authorization:key = '.$firebase_server_key,
      'Content-Type: application/json'
    );

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);  
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
    $result = curl_exec($ch);           
    curl_close($ch);

    return $result;
  }
}
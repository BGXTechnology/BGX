<?php
  class Home extends CI_Controller {

    private $_magentoApiToken = 'fc07kjwqr9knjq3y6nlyh4r31ddfr4x7';
    private $_magentoApiBaseUrl = 'http://172.16.11.178:9000/rest/V1/';
    private $_imageHost = '';
    private $_walletApiBaseUrl = 'http://18.222.233.160:8008/';

    public function __construct(){
      parent::__construct();
      $this->load->model('language_model');
      $this->load->helper('url');
    }

    public function index(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      $method = (!empty($postData)) ? $postData['method'] : "";
      switch ($method) {
        case 'login':
          $this->login();
          break;

        case 'register':
          $this->addUser();
          break;

        case 'update_profile':
          $this->updateUser();
          break;

        case 'reset_password':
          $this->resetPassword();
          break;

        case 'resend_active_email':
          $this->resendActiveEmail();
          break;

        case 'list_card_type':
          $this->listCardType();
          break;

        case 'list_card':
          $this->listCard();
          break;

        case 'add_card':
          $this->addCard();
          break;

        case 'update_card':
          $this->updateCard();
          break;

        case 'interface_code':
          $this->interfaceCode();
          break;

        case 'user_avavtar':
          $this->userAvatar();
          break;

        case 'list_shopping_cart_item':
          $this->listShoppingCartItem();
          break;

        case 'add_shopping_cart_item':
          $this->addShoppingCartItem();
          break;

        case 'delete_shopping_cart_item':
          $this->deleteShoppingCartItem();
          break;

        case 'clear_shopping_cart_item':
          $this->clearShoppingCartItem();
          break;

        case 'count_twitter':
          $this->count_twitter();
          break;

        case 'send_payment_email':
          $this->sendPaymentEmail();
          break;

        default:
          $this->methodNotFound();
          break;
      }
    }

    public function addUser(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['email'])){
        $postData['email'] = NULL;
      }

      if(!isset($postData['password'])){
        $postData['password'] = NULL;
      }

      if(!isset($postData['ethereumAddress'])){
        $postData['ethereumAddress'] = NULL;
      }

      if(!isset($postData['noHash'])){
        $postData['noHash'] = NULL;
      }

      if(!isset($postData['token'])){
        $postData['token'] = NULL;
      }

      if(!isset($postData['deviceType'])){
        $postData['deviceType'] = NULL;
      }

      $this->load->model('user_model');

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $email = trim($postData['email']);
      $password = trim($postData['password']);
      $ethereumAddress = trim($postData['ethereumAddress']);

      if($email === ""){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('please_enter_email')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }

      if(!filter_var($email, FILTER_VALIDATE_EMAIL)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('email_is_invalid')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }
      
      if($this->user_model->isEmailExist($email)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('email_already_exist')
          ),
          'data' => NULL
        );
        
        return  $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
        
      }

      if($password === ""){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('please_enter_password')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }

      $userHash = md5(time().$email);
      $params = array(
        'email' => $email,
        'password' => $password,
        'hash' => $userHash,
        'active' => 0,
        'created' => date('Y-m-d H:m:s', time()),
        'createdLink' => date('Y-m-d H:m:s', time())
      );

      if(!empty($ethereumAddress)){
        $params['ethereumAddress'] = $ethereumAddress;
      }

      // Create customer in Magento
      $noHash = trim($postData['noHash']);
      if(empty($noHash)){
        $noHash = "1234qwer!@";
      }

      $dataMegento = array(
        "customer" => array(
          "id" => 0,
          "groupId" => 1,
          "email" => $email,
          "firstname" => "First name",
          "lastname" => "Last name",
          "middlename" => "",
          "gender" => 1,
          "storeId" => 1,
          "websiteId" => 1
        ),
        "password" => $noHash,
        "redirectUrl" => "unneed_send_email"
      );
      $magentoApiCustomers = $this->_magentoApiBaseUrl.'customers';
      $magentoHeader = array(
        "Authorization: Bearer ".$this->_magentoApiToken
      );
      $magentoResult = $this->exeUrlData($magentoApiCustomers, $dataMegento, 'POST',$magentoHeader);
      
      if(empty($magentoResult) || !empty($magentoResult->message)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('email_already_exist')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }else{
        $params['magentoId'] = $magentoResult->id;
      }

      $result = $this->user_model->addUser($params);

      if($result){
        
        $user = $this->user_model->getUser(array('id' => $result));

        $this->load->model('emailtemplate_model');

        $emailTemplate = $this->emailtemplate_model->getItem(array('locale' => $locale, 'type' => 'active'));
        $activeLink = '<a href="'.base_url("activation/".$userHash).'?locale='. $locale .'">'.base_url("activation/".$userHash).'</a>';
        $emailTemplate->message = str_replace("<link>", $activeLink, $emailTemplate->message);
        $emailTemplate->message = str_replace("<href>", base_url("activation/".$userHash).'?locale='. $locale, $emailTemplate->message);
        $emailTemplate->message = str_replace("<img_url>", $this->_imageHost, $emailTemplate->message);
        $emailTemplate->message = str_replace("<valid_till>", date('d.m.Y H:m:s', time() + 43200), $emailTemplate->message);
        
        $this->sendEmail($email, $emailTemplate->subject, $emailTemplate->message);

        // Add device token
        if(!empty($postData['token'])){
          $this->load->model('usertoken_model');
          $paramsToken = array(
            'userId' => $result,
            'token' => $postData['token'],
            'deviceType' => $postData['deviceType']
          );

          $checkToken = $this->usertoken_model->isTokenExist($paramsToken);
          if(!$checkToken){
            $this->usertoken_model->addItem($paramsToken);
          }
        }

        $data = array(
          'status' => array(
            'error' => 0,
            'msg' => sprintf($this->lang->line('register_successfully'), $email)
          ),
          'data' => $user
        );

      }else{
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('register_failed')
          ),
          'data' => NULL
        );

      }

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));

    }

    public function login(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['email'])){
        $postData['email'] = NULL;
      }

      if(!isset($postData['password'])){
        $postData['password'] = NULL;
      }

      if(!isset($postData['token'])){
        $postData['token'] = NULL;
      }

      if(!isset($postData['deviceType'])){
        $postData['deviceType'] = NULL;
      }

      $this->load->model('user_model');

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $params = array(
        'email' => trim($postData['email']),
        'password' => trim($postData['password'])
      );
      
      if($params['email'] === "" || $params['password'] === ""){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('email_or_password_can_not_empty')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
        
      }

      $result = $this->user_model->getUser($params);
      
      if($result === NULL){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_email_password')
          ),
          'data' => NULL
        );

      }else{
        // Add device token
        if(!empty($postData['token'])){
          $this->load->model('usertoken_model');
          $paramsToken = array(
            'userId' => $result->id,
            'token' => $postData['token'],
            'deviceType' => $postData['deviceType']
          );

          $checkToken = $this->usertoken_model->isTokenExist($paramsToken);
          if(!$checkToken){
            $this->usertoken_model->addItem($paramsToken);
          }
        }

        $data = array(
          'status' => array(
            'error' => 0,
            'msg' => $this->lang->line('login_successfully')
          ),
          'data' => $result
        );
      }

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));

    }

    public function updateUser(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }
      
      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);
      $this->load->model('user_model');

      if(!isset($postData['email']) || !isset($postData['currentPassword'])){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('please_login_again')
          ),
          'data' => NULL
        );

        return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
      }

      $email = trim($postData['email']);
      $currentPassword = trim($postData['currentPassword']);
      $user = $this->user_model->getUser(array('email' => $email, 'password' => $currentPassword));

      if(!$user){
        $data = array(
          'status' => array(
            'error' => 10,
            'msg' => $this->lang->line('please_login_again')
          ),
          'data' => NULL
        );

        return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
      }

      if(!isset($postData['password'])){
        $postData['password'] = NULL;
      }

      if(!isset($postData['username'])){
        $postData['username'] = NULL;
      }

      if(!isset($postData['phone'])){
        $postData['phone'] = NULL;
      }

      if(!isset($postData['ethereumAddress'])){
        $postData['ethereumAddress'] = NULL;
      }

      if(!isset($postData['BGXAccount'])){
        $postData['BGXAccount'] = NULL;
      }

      if(!isset($postData['bgtAddress'])){
        $postData['bgtAddress'] = NULL;
      }

      if(!isset($postData['avatar'])){
        $postData['avatar'] = NULL;
      }

      if(!isset($postData['token'])){
        $postData['token'] = NULL;
      }

      $password = trim($postData['password']);
      $username = trim($postData['username']);
      $phone = trim($postData['phone']);
      $ethereumAddress = trim($postData['ethereumAddress']);
      $BGXAccount = trim($postData['BGXAccount']);
      $bgtAddress = trim($postData['bgtAddress']);
      
      $params = array();

      $params['password'] = $password;
      $params['username'] = $username;
      $params['phone'] = $phone;

      if(!empty($ethereumAddress) && !$this->isEthereumAddress($ethereumAddress)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('ethereum_invalid_format')
          ),
          'data' => NULL
        );

        return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
      }
      
      $params['ethereumAddress'] = $ethereumAddress;
      $params['BGXAccount'] = $BGXAccount;
      $params['bgtAddress'] = $bgtAddress;

      $config['upload_path']          = './uploads/user/';
      if(empty($user->avatar)){
        $config['file_name'] = 'user-'.$user->id.'.jpg';
      }else{
        $imgName = str_replace(".jpg", "", $user->avatar);
        $imgNumber = (int) str_replace("user/user-".$user->id."-", "", $imgName) + 1;
        $config['file_name'] = 'user-'.$user->id.'-'.$imgNumber.'.jpg';
      }
    
      if(!empty($postData['avatar'])){
        $img = $postData['avatar'];
        $imgData = explode(",", $img);
        $img = str_replace(' ','+',$imgData[1]);
        $data = base64_decode($img);
        $file = $config['upload_path'].$config['file_name'];
        $success = file_put_contents($file, $data);
        if($success){
          @unlink("./uploads/".$user->avatar);
          $params['avatar'] = 'user/'.$config['file_name'];
        }
      }

      if(empty($params)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('no_data_to_update')
          ),
          'data' => NULL
        );

        return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
      }
      
      $update = $this->user_model->updateUser($params, $email);

      if(!$update){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('update_failed')
          ),
          'data' => NULL
        );

      }else{
        $result = $this->user_model->getUser(array('email' => $email));

        if(!empty($username)){
          $customerFirstName = "";
          $customerLastName = "";
          $customerMiddleName = "";
          $arrName = explode(" ", $username);

          if(count($arrName) > 1){
            $customerFirstName = $arrName[0];
            $customerLastName = end($arrName);
          }else{
            $customerFirstName = $arrName[0];
            $customerLastName = $arrName[0];
          }

          if(count($arrName) > 2){
            $customerMiddleName = str_replace($customerFirstName, "", $username);
            $customerMiddleName = str_replace($customerLastName, "", $customerMiddleName);
          }

          // Update to Magento
          $dataMegento = array(
            "customer" => array(
              "id" => $result->magentoId,
              "email" => $email,
              "firstname" => $customerFirstName,
              "lastname" => $customerLastName,
              "middlename" => trim($customerMiddleName),
              "storeId" => 1,
              "websiteId" => 1
            )
          );

          $magentoApiCustomers = $this->_magentoApiBaseUrl.'customers/'.$result->magentoId;
          $magentoHeader = array(
            "Authorization: Bearer ".$this->_magentoApiToken
          );
          $magentoResult = $this->exeUrlData($magentoApiCustomers, $dataMegento, 'PUT', $magentoHeader);

        }

        // pushNotification
        $this->load->model('usertoken_model');
        $this->load->library('pushnotification');
        $paramsTokenAndroid = array(
          'userId' => $result->id,
          'deviceType' => 1
        );
        $tokensAndroid = $this->usertoken_model->getList($paramsTokenAndroid);

        $paramsTokenIOS = array(
          'userId' => $result->id,
          'deviceType' => 2
        );
        $tokensIOS = $this->usertoken_model->getList($paramsTokenIOS);
        $pushMsg = array(
          'title' => $this->lang->line('update_profile'),
          'text' => $this->lang->line('your_information_has_been_updated_on_another_device'),
          'data' => array(
            'profile' => (array) $result,
            'type' => 'update_profile'
          )
        );
        $deviceToken = trim($postData['token']);
        if(!empty($tokensAndroid)){
          $tokens = array();
          foreach ($tokensAndroid as $token) {
            if($token['token'] != $deviceToken){
              $tokens[] = $token['token'];
            }
          }

          $this->pushnotification->push($tokens, $pushMsg);
        }
        
        if(!empty($tokensIOS)){
          $tokens = array();
          foreach ($tokensIOS as $token) {
            if($token['token'] != $deviceToken){
              $tokens[] = $token['token'];
            }
          }

          $notification = array(
            'title' => $this->lang->line('update_profile'),
            'body' => $this->lang->line('your_information_has_been_updated_on_another_device'),
            'click_action' => 'fcm.ACTION.HELLO',
            'sound'=> 'default',
            'isShow' => 1,
            'is_background' => 1,
            'badge' => 0
          );

          $this->pushnotification->push($tokens, $pushMsg, 2, $notification);
        }

        $data = array(
          'status' => array(
            'error' => 0,
            'msg' => $this->lang->line('update_successfully')
          ),
          'data' => $result
        );

      }

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));

    }

    public function resetPassword(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['email'])){
        $postData['email'] = NULL;
      }

      $this->load->model('user_model');

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $email = trim($postData['email']);

      if($email === ""){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('please_enter_email')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }

      if(!filter_var($email, FILTER_VALIDATE_EMAIL)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('email_is_invalid')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }
      
      $params = array(
        'email' => $email
      );

      $result = $this->user_model->getUser($params);

      if($result){
        $resetHash = md5(time().$email);
        $paramsUpdate = array(
          'resetedLink' => date('Y-m-d H:m:s', time()),
          'resetHash' => $resetHash,
          'isReseted' => 0
        );

        $this->user_model->updateUser($paramsUpdate, $email);

        $this->load->model('emailtemplate_model');

        $emailTemplate = $this->emailtemplate_model->getItem(array('locale' => $locale, 'type' => 'resetpass'));
        $activeLink = '<a href="'.base_url("reset/".$resetHash).'?locale='. $locale .'">'.base_url("reset/".$resetHash).'</a>';
        $emailTemplate->message = str_replace("<link>", $activeLink, $emailTemplate->message);
        $emailTemplate->message = str_replace("<href>", base_url("reset/".$resetHash).'?locale='. $locale, $emailTemplate->message);
        $emailTemplate->message = str_replace("<img_url>", $this->_imageHost, $emailTemplate->message);
        $emailTemplate->message = str_replace("<valid_till>", date('d.m.Y H:m:s', time() + 43200), $emailTemplate->message);
        $this->sendEmail($email, $emailTemplate->subject, $emailTemplate->message);

        $data = array(
          'status' => array(
            'error' => 0,
            'msg' => sprintf($this->lang->line('reset_password_successfully'), $email)
          ),
          'data' => NULL
        );

      }else{
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('email_is_invalid')
          ),
          'data' => NULL
        );

      }

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));

    }

    public function resendActiveEmail(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['email'])){
        $postData['email'] = NULL;
      }

      $this->load->model('user_model');

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $email = trim($postData['email']);

      $params = array(
        'email' => $email
      );
      
      if($params['email'] === ""){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('email_is_invalid')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
        
      }

      $result = $this->user_model->getUser($params);
      
      if($result === NULL){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

      }else{
        $this->load->model('emailtemplate_model');

        $userHash = $result->hash;
        $paramsUpdate = array(
          'createdLink' => date('Y-m-d H:m:s', time())
        );

        $this->user_model->updateUser($paramsUpdate, $email);

        $emailTemplate = $this->emailtemplate_model->getItem(array('locale' => $locale, 'type' => 'active'));
        $activeLink = '<a href="'.base_url("activation/".$userHash).'?locale='. $locale .'">'.base_url("activation/".$userHash).'</a>';
        $emailTemplate->message = str_replace("<link>", $activeLink, $emailTemplate->message);
        $emailTemplate->message = str_replace("<href>", base_url("activation/".$userHash).'?locale='. $locale, $emailTemplate->message);
        $emailTemplate->message = str_replace("<img_url>", $this->_imageHost, $emailTemplate->message);
        $emailTemplate->message = str_replace("<valid_till>", date('d.m.Y H:m:s', time() + 43200), $emailTemplate->message);
        $this->sendEmail($postData['email'], $emailTemplate->subject, $emailTemplate->message);
        
        $data = array(
          'status' => array(
            'error' => 0,
            'msg' => $this->lang->line('resend_email_successfully')
          ),
          'data' => NULL
        );
      }

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    public function listCardType(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      $locale = $this->language_model->checkLocale($postData['locale']);
      
      $this->load->model('cardtype_model');

      $params = array();

      $result = $this->cardtype_model->getList($params, $locale, NULL);

      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => ''
        ),
        'data' => $result
      );

      return  $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    public function listCard(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['email'])){
        $postData['email'] = NULL;
      }

      if(!isset($postData['currentPassword'])){
        $postData['currentPassword'] = NULL;
      }

      $this->load->model(array('card_model', 'user_model'));
      
      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $email = trim($postData['email']);
      $currentPassword = trim($postData['currentPassword']);
      
      // Check user Info
      $paramsUser = array(
        'email' => $email,
        'password' => $currentPassword
      );
      $user = $this->user_model->getUser($paramsUser);
      
      if(!$user){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }

      $params = array(
        'userId' => $user->id
      );

      $result = $this->card_model->getList($params, $locale);

      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => ''
        ),
        'data' => $result
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data)); 
    }

    public function addCard(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['email'])){
        $postData['email'] = NULL;
      }

      if(!isset($postData['currentPassword'])){
        $postData['currentPassword'] = NULL;
      }

      if(!isset($postData['cardTypeId'])){
        $postData['cardTypeId'] = NULL;
      }

      if(!isset($postData['template'])){
        $postData['template'] = NULL;
      }

      if(!isset($postData['cardHolder'])){
        $postData['cardHolder'] = NULL;
      }

      if(!isset($postData['issueDate'])){
        $postData['issueDate'] = NULL;
      }

      if(!isset($postData['validTill'])){
        $postData['validTill'] = NULL;
      }

      if(!isset($postData['PIN'])){
        $postData['PIN'] = NULL;
      }

      if(!isset($postData['address'])){
        $postData['address'] = NULL;
      }

      if(!isset($postData['publicKey'])){
        $postData['publicKey'] = NULL;
      }

      if(!isset($postData['public_key_hashed'])){
        $postData['public_key_hashed'] = NULL;
      }

      if(!isset($postData['privateKey'])){
        $postData['privateKey'] = NULL;
      }

      if(!isset($postData['token'])){
        $postData['token'] = NULL;
      }

      $this->load->model(array('card_model', 'user_model', 'cardtype_model'));

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $email = trim($postData['email']);
      $currentPassword = trim($postData['currentPassword']);
      $cardTypeId = trim($postData['cardTypeId']);
      $template = trim($postData['template']);
      $cardHolder = trim($postData['cardHolder']);
      $issueDate = trim($postData['issueDate']);
      $validTill = trim($postData['validTill']);
      $PIN = trim($postData['PIN']);
      $address = trim($postData['address']);
      $publicKey = trim($postData['publicKey']);
      $publicKeyHashed = trim($postData['public_key_hashed']);
      $privateKey = trim($postData['privateKey']);

      if(empty($email) || empty($currentPassword) || empty($cardTypeId) || empty($cardHolder) || empty($issueDate)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data)); 
      }

      // Check user Info
      $paramsUser = array(
        'email' => $email,
        'password' => $currentPassword
      );
      $user = $this->user_model->getUser($paramsUser);
      
      if(!$user){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }

      // Check user public key to card public key
      $cardType = $this->cardtype_model->getItem(array('id' => $cardTypeId));

      // Create wallet when card allow payment
      $isNeedPush = false;
      if($cardType && $cardType->payment == 1){
        // Create wallet
        $walletApiCreate = $this->_walletApiBaseUrl.'wallets';
        $walletHeader = array(
          "public_key: ".$publicKey
        );
        $walletResult = $this->exeUrlData($walletApiCreate, array(), 'POST', $walletHeader);

        if(!is_object($walletResult)){
          $data = array(
            'status' => array(
              'error' => 1,
              'msg' => $this->lang->line('server_error')
            ),
            'data' => NULL
          );
  
          return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));   
        }

        if(!empty($walletResult)){
          $address = "";
          foreach ($walletResult as $key => $value){
            $address = $key;
          }

          $paramsUpdateUser = array(
            'BGXAccount' => $publicKey,
            'bgtAddress' => $address
          );
          $this->user_model->updateUser($paramsUpdateUser, $email);
          $user->BGXAccount = $publicKey;
          $user->bgtAddress = $address;
          $isNeedPush = true;
        }

      }
      
      $params = array(
        'userId' => $user->id,
        'cardTypeId' => $cardTypeId,
        'template' => $template,
        'cardHolder' => $cardHolder,
        'issueDate' => $issueDate,
        'validTill' => $validTill,
        'PIN' => $PIN,
        'address' => $address,
        'publicKey' => $publicKey,
        'public_key_hashed' => $publicKeyHashed,
        'privateKey' => $privateKey,
        'active' => 1
      );

      $result = $this->card_model->addItem($params);

      if($result){
        $card = $this->card_model->getItem(array('id' => $result));

        if($cardType && $cardType->payment == 0 && !empty($address)){
          $paramsUpdateUser['ethereumAddress'] = $address;
          $this->user_model->updateUser($paramsUpdateUser, $email);
          $user->ethereumAddress = $address;

          $isNeedPush = true;
        }

        if($isNeedPush){
          // pushNotification
          $this->load->model('usertoken_model');
          $this->load->library('pushnotification');
          $paramsTokenAndroid = array(
            'userId' => $user->id,
            'deviceType' => 1
          );
          $tokensAndroid = $this->usertoken_model->getList($paramsTokenAndroid);

          $paramsTokenIOS = array(
            'userId' => $user->id,
            'deviceType' => 2
          );
          $tokensIOS = $this->usertoken_model->getList($paramsTokenIOS);
          $pushMsg = array(
            'title' => $this->lang->line('update_profile'),
            'text' => $this->lang->line('your_information_has_been_updated_on_another_device'),
            'data' => array(
              'profile' => (array) $user,
              'type' => 'update_profile'
            )
          );

          // $deviceToken = trim($postData['token']);
          $deviceToken = '';
          if(!empty($tokensAndroid)){
            $tokens = array();
            foreach ($tokensAndroid as $token) {
              if($token['token'] != $deviceToken){
                $tokens[] = $token['token'];
              }
            }

            $this->pushnotification->push($tokens, $pushMsg);
          }
          
          if(!empty($tokensIOS)){
            $tokens = array();
            foreach ($tokensIOS as $token) {
              if($token['token'] != $deviceToken){
                $tokens[] = $token['token'];
              }
            }

            $notification = array(
              'title' => $this->lang->line('update_profile'),
              'body' => $this->lang->line('your_information_has_been_updated_on_another_device'),
              'click_action' => 'fcm.ACTION.HELLO',
              'sound'=> 'default',
              'isShow' => 1,
              'is_background' => 1,
              'badge' => 0
            );

            $this->pushnotification->push($tokens, $pushMsg, 2, $notification);
          }
        }

        $data = array(
          'status' => array(
            'error' => 0,
            'msg' => $this->lang->line('create_card_successfully')
          ),
          'data' => $card
        );

      }else{
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('create_card_failed')
          ),
          'data' => NULL
        );

      }

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data)); 
    }

    public function updateCard(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['email'])){
        $postData['email'] = NULL;
      }

      if(!isset($postData['currentPassword'])){
        $postData['currentPassword'] = NULL;
      }

      if(!isset($postData['cardId'])){
        $postData['cardId'] = NULL;
      }

      if(!isset($postData['token'])){
        $postData['token'] = NULL;
      }

      $this->load->model(array('card_model', 'user_model', 'cardtype_model'));
      
      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $email = trim($postData['email']);
      $currentPassword = trim($postData['currentPassword']);
      $cardId = trim($postData['cardId']);
      
      // Check user Info
      $paramsUser = array(
        'email' => $email,
        'password' => $currentPassword
      );
      $user = $this->user_model->getUser($paramsUser);
      
      if(!$user){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }

      // Check card info
      $paramsCard = array(
        'id' => $cardId
      );
      $card = $this->card_model->getItem($paramsCard);
      
      if(!$card){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('card_do_not_exist')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }

      $paramsUpdate = array();

      if(isset($postData['template'])){
        $paramsUpdate['template'] = trim($postData['template']);
      }

      if(isset($postData['cardHolder'])){
        $paramsUpdate['cardHolder'] = trim($postData['cardHolder']);
      }

      if(isset($postData['issueDate'])){
        $paramsUpdate['issueDate'] = trim($postData['issueDate']);
      }

      if(isset($postData['validTill'])){
        $paramsUpdate['validTill'] = trim($postData['validTill']);
      }

      if(isset($postData['PIN'])){
        $paramsUpdate['PIN'] = trim($postData['PIN']);
      }

      if(isset($postData['address'])){
        $paramsUpdate['address'] = trim($postData['address']);
      }

      $cardType = $this->cardtype_model->getItem(array('id' => $card->cardTypeId));
      if(isset($postData['publicKey'])){
        $paramsUpdate['publicKey'] = trim($postData['publicKey']);
        
        // Check public belong user
        if(!empty($paramsUpdate['publicKey'])){
          $paramsCheckCard = array(
            'publicKey' => $paramsUpdate['publicKey']
          );
          $checkCard = $this->card_model->getItem($paramsCheckCard);
  
          if($checkCard && $user->id != $checkCard->userId){
            $data = array(
              'status' => array(
                'error' => 1,
                'msg' => $this->lang->line('publicKey_import_failed')
              ),
              'data' => NULL
            );
    
            return $this->output
            ->set_content_type('application/json')
            ->set_output(json_encode($data)); 
          }
        }

      }

      // Create wallet when card allow payment
      $isNeedPush = false;
      if($cardType && $cardType->payment == 1 && !empty($paramsUpdate['publicKey']) && $card->publicKey != $paramsUpdate['publicKey']){
        // Create wallet
        $walletApiCreate = $this->_walletApiBaseUrl.'wallets';
        $walletHeader = array(
          "public_key: ".$paramsUpdate['publicKey']
        );
        $walletResult = $this->exeUrlData($walletApiCreate, array(), 'POST', $walletHeader);

        if(!is_object($walletResult)){
          $data = array(
            'status' => array(
              'error' => 1,
              'msg' => $this->lang->line('server_error')
            ),
            'data' => NULL
          );
  
          return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));   
        }

        if(!empty($walletResult)){
          $address = "";
          foreach ($walletResult as $key => $value){
            $address = $key;
          }

          $paramsUpdate['address'] = $address;

          $paramsUpdateUser = array(
            'BGXAccount' => $paramsUpdate['publicKey'],
            'bgtAddress' => $address
          );
          $this->user_model->updateUser($paramsUpdateUser, $email);
          $user->BGXAccount = $paramsUpdate['publicKey'];
          $user->bgtAddress = $address;
          $isNeedPush = true;
        }

      }

      if(isset($postData['public_key_hashed'])){
        $paramsUpdate['public_key_hashed'] = trim($postData['public_key_hashed']);
      }

      if(isset($postData['privateKey'])){
        $paramsUpdate['privateKey'] = trim($postData['privateKey']);
      }

      if(isset($postData['active'])){
        $paramsUpdate['active'] = trim($postData['active']);
      }

      if(empty($paramsUpdate)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('no_data_to_update')
          ),
          'data' => NULL
        );

        return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));        
      }

      $result = $this->card_model->updateItem($paramsUpdate, $cardId);

      if($result){
        
        if($cardType && $cardType->payment == 1 && isset($paramsUpdate['active']) && $paramsUpdate['active'] == 0){
          $paramsUpdateUser = array(
            'BGXAccount' => NULL,
            'bgtAddress' => NULL
          );
          $this->user_model->updateUser($paramsUpdateUser, $email);
          $user->BGXAccount = NULL;
          $user->bgtAddress = NULL;
          $isNeedPush = true;
        }

        if($cardType && $cardType->payment == 0 && !empty($paramsUpdate['address'])){
          $paramsUpdateUser['ethereumAddress'] = $paramsUpdate['address'];
          $this->user_model->updateUser($paramsUpdateUser, $email);
          $user->ethereumAddress = $paramsUpdate['address'];

          $isNeedPush = true;
        }

        if($isNeedPush){
          // pushNotification
          $this->load->model('usertoken_model');
          $this->load->library('pushnotification');
          $paramsTokenAndroid = array(
            'userId' => $user->id,
            'deviceType' => 1
          );
          $tokensAndroid = $this->usertoken_model->getList($paramsTokenAndroid);

          $paramsTokenIOS = array(
            'userId' => $user->id,
            'deviceType' => 2
          );
          $tokensIOS = $this->usertoken_model->getList($paramsTokenIOS);
          $pushMsg = array(
            'title' => $this->lang->line('update_profile'),
            'text' => $this->lang->line('your_information_has_been_updated_on_another_device'),
            'data' => array(
              'profile' => (array) $user,
              'type' => 'update_profile'
            )
          );
          // $deviceToken = trim($postData['token']);
          $deviceToken = '';
          if(!empty($tokensAndroid)){
            $tokens = array();
            foreach ($tokensAndroid as $token) {
              if($token['token'] != $deviceToken){
                $tokens[] = $token['token'];
              }
            }

            $this->pushnotification->push($tokens, $pushMsg);
          }
          
          if(!empty($tokensIOS)){
            $tokens = array();
            foreach ($tokensIOS as $token) {
              if($token['token'] != $deviceToken){
                $tokens[] = $token['token'];
              }
            }

            $notification = array(
              'title' => $this->lang->line('update_profile'),
              'body' => $this->lang->line('your_information_has_been_updated_on_another_device'),
              'click_action' => 'fcm.ACTION.HELLO',
              'sound'=> 'default',
              'isShow' => 1,
              'is_background' => 1,
              'badge' => 0
            );

            $this->pushnotification->push($tokens, $pushMsg, 2, $notification);
          }
        }

        $cards = $this->card_model->getList(array('userId' => $user->id), $locale);

        $data = array(
          'status' => array(
            'error' => 0,
            'msg' => $this->lang->line('delete_card_successfully')
          ),
          'data' => $cards
        );

      }else{
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('delete_card_failed')
          ),
          'data' => NULL
        );

      }

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data)); 
    }

    public function interfaceCode(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');
      
      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      $interfaceCode = $postData['code'];

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $this->load->helper(array('file', 'xml'));

      $string = read_file('uploads/interface/code.xml');
      $items =  simplexml_load_string($string);
      foreach ($items as $item){
        if((string)$item->code == (string)$interfaceCode){
          $result = array(
            'color' => (string)$item->value->color,
            'language' => (string)$item->value->language,
            'logo' => (string)$item->value->logo,
            'text' => (string)$item->value->text
          );

          $data = array(
            'status' => array(
              'error' => 0,
              'msg' => ''
            ),
            'data' => $result
          );

          return $this->output
            ->set_content_type('application/json')
            ->set_output(json_encode($data));
        }
      }

      $data = array(
        'status' => array(
          'error' => 1,
          'msg' => $this->lang->line('interface_code_is_invalid')
        ),
        'data' => NULL
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    
    }

    public function userAvatar(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['userIdList'])){
        $postData['userIdList'] = NULL;
      }

      $this->load->model(array('user_model'));
      
      $userIdList = trim($postData['userIdList']);
      $arrayUserIdList = explode(",", $userIdList);
      $result = $this->user_model->getAvatars($arrayUserIdList);
      
      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => ''
        ),
        'data' => $result
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    public function listShoppingCartItem(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['userId'])){
        $postData['userId'] = NULL;
      }

      $this->load->model('shoppingcart_model');

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $params = array(
        'userId' => trim($postData['userId'])
      );
      
      if($params['userId'] === ""){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
        
      }

      $result = $this->shoppingcart_model->getList($params);

      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => ''
        ),
        'data' => $result
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    public function addShoppingCartItem(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['userId'])){
        $postData['userId'] = NULL;
      }

      if(!isset($postData['productId'])){
        $postData['productId'] = NULL;
      }

      $this->load->model('shoppingcart_model');

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $params = array(
        'userId' => trim($postData['userId']),
        'productId' => $postData['productId'],
        'created' => date('Y-m-d H:m:s', time())
      );
      
      if(empty($params['userId']) || empty($params['productId'])){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
        
      }
      
      $this->shoppingcart_model->addItem($params);
      $result = $this->shoppingcart_model->getList(array('userId' => trim($postData['userId'])));

      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => ''
        ),
        'data' => $result
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    public function deleteShoppingCartItem(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['userId'])){
        $postData['userId'] = NULL;
      }

      if(!isset($postData['productId'])){
        $postData['productId'] = 0;
      }

      $this->load->model('shoppingcart_model');

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $params = array(
        'userId' => trim($postData['userId']),
        'productId' => $postData['productId']
      );
      
      if(empty($params['userId']) || $postData['productId'] == 0){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
        
      }
      
      $item = $this->shoppingcart_model->getItem($params);

      if(!empty($item)){
        $this->shoppingcart_model->deleteItem($item->id);
      }

      $result = $this->shoppingcart_model->getList(array('userId' => trim($postData['userId'])));

      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => ''
        ),
        'data' => $result
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    public function clearShoppingCartItem(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['userId'])){
        $postData['userId'] = NULL;
      }

      $this->load->model('shoppingcart_model');

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $params = array(
        'userId' => trim($postData['userId'])
      );
      
      if(empty($params['userId'])){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('invalid_data')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
        
      }
      
      $this->shoppingcart_model->deleteAllItemByUser($params['userId']);
      $result = $this->shoppingcart_model->getList(array('userId' => trim($postData['userId'])));

      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => ''
        ),
        'data' => $result
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    function count_twitter(){
      
      require_once(APPPATH . 'third_party/twitteroauth/twitteroauth/twitteroauth.php');

      $twitteruser = "BGXGlobal";
      $notweets = 30;
      $consumerkey = "we9P7PHBbboUgucXt3GgxCtx6";
      $consumersecret = "LAwYMorRLfdDEjbE8wzvhH50OsYNo1wkxaoPyu0SIIJvqhlu9f";
      $accesstoken = "1053124645493194752-ycGoxOlaEm9dmtc9k6fHyppO7jIVhz";
      $accesstokensecret = "Hy5BanGojQxGVXhrzJWTH9qbC6oCOFmnKv7U72T6R4Ufl";
      
      function getConnectionWithAccessToken($cons_key, $cons_secret, $oauth_token, $oauth_token_secret) {
        $connection = new TwitterOAuth($cons_key, $cons_secret, $oauth_token, $oauth_token_secret);
        return $connection;
      }
      
      $connection = getConnectionWithAccessToken($consumerkey, $consumersecret, $accesstoken, $accesstokensecret);

      $tweets = $connection->get("https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=".$twitteruser."&count=".$notweets);
      
      $count = (int)$tweets[0]->user->statuses_count;

      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => ''
        ),
        'data' => $count
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    public function sendPaymentEmail(){
      $_POST = json_decode(file_get_contents('php://input'), true);
      $postData = $this->input->post('data');

      if(!isset($postData['locale'])){
        $postData['locale'] = NULL;
      }

      if(!isset($postData['email'])){
        $postData['email'] = NULL;
      }

      $locale = $this->language_model->checkLocale($postData['locale']);
      $this->lang->load('messages', $locale);

      $email = $postData['email'];

      if(!filter_var($email, FILTER_VALIDATE_EMAIL) || empty($email)){
        $data = array(
          'status' => array(
            'error' => 1,
            'msg' => $this->lang->line('email_is_invalid')
          ),
          'data' => NULL
        );

        return $this->output
          ->set_content_type('application/json')
          ->set_output(json_encode($data));
      }

      $this->load->model(array('emailtemplate_model', 'user_model', 'shoppingcart_model'));

      $emailTemplate = $this->emailtemplate_model->getItem(array('locale' => $locale, 'type' => 'payment'));
      $emailTemplate->message = str_replace("<img_url>", $this->_imageHost, $emailTemplate->message);
      $this->sendEmail($email, $emailTemplate->subject, $emailTemplate->message);

      $paramsUser = array(
        'email' => trim($postData['email'])
      );
      $user = $this->user_model->getUser($paramsUser);

      if($user){
        $this->shoppingcart_model->deleteAllItemByUser($user->id);
      }

      $data = array(
        'status' => array(
          'error' => 0,
          'msg' => '' 
        ),
        'data' => NULL
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    public function methodNotFound(){
      $data = array(
        'status' => array(
          'error' => 1,
          'msg' => 'Method not found'
        ),
        'data' => NULL
      );

      return $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }

    private function isEthereumAddress($address){
      return true;

      // Tmp disable check ETH format
      // if(strlen($address) != 42 || substr($address, 0 ,2) != "0x"){
      //   return false;
      // }
      // return true;
    }

    private function sendEmail($to, $subject, $message){
      $this->load->library('email');

      // $config = array(
      //   'protocol'  => 'smtp',
      //   'smtp_host' => 'ssl://smtp.googlemail.com',
      //   'smtp_port' => 465,
      //   'smtp_user' => 'email',
      //   'smtp_pass' => 'password',
      //   'mailtype'  => 'html',
      //   'charset'   => 'utf-8'
      // );
      // $this->email->initialize($config);
      $this->email->set_mailtype("html");
      $this->email->set_newline("\r\n");
      $this->email->set_crlf("\r\n");

      $this->email->from('nguyentam.titan@gmail.com', 'BGX');
      $this->email->to($to);
      
      $this->email->subject($subject);
      $this->email->message($message);
      
      if($this->email->send()){
        return true;
      }
      return false;
    }

    private function exeUrlData($url = '', $data, $method = "GET", $header = array()){
      $curl = curl_init();

      $header[] = "cache-control: no-cache";
      $header[] = "Content-Type:application/json";

      curl_setopt_array($curl, array(
        CURLOPT_URL => $url,
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_TIMEOUT => 30,
        CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
        CURLOPT_CUSTOMREQUEST => $method,
        CURLOPT_HTTPHEADER => $header,
        CURLOPT_POSTFIELDS => json_encode($data)
      ));

      $response = curl_exec($curl);
      $err = curl_error($curl);

      curl_close($curl);

      return json_decode($response);
    }

  }
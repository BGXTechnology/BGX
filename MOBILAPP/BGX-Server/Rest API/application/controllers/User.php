<?php
  class User extends CI_Controller {
    private $locale;
    
    public function __construct(){
      parent::__construct();
      $this->load->model('language_model');
      $this->load->helper('url');

      $this->locale = $this->input->get('locale');
    }

    public function activation($hash){
      $this->load->model(array('user_model', 'usertoken_model'));

      $params = array(
        'hash' => $hash
      );

      $locale = $this->language_model->checkLocale($this->locale);
      $this->lang->load('messages', $locale);
      $user = $this->user_model->getUser($params);
      if($user){
        $time = strtotime($user->createdLink);
        $now = time();
        // 12 hours = 43200
        if($now - $time < 43200){
          if($user->active == 1){
            $msg = $this->lang->line('already_activated');
          }else{
            $paramsUpdate = array(
              'active' => 1
            );
  
            $this->user_model->updateUser($paramsUpdate, $user->email);

            // pushNotification
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
              'title' => $this->lang->line('active_successfully'),
              'text' => $this->lang->line('active_successfully'),
              'data' => array(
                'userId' => $user->id,
                'type' => 'activation'
              )
            );

            if(!empty($tokensAndroid)){
              $tokens = array();
              foreach ($tokensAndroid as $token) {
                $tokens[] = $token['token'];
              }

              $this->pushnotification->push($tokens, $pushMsg);
            }
            
            if(!empty($tokensIOS)){
              $tokens = array();
              foreach ($tokensIOS as $token) {
                $tokens[] = $token['token'];
              }

              $notification = array(
                'title' => $this->lang->line('active_successfully'),
                'body' => $this->lang->line('active_successfully'),
                'click_action' => 'fcm.ACTION.HELLO',
                'sound'=> 'default',
                'isShow' => 1,
                'is_background' => 1,
                'badge' => 0
              );

              $this->pushnotification->push($tokens, $pushMsg, 2, $notification);
            }
  
            $msg = $this->lang->line('active_successfully');
          }
        }else{
          $msg = $this->lang->line('activation_link_was_expired');
        }
      }else{
        $msg = $this->lang->line('invalid_data');
      }

      $data['msg'] = $msg;
      $this->load->view('user/activation', $data);
    }

    public function reset($hash){
      $this->load->model('user_model');
      $this->load->helper('form');
      $this->load->library('form_validation');

      $locale = $this->language_model->checkLocale($this->locale);
      $this->lang->load('messages', $locale);

      if(empty($hash)){
        $msg = $this->lang->line('invalid_data');

        $data['msg'] = $msg;
        $this->load->view('user/resetpass', $data);
      }

      $params = array(
        'resetHash' => $hash
      );

      $locale = $this->language_model->checkLocale($this->locale);
      $data['locale'] = $locale;
      $this->lang->load('messages', $locale);
      $user = $this->user_model->getUser($params);
      $msg = '';

      if($user){
        $time = strtotime($user->resetedLink);
        $now = time();
        // 12 hours = 43200

        if($now - $time < 43200){
          if($user->isReseted == 0){
            $this->form_validation->set_rules('password', 'Password', 'required|min_length[6]', array('required' => $this->lang->line('password_required'), 'min_length' => $this->lang->line('password_must_be_at_least_6_symbols_web'), 'max_length' => $this->lang->line('password_cannot_exceed_64_symbols_web')));
            $this->form_validation->set_rules('passconf', 'Password', 'matches[password]', array('matches' => $this->lang->line('password_does_not_match')));

            if ($this->form_validation->run() == FALSE){
              $data['msg'] = validation_errors();
              $data['listMsg'] = array(
                $this->lang->line('password_required'),
                $this->lang->line('password_cannot_space_only'),
                $this->lang->line('password_cannot_exceed_64_symbols_web'),
                $this->lang->line('password_does_not_match')
              );
              $this->load->view('user/resetpass', $data);
            }else{
              $password = md5($this->input->post('password'));

              $paramsUpdate = array(
                'password' => $password,
                'isReseted' => 1
              );

              $this->user_model->updateUser($paramsUpdate, $user->email);

              $msg = $this->lang->line('reset_password_successfully_web');;

              $data['msg'] = $msg;
              $this->load->view('user/resetpass_success', $data);
            }

          }else{
            $msg = $this->lang->line('already_reset');

            $data['msg'] = $msg;
            $this->load->view('user/resetpass_success', $data);
          }
        }else{
          $msg = $this->lang->line('reset_link_was_expired');

          $data['msg'] = $msg;
          $this->load->view('user/resetpass_success', $data);
        }
      }else{
        $msg = $this->lang->line('invalid_data');

        $data['msg'] = $msg;
        $this->load->view('user/resetpass_success', $data);
      }

    }

  }
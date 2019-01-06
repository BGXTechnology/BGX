<?php

  class Errorpage extends CI_Controller {

    public function pageNotFound()
    {
      $data = array(
        'status' => array(
          'error' => 1,
          'msg' => '404 Page not found'
        ),
        'data' => NULL
      );

      return  $this->output
        ->set_content_type('application/json')
        ->set_output(json_encode($data));
    }
  }

<?php
  class Emailtemplate_model extends CI_Model {

    public function __construct(){

      $this->load->database();
    }

    public function getItem($params = NULL){
      $query = $this->db->get_where('emailtemplate', $params);
      return $query->row();
    }
    
  }
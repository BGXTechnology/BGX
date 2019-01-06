<?php
  class Usertoken_model extends CI_Model {

    public function __construct(){

      $this->load->database();
    }

    public function addItem($params = array()){
      if(empty($params)){
        return false;
      }
      
      if($this->db->insert('user_token', $params)){
        return $this->db->insert_id();
      }
      return false;
    }

    public function getList($params = NULL){
      $this->db->select('*');
      $query = $this->db->get_where('user_token', $params);

      return $query->result_array();
    }

    public function isTokenExist($params){
      $query = $this->db->get_where('user_token', $params);
      $result = $query->row();
      
      return ($result === NULL) ? false :  true;
    }

    public function deleteUserTokens($userId){
      $this->db->where('userId', $userId);
      return $this->db->delete('user_token');
    }

  }
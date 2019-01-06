<?php
  class User_model extends CI_Model {

    public function __construct(){

      $this->load->database();
    }

    public function addUser($params = array()){
      if(empty($params)){
        return false;
      }
      
      if($this->db->insert('user', $params)){
        return $this->db->insert_id();
      }
      return false;
    }

    public function getUser($params = NULL){
      $query = $this->db->get_where('user', $params);
      return $query->row();
    }

    public function updateUser($params = NULL, $email){
      $this->db->where('email', $email);
      return $this->db->update('user', $params);
    }

    public function isEmailExist($email){
      $query = $this->db->get_where('user', array('email' => $email));
      $result = $query->row();
      
      return ($result === NULL) ? false :  true;
    }

    public function getAvatars($userIdList =  array()){
      $result = array();

      if(!empty($userIdList)){
        $this->db->select('magentoId, avatar');
        $this->db->from('user');
        $this->db->where_in('magentoId', $userIdList);
        $query = $this->db->get();

        $result = $query->result_array();
      }
      
      return $result;
    }

  }
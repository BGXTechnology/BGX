<?php
  class Shoppingcart_model extends CI_Model {

    public function __construct(){

      $this->load->database();
    }

    public function addItem($params = array()){
      if(empty($params)){
        return false;
      }
      
      if($this->db->insert('shopping_cart', $params)){
        return $this->db->insert_id();
      }
      return false;
    }

    public function getItem($params = NULL){
      $query = $this->db->get_where('shopping_cart', $params);
      return $query->row();
    }

    public function getList($params = NULL){
      $this->db->order_by('created', 'DESC');
      $query = $this->db->get_where('shopping_cart', $params);
      return $query->result_array();
    }

    public function deleteItem($id){
      $this->db->where('id', $id);
      return $this->db->delete('shopping_cart');
    }

    public function deleteAllItemByUser($userId){
      $this->db->where('userId', $userId);
      return $this->db->delete('shopping_cart');
    }

  }
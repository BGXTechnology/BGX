<?php
  class Card_model extends CI_Model {

    public function __construct(){

      $this->load->database();
    }

    public function addItem($params = array()){
      if(empty($params)){
        return false;
      }
      
      if($this->db->insert('card', $params)){
        return $this->db->insert_id();
      }
      return false;
    }

    public function getItem($params = NULL){
      $query = $this->db->get_where('card', $params);
      return $query->row();
    }

    public function getList($params = NULL, $locale = 'en'){
      $where = "";
      if(!empty($params)){
        $arr = array();
        foreach($params as $k => $v){
          $arr[] = "card.".$k."=".$this->db->escape($v);
        }
        $where = implode(" AND ", $arr);
      }

      $this->db->select('*, card.id as id');
      $this->db->from('card');
      $this->db->join('cardtype_description', 'card.cardTypeId = cardtype_description.cardTypeId');
      $this->db->join('cardtype', 'cardtype.id = cardtype_description.cardTypeId');
      $this->db->where('cardtype_description.locale', $locale);
      $this->db->where($where);

      $query = $this->db->get();
      
      return $query->result_array();
    }

    public function updateItem($params = NULL, $id){
      $this->db->where('id', $id);
      return $this->db->update('card', $params);
    }

    public function deleteItem($id){
      $this->db->where('id', $id);
      return $this->db->delete('card');
    }

  }
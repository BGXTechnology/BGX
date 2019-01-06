<?php
  class Cardtype_model extends CI_Model {

    public function __construct(){
      $this->load->database();
    }

    public function getList($params = array(), $locale = 'en', $select = array()){
      
      if(empty($select)){
        $select = '*';
      }

      $where = "";
      if(!empty($params)){
        $arr = array();
        foreach($params as $k => $v){
          $arr[] = "cardtype.".$k."=".$this->db->escape($v);
        }
        $where = implode(" AND ", $arr);
      }
      
      $this->db->select($select);
      $this->db->from('cardtype');
      $this->db->join('cardtype_description', 'cardtype.id = cardtype_description.cardTypeId', 'left');
      $this->db->where('cardtype_description.locale', $locale);
      if($where != ""){
        $this->db->where($where);
      }
      
      $query = $this->db->get();
      
      return $query->result_array();
    }

    public function getItem($params = array()){
      $query = $this->db->get_where('cardtype', $params);
      return $query->row();
    }

  }
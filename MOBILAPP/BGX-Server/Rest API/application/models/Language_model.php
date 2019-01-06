<?php
  class Language_model extends CI_Model {

    private $defaultLanguage = 'en';

    public function __construct(){
      $this->load->database();
    }

    public function getLanguages($params = array(), $select = array()){
      if(empty($select)){
        $select = '*';
      }

      $this->db->select($select);
      $query = $this->db->get_where('language', $params);
      return $query->result_array();
    }

    public function getLanguage($params = array(), $select = array()){
      if(empty($select)){
        $select = '*';
      }

      $this->db->select($select);
      $query = $this->db->get_where('language', $params);
      return $query->row_array();
    }

    public function checkLocale($locale){
      $arrayLanguage = $this->getLanguages(array(), 'locale');

      if($locale === NULL || array_search($locale, array_column($arrayLanguage, 'locale')) === false){
        return $this->defaultLanguage;
      }else{
        return $locale;
      }
    }

  }
#include "graphclass.h"
#include "dxatom.h"

void initELM( ELM *e){
 
  ELM_IDX(e) = 0;
  ELM_DATA(e) = NULL;

}

ELM *makeELM () {
	ELM *e = (ELM *) ( malloc (sizeof (ELM)));
	initELM (e);
	return e;
}

void freeELM( ELM *e){

  if( e != NULL){
  
    if( ELM_DATA(e) != NULL){
    
      free( ELM_DATA(e));
      ELM_DATA(e) = NULL;
    
    }
  
    free(e);
    e = NULL;
  
  }

}


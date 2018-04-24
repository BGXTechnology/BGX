#include "graphclass.h"
#include "dxatom.h"
#include "dxlist.h"


void ELinit( dxlist *el){

  dxlist_CURR( el) = NULL;
  dxlist_PREV( el) = NULL;
  dxlist_NEXT( el) = NULL;

}

dxlist *ELfreeNonRecursive( dxlist *el){

  if( el != NULL){
    free( el);
    el = NULL;
  }
  
  return el;

}

dxlist *ELfreeRecursive( dxlist *el){

  if( dxlist_CURR( el) == NULL){
    freeELM( dxlist_CURR( el));
  }

  el = ELfreeNonRecursive( el);

  return el;

}

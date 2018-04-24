#include "dxgraphserv.h"
#include "dxatom.h"
#include "dxvector.h"
#include "dxheap.h"
#include "allprint.h"


void PQinsertELM( ELM *e, dxvector *q) {

  int i, mid;


  i = dxvector_TOTALELMS(q);
  addToArray( q, e);

  while (1) {

    mid = (i-1)/2;

    if( i==mid ||
    		ELM_IDX( dxvector_ELMS_POS( q, mid)) <= ELM_IDX(e)) {
      break;
    }


    dxvector_ELMS_POS( q, i) = dxvector_ELMS_POS( q, mid);

    i = mid;

  }

  dxvector_ELMS_POS( q, i) = e;

}

void PQinsert( int x, dxvector *q) {

  ELM *e;

  e = (ELM *) malloc (sizeof( ELM));
  ELM_DATA(e) = NULL;
  ELM_IDX(e) = x;

  PQinsertELM( e, q);

}

void PQdeleteMin( dxvector *q) {

  int i, child;
  ELM *last;

  if (!(dxvector_TOTALELMS(q) > 0)) {
	  printf ("PQdeleteMin : Priority queue is empty\n");
	  exit(-1);
  }

  last = dxvector_ELMS_POS( q, dxvector_TOTALELMS(q) - 1);

  for (i = 0; i * 2 < dxvector_TOTALELMS(q) - 2; i = child) {

    child = i * 2 + 1;

    if( ELM_IDX( dxvector_ELMS_POS( q, child + 1)) <
	ELM_IDX( dxvector_ELMS_POS( q, child ))) {
      child++;
    }

    if( ELM_IDX( last) > ELM_IDX( dxvector_ELMS_POS( q, child ))) {

      if( i == 0) {
    	  freeELM( dxvector_ELMS_POS( q, i));
      }

      dxvector_ELMS_POS( q, i) = dxvector_ELMS_POS( q, child);

    } else {

      break;

    }

  }

  dxvector_ELMS_POS( q, i) = last;
  dxvector_ELMS_POS( q, --dxvector_TOTALELMS(q)) = NULL;

}

ELM* PQgetMinELM( dxvector *q) {

  if (!(dxvector_TOTALELMS(q) > 0)) {
	  printf ("PQgetMinELM : Priority queue is empty\n");
	  exit(-1);
  }

  ELM *result;
  result = dxvector_ELMS_POS( q, 0);

  return result;

}

int PQgetMin( dxvector *q) {

  if (!(dxvector_TOTALELMS(q) > 0)) {
	  printf ("PQgetMin : Priority queue is empty\n");
	  exit(-1);
  }

  int result;
  result = ELM_IDX( PQgetMinELM(q));

  return result;

}

void PQprint( dxvector *q){

  printdxvector(q);

}

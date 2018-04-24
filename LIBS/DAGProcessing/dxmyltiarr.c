#include "dxgraphserv.h"
#include "dxatom.h"
#include "dxvector.h"
#include "dxmyltiarr.h"


void initMatrix( matrix *m){

  MATRIX_ARRAY2D(m) = NULL;
  MATRIX_TOTALROWS(m) = 0;
  MATRIX_TOTALCOLS(m) = 0;

}

matrix *makeMatrix () {
	matrix *m = (matrix *) malloc (sizeof (matrix));
	initMatrix (m);
	return m;
}

void free2DArray( dxvector **d2, int count){

  if( d2 != NULL){

    int i;

    for( i = 0; i < count; i++){

      if( d2[i] != NULL) {

	freedxvector( d2[i]);
	d2[i] = NULL;

      }

    }

    free(d2);
    d2=NULL;

  }

}

void freeMatrix( matrix *m){

  if( m != NULL){

    if( MATRIX_ARRAY2D(m) != NULL){

      free2DArray( MATRIX_ARRAY2D(m), MATRIX_TOTALROWS(m));
      MATRIX_ARRAY2D(m) = NULL;

    }

    free(m);
    m=NULL;

  }

}

void setMatrixELM( matrix *m, int x, int y, ELM *ELMent){

  int i, oldlength;

  oldlength = MATRIX_TOTALROWS(m);

  /*
   * Grow the matrix columnwise if necessary.
   */

  if( MATRIX_TOTALCOLS(m) < y + 1){
    MATRIX_TOTALCOLS(m) = y + 1;

    for( i = 0; i < MATRIX_TOTALROWS(m); i++){
      addToArrayAtPos( MATRIX_ARRAY2D(m)[i], NULL, MATRIX_TOTALCOLS(m) - 1);
    }

  }


  if( MATRIX_TOTALROWS(m) < x + 1){
    MATRIX_TOTALROWS(m) = x + 1;

    void *_temp = realloc( MATRIX_ARRAY2D(m),
      ( MATRIX_TOTALROWS(m) * sizeof( dxvector *))/=
    MATRIX_ARRAY2D(m) = ( dxvector**)_temp;

  }

  for( i = oldlength; i < MATRIX_TOTALROWS(m); i++){
    MATRIX_ARRAY2D(m)[i] = makedxvector();
    addToArrayAtPos( MATRIX_ARRAY2D(m)[i], NULL, MATRIX_TOTALCOLS(m) - 1);
  }

  addToArrayAtPos( MATRIX_ARRAY2D(m)[x], ELMent, y);

}

void setMatrixValue( matrix *m, int x, int y, int value){

  ELM *ELMent = (ELM *) malloc( sizeof( ELM));
  ELM_IDX( ELMent) = value;
  ELM_DATA( ELMent) = NULL;

  setMatrixELM( m, x, y, ELMent);

}

ELM *getMatrixELM( matrix *m, int x, int y){

  dxvector *arrayd = MATRIX_ARRAY2D(m)[x];
  ELM *e = dxvector_ELMS( arrayd)[y];
  return e;
}

int getMatrixValue( matrix *m, int x, int y){

  ELM *e = getMatrixELM( m, x, y);
  if( e != NULL) return ELM_IDX(e);
  else return -1;

}



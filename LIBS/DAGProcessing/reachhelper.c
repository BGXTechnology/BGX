#include "graphclass.h"
#include "dxgraphserv.h"
#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "reachhelper.h"
#include "allprint.h"

dxvector* buildTransitiveLinkTable( dxvector *arrayd, dxvector *csrc,
		dxvector *ctar, dxvector *prearr){



	int i, j, k, l, lower, upper, source, target;
	ELM *e;
	//int i_idx, k_idx, j_data, k_data;
	int s, t, idx, data;
	matrix *adjmat;
	dxvector *result;
	vertex *v;

	adjmat = makeMatrix();

	for( i = 0 ; i < dxvector_TOTALELMS( arrayd) ; i++){
		idx = ELM_IDX( dxvector_ELMS_POS( arrayd, i));
		data = *(int*) ELM_DATA( dxvector_ELMS_POS( arrayd, i));
		s = getPositionInArray (csrc, idx);
		t = getPositionInArray (ctar, data);
		setMatrixValue( adjmat, s, t, 1);
	}


	for( i = 0 ; i < MATRIX_TOTALROWS( adjmat) ; i++){

		for( j = 0 ; j < dxvector_TOTALELMS( MATRIX_ARRAY2D( adjmat)[i]) ; j++){
			source = ELM_IDX ( dxvector_ELMS_POS( csrc, i));
			target = ELM_IDX ( dxvector_ELMS_POS( ctar, j));
			if (source == target) {
				setMatrixValue( adjmat, i, j, 1);
			}


			if (getMatrixValue(adjmat, i, j) == 1) {
				for (k = 0; k < dxvector_TOTALELMS( csrc); k++) {

					lower = ELM_IDX ( dxvector_ELMS_POS( csrc, k));
					v = (vertex *) ELM_DATA (dxvector_ELMS_POS( prearr, lower-1));
					upper = VERTEX_PREMAX (v);

					source = ELM_IDX( dxvector_ELMS_POS( csrc, i));

					if( lower <= source && source < upper){
						setMatrixValue( adjmat, k, j, 1);
					}
				}

			}

			if (getMatrixValue(adjmat, i, j) == 1) {
				for (k = 0; k < dxvector_TOTALELMS( ctar); k++) {

					lower = ELM_IDX ( dxvector_ELMS_POS( ctar, j));
					v = (vertex *) ELM_DATA (dxvector_ELMS_POS( prearr, lower-1));
					upper = VERTEX_PREMAX (v);

					target = ELM_IDX( dxvector_ELMS_POS( ctar, k));

					if( lower <= target && target < upper){
						setMatrixValue( adjmat, i, k, 1);
					}
				}

			}

			/*
			 * now close sources reaching targets reaching sources reaching targets
			 */

			if( getMatrixValue( adjmat, i, j) == 1){

				lower = ELM_IDX ( dxvector_ELMS_POS( ctar, j));
				v = (vertex *) ELM_DATA (dxvector_ELMS_POS( prearr, lower-1));
				upper = VERTEX_PREMAX (v);

				for( k = 0 ; k < dxvector_TOTALELMS( csrc) ; k++){

					source = ELM_IDX( dxvector_ELMS_POS( csrc, k));



					if( lower <= source && source < upper){



						for ( l = 0; l < dxvector_TOTALELMS (MATRIX_ARRAY2D (adjmat)[k]); l++){
							if (getMatrixValue( adjmat, k, l) == 1)
								setMatrixValue( adjmat, i, l, 1);
						}
						//printf("yes\n");
					}

				}

			}

		}

	}

	result = makedxvector ();

	for( i = 0 ; i < MATRIX_TOTALROWS( adjmat) ; i++){

		for( j = 0 ; j < dxvector_TOTALELMS( MATRIX_ARRAY2D( adjmat)[i]) ; j++){

			if( /*i != j &&*/ getMatrixValue( adjmat, i, j) == 1){
				e = (ELM *) malloc( sizeof( ELM));
				ELM_IDX(e) = ELM_IDX( dxvector_ELMS_POS( csrc, i));
				ELM_DATA(e) = malloc(2 * sizeof(int));

				/*
				 * Tha data bit in the csrc ELMs contains pre-max values
				 */

				*(int *) ELM_DATA(e) = ELM_IDX( dxvector_ELMS_POS( ctar, j));
				ELM *e_v = dxvector_ELMS_POS (prearr, *(int *) ELM_DATA(e));
				vertex *v = (vertex *) ELM_DATA (e_v);
				((int *) ELM_DATA(e))[1] = VERTEX_PREMAX (v);
				addToArray( result, e);
			}

		}

	}

	freeMatrix( adjmat);
	return result;

}



void setSrcTarArrays( dxvector *arrayd, dxvector** arrX, dxvector** arrY){

	int a;
	ELM *e;
	dxvector *arraydX, *arraydY;

	arraydX = (dxvector *) malloc( sizeof( dxvector));
	initdxvector( arraydX);
	arraydY = (dxvector *) malloc( sizeof( dxvector));
	initdxvector( arraydY);

	for( a = 0 ; a < dxvector_TOTALELMS( arrayd) ; a++){

		if( !indexExistsInArray( arraydX, ELM_IDX( dxvector_ELMS_POS( arrayd, a)))){
			e = (ELM *) malloc( sizeof( ELM));
			ELM_IDX( e) = ELM_IDX( dxvector_ELMS_POS( arrayd, a));
			addToArray( arraydX, e);
		}

		if( !indexExistsInArray(arraydY,
				*((int *)ELM_DATA(dxvector_ELMS_POS(arrayd,a))))){
			e = (ELM *) malloc( sizeof( ELM));
			ELM_IDX(e) = *((int *)ELM_DATA( dxvector_ELMS_POS( arrayd, a)));
			ELM_DATA(e) = malloc( sizeof( int));

			*((int *)ELM_DATA(e)) = 1;
			addToArray( arraydY, e);
		}
	}

	sortArray( dxvector_ELMS( arraydX), 0, dxvector_TOTALELMS( arraydX) - 1, 0);
	sortArray( dxvector_ELMS( arraydY), 0, dxvector_TOTALELMS( arraydY) - 1, 0);

	*arrX = arraydX;
	*arrY = arraydY;

}


matrix* computeTLCMatrix( dxvector *tltable, dxvector* csrc, dxvector* ctar){

	int i, j, a, i_pos, j_pos;
	matrix *tlc;

	tlc = makeMatrix();

	sortArray( dxvector_ELMS( tltable), 0, dxvector_TOTALELMS( tltable) - 1, 1);

	for( a = 0 ; a < dxvector_TOTALELMS( tltable) ; a++){
		i = ELM_IDX (dxvector_ELMS_POS (tltable, a));
		j = *((int *)ELM_DATA( dxvector_ELMS_POS( tltable, a)));
		i_pos = getPositionInArray (csrc, i);
		j_pos = getPositionInArray (ctar, j);
		setMatrixValue( tlc, i_pos, j_pos,
				(*(int *)ELM_DATA(dxvector_ELMS_POS(ctar,j_pos)))++);


	}

	for (i= dxvector_TOTALELMS (csrc) - 2; i>=0; i-- ) {
		for (j=0; j<dxvector_TOTALELMS (ctar); j++) {
			if (getMatrixValue (tlc, i, j) == -1) {
				setMatrixValue (tlc, i, j, getMatrixValue (tlc, i+1, j));
			}
		}

	}

	return tlc;

}


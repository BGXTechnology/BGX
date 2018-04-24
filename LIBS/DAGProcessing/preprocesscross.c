

#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "dxgraphserv.h"
#include "graphclass.h"
#include "allprint.h"
#include "preprocesstree.h"
#include "preprocesscross.h"
#include "dxheap.h"
#include "query.h"

typedef struct POSTINFO{
	int iscsrc;
	int colidx;
	vertex *vertex;
}postinfo;

#define POSTINFO_ISCSRC( n) ( (n)->iscsrc)
#define POSTINFO_COLIDX( n) ( (n)->colidx)
#define POSTINFO_VERTEX( n) ( (n)->vertex)

typedef struct TOPOINFO{
	int colidx;
	vertex *vertex;
}topoinfo;

#define TOPOINFO_COLIDX( n) ( (n)->colidx)
#define TOPOINFO_VERTEX( n) ( (n)->vertex)

typedef struct PCPCINFO{
	dxvector *csrc;
	matrix *csrcmat;
	dxvector *noncsrc;
	matrix *noncsrcmat;
}pcpcinfo;

#define PCPCINFO_CSRC( n) ( (n)->csrc)
#define PCPCINFO_CSRCMAT( n) ( (n)->csrcmat)
#define PCPCINFO_NONCSRC( n) ( (n)->noncsrc)
#define PCPCINFO_NONCSRCMAT( n) ( (n)->noncsrcmat)


postinfo *makePostinfo () {
	postinfo *pi = (postinfo *) malloc (sizeof (postinfo));
	POSTINFO_ISCSRC (pi) = 0;
	POSTINFO_COLIDX (pi) = 0;
	POSTINFO_VERTEX (pi) = NULL;
	return pi;
}


void printTopoverts (dxvector *topoarr) {
	int i, total = dxvector_TOTALELMS (topoarr);
	ELM *e;
	vertex *v;
	printf ("[");
	for (i=0; i<total; i++) {
		e = dxvector_ELMS_POS (topoarr, i);
		v = TOPOINFO_VERTEX (((topoinfo *) ELM_DATA (e)));
		printf ("%d,", VERTEX_PRE (v));
	}
	printf("]\n");
}

matrix *preprocesscreateReachMat( compinfo *ci){

	dxvector *csrc, *ctar, *prearr;
	int i, j;
	matrix *result;
	vertex *srcvert, *tarvert;
	ELM *e;

	result = makeMatrix ();

	csrc = COMPINFO_CSRC( ci);
	ctar = COMPINFO_CTAR( ci);
	prearr = COMPINFO_PREARR( ci);

	for( i = 0; i < dxvector_TOTALELMS( csrc); i++){

		e = dxvector_ELMS_POS( prearr, ELM_IDX(
				dxvector_ELMS_POS( csrc, i)) - 1);
		srcvert = ( vertex *) ELM_DATA ( e);

		for( j = 0; j < dxvector_TOTALELMS( ctar); j++){

			e = dxvector_ELMS_POS( prearr, ELM_IDX(
					dxvector_ELMS_POS( ctar, j)) - 1);
			tarvert = ( vertex *) ELM_DATA ( e);

			if( GINisReachable( srcvert, tarvert, ci)){
				//printf ("%d reaches %d\n", srcvert->pre, tarvert->pre);
				setMatrixValue( result, j, i, 1);
			} else {
				setMatrixValue( result, j, i, 0);
			}

		}

	}

	return result;

}

matrix* preprocesscreatePCPTMat( matrix *reachmat, compinfo *ci){

	matrix *pcptmat;
	dxstack *stk;
	ELM *e;
	dxvector *csrc, *ctar;

	csrc = COMPINFO_CSRC( ci);
	ctar = COMPINFO_CTAR( ci);

	int i, j;
	int prev_lower = -1;

	stk = (dxstack *) malloc( sizeof( dxstack));
	initdxstack(stk);

	pcptmat = makeMatrix ();

	for( i = 0; i < dxvector_TOTALELMS( ctar); i++){

		prev_lower = -1;

		for( j = 0; j < dxvector_TOTALELMS( csrc) + 1; j++){

			e = (ELM *) malloc( sizeof( ELM));
			ELM_IDX(e) = j;
			ELM_DATA(e) = malloc( 2 * sizeof( int));
			((int *)ELM_DATA(e))[0] = prev_lower;

			pushdxstack( &stk, e);

			if ( j >= dxvector_TOTALELMS( csrc)) {
				break;
			}

			if( getMatrixValue( reachmat, i, j) == 1){

				while( !isdxstackEmpty(stk)){

					e = popdxstack( &stk);


					((int *)ELM_DATA(e))[1] = ELM_IDX( dxvector_ELMS_POS( csrc, j));
					setMatrixELM( pcptmat, i, ELM_IDX(e), e);

				}

				prev_lower = ELM_IDX( dxvector_ELMS_POS( csrc, j));

			}

		}


		while( !isdxstackEmpty( stk)){

			e = popdxstack( &stk);
			((int *)ELM_DATA(e))[1] = -1;
			setMatrixELM( pcptmat, i, ELM_IDX(e), e);

		}

	}

	return pcptmat;

}


dxvector * preprocesssortInPostorder( compinfo *ci){

	dxvector *result, *prearr, *csrc;
	int i;
	ELM *e;
	postinfo *data;
	vertex *v;
	int prenum;

	prearr = COMPINFO_PREARR( ci);
	csrc = COMPINFO_CSRC( ci);

	if (!(prearr != NULL && csrc != NULL)) {
		printf ("Incompatible arguments passed to preprocesssortInPostorder");
		exit(-1);
	}

	result = (dxvector *) malloc( sizeof( dxvector));
	initdxvector( result);

	for( i = 0; i < dxvector_TOTALELMS( csrc); i++){

		e = (ELM *) malloc( sizeof( ELM));

		prenum = ELM_IDX( dxvector_ELMS_POS( csrc, i));
		v = (vertex *) ELM_DATA( dxvector_ELMS_POS( prearr, prenum - 1));

		ELM_IDX(e) = VERTEX_POST( v);
		ELM_DATA(e) = makePostinfo();

		data = ( postinfo *)( ELM_DATA(e));

		POSTINFO_ISCSRC( data) = 1;
		POSTINFO_COLIDX( data) = i;
		POSTINFO_VERTEX( data) = v;

		addToArray( result, e);

	}

	sortArray( dxvector_ELMS( result), 0,
			dxvector_TOTALELMS( result) - 1, 0);

	return result;

}

void preprocessorColumnsAndUpdate( matrix *m1, int colidx1,
		matrix *m2, int colidx2,
		matrix *result, int rescolidx){

	if (!( MATRIX_TOTALROWS( m1) == MATRIX_TOTALROWS( m2))) {
		printf ("The two matrices in preprocessorColumnsAndAppend do "
				"not have the same row count");
		exit (-1);
	}

	if (!(result != NULL)) {
		printf ("Result matrix cannot be empty");
		exit (-1);
	}

	int i, value;

	for( i = 0; i < MATRIX_TOTALROWS( m1); i++){



		if( getMatrixValue( m1, i, colidx1) == 1 ||
				getMatrixValue( m2, i, colidx2) == 1) {
			value = 1;
		} else {
			value = 0;
		}

		setMatrixValue( result, i, rescolidx, value);

	}

}

int preprocessisNodeCsrc( vertex *n, dxvector *csrc){

	int i, result = 0;

	for( i = 0; i < dxvector_TOTALELMS( csrc); i++){

		if( VERTEX_PRE(n) == ELM_IDX( dxvector_ELMS_POS( csrc, i))){
			result = 1;
			break;
		}

	}

	return result;

}

dxvector *preprocessrearrangeCsrcOnTopo( dxvector *csrc, dxvector *prearr){

	dxvector *result;
	vertex *v;
	int i;
	ELM *e, *currpre, *currcsrc;

	result = (dxvector *) malloc( sizeof( dxvector));
	initdxvector( result);

	for( i = 0; i < dxvector_TOTALELMS( csrc); i++){

		currcsrc = dxvector_ELMS_POS( csrc, i);
		currpre = dxvector_ELMS_POS( prearr, ELM_IDX( currcsrc) - 1);
		v = ((vertex *) (ELM_DATA( currpre)));

		e = (ELM *) malloc( sizeof( ELM));
		ELM_IDX( e) = VERTEX_TOPO( v);
		ELM_DATA( e) = malloc( sizeof( topoinfo));

		TOPOINFO_COLIDX( (topoinfo *) ELM_DATA( e)) = i;
		TOPOINFO_VERTEX( (topoinfo *) ELM_DATA( e)) = v;

		addToArray( result, e);

	}

	sortArray( dxvector_ELMS( result), 0, dxvector_TOTALELMS( result) - 1, 0);

	return result;

}

dxvector *preprocessrearrangeNoncsrcOnTopo( dxvector *noncsrc){

	dxvector *result;
	int i;
	ELM *e1, *e2;
	vertex *vertex;

	result = (dxvector *) malloc( sizeof( dxvector));
	initdxvector( result);

	for( i = 0; i < dxvector_TOTALELMS( noncsrc); i++){

		e1 = dxvector_ELMS_POS( noncsrc, i);
		vertex = POSTINFO_VERTEX( ( postinfo *) ELM_DATA( e1));

		e2 = (ELM *) malloc( sizeof( ELM));
		ELM_IDX( e2) = VERTEX_TOPO( vertex);
		ELM_DATA( e2) = malloc( sizeof( topoinfo));

		TOPOINFO_COLIDX( (topoinfo *) ELM_DATA( e2)) =
				POSTINFO_COLIDX( ( postinfo *) ELM_DATA( e1));
		TOPOINFO_VERTEX( (topoinfo *) ELM_DATA( e2)) = vertex;

		addToArray( result, e2);

	}

	sortArray( dxvector_ELMS( result), 0, dxvector_TOTALELMS( result) - 1, 0);

	return result;

}

matrix *preprocesscomputeMaximalWitness( pcpcinfo *ppi, vertex *top){

	matrix *result;
	matrix *csrcmax, *noncsrcmax;

	dxvector *csrc, *noncsrc;
	matrix *csrcmat, *noncsrcmat;
	int i, j, k, max = -1, idx;
	vertex *vertex_csrc, *vertex_noncsrc;

	csrc = PCPCINFO_CSRC( ppi);
	csrcmat = PCPCINFO_CSRCMAT( ppi);



	csrcmax = makeMatrix ();

	for( i = 0; i < MATRIX_TOTALROWS( csrcmat); i++){

		for( j = 0; j < MATRIX_TOTALROWS( csrcmat); j++){

			for( k = 0; k < MATRIX_TOTALCOLS( csrcmat); k++){

				if( getMatrixValue( csrcmat, i, k) &&
						getMatrixValue(csrcmat, j, k)){
					max = k;
				}

			}

			setMatrixValue( csrcmax, i, j, max);
			max = -1;

		}

	}


	if(PCPCINFO_NONCSRCMAT( ppi) == NULL) {
		for( i = 0; i < MATRIX_TOTALROWS( csrcmax); i++) {

			for( j = 0; j < MATRIX_TOTALCOLS( csrcmax); j++){
				idx = getMatrixValue( csrcmax, i, j);
				if (idx != -1) {
					vertex_csrc = TOPOINFO_VERTEX( ( topoinfo *) ELM_DATA(
									dxvector_ELMS_POS( csrc, idx)));
				} else {
					vertex_csrc = top;
				}
				setMatrixValue (csrcmax, i, j, VERTEX_PRE( vertex_csrc));
			}

		}
		return csrcmax;
	}

	noncsrc = PCPCINFO_NONCSRC( ppi);
	noncsrcmat = PCPCINFO_NONCSRCMAT( ppi);
	noncsrcmax = makeMatrix( );

	//printTopoverts (noncsrc);
	//printMatrix (noncsrcmat);

	max = -1;

	for( i = 0; i < MATRIX_TOTALROWS( noncsrcmat); i++){

		for( j = 0; j < MATRIX_TOTALROWS( noncsrcmat); j++){

			for( k = 0; k < MATRIX_TOTALCOLS( noncsrcmat); k++){

				if( getMatrixValue( noncsrcmat, i, k) &&
						getMatrixValue( noncsrcmat, j, k)){
					max = k;
				}

			}

			setMatrixValue( noncsrcmax, i, j, max);
			max = -1;

		}

	}


	if (!( MATRIX_TOTALROWS( csrcmax) == MATRIX_TOTALROWS( noncsrcmax) &&
			MATRIX_TOTALCOLS( csrcmax) == MATRIX_TOTALCOLS( noncsrcmax))) {
		printf ("Matrix shape mismatch while building PC-PC matrix.");
		exit(-1);
	}

	result = makeMatrix();

	for( i = 0; i < MATRIX_TOTALROWS( csrcmax); i++) {

		for( j = 0; j < MATRIX_TOTALCOLS( csrcmax); j++){

			if (getMatrixValue( csrcmax, i, j) == -1) {
				vertex_csrc = top;
			} else {
				vertex_csrc = TOPOINFO_VERTEX( ( topoinfo *) ELM_DATA(
									dxvector_ELMS_POS( csrc,
											getMatrixValue( csrcmax, i, j))));
			}

			if (getMatrixValue( noncsrcmax, i, j) == -1) {
				vertex_noncsrc = top;
			} else {
				vertex_noncsrc = TOPOINFO_VERTEX( ( topoinfo *) ELM_DATA(
									dxvector_ELMS_POS( noncsrc,
											getMatrixValue( noncsrcmax, i, j))));
			}

			if( VERTEX_TOPO( vertex_csrc) > VERTEX_TOPO( vertex_noncsrc)){
				setMatrixValue( result, i, j, VERTEX_PRE( vertex_csrc));
			} else {
				setMatrixValue( result, i, j, VERTEX_PRE( vertex_noncsrc));
			}

		}

	}

	freeMatrix( csrcmax);
	freeMatrix( noncsrcmax);

	return result;

}

matrix *preprocessrearrangeMatOnTopo( dxvector *topoarr, matrix *mat){

	matrix *result;
	topoinfo *ti;
	int i, j, value;

	result = makeMatrix();

	for( i = 0; i < dxvector_TOTALELMS( topoarr); i++){

		ti = (topoinfo *) ELM_DATA( dxvector_ELMS_POS( topoarr, i));

		for( j = 0; j < MATRIX_TOTALROWS( mat); j++){
			value = getMatrixValue( mat, j, TOPOINFO_COLIDX( ti));
			setMatrixValue( result, j, i, value);
		}

	}

	return result;

}

matrix* preprocesscreatePCPCMat( matrix *reachmat, dxvector *postarr, compinfo *ci){

	vertex *n1 = NULL, *n2, *treelca;
	matrix *result = NULL;
	matrix *currmat = NULL, *mat1, *mat2;
	postinfo *pi1, *pi2, *pi, *pi3;
	dxvector *noncsrc = NULL, *q = makedxvector();
	ELM *e, *e1, *e_min;
	int colidx=0, colidx_pi1, rescol = 0, i;
	pcpcinfo *ppi = NULL;
	vertex *top = NULL;

	for (i=0; i<dxvector_TOTALELMS (postarr); i++) {
		PQinsertELM (dxvector_ELMS_POS(postarr, i), q);
	}

	while( dxvector_TOTALELMS(q) > 0){

		//printdxvector (q);
		e_min = PQgetMinELM( q);
		pi1 = ( postinfo *) ELM_DATA( e_min);
		n1 = POSTINFO_VERTEX( pi1);
		colidx_pi1 = POSTINFO_COLIDX( pi1);
		PQdeleteMin(q);
		//printdxvector (q);

		if( dxvector_TOTALELMS(q) == 0){
			break;
		} else {
			pi2 = ( postinfo *) ELM_DATA( PQgetMinELM( q));
			n2 = POSTINFO_VERTEX( pi2);
			treelca = preprocesstreeLCAfromNodes( n1, n2, ci);
			//printf("TREElca (%d, %d) = %d, post = %d\n",
			//		n1->post, n2->post, treelca->post, treelca->pre);
		}

		if (preprocessisNodeCsrc( treelca, COMPINFO_CSRC( ci))) {
			if ( treelca == n1) {
				PQdeleteMin(q);
				e = (ELM *) malloc( sizeof( ELM));
				ELM_IDX(e) = VERTEX_POST( n1);
				pi = makePostinfo();
				POSTINFO_ISCSRC( pi) = 1;
				POSTINFO_COLIDX( pi) = colidx_pi1;
				POSTINFO_VERTEX( pi) = n1;
				ELM_DATA(e) = pi;
				PQinsertELM (e, q);
				continue;
			}
		}

		if( !preprocessisNodeCsrc( treelca, COMPINFO_CSRC( ci))){


			if( noncsrc == NULL) {

				noncsrc = makedxvector ();
				currmat = makeMatrix ();

			}



			if (!indexExistsInArray( noncsrc, VERTEX_POST( treelca))) {
				e1 = (ELM *) malloc( sizeof( ELM));
				ELM_IDX(e1) = VERTEX_POST( treelca);
				pi3 = makePostinfo();
				POSTINFO_ISCSRC( pi3) = 0;
				POSTINFO_COLIDX( pi3) = colidx++;
				POSTINFO_VERTEX( pi3) = treelca;
				ELM_DATA(e1) = pi3;

				addToArray( noncsrc, e1);
				//printdxvector (noncsrc);
			}

			if( !indexExistsInArray( q, VERTEX_POST( treelca))){
				//printf("inserting: %d\n", treelca->post);
				e = (ELM *) malloc( sizeof( ELM));
				ELM_IDX(e) = VERTEX_POST( treelca);
				pi = makePostinfo();
				POSTINFO_ISCSRC( pi) = 0;
				POSTINFO_COLIDX( pi) = colidx-1;
				POSTINFO_VERTEX( pi) = treelca;
				ELM_DATA(e) = pi;
				PQinsertELM( e, q);

			}

			e = getELMFromArray (noncsrc, VERTEX_POST(treelca));
			rescol = POSTINFO_COLIDX ((postinfo *) ELM_DATA(e));


			if( preprocessisNodeCsrc( n1, COMPINFO_CSRC( ci))){
				mat1 = reachmat;
			} else {
				mat1 = currmat;
			}

			if( preprocessisNodeCsrc( n2, COMPINFO_CSRC( ci))){
				mat2 = reachmat;
			} else {
				mat2 = currmat;
			}

			

			preprocessorColumnsAndUpdate( mat1, colidx_pi1,
					mat2, POSTINFO_COLIDX(pi2),
					currmat, rescol/*colidx - 1*/);

		}

	}

	ppi = (pcpcinfo *) malloc( sizeof( pcpcinfo));

	PCPCINFO_CSRC( ppi) = preprocessrearrangeCsrcOnTopo( COMPINFO_CSRC( ci),
			COMPINFO_PREARR( ci));
	PCPCINFO_CSRCMAT( ppi) = preprocessrearrangeMatOnTopo( PCPCINFO_CSRC( ppi),
			reachmat);

	if( noncsrc != NULL){


		PCPCINFO_NONCSRC( ppi) = preprocessrearrangeNoncsrcOnTopo( noncsrc);
		PCPCINFO_NONCSRCMAT( ppi) = preprocessrearrangeMatOnTopo( PCPCINFO_NONCSRC( ppi),
				currmat);

	}

	top = (vertex *) ELM_DATA (dxvector_ELMS_POS (COMPINFO_PREARR(ci), 0));

	result = preprocesscomputeMaximalWitness( ppi, top);

	return result;

}

void preprocessincorporateCrossEdges( compinfo *ci){

	matrix *reachmat;
	dxvector *postarr;

	if( COMPINFO_CSRC( ci) != NULL){

		reachmat = preprocesscreateReachMat( ci);

		//printMatrix (reachmat);
		COMPINFO_CROSSCLOS (ci) = reachmat;
		postarr = preprocesssortInPostorder( ci);

		preprocessINFO_PCPTMAT( COMPINFO_preprocess( ci)) = preprocesscreatePCPTMat( reachmat, ci);
		preprocessINFO_PCPCMAT( COMPINFO_preprocess( ci)) = preprocesscreatePCPCMat( reachmat,
				postarr,
				ci);

	}

}

#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "dxgraphserv.h"
#include "graphclass.h"
#include "allprint.h"
#include "preprocesstree.h"


void preprocesssetBlockIds( dxvector *eulertour, int blocksize){

	int i, j, prevdepth, currdepth, blockid = 0;

	for( i = 0; i < dxvector_TOTALELMS( eulertour); i = i + blocksize){

		prevdepth = ELM_IDX( dxvector_ELMS_POS( eulertour, i));

		for( j = i + 1 ; j < i + blocksize; j++){

			if( j < dxvector_TOTALELMS( eulertour)){

				currdepth = ELM_IDX( dxvector_ELMS_POS( eulertour, j));


				if( prevdepth > currdepth){
					blockid += pow( 2, ( blocksize - 2 - (j - ( i + 1))));
				}

				prevdepth = currdepth;

			} else {

				blockid *= 2;

			}

		}

		for( j = i; j < i + blocksize; j++){

			if( j < dxvector_TOTALELMS( eulertour)){
				( ( int *)ELM_DATA( dxvector_ELMS_POS( eulertour, j)))[1] = blockid;
			}

		}

		blockid = 0;

	}

}

matrix* preprocesscomputeIntraTable( dxvector *eulertour, int start, int end){

	if (!( start <= end && eulertour != NULL)) {
		printf ("Incompatible arguments passed to preprocesscomputeIntraTable");
		exit(-1);
	}

	int i, j, minvalue, minindex, currdepth;
	matrix *result = makeMatrix ();

	for( i = 0; i <= end - start + 1; i++){


		if( start + i < dxvector_TOTALELMS( eulertour)){
			minvalue = ELM_IDX( dxvector_ELMS_POS( eulertour, start + i));
			minindex = start + i;

			for( j = i; j <= end - start; j++){


				if( start + j < dxvector_TOTALELMS( eulertour)){

					currdepth = ELM_IDX( dxvector_ELMS_POS( eulertour, start + j));

					if( minvalue > currdepth){
						minvalue = currdepth;
						minindex = start + j;
					}

				}

				setMatrixValue( result, i, j, minindex - start);
				setMatrixValue( result, j, i, minindex - start);

			}

		}

	}

	return result;

}

dxvector* preprocesscomputePerBlockMin( dxvector *eulertour, int blocksize){

	if (!( blocksize > 0 && eulertour != NULL)) {
		printf ("Incompatible arguments passed to preprocesscomputePerBlockMin");
		exit(-1);
	}

	dxvector *result;
	int mindepth, currdepth, minindex, i, j;
	ELM *e;

	result =  makedxvector();

	for( i = 0; i < dxvector_TOTALELMS( eulertour); i = i + blocksize){

		mindepth = ELM_IDX( dxvector_ELMS_POS( eulertour, i));
		minindex = i;

		for( j = i + 1 ; j < i + blocksize; j++){

			if( j < dxvector_TOTALELMS( eulertour)){

				currdepth = ELM_IDX( dxvector_ELMS_POS( eulertour, j));

				if( mindepth > currdepth){
					mindepth = currdepth;
					minindex = j;
				}

			}

		}

		e = makeELM ();
		ELM_IDX( e) = mindepth;
		ELM_DATA(e) = malloc( sizeof( int));

		*(int *) ELM_DATA(e) = minindex;

		addToArray( result, e);

	}

	return result;

}

matrix * preprocessprocessBlockMinArray ( dxvector *a){

	if (!( a != NULL && dxvector_TOTALELMS(a) > 0)) {
		printf ("Incompatible arguments passed to preprocessprocessBlockMinArray");
		exit(-1);
	}

	int i, j, halfstep, fullstep, totalELMs;
	matrix *m = makeMatrix ();

	totalELMs = dxvector_TOTALELMS(a);

	if( totalELMs == 1 ){
		setMatrixValue( m, 0, 0, 0);
		return m;
	}

	for( j = 0; j < ceil( log2( totalELMs)); j++){

		setMatrixValue( m, totalELMs - 1, j, totalELMs - 1);

	}

	for( j = 0; j < ceil( log2( totalELMs)); j++){

		for( i = 0; i < totalELMs - 1; i++){

			if( j == 0){

				if( ELM_IDX( dxvector_ELMS_POS( a, i)) <
						ELM_IDX( dxvector_ELMS_POS( a, i + 1))){

					setMatrixValue( m, i, 0, i);

				} else {

					setMatrixValue( m, i, 0, i + 1);

				}

			} else {

				halfstep = getMatrixValue( m, i, j - 1);

				if( i + (int)pow( 2, j - 1) < totalELMs){

					fullstep = getMatrixValue( m, i + (int)pow( 2, j - 1), j - 1);

				} else {

					fullstep = getMatrixValue( m, totalELMs - 1, j - 1);
				}

				if( ELM_IDX( dxvector_ELMS_POS( a, halfstep)) <
						ELM_IDX( dxvector_ELMS_POS( a, fullstep))) {

					setMatrixValue( m, i, j, halfstep);

				} else {

					setMatrixValue( m, i, j, fullstep);

				}

			}

		}

	}

	return m;

}

int preprocessgetBlockId( dxvector *eulertour, int index){

	ELM *e = dxvector_ELMS_POS( eulertour, index);

	return ((( int *)ELM_DATA(e))[1]);

}

preprocessinfo * preprocesscreatePartitions( dxvector *eulertour){

	int i, j, totalELMs, blocksize, oldsize, index;
	preprocessinfo *preprocess = makepreprocessinfo();

	totalELMs = dxvector_TOTALELMS( eulertour);


	if( totalELMs == 1){
		blocksize = 1;
	} else {
		blocksize = log2( totalELMs) / 2.0;
	}

	preprocessINFO_BLOCKSIZE( preprocess) = blocksize;


	preprocesssetBlockIds( eulertour, blocksize);

	for( i = 0; i < totalELMs; i += blocksize) {

		oldsize = preprocessINFO_NUMINTRA( preprocess);

		index = preprocessgetBlockId( eulertour, i);


		if( index > oldsize - 1){

			void *_temp = realloc( preprocessINFO_INTRAMATS( preprocess),
					( (index + 1) * sizeof( matrix *))/*,
					( oldsize * sizeof( matrix *))*/);
			if ( !_temp){
				printf ( "preprocesscreatePartitions couldn't realloc memory!\n");
				exit(-1);
			}
			//TODO:check suspected shallow free

			/*freeMatrix( preprocessINFO_INTRAMATS( preprocess));*/
			preprocessINFO_INTRAMATS( preprocess) = ( matrix **)_temp;
			preprocessINFO_NUMINTRA( preprocess) = index + 1;

			for( j = oldsize /*- 1*/; j < preprocessINFO_NUMINTRA( preprocess); j++){
				preprocessINFO_INTRAMATS_POS( preprocess, j) = NULL;
			}

		}

		if( preprocessINFO_INTRAMATS_POS( preprocess, index) == NULL){
			preprocessINFO_INTRAMATS_POS( preprocess, index) =
					preprocesscomputeIntraTable( eulertour, i, i + blocksize - 1);
		}

	}

	preprocessINFO_BLOCKMIN( preprocess) = preprocesscomputePerBlockMin( eulertour, blocksize);

	preprocessINFO_INTERMAT( preprocess) =
			preprocessprocessBlockMinArray( preprocessINFO_BLOCKMIN( preprocess));

	return preprocess;

}

int preprocessgetLowestFromCandidates( dxvector *d, int indices[4]){

	int i, result;
	int mindepth;

	mindepth = ELM_IDX( dxvector_ELMS_POS( d, indices[0]));
	result = *( int *) ELM_DATA( dxvector_ELMS_POS( d, indices[0]));

	for( i = 1; i < 4; i++){
		if( mindepth >= ELM_IDX( dxvector_ELMS_POS( d, indices[i] ))){
			mindepth = ELM_IDX( dxvector_ELMS_POS( d, indices[i] ));
			result = *( int *) ELM_DATA( dxvector_ELMS_POS( d, indices[i]));
		}
	}

	return result;

}

vertex *preprocesstreeLCAfromNodes( vertex *n1, vertex *n2, compinfo *ci){

	if (!( n1 != NULL && n2 != NULL && ci != NULL)) {
		printf ("Incompatible arguments passed to preprocesstreeLCAfromNodes");
		exit(-1);
	}

	vertex *result;
	int lblockid, lmatrow, lmatcol;
	int ublockid, umatrow, umatcol;
	int lowerid, upperid;
	int blocksize;
	int etindices[4] = {0, 0, 0, 0};
	int base, jump;
	int indexlower, indexupper;
	int resultidx;
	matrix *intermat;
	matrix **intramats;
	dxvector *blockmin;
	ELM *e;

	preprocessinfo *preprocess = COMPINFO_preprocess( ci);

	if (!(preprocess != NULL)){
		printf ("The type component graph lacks LCA info");
		exit(-1);
	}

	intramats = preprocessINFO_INTRAMATS( preprocess);

	if (!(intramats != NULL)) {
		printf ("No intra matrices found");
		exit(-1);
	}

	blocksize = preprocessINFO_BLOCKSIZE( preprocess);

	if ( !(blocksize > 0)) {
		printf ("Blocksize should be a positive integer");
		exit(-1);
	}

	if( VERTEX_EULERID( n1) < VERTEX_EULERID( n2)){
		lowerid = VERTEX_EULERID( n1);
		upperid = VERTEX_EULERID( n2);
	} else {
		lowerid = VERTEX_EULERID( n2);
		upperid = VERTEX_EULERID( n1);
	}


	lblockid = preprocessgetBlockId( COMPINFO_EULERTOUR( ci), lowerid);
	ublockid = preprocessgetBlockId( COMPINFO_EULERTOUR( ci), upperid);


	if( upperid/blocksize == lowerid/blocksize){
		lmatrow = lowerid % blocksize;
		lmatcol = upperid % blocksize;

		indexlower = ( lowerid/blocksize) * blocksize +
				getMatrixValue( intramats[lblockid], lmatrow, lmatcol);

		e = dxvector_ELMS_POS( COMPINFO_EULERTOUR( ci), indexlower);

		etindices[0] = indexlower;
		etindices[1] = indexlower;
		etindices[2] = indexlower;
		etindices[3] = indexlower;

	} else {
		/*
		 * The two vertices do not belong to the same intra-block
		 */

		lmatrow = lowerid % blocksize;
		lmatcol = blocksize - 1;

		indexlower = ( lowerid/blocksize) * blocksize +
				getMatrixValue( intramats[lblockid], lmatrow, lmatcol);

		etindices[0] = indexlower;

		umatrow = 0;
		umatcol = upperid % blocksize;

		indexupper = ( upperid/blocksize) * blocksize +
				getMatrixValue( intramats[ublockid], umatrow, umatcol);

		etindices[3] = indexupper;


		intermat = preprocessINFO_INTERMAT( preprocess);
		if (!( intermat != NULL)) {
			printf ("No inter-block query matrix found");
			exit (-1);
		}

		blockmin = preprocessINFO_BLOCKMIN( preprocess);
		if (! (blockmin != NULL)) {
			printf ("No block minimum array found");
			exit (-1);
		}

		if( (upperid/blocksize) > (lowerid/blocksize + 1)){

			jump = ceil( log2( upperid/blocksize - lowerid/blocksize - 1)) - 1;

			if(jump>=0) {
			base = lowerid/blocksize + 1;
			e =  dxvector_ELMS_POS( blockmin,
					getMatrixValue( intermat, base, jump));
			etindices[1] = *(int *) ELM_DATA(e);


			base = upperid/blocksize - 1 - pow( 2, jump);
			e =  dxvector_ELMS_POS( blockmin,
					getMatrixValue( intermat, base, jump));
			etindices[2] = *(int *) ELM_DATA(e);

			} else {
				e =  dxvector_ELMS_POS( blockmin, lowerid/blocksize+1);
				etindices[1] = *(int *) ELM_DATA(e);
				etindices[2] = etindices[1];
			}

		} else {


			etindices[1] = etindices[0] + 1;
			etindices[2] = etindices[3] - 1;

		}

	}




	resultidx = preprocessgetLowestFromCandidates( COMPINFO_EULERTOUR(ci),
			etindices);


	e = dxvector_ELMS_POS( COMPINFO_PREARR(ci), resultidx - 1);

	result = (vertex *) ELM_DATA(e);

	return result;

}

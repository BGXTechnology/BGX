#include "dxgraphserv.h"
#include "dxatom.h"
#include "dxvector.h"

void initdxvector(dxvector *arrayd){

	dxvector_ELMS( arrayd) = NULL;
	dxvector_TOTALELMS( arrayd) = 0;
	dxvector_ALLOCELMS( arrayd) = 0;

}

dxvector * makedxvector (){
	dxvector *d = (dxvector *) (malloc(sizeof(dxvector)));
	initdxvector(d);
	return d;
}

void freeELMArray( ELM **e, int count){

	int i;

	if(e != NULL){

		for( i = 0; i < count; i++){

			if( e[i] != NULL) {
				freeELM( e[i]);
				e[i] = NULL;
			}

		}

		free(e);
		e=NULL;

	}

}

void freedxvector( dxvector *arrayd){

	if( arrayd != NULL){

		int i;

		for( i = 0; i < dxvector_ALLOCELMS( arrayd); i++){

			if( dxvector_ELMS( arrayd)[i] != NULL){

				freeELM( dxvector_ELMS( arrayd)[i]);
				dxvector_ELMS( arrayd)[i] = NULL;

			}

		}

		free(arrayd);
		arrayd=NULL;

	}

}


void freedxvectorShallow( dxvector *arrayd){

	if( arrayd != NULL){

		int i;

		for( i = 0; i < dxvector_ALLOCELMS( arrayd); i++){

			if( dxvector_ELMS( arrayd)[i] != NULL){

	
	
				dxvector_ELMS( arrayd)[i] = NULL;

			}

		}

		free(arrayd);
		arrayd=NULL;

	}

}

int addToArray( dxvector *arrayd, ELM *item){

	int pos, oldsize;

	if(dxvector_TOTALELMS( arrayd) == dxvector_ALLOCELMS( arrayd)){

		oldsize = dxvector_ALLOCELMS( arrayd);
		dxvector_ALLOCELMS( arrayd) += 3;

		void *_temp = realloc( dxvector_ELMS( arrayd),
				(dxvector_ALLOCELMS( arrayd) * sizeof( ELM *))/*,
				oldsize * sizeof( ELM *)*/);

		if ( !_temp){
			printf ( "addToArray couldn't realloc memory!\n");
			exit (-1);
		}

		
		dxvector_ELMS( arrayd) = ( ELM**)_temp;

	}

	pos = dxvector_TOTALELMS( arrayd);
	dxvector_TOTALELMS( arrayd)++;
	dxvector_ELMS_POS( arrayd, pos) = item;

	return dxvector_TOTALELMS( arrayd);

}

int indexExistsInArray( dxvector *arrayd, int idx){

	int i;

	for( i = 0; i < dxvector_TOTALELMS( arrayd); i++){

		if( idx == ELM_IDX( dxvector_ELMS_POS( arrayd,i))){
			return 1;
		}

	}

	return 0;

}

ELM* getELMFromArray( dxvector *arrayd, int idx){

	int i;

	for( i = 0; i < dxvector_TOTALELMS( arrayd); i++){

		if( idx == ELM_IDX( dxvector_ELMS_POS( arrayd,i))){
			return dxvector_ELMS_POS( arrayd,i);
		}

	}

	return NULL;

}

int getPositionInArray( dxvector *arrayd, int idx){

	int i;

	for( i = 0; i < dxvector_TOTALELMS( arrayd); i++){

		if( idx == ELM_IDX( dxvector_ELMS_POS( arrayd,i))){
			return i;
		}

	}

	return -1;

}

int addToArrayAtPos( dxvector *arrayd, ELM *item, int pos){

	int i, oldsize = dxvector_TOTALELMS( arrayd);

	if( pos >= dxvector_ALLOCELMS( arrayd)){

		//int oldsize = dxvector_ALLOCELMS( arrayd);
		dxvector_ALLOCELMS( arrayd) = pos + 1;

		void *_temp = realloc( dxvector_ELMS( arrayd),
				( dxvector_ALLOCELMS( arrayd) * sizeof( ELM *))/*,
				oldsize * sizeof( ELM *)*/);

		if ( !_temp){
			printf ( "addToArrayAtPos couldn't realloc memory!\n");
			exit(-1);
		}

		/*free( dxvector_ELMS( arrayd));*/
		dxvector_ELMS( arrayd) = ( ELM**)_temp;

	}

	for(i=oldsize; i<dxvector_ALLOCELMS( arrayd); i++){
		dxvector_ELMS_POS( arrayd, i) = NULL;
	}

	dxvector_TOTALELMS( arrayd) = dxvector_ALLOCELMS( arrayd);
	dxvector_ELMS_POS( arrayd, pos) = item;

	return dxvector_TOTALELMS( arrayd);

}

void merge( ELM **ELMs, int lower, int upper, int desc){

	ELM **left, **right, **result;
	int mid = (lower + upper)/2;
	int ll, lr, i, total = 0;
	int cond;

	ll = mid-lower + 1;
	lr = upper - mid;
	left = ELMs + lower;
	right = ELMs + mid + 1;
	result = (ELM **) malloc(( ll + lr) * sizeof( ELM *));

	while( ll > 0 && lr > 0){

		if( ELM_IDX( left[0]) <= ELM_IDX( right[0])){

			if( desc) cond = 0; else cond = 1;

		} else{

			if(desc) cond=1; else cond=0;

		}

		if(cond){

			result[total++]=*left; left++; ll--;

		} else {

			result[total++]=*right; right++; lr--;

		}

	}

	if(ll>0){

		while(ll>0){
			result[total++]=*left; left++; ll--;
		}

	} else{

		while(lr>0){
			result[total++]=*right; right++; lr--;
		}

	}

	ll = mid - lower + 1;
	lr = upper - mid;
	left = ELMs + lower;
	right = ELMs + mid + 1;

	for( i = 0; i < ll; i++){
		left[i] = result[i];
	}

	for(i = 0; i < lr; i++){
		right[i] = result[i+ll];
	}

	free( result);

}

void sortArray( ELM **ELMs, int lower, int upper, int desc){

	if( ELMs == NULL){
		printf ( "Typechecker trying to sort dxvector with null ELMents");
		exit (-1);
	}

	if( upper - lower > 0){

		int mid = (upper + lower)/2;
		sortArray( ELMs, lower, mid, desc);
		sortArray( ELMs, mid + 1, upper, desc);
		merge( ELMs, lower, upper, desc);

	}

}



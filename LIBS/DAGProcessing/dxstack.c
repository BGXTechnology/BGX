#include "graphclass.h"
#include "dxatom.h"
#include "dxstack.h"

int isdxstackEmpty( dxstack *s){

	int result = 0;

	if( dxstack_CURR(s) == NULL){
		result = 1;
	}

	return result;

}

void initdxstack( dxstack *s){

	dxstack_CURR(s) = NULL;
	dxstack_NEXT(s) = NULL;

}

dxstack *makedxstack (){
	dxstack *estack = (dxstack *) malloc ( sizeof (dxstack));
	initdxstack (estack);
	return estack;
}

void pushdxstack( dxstack **s, ELM *e){

	dxstack *top = (dxstack *) malloc( sizeof( dxstack));
	dxstack_CURR( top) = e;
	dxstack_NEXT( top) = *s;
	*s = top;

}

ELM* popdxstack( dxstack **s){

	dxstack *top = NULL;
	ELM *e;

	if( *s == NULL){
		printf ( "Trying to pop from empty dxstack\n");
		exit(-1);
	}
	else{
		top = *s;
		*s = dxstack_NEXT( top);
	}

	e = dxstack_CURR( top);
	free( top);

	return e;

}

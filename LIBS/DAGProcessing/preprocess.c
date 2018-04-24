#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "graphclass.h"
#include "throuhierarch.h"
#include "allprint.h"
#include "preprocesstree.h"
#include "preprocesscross.h"
#include "dxheap.h"
#include <time.h>


typedef struct INFO {
	dxvector *euler;
}info;


#define INFO_EULER(n) n->euler


static info *MakeInfo( void)
{
	info *result;

	result = (info *) malloc( sizeof( info));
	INFO_EULER( result) = NULL;
	return result;

}

static info *FreeInfo( info *info)
{

	free( info);
	info = NULL;

	return info;
}



void PLBtravVertex( vertex *v, info *arg_info)
{

	edges *children;
	ELM *e;

	children = VERTEX_CHILDREN (v);

	if( INFO_EULER( arg_info) == NULL){
		INFO_EULER( arg_info) = makedxvector ();
	}

	e = makeELM ();
	ELM_IDX(e) = VERTEX_DEPTH( v);



	ELM_DATA(e) = malloc( 2 * sizeof(int));
	((int *) ELM_DATA(e))[0] = VERTEX_PRE (v);
	((int *) ELM_DATA(e))[1] = 0;

	addToArray( INFO_EULER( arg_info), e);

	VERTEX_EULERID (v) = dxvector_TOTALELMS( INFO_EULER( arg_info)) - 1;

	while( children != NULL){

		if( EDGES_EDGETYPE( children) == edgetree){

			PLBtravVertex( EDGES_TARGET( children), arg_info);


			e = makeELM ();
			ELM_IDX(e) = VERTEX_DEPTH(v);

			ELM_DATA(e) = malloc( 2 * sizeof(int));
			((int*) ELM_DATA(e))[0] = VERTEX_PRE (v);
			((int*) ELM_DATA(e))[1] = 0;

			addToArray( INFO_EULER( arg_info), e);

		}

		children = EDGES_NEXT(children);

	}

}


void PLBdopreprocessPreprocessing(dag *g)
{
	info *arg_info;
	arg_info = MakeInfo();
	compinfo *ci;

	/*
	 * First label nodes for tree reachability
	 */

	INFO_EULER( arg_info) = NULL;

	PLBtravVertex ( DAG_TOP( g ), arg_info);

	ci = DAG_INFO (g);

	COMPINFO_EULERTOUR(ci) = INFO_EULER( arg_info);
	COMPINFO_preprocess( ci) = preprocesscreatePartitions( COMPINFO_EULERTOUR( ci));

	preprocessincorporateCrossEdges( ci);




	arg_info = FreeInfo(arg_info);

}


void randNumGen( int max, int* testpre){

	testpre[0] = rand() % (max);
	testpre[1] = rand() % (max);

}

void testPriorityQueue( void) {

	int i, j, random, totalELMents;
	dxvector *q;

	srand(time(NULL));

	for( j = 0; j < 10; j++){

		q = makedxvector ();

		for( i = 0; i < 10; i++){
			random = rand() % 10 + 1;
			PQinsert( random, q);
		}

		PQprint(q);

		totalELMents = dxvector_TOTALELMS(q);

		for( i = 0; i < totalELMents; i++){
			printf( "%d,", PQgetMin(q));
			PQdeleteMin(q);
		}

		printf("\n-----------\n");

		freedxvector(q);
		q = NULL;

	}

}

void testpreprocesstree( dag *g){

	dxvector *prearr;
	int j, nodecount;
	int testpre[2];
	vertex *n1, *n2, *result;

	unsigned int iseed = (unsigned int)time(NULL);
	srand (iseed);

	prearr = COMPINFO_PREARR( DAG_INFO( g));
	nodecount = dxvector_TOTALELMS( prearr);
	printDepthAndPre (COMPINFO_EULERTOUR( DAG_INFO( g)));
	printpreprocessInfo( DAG_INFO(g));

	for( j = 0; j < nodecount; j++){
		randNumGen( nodecount, testpre);
		n1 = (vertex *) ELM_DATA( dxvector_ELMS_POS( prearr, testpre[0]));
		n2 = (vertex *) ELM_DATA( dxvector_ELMS_POS( prearr, testpre[1]));
		printf("preprocess(%d,%d) = ", VERTEX_PRE( n1), VERTEX_PRE( n2));
		result = preprocesstreeLCAfromNodes( n1, n2, DAG_INFO( g));
		printf("Result = %d \n", VERTEX_PRE( result));
		fflush(stdout);

	}

}

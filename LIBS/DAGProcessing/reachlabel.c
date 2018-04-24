#include "graphclass.h"
#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "reachlabel.h"

typedef struct INFO {
	int collabel;
	int totalcols;
	int preprocesscol;
	dxvector *csrc;
	dxvector *ctar;
	dxvector *prearr;
	dxstack **estack;
} info;

/*
 * INFO macros
 */
#define INFO_COLLABEL(n) n->collabel
#define INFO_TOTALCOLS(n) n->totalcols
#define INFO_preprocessCOL(n) n->preprocesscol
#define INFO_CSRC(n) n->csrc
#define INFO_CTAR(n) n->ctar
#define INFO_PREARR(n) n->prearr
#define INFO_ESTACK(n) n->estack


/*
 * INFO functions
 */

static info *MakeInfo( void)
{
	info *result;
	result = (info *) malloc( sizeof( info));
	INFO_COLLABEL( result) = 0;
	INFO_TOTALCOLS( result) = 0;
	INFO_preprocessCOL( result) = 0;
	INFO_CSRC( result) = NULL;
	INFO_CTAR( result) = NULL;
	INFO_PREARR (result) = NULL;
	INFO_ESTACK( result) = NULL;
	return result;
}

static info *FreeInfo( info *info)

{
	free( info);
	info = NULL;

	return info;
}


void RCHtravVertex( vertex *v, info *arg_info)
{

	edges *children, *parents;


	parents = VERTEX_PARENTS(v);

	int pop = 0, i, xpre = 0, idx;


	while( parents != NULL){

		if( EDGES_EDGETYPE( parents) == edgecross){

			ELM *e = (ELM *) malloc( sizeof( ELM));
			ELM_DATA(e) = NULL;


			for( i = 0; i < dxvector_TOTALELMS( INFO_CTAR( arg_info)); i++){

				if( VERTEX_PRE(v) ==
						ELM_IDX( dxvector_ELMS( INFO_CTAR( arg_info))[i])){
					ELM_IDX(e) = i+1;
				}

			}

			pushdxstack( INFO_ESTACK( arg_info), e);
			pop = 1;
			break;

		}

		parents = EDGES_NEXT(parents);

	}


	if( INFO_COLLABEL( arg_info) < INFO_TOTALCOLS( arg_info)){
		idx = INFO_COLLABEL(arg_info);
	}
	else {
		idx = INFO_TOTALCOLS( arg_info) - 1;
	}
	xpre = ELM_IDX( dxvector_ELMS( INFO_CSRC( arg_info))[idx]);

	if (VERTEX_PRE(v) == xpre+1){
		VERTEX_REACHCOLA(v) = ++INFO_COLLABEL( arg_info);
	} else {
		VERTEX_REACHCOLA(v) = INFO_COLLABEL( arg_info);
	}

	VERTEX_ISRCHCOLAMARKED(v) = 1;

	VERTEX_preprocessCOL (v) = VERTEX_REACHCOLA(v);

	if (VERTEX_REACHCOLA(v) >= INFO_TOTALCOLS( arg_info)){
		VERTEX_REACHCOLA(v) = -1;
	}


	children = VERTEX_CHILDREN(v);

	while( children != NULL){

		if( EDGES_EDGETYPE( children) == edgetree){
			RCHtravVertex( EDGES_TARGET( children), arg_info);
		}

		children = EDGES_NEXT( children);
	}



	if( *(INFO_ESTACK( arg_info)) != NULL){

		if( dxstack_CURR( *(INFO_ESTACK(arg_info))) != NULL){
			VERTEX_ROW (v) = ELM_IDX( dxstack_CURR(*(INFO_ESTACK( arg_info))));
			VERTEX_ISROWMARKED (v) = 1;
		}

	}


	if( pop == 1){
		freeELM (popdxstack (INFO_ESTACK( arg_info)));
	}

}



/** <!--********************************************************************-->
 *
 * @fn node *TFPPGdoPreprocessTFGraph( node *syntax_tree)
 *
 *   @brief  Inits the traversal for this phase
 *
 *   @param syntax_tree
 *   @return
 *
 *****************************************************************************/

void RCHdoReachabilityAnalysis( dag *g)
{
	info *arg_info;
	arg_info = MakeInfo();

	compinfo *ci;

	vertex *v_premax;
	ELM *e;
	int premax,i;
	vertex *v;

	ci = DAG_INFO(g);

	if( ci != NULL && COMPINFO_TLTABLE( ci) != NULL){

		INFO_TOTALCOLS(arg_info) = dxvector_TOTALELMS( COMPINFO_CSRC( ci));
		INFO_CSRC(arg_info) = COMPINFO_CSRC( ci );
		INFO_CTAR(arg_info) = COMPINFO_CTAR( ci );
		INFO_ESTACK( arg_info) = (dxstack **) malloc( sizeof( dxstack *));
		*INFO_ESTACK( arg_info) = NULL;
		INFO_COLLABEL( arg_info) = 0;
		INFO_PREARR (arg_info) = COMPINFO_PREARR (ci);

		RCHtravVertex( DAG_TOP (g), arg_info);

		for (i=0;i<dxvector_TOTALELMS(INFO_PREARR(arg_info)); i++){
			v = (vertex *) ELM_DATA(dxvector_ELMS_POS(INFO_PREARR(arg_info),i));
			premax = VERTEX_PREMAX (v);
			if (premax > dxvector_TOTALELMS(INFO_PREARR (arg_info))) {
				//premax --;
				VERTEX_REACHCOLB (v) = -1;
			} else {
				e = dxvector_ELMS_POS
						(INFO_PREARR(arg_info), (premax - 1));
				v_premax = (vertex *) ELM_DATA (e);
				VERTEX_REACHCOLB (v) = VERTEX_REACHCOLA (v_premax);
			}
			VERTEX_ISRCHCOLBMARKED (v) = 1;


		}



	}

	arg_info = FreeInfo(arg_info);
}

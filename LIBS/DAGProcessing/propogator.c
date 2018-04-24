#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "graphclass.h"
#include "propogator.h"
#include "reachhelper.h"

typedef struct INFO {
	dxvector *tltable;
	dxvector *arrX;
	dxvector *arrY;
}info;

#define INFO_TLTABLE(n) n->tltable

static info *MakeInfo( void)
{
	info *result;

	result = (info *) malloc( sizeof( info));
	INFO_TLTABLE(result) = NULL;

	return result;

}

static info *FreeInfo( info *info)
{

	free (info);
	info = NULL;

	return info;
}


void CTRtravVertex( vertex *v, info *arg_info)
{

	edges *children, *parents;
	int pre_parent, pre_child, post_parent, post_child, premax_child;

	children = VERTEX_CHILDREN (v);
	pre_parent = VERTEX_PRE (v);
	post_parent = VERTEX_POST (v);

	while( children != NULL){

		if( !EDGES_WASCLASSIFIED( children)){

			/* Tree edges have already been classified during depth first walk of the
			 * graph.
			 */

			pre_child = VERTEX_PRE( EDGES_TARGET( children));
			premax_child = VERTEX_PREMAX( EDGES_TARGET( children));
			post_child = VERTEX_POST( EDGES_TARGET( children));

			if( pre_parent < pre_child && post_child < post_parent){


				printf ( "Forward edge found in subtyping hierarchy between %s and %s\n",
						VERTEX_LABEL(v), VERTEX_LABEL(EDGES_TARGET(children)));
				exit(-1);

			} else if( pre_child < pre_parent && post_parent < post_child){


				printf ( "Back edge found in subtyping hierarchy\n");
				exit(-1);

			} else if(pre_child < pre_parent && post_child < post_parent){


				EDGES_EDGETYPE( children) = edgecross;


				parents = VERTEX_PARENTS( EDGES_TARGET( children));

				while( parents != NULL){
					if( EDGES_TARGET( parents) == v){
						EDGES_EDGETYPE( parents) = edgecross;
					}
					parents = EDGES_NEXT( parents);
				}


				if( INFO_TLTABLE( arg_info) == NULL){
					INFO_TLTABLE( arg_info) = makedxvector ();
				}


				ELM *e = (ELM *) malloc( sizeof( ELM));
				ELM_DATA(e) = malloc( 2*sizeof( int));
				ELM_IDX(e) = pre_parent;
				*((int *)ELM_DATA(e)) = pre_child;
				*((int *)ELM_DATA(e) + 1) = premax_child;

				addToArray( INFO_TLTABLE( arg_info),e);

			} else {

				printf ("Unclassifiable edge found in subtyping hierarchy\n");
				exit(-1);

			}

			EDGES_WASCLASSIFIED( children) = 1;

		} else {

			CTRtravVertex (EDGES_TARGET (children), arg_info);

		}

		children = EDGES_NEXT(children);

	}

}


void CTRdoCrossClosure (dag *g)
{
	info *arg_info = MakeInfo();

	compinfo *ci;

	CTRtravVertex( DAG_TOP (g), arg_info);


	if( INFO_TLTABLE (arg_info) != NULL){
		ci = DAG_INFO (g);
		setSrcTarArrays( INFO_TLTABLE(arg_info),
				&( COMPINFO_CSRC( ci)),
				&( COMPINFO_CTAR( ci)));



		if( dxvector_TOTALELMS( INFO_TLTABLE( arg_info)) > 0){
			COMPINFO_TLTABLE(ci) =
					buildTransitiveLinkTable( INFO_TLTABLE( arg_info),
					COMPINFO_CSRC( ci), COMPINFO_CTAR( ci), COMPINFO_PREARR(ci));

			COMPINFO_TLC( ci)= computeTLCMatrix( COMPINFO_TLTABLE( ci),
					COMPINFO_CSRC( ci),
					COMPINFO_CTAR( ci));
		}


	}

	arg_info = FreeInfo(arg_info);

}



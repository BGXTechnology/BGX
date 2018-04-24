#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "graphclass.h"
#include "dxgraphserv.h"

bool GUvertInList( vertex *n, vertices *nl) {

	while( nl!=NULL){

		if( VERTICES_CURR(nl) == n){
			return 1;
		}

		nl = VERTICES_NEXT(nl);

	}

	return 0;

}

vertices* GUmergeLists( vertices *nla, vertices *nlb){

	vertices *nlx, *itr_nlx = NULL, *itr_nla, *itr_nlb;

	itr_nla = nla;
	itr_nlb = nlb;

	nlx = NULL;

	while( itr_nla != NULL){


		if( !GUvertInList( VERTICES_CURR( nla), nlb)) {

			if( nlx == NULL){

				nlx = (vertices *) malloc( sizeof( vertices));
				itr_nlx = nlx;

			} else {

				VERTICES_NEXT( itr_nlx) =  (vertices *) malloc( sizeof( vertices));
				itr_nlx = VERTICES_NEXT( itr_nlx);

			}

			VERTICES_CURR( itr_nlx) = VERTICES_CURR( itr_nla);
			VERTICES_NEXT( itr_nlx) = NULL;

		}

		itr_nla = VERTICES_NEXT( itr_nla);

	}

	/*
	 * Now add the nodes in nlb to nlx
	 */

	itr_nlb = nlb;

	while( itr_nlb !=NULL){

		if( nlx == NULL){

			nlx = (vertices *) malloc( sizeof( vertices));
			itr_nlx = nlx;

		} else {

			VERTICES_NEXT( itr_nlx) = (vertices *) malloc( sizeof( vertices));
			itr_nlx = VERTICES_NEXT( itr_nlx);

		}

		VERTICES_CURR( itr_nlx) = VERTICES_CURR( itr_nlb);
		VERTICES_NEXT( itr_nlx) = NULL;

		itr_nlb = VERTICES_NEXT( itr_nlb);

	}

	return nlx;

}

void GUremoveEdge( vertex *src, vertex *tar){

	edges *prev_itr, *curr_itr;


	prev_itr = NULL;

	curr_itr = VERTEX_CHILDREN( src);

	while( curr_itr != NULL){

		if( EDGES_TARGET(curr_itr) == tar){

			if( prev_itr == NULL){

				VERTEX_CHILDREN( src) = freeCurrentEdge ( VERTEX_CHILDREN( src));
				curr_itr = VERTEX_CHILDREN( src);

			} else {

				EDGES_NEXT( prev_itr) = freeCurrentEdge ( curr_itr);
				curr_itr = EDGES_NEXT( prev_itr);

			}

			continue;

		}

		prev_itr = curr_itr;
		curr_itr = EDGES_NEXT( curr_itr);

	}

	prev_itr = NULL;

	curr_itr = VERTEX_PARENTS( tar);

	while( curr_itr != NULL){

		if( EDGES_TARGET(curr_itr) == src){

			if( prev_itr == NULL){

				/*
				 * The first node in the list is a match.
				 */

				VERTEX_PARENTS( tar) = freeCurrentEdge ( VERTEX_PARENTS( tar));
				curr_itr = VERTEX_PARENTS( src);

			} else {

				EDGES_NEXT( prev_itr) = freeCurrentEdge ( curr_itr);
				curr_itr = EDGES_NEXT( prev_itr);

			}

			continue;

		}

		prev_itr = curr_itr;
		curr_itr = EDGES_NEXT( curr_itr);

	}

}


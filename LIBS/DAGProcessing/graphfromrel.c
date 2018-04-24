#include "graphclass.h"
#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "graphfromrel.h"
#include "dxgraphserv.h"

void MINtravVertex( vertex *v){

  edges *parents_itr1, *parents_itr2, *edge;
  vertices *nl_next;
  int children_visited, total_children, inlist = 0;

  parents_itr1 = VERTEX_PARENTS(v);

  while( parents_itr1 != NULL){

    parents_itr2 = VERTEX_PARENTS(v);

    while( parents_itr2 != NULL) {

      inlist = GUvertInList( EDGES_TARGET( parents_itr1),
	  		VERTEX_ANCESTORS( EDGES_TARGET( parents_itr2)));

      if( inlist) break;

      parents_itr2 = EDGES_NEXT(parents_itr2);

    }

    if( inlist){

      printf ( "Removing superfluous edge between %s and %s.\n",
		       	VERTEX_LABEL( EDGES_TARGET( parents_itr1)),
	  		VERTEX_LABEL(v));
      edge = parents_itr1;
      parents_itr1 = EDGES_NEXT( parents_itr1);
      GUremoveEdge( EDGES_TARGET( edge), v);

    } else {

      parents_itr1 = EDGES_NEXT( parents_itr1);

    }

  }


  parents_itr1 = VERTEX_PARENTS(v);

  while( parents_itr1 != NULL){

    children_visited = VERTEX_graphfromrelCHILDVISITS( EDGES_TARGET( parents_itr1))++;
    total_children = VERTEX_NUMCHILDREN( EDGES_TARGET( parents_itr1));


    VERTEX_ANCESTORS(v) = GUmergeLists( VERTEX_ANCESTORS(v),
			VERTEX_ANCESTORS( EDGES_TARGET( parents_itr1)));

    nl_next = VERTEX_ANCESTORS(v);
    VERTEX_ANCESTORS (v) = (vertices *) malloc( sizeof(vertices));
    VERTICES_CURR (VERTEX_ANCESTORS (v)) = EDGES_TARGET( parents_itr1);
    VERTICES_NEXT (VERTEX_ANCESTORS (v)) = nl_next;


    if( children_visited == total_children) {
		freevertices( VERTEX_ANCESTORS( EDGES_TARGET( parents_itr1)));
		VERTEX_ANCESTORS( EDGES_TARGET( parents_itr1)) = NULL;
    }

    parents_itr1 = EDGES_NEXT( parents_itr1);

  }

}


void MINdoReduceGraph(dag *g)
{

  vertices *nl;
  compinfo *ci = DAG_INFO(g);

  if( ci != NULL) {
    nl = COMPINFO_TOPOLIST( ci);
    while(nl!=NULL){
      MINtravVertex (VERTICES_CURR(nl));
      nl = VERTICES_NEXT(nl);
    }
  }

}



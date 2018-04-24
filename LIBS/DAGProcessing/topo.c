#include "throuhierarch.h"
#include "dxgraphserv.h"
#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "graphclass.h"
#include "topo.h"

typedef struct INFO {
  int topo;
  vertices *head;
  vertices *list;
}info;

#define INFO_TOPO(n) n->topo
#define INFO_HEAD(n) n->head
#define INFO_LIST(n) n->list

static info *MakeInfo( void)
{
  info *result;
  result = (info *) malloc( sizeof( info));
  INFO_TOPO(result) = 1;
  INFO_HEAD(result) = NULL;
  INFO_LIST(result) = NULL;
  return result;

}

static info *FreeInfo( info *info)
{
  free( info);
  return info;
}

void TOPtravVertex( vertex *v, info *arg_info)
{

  edges *children;

  children = VERTEX_CHILDREN (v);
  VERTEX_TOPO (v) = INFO_TOPO( arg_info)++;

  if( INFO_HEAD( arg_info) == NULL){


    INFO_HEAD( arg_info) = makevertices();
    INFO_LIST( arg_info) = INFO_HEAD( arg_info);

    VERTICES_CURR( INFO_HEAD( arg_info)) = v;

  } else if( VERTICES_NEXT( INFO_LIST( arg_info)) == NULL) {

    VERTICES_NEXT( INFO_LIST( arg_info)) = makevertices();
    INFO_LIST( arg_info) = VERTICES_NEXT( INFO_LIST( arg_info));
    VERTICES_CURR( INFO_LIST( arg_info)) = v;
    VERTICES_NEXT( INFO_LIST( arg_info)) = NULL;

  }

  while( children != NULL){


    if( VERTEX_NUMPARENTS( EDGES_TARGET( children)) ==
        ++VERTEX_NUMTOPOVISITS( EDGES_TARGET( children))){
      TOPtravVertex (EDGES_TARGET( children), arg_info);
    }

    children = EDGES_NEXT(children);

  }

}

void TOPdoTopoSort(dag *g)
{
  info *arg_info;

  arg_info = MakeInfo();

  TOPtravVertex( g->top, arg_info);
  if (g->info == NULL){
	  g->info = makecompinfo();
  }
  g->info->topolist = INFO_HEAD( arg_info);

  arg_info = FreeInfo(arg_info);

}


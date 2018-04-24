#include "graphclass.h"
#include "throuhierarch.h"
#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "graphclass.h"
#include "throuhierarch.h"
#include "allprint.h"

typedef struct INFO {
  int pre;
  int post;
  dxvector *prearr;
}info;

#define INFO_PRE(n) n->pre
#define INFO_POST(n) n->post
#define INFO_PREARR(n) n->prearr

static info *MakeInfo( void)
{
  info *result;

  result = (info *) malloc( sizeof( info));
  INFO_PRE(result) = 1;
  INFO_POST(result) = 1;
  INFO_PREARR( result) = NULL;

  return result;

}

static info *FreeInfo( info *info)
{

  free( info);

  return info;
}

void DFWtravVertex( vertex *v, info *arg_info)
{

  edges *children;
  ELM *e;

  children = VERTEX_CHILDREN( v);
  VERTEX_PRE(v) = INFO_PRE( arg_info)++;

  if( INFO_PREARR( arg_info) == NULL){
    INFO_PREARR( arg_info) = makedxvector();
  }

  e = (ELM *) malloc( sizeof( ELM));
  ELM_IDX(e) = VERTEX_PRE(v);
  ELM_DATA(e) = v;
  addToArray( INFO_PREARR( arg_info), e);

  while( children != NULL){

    if( VERTEX_PRE( EDGES_TARGET( children)) == 0){
      EDGES_EDGETYPE( children) = edgetree;
      EDGES_WASCLASSIFIED( children) = 1;

      VERTEX_DEPTH( EDGES_TARGET( children))
      =  VERTEX_DEPTH( v) + 1;

      DFWtravVertex( EDGES_TARGET( children), arg_info);

    }

    children = EDGES_NEXT(children);

  }


  VERTEX_PREMAX( v) = INFO_PRE( arg_info);
  VERTEX_POST( v) = INFO_POST( arg_info)++;

}


void DFWdothrouhierarch(dag *g)
{
  info *arg_info;

  arg_info = MakeInfo();


  INFO_PREARR( arg_info) = NULL;

  DFWtravVertex( DAG_TOP(g), arg_info);

  COMPINFO_PREARR( DAG_INFO(g))
    = INFO_PREARR( arg_info);

  arg_info = FreeInfo(arg_info);

}

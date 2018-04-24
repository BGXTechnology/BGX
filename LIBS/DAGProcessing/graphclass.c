#include "graphclass.h"
#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "graphclass.h"

dag* makedag (){
	dag *g = (dag *) malloc (sizeof (dag));
	g->dirty = 1;
	g->vs = NULL;
	g->top = NULL;
	g->bottom = NULL;
	g->info = NULL;
	return g;
}

vertex* makevertex (){
	vertex *v = (vertex *) malloc (sizeof (vertex));
	v->label[0] = '\0';
	v->pre = 0;
	v->premax = 0;
	v->post = 0;
	v->topo = 0;
	v->eulerid = 0;
	v->depth = 0;
	v->numtopovisits = 0;
	v->graphfromrelchildvisits = 0;
	v->numparents = 0;
	v->numchildren = 0;
	v->row = 0;
	v->reachcola = 0;
	v->reachcolb = 0;
	v->preprocesscol = 0;
	v->distcol = 0;
	v->isCompRoot = false;
	v->isTopoVisited = false;
	v->isDFSVisited = false;
	v->isRchColaMarked = false;
	v->isRchColbMarked = false;
	v->isRowMarked = false;
	v->isDotVisited = false;
	v->ancestors = NULL;
	v->parents = NULL;
	v->children = NULL;
	return v;
}

vertices* makevertices (){

	vertices *vs = (vertices *) malloc (sizeof (vertices));
	vs->curr = NULL;
	vs->next = NULL;
	return vs;

}

void freevertices (vertices *vs) {
	vertices *vs1 = vs, *vs2=vs;
	/*
	 * Note: shallow free
	 */
	while (vs1!=NULL) {
		vs2=vs1;
		vs1=vs1->next;
		free(vs2);
	}
	//free (vs1);
}

edges* makeedges (){

	edges *es = (edges *) malloc (sizeof (edges));
	es->edgetype = edgedefault;
	es->wasClassified = false;
	es->target = NULL;
	es->next = NULL;
	return es;

}

compinfo* makecompinfo (){
	compinfo *ci = (compinfo *) malloc (sizeof (compinfo));
	ci->csrc = NULL;
	ci->ctar = NULL;
	ci->tltable = NULL;
	ci->eulertour = NULL;
	ci->prearr = NULL;
	ci->crossclos = NULL;
	ci->tlc = NULL;
	ci->preprocess = NULL;
	ci->dist = NULL;
	ci->topolist = NULL;
	return ci;
}

preprocessinfo* makepreprocessinfo (){
	preprocessinfo *li = (preprocessinfo *) malloc (sizeof (preprocessinfo));
	li->numintra = 0;
	li->blocksize = 0;
	li->blockmin = NULL;
	li->intermat = NULL;
	li->intramats = NULL;
	li->pcptmat = NULL;
	li->pcpcmat = NULL;
	return li;
}


edges *freeCurrentEdge ( edges *e){
	edges *to_free = e;
	e = e->next;
	free (to_free);
	return e;
}

void freepreprocessInfo( preprocessinfo *linfo){

	int i;

	if( linfo != NULL) {

		if( preprocessINFO_BLOCKMIN( linfo) != NULL){
			freedxvector( preprocessINFO_BLOCKMIN( linfo));
		}

		if( preprocessINFO_INTRAMATS( linfo) != NULL){

			for( i = 0; i < preprocessINFO_NUMINTRA( linfo); i++){

				if( preprocessINFO_INTRAMATS_POS( linfo, i) != NULL){
					freeMatrix( preprocessINFO_INTRAMATS_POS( linfo, i));
				}

			}

		}

		if( preprocessINFO_INTERMAT( linfo) != NULL){
			freeMatrix( preprocessINFO_INTERMAT( linfo));
		}

		if( preprocessINFO_PCPTMAT( linfo) != NULL){
			freeMatrix( preprocessINFO_PCPTMAT( linfo));
		}

		if( preprocessINFO_PCPCMAT( linfo) != NULL){
			freeMatrix( preprocessINFO_PCPCMAT( linfo));
		}

		free( linfo);

	}

}

void freeCompInfo( compinfo *ci){
	vertices *vs;

	if( ci != NULL){

		if( COMPINFO_CSRC( ci)!=NULL){
			freedxvector( COMPINFO_CSRC( ci));
		}

		if( COMPINFO_CTAR( ci)!=NULL){
			freedxvector( COMPINFO_CTAR( ci));
		}

		if( COMPINFO_TLTABLE( ci)!=NULL){
			freedxvector( COMPINFO_TLTABLE( ci));
		}

		if( COMPINFO_PREARR( ci)!=NULL){
			//freedxvector( COMPINFO_PREARR( ci));
		}

		if( COMPINFO_EULERTOUR( ci)!=NULL){
			freedxvector( COMPINFO_EULERTOUR( ci));
		}

		if( COMPINFO_CROSSCLOS( ci)!=NULL){
			freeMatrix( COMPINFO_CROSSCLOS( ci));
		}

		if ( COMPINFO_TLC( ci) != NULL){
			freeMatrix( COMPINFO_TLC( ci));
		}

		if ( COMPINFO_preprocess( ci) != NULL){
			freepreprocessInfo( COMPINFO_preprocess( ci));
		}

		if ( COMPINFO_DIST(ci) != NULL){
			freeMatrix( COMPINFO_DIST( ci));
		}

		while( COMPINFO_TOPOLIST( ci) != NULL){
			vs = COMPINFO_TOPOLIST( ci);
			COMPINFO_TOPOLIST( ci) =
					VERTICES_NEXT( COMPINFO_TOPOLIST( ci));
			free( vs);
		}

		free( ci);

	}

}


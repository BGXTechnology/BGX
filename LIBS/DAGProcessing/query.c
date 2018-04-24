#include "throuhierarch.h"
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
#include "query.h"

int GINisReachable( vertex *n1, vertex *n2, compinfo *ci){

	int result = 0;
	int reachtree = 0, reachcross = 0;
	int cola, colb, row;
	int reaching_csrc_after_pre;
	int reaching_csrc_after_premax;

	if(VERTEX_PRE(n1) == VERTEX_PRE(n2)) return 1;

	if( VERTEX_POST( n1) > VERTEX_POST( n2)){

		if( ( VERTEX_PRE( n2) >= VERTEX_PRE( n1) &&
				VERTEX_PRE( n2) < VERTEX_PREMAX( n1))){

			reachtree = 1;

		}

		cola = VERTEX_REACHCOLA( n1);
		colb = VERTEX_REACHCOLB( n1);
		row = VERTEX_ROW( n2)-1;


		if( !VERTEX_ISROWMARKED( n2)){

			reachcross = 0;

		} else {

			if( !VERTEX_ISRCHCOLAMARKED( n1) || cola == -1){
				reaching_csrc_after_pre = 0;
			} else {
				reaching_csrc_after_pre = getMatrixValue( COMPINFO_TLC( ci),
						cola, row);
			}

			if( !VERTEX_ISRCHCOLBMARKED( n1) || colb == -1){
				reaching_csrc_after_premax = 0;
			} else {
				reaching_csrc_after_premax = getMatrixValue( COMPINFO_TLC( ci),
						colb, row);
			}

			if( reaching_csrc_after_pre - reaching_csrc_after_premax > 0){
				reachcross = 1;
			} else {
				reachcross = 0;
			}

		}

		if( reachtree || reachcross){
			result = 1;
			//printf ("%d reaches %d\n", n1->pre, n2->pre);
		}

	}

	return result;

}

vertex *GINlcaFromNodes( vertex *v1, vertex *v2, compinfo *ci){

	matrix *pcpt_matrix, *pcpc_matrix;
	int pcpt_col, pcpt_row, pcpc_row, pcpc_col;
	ELM *pcpt_ELM;
	int lower_pcpt_pre, upper_pcpt_pre;
	vertex *lower_pcpt_node, *upper_pcpt_node;
	int pcpc_plca_pre;
	vertex *sptree_plca, *pcpt_plca1, *pcpt_plca2, *pcpc_plca;
	vertex *n1, *n2;

	if(VERTEX_POST (v1) > VERTEX_POST(v2)){
		n1=v2; n2=v1;
	} else{
		n1=v1; n2=v2;
	}

	pcpt_matrix = preprocessINFO_PCPTMAT( COMPINFO_preprocess( ci));
	pcpc_matrix = preprocessINFO_PCPCMAT( COMPINFO_preprocess( ci));

	sptree_plca = preprocesstreeLCAfromNodes( n1, n2, ci);

	if (ci->tltable == NULL) return sptree_plca;

	pcpt_col = VERTEX_preprocessCOL( n2);
	pcpt_row = VERTEX_ROW( n1)-1;

	pcpc_col = VERTEX_ROW( n2);
	pcpc_row = VERTEX_ROW( n1);


	if(pcpt_col == -1 || pcpt_row == -1) {
		lower_pcpt_pre = -1;

	} else {
		pcpt_ELM = getMatrixELM( pcpt_matrix, pcpt_row, pcpt_col);

		lower_pcpt_pre = ( (int *) ELM_DATA( pcpt_ELM))[0];

		upper_pcpt_pre = ( (int *) ELM_DATA( pcpt_ELM))[1];
	}


	if ( lower_pcpt_pre == -1){

		lower_pcpt_node = ( vertex *) ELM_DATA(
				dxvector_ELMS_POS( COMPINFO_PREARR( ci), 0));

	} else {

		lower_pcpt_node = ( vertex *) ELM_DATA(
				dxvector_ELMS_POS( COMPINFO_PREARR( ci), lower_pcpt_pre - 1));

	}

	if ( upper_pcpt_pre == -1){

		upper_pcpt_node = ( vertex *) ELM_DATA(
				dxvector_ELMS_POS( COMPINFO_PREARR( ci), 0));

	} else {

		upper_pcpt_node = ( vertex *) ELM_DATA(
				dxvector_ELMS_POS( COMPINFO_PREARR( ci), upper_pcpt_pre - 1));

	}


	pcpt_plca1 = preprocesstreeLCAfromNodes( lower_pcpt_node, n2, ci);
	pcpt_plca2 = preprocesstreeLCAfromNodes( n2, upper_pcpt_node, ci);

	if(pcpc_matrix != NULL){
		if (pcpc_row==0 || pcpc_col==0) {
			pcpc_plca = NULL;
		} else {
			pcpc_plca_pre = getMatrixValue( pcpc_matrix, pcpc_row-1, pcpc_col-1);
			pcpc_plca = ( vertex *) ELM_DATA(
					dxvector_ELMS_POS( COMPINFO_PREARR( ci), pcpc_plca_pre - 1));
		}
	} else {
		pcpc_plca =NULL;
	}


	vertex *n[4] = { sptree_plca, pcpt_plca1, pcpt_plca2, pcpc_plca};
	int i;
	vertex *result = sptree_plca;


	for( i = 0 ; i < 4; i++){
		if( n[i]!=NULL){
			//printf( "%d:%d\n", i, n[i]->pre);
		}
	}


	for( i = 1 ; i < 4; i++){
		if( n[i]!=NULL && VERTEX_TOPO( n[i]) > VERTEX_TOPO( result)){
			result = n[i];
		}
	}

	return result;

}

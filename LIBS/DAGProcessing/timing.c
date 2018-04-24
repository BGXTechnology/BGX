#include "timing.h"
#include "graphclass.h"
#include "dag.h"
#include "query.h"
#include "allprint.h"
#include "dxvector.h"
#include "dxatom.h"
#include "dxmyltiarr.h"
#include "topo.h"
#include "throuhierarch.h"
#include "clusters.h"
#include <time.h>

void verifypreprocess (int a, int b, dag *g, matrix *greedy, tr* result) {
	dxvector *prearr = COMPINFO_PREARR(DAG_INFO(g));
	vertex *va = (vertex *) ELM_DATA (dxvector_ELMS_POS (prearr, a-1));
	vertex *vb = (vertex *) ELM_DATA (dxvector_ELMS_POS (prearr, b-1));
	int eid_a = VERTEX_EULERID(va), eid_b = VERTEX_EULERID(vb);
	int v1, v2;
    clock_t t;

    t = clock();
	v1 = VERTEX_PRE (DAGgetpreprocess ( g, va, vb));
	t = clock() - t;
	result->qt_adaptive += ((double)t)/CLOCKS_PER_SEC;

	t = clock();
	v2 = getMatrixValue (greedy, a-1, b-1);
	t = clock() - t;
	result->qt_greedy += ((double)t)/CLOCKS_PER_SEC;

			if (v1!=v2) {
		printf ("a = %d b = %d v1 = %d v2 = %d\n",
				a, b, v1, v2);
		printf ("eid_a = %d, eid_b = %d\n", eid_a, eid_b);
		printf ("LCA unequal\n"); exit(-1);
	}
}


matrix* preprocessFromAdjmat (matrix *m, dxvector *prearr) {
	int i, j, k, n;
	matrix *result = makeMatrix();
	if(MATRIX_TOTALROWS (m) != MATRIX_TOTALCOLS (m)) {
		printf ("Cannot compute closure because matrix is not square\n");
		exit (-1);
	}
	n = MATRIX_TOTALROWS (m);

	for (i=0; i<n; i++) {
		for (j=0; j<n; j++) {
			setMatrixValue (result, i, j, 0);
		}
	}

	//printMatrix(m);
	/*Transitive Closure */

	for (k=0; k<n; k++) {
		for (i=0; i<n; i++) {
			for (j=0; j<n; j++) {
				if (getMatrixValue (m, i, k) == 1 &&
						getMatrixValue (m, k, j) == 1)
					setMatrixValue (m, i, j, 1);
			}
		}
	}

	//printMatrix(m);

	vertex *max, *k_vert;

	for (i=0; i<n; i++) {
		for (j=0; j<n; j++) {
			max = NULL;
			for (k=0; k<n; k++) {
				if (getMatrixValue (m, k, i) == 1 &&
						getMatrixValue (m, k, j) == 1) {
					k_vert = (vertex *) (ELM_DATA (dxvector_ELMS_POS (prearr, k)));
					if (max == NULL ||
							VERTEX_TOPO(k_vert) > VERTEX_TOPO(max)) {
						max = k_vert;
					}
				}

			}
			if (max!=NULL)
				setMatrixValue (result, i, j, VERTEX_PRE(max));
			else
				setMatrixValue (result, i, j, 1);
		}
	}

	return result;

}

matrix *adjmatFromGraph(dag *g) {

	matrix *result = makeMatrix();
	dxvector *prearr = COMPINFO_PREARR(DAG_INFO(g));
	int total = dxvector_TOTALELMS (prearr);
	int i, j;
	vertex *v, *child;
	edges *children;
	for(i=0; i<total; i++) {
		for(j=0; j<total; j++){
			setMatrixValue (result, i, j, 0);
		}
	}
	for(i=0; i<total; i++) {
		v = (vertex *) ELM_DATA( dxvector_ELMS_POS (prearr, i));
		children = VERTEX_CHILDREN (v);
		while (children != NULL) {
			child = EDGES_TARGET (children);
			setMatrixValue (result, VERTEX_PRE (v)-1, VERTEX_PRE (child)-1, 1);
			children = EDGES_NEXT (children);
		}
		setMatrixValue (result, VERTEX_PRE (v)-1, VERTEX_PRE (v)-1, 1);
	}
	return result;
}

void init_result(tr *result) {
	result->total = 0;
	result->cratio = 0.0;
	result->ppt_adaptive = 0.0;
	result->ppt_greedy = 0.0;
	result->ppt_ratio = 0.0;
	result->qt_adaptive = 0.0;
	result->qt_greedy = 0.0;
	result->qt_ratio = 0.0;
}


tr* time_difference(dag *g){
	tr *result = (tr*) malloc(sizeof(tr));
	init_result(result);
	matrix *preprocessmat;
	dxvector *prearr;
	int i,j,total;
	double time_taken1=0.0, time_taken2=0.0;
    clock_t t;

	TOPdoTopoSort(g);
	DFWdothrouhierarch(g);

    //dumpDAG(g);

    t = clock();
    preprocessDAG(g);
    t = clock() - t;
    time_taken1 = ((double)t)/CLOCKS_PER_SEC; // in seconds
    result->ppt_adaptive = time_taken1;
    //printf("adaptive took %f seconds to execute \n", time_taken1);

    t = clock();
	preprocessmat = preprocessFromAdjmat ( adjmatFromGraph (g), g->info->prearr);
    t = clock() - t;
    time_taken2 = ((double)t)/CLOCKS_PER_SEC; // in seconds
    result->ppt_greedy = time_taken2;
   //printf("greedy took %f seconds to execute \n", time_taken2);

    result->ppt_ratio = result->ppt_adaptive/result->ppt_greedy;



	prearr = COMPINFO_PREARR(DAG_INFO(g));
	total = dxvector_TOTALELMS (prearr);

	for (i=0; i<total; i++) {
		for (j=0; j<total; j++) {
			verifypreprocess(i+1, j+1, g, preprocessmat, result);

		}
	}

	result->qt_adaptive /= total*total;
	result->qt_greedy /= total*total;

	int num_csrc = 0, num_ctar = 0;
	if (g->info->csrc != NULL) num_csrc = dxvector_TOTALELMS (g->info->csrc);
	if (g->info->ctar != NULL) num_ctar = dxvector_TOTALELMS (g->info->ctar);
	int c = (num_csrc>num_ctar?num_csrc:num_ctar);
	result->cratio = ((double) c)/ ((double) total);
	result->ppt_ratio = time_taken1/time_taken2;
	result->qt_ratio = result->qt_adaptive/result->qt_greedy;

	result->total = total;

	return result;

}

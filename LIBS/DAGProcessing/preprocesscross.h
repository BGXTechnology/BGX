#ifndef _preprocessCROSS_H_
#define _preprocessCROSS_H_

matrix* preprocesscreateReachMat( compinfo *ci);

matrix* preprocesscreatePCPTMat( matrix *reachmat, compinfo *ci);

dxvector * preprocesssortInPostorder( compinfo *ci);

void preprocessorColumnsAndUpdate( matrix *m1, int colidx1, 
    			    matrix *m2, int colidx2, 
			    matrix *result, int rescolidx);

int preprocessisNodeCsrc( vertex *n, dxvector *csrc);

dxvector *preprocessrearrangeCsrcOnTopo( dxvector *csrc, dxvector *prearr);

dxvector *preprocessrearrangeNoncsrcOnTopo( dxvector *noncsrc);

matrix *preprocessrearrangeMatOnTopo( dxvector *topoarr, matrix *mat);

matrix* preprocesscreatePCPCMat( matrix *reachmat, dxvector *postarr, compinfo *ci);

void preprocessincorporateCrossEdges( compinfo *ci);

#endif

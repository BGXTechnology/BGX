#ifndef _allprint_H_
#define _allprint_H_

extern void printdxvector( dxvector *arrayd);
extern void printMatrix( matrix *m);
extern void printMatrixInDotFormat( FILE *fp, matrix *m);
extern void printTransitiveLinkTable( dxvector *arrayd);
extern void printDepthAndPre( dxvector *d);
extern void printpreprocessInfo( compinfo *ci);
void printTLCmatrix( matrix *tlc, dxvector *csrc, dxvector *ctar);
void printpreprocessMatrix( matrix *m);
void recursiveDump(FILE *fp, vertex *v, char *cross);
void dumpDAG(dag *g);

#endif

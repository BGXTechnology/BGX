#include "graphclass.h"
#include "dxatom.h"
#include "dxstack.h"
#include "dxvector.h"
#include "dxmyltiarr.h"
#include "allprint.h"
#include <string.h>

void printdxvector( dxvector *arrayd){

	int i;

	printf( "[");

	for( i = 0; i < dxvector_TOTALELMS( arrayd); i++){
		printf( "%d,", ELM_IDX( dxvector_ELMS_POS( arrayd, i)));
	}

	printf( "]\n");

}

void printEuler( dxvector *arrayd){

	int i;

	printf( "[");

	for( i = 0; i < dxvector_TOTALELMS( arrayd); i++){
		printf( "(%d,",i);
		printf( "%d,", ELM_IDX( dxvector_ELMS_POS( arrayd, i)));
		printf( "%d)", *(int *)ELM_DATA( dxvector_ELMS_POS( arrayd, i)));
	}

	printf( "]\n");

}



void printpreprocessMatrix( matrix *m){

	int i, j;
	dxvector *arrayd;

	dxvector **array2d = MATRIX_ARRAY2D(m);

	printf("\n");

	for( i = 0; i < MATRIX_TOTALROWS(m); i++){

		arrayd = array2d[i];

		if( arrayd != NULL){

			for( j = 0; j < dxvector_TOTALELMS( arrayd); j++){

				if( dxvector_ELMS_POS( arrayd, j) != NULL){
					printf( "%d\t", ELM_IDX( dxvector_ELMS_POS( arrayd, j)));
					fflush(stdout);
				} else printf("-\t");

			}

			for( j = dxvector_TOTALELMS(arrayd); j < MATRIX_TOTALCOLS(m); j++){
				printf("-\t");
			}

		} else {

			for( j = 0; j < MATRIX_TOTALCOLS(m); j++) printf("-\t");

		}

		printf("\n");

	}

}

void printMatrix( matrix *m){

	int i, j;
	dxvector *arrayd;

	dxvector **array2d = MATRIX_ARRAY2D(m);

	printf("\n");

	for( i = 0; i < MATRIX_TOTALROWS(m); i++){

		arrayd = array2d[i];

		if( arrayd != NULL){

			for( j = 0; j < dxvector_TOTALELMS( arrayd); j++){

				if( dxvector_ELMS_POS( arrayd, j) != NULL){
					printf( "%d,", ELM_IDX( dxvector_ELMS_POS( arrayd, j)));
					fflush(stdout);
				} else printf("-,");

			}

			for( j = dxvector_TOTALELMS(arrayd); j < MATRIX_TOTALCOLS(m); j++){
				printf("-,");
			}

		} else {

			for( j = 0; j < MATRIX_TOTALCOLS(m); j++) printf("-,");

		}

		printf("\n");

	}

}

void printMatrixInDotFormat( FILE *outfile, matrix *m){

	int i, j;
	static int id = 0;
	dxvector **array2d = MATRIX_ARRAY2D(m);
	dxvector *arrayd;

	fprintf( outfile, "struct%d [label=\"", id++);

	for(i = 0; i < MATRIX_TOTALROWS(m); i++){

		arrayd = array2d[i];

		if(arrayd != NULL){

			fprintf( outfile,"{");

			for(j = 0; j < dxvector_TOTALELMS(arrayd); j++){

				if( dxvector_ELMS_POS( arrayd, j) != NULL){

					fprintf( outfile, "%d", ELM_IDX( dxvector_ELMS_POS( arrayd, j)));

				} else {

					fprintf( outfile, "-");

				}

				if(j != dxvector_TOTALELMS( arrayd) - 1) printf("|");

			}

			for( j = dxvector_TOTALELMS( arrayd); j < MATRIX_TOTALCOLS(m); j++){

				fprintf( outfile, "-");
				if( j != MATRIX_TOTALCOLS(m) - 1) fprintf( outfile, "|");

			}

		} else {

			for( j = 0; j < MATRIX_TOTALCOLS(m); j++){

				fprintf(outfile, "-");
				if( j != MATRIX_TOTALCOLS(m) - 1) fprintf( outfile, "|");

			}

		}

		fprintf( outfile, "}");

		if( i != MATRIX_TOTALROWS(m) - 1) fprintf( outfile, "|");

	}

	fprintf( outfile, "\"];\n");

}

void printTransitiveLinkTable( dxvector *arrayd){

	int i;

	for( i = 0; i < dxvector_TOTALELMS( arrayd); i++){

		printf( "%d->[%d,%d)\n", ELM_IDX( dxvector_ELMS_POS( arrayd, i)),
				*(( int *) ELM_DATA( dxvector_ELMS_POS( arrayd, i))),
				*(( int *) ELM_DATA( dxvector_ELMS_POS( arrayd, i)) + 1));

	}

}

void printDepthAndPre( dxvector *d){

	if (d==NULL) {
		printf ("Cannot print information for a NULL array");
		exit(-1);
	}

	int i;

	printf("\n----------\n");

	for( i = 0 ; i < dxvector_TOTALELMS(d); i++){
		printf("{%d,", *(int *)ELM_DATA( dxvector_ELMS_POS( d ,i)));
		printf("%d} ", ELM_IDX( dxvector_ELMS_POS( d ,i)));
	}

	printf("\n----------\n");

}

void printPCPTMat( matrix *pcptmat, dxvector *csrc, dxvector *ctar){

	printf( "\n");
	printf( "PCPT Matrix \n");
	printf( "----------- \n");

	int i,j;
	ELM *e;
	int lower, upper;

	for( i = -1; i < dxvector_TOTALELMS( ctar); i++) {

		if( i == -1){

			printf( "| \t");

			for( j = -1; j < dxvector_TOTALELMS( csrc); j++){
				if (j==-1){
					printf( "|1\t\t");
				} else {
					printf( "| %d\t\t",
						ELM_IDX( dxvector_ELMS_POS( csrc, j)));
				}
			}

		}
		else {

			for( j = -1; j < dxvector_TOTALELMS( csrc) + 1; j++){

				if( j == -1) {

					printf( "| %d\t",
							ELM_IDX( dxvector_ELMS_POS( ctar, i)));

				} else {

					e = getMatrixELM( pcptmat, i, j);

					lower = ((int *)ELM_DATA( e))[0];
					upper = ((int *)ELM_DATA( e))[1];

					printf( "| (%d, %d)\t", lower, upper);

				}

			}

		}

		printf( "|\n");

	}

}

void printPCPCMat( matrix *pcpcmat, dxvector *ctar){

	int i,j;

	printf( "\n");
	printf( "PCPC Matrix \n");
	printf( "----------- \n");

	for( i = -1; i < dxvector_TOTALELMS( ctar); i++) {

		if( i == -1){

			printf( "| \t");

			for( j = 0; j < dxvector_TOTALELMS( ctar); j++){

				printf( "| %d\t",
						ELM_IDX( dxvector_ELMS_POS( ctar, j)));

			}

		} else {

			for( j = -1; j < dxvector_TOTALELMS( ctar); j++){

				if( j == -1) {

					printf( "| %d\t",
							ELM_IDX( dxvector_ELMS_POS( ctar, i)));

				} else {

					printf( "| %d\t",
							getMatrixValue( pcpcmat, i, j));

				}

			}

		}

		printf( "|\n");

	}

}



void printTLCmatrix( matrix *tlc, dxvector *csrc, dxvector *ctar){

	int i,j;

	printf( "\n");
	printf( "TLC Matrix \n");
	printf( "----------- \n");

	for( i = -1; i < dxvector_TOTALELMS( ctar); i++) {

		if( i == -1){

			printf( "| \t");

			for( j = 0; j < dxvector_TOTALELMS( csrc); j++){

				printf( "| %d\t",
						ELM_IDX( dxvector_ELMS_POS( csrc, j)));

			}

		} else {

			for( j = -1; j < dxvector_TOTALELMS( csrc); j++){

				if( j == -1) {

					printf( "| %d\t",
							ELM_IDX( dxvector_ELMS_POS( ctar, i)));

				} else {

					printf( "| %d\t",
							getMatrixValue( tlc, j, i));

				}

			}

		}

		printf( "|\n");

	}

}

void printpreprocessInfo( compinfo *ci){

	preprocessinfo *linfo;

	int i;

	if (COMPINFO_TLTABLE (ci) != NULL) {

	printTransitiveLinkTable (COMPINFO_TLTABLE (ci));

	printTLCmatrix (COMPINFO_TLC (ci), COMPINFO_CSRC( ci),
			COMPINFO_CTAR( ci));
	}

	if (COMPINFO_EULERTOUR (ci) != NULL) printEuler (COMPINFO_EULERTOUR (ci));


	linfo = COMPINFO_preprocess( ci);

	if( linfo != NULL) {

		if( preprocessINFO_BLOCKMIN( linfo) != NULL){
			printDepthAndPre( preprocessINFO_BLOCKMIN( linfo));
		}

		if( preprocessINFO_INTRAMATS( linfo) != NULL){

			for( i = 0; i < preprocessINFO_NUMINTRA( linfo); i++){

				if( preprocessINFO_INTRAMATS_POS( linfo, i) != NULL){
					printMatrix( preprocessINFO_INTRAMATS_POS( linfo, i));
					printf("--\n");
				}

			}

		}

		if( preprocessINFO_INTERMAT( linfo) != NULL){
			printMatrix( preprocessINFO_INTERMAT( linfo));
		}

		if( preprocessINFO_PCPTMAT( linfo) != NULL){
			printPCPTMat( preprocessINFO_PCPTMAT( linfo), COMPINFO_CSRC( ci),
					COMPINFO_CTAR( ci));
		}

		if( preprocessINFO_PCPCMAT( linfo) != NULL){
			printPCPCMat( preprocessINFO_PCPCMAT( linfo), COMPINFO_CTAR( ci));
		}

	}
}

void recursiveDump(FILE *fp, vertex *v, char *cross){
	if (VERTEX_ISDOTVISITED(v))  return;
	edges *children = VERTEX_CHILDREN (v);
	char str[80];
	while (children!=NULL) {
		if (EDGES_EDGETYPE(children) != edgetree) {
			sprintf(str, "\"%d, %d\" -> \"%d, %d\" [style=dotted];\n",
					VERTEX_PRE(v), VERTEX_TOPO(v),
					VERTEX_PRE (EDGES_TARGET (children)),
					VERTEX_TOPO (EDGES_TARGET (children)));
			//printf ("%s\n",str);
			strcat (cross, str);
		} else {
			fprintf(fp, "\"%d, %d\" -> \"%d, %d\";\n", VERTEX_PRE(v),
					VERTEX_TOPO(v), VERTEX_PRE (EDGES_TARGET (children)),
					VERTEX_TOPO (EDGES_TARGET (children)));
			recursiveDump(fp, EDGES_TARGET(children), cross);
		}
		children = EDGES_NEXT (children);
	}
	VERTEX_ISDOTVISITED (v) = true;
}


void dumpDAG(dag *g){
	FILE *fp = fopen("dotfile", "w");
	char cross[100000];
	cross[0] = '\0';
	fprintf (fp, "digraph dag{\n");
	recursiveDump (fp, DAG_TOP(g), cross);
	//printf ("%s\n", cross);
	if (cross[0] != '\0')
		fprintf (fp, "%s}\n", cross);
	else
		fprintf(fp, "}\n");
	fclose (fp);

}


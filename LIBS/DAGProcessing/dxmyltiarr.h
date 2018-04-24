
#ifndef _dxmyltiarr_H_
#define _dxmyltiarr_H_

extern void initMatrix(matrix *m);
matrix *makeMatrix ();
extern void free2DArray(dxvector **d2, int count);
extern void freeMatrix(matrix *m);
extern void setMatrixValue(matrix *m, int x, int y, int value);
extern void setMatrixELM(matrix *m, int x, int y, ELM *ELMent);
extern ELM *getMatrixELM(matrix *m, int x, int y);
extern int getMatrixValue(matrix *m, int x, int y);

#define MATRIX_ARRAY2D(n) ((n)->array2d)
#define MATRIX_TOTALROWS(n) ((n)->totalrows)
#define MATRIX_TOTALCOLS(n) ((n)->totalcols)

#endif 

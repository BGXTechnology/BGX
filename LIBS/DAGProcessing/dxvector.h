#ifndef _dxvector_H_
#define _dxvector_H_

#include "graphclass.h"

#define dxvector_ELMS(n) ((n)->ELMs)
#define dxvector_ELMS_POS(n,i) ((n)->ELMs[i])
#define dxvector_TOTALELMS(n) ((n)->totalELMs)
#define dxvector_ALLOCELMS(n) ((n)->allocELMs)

extern void initdxvector(dxvector *arrayd);
dxvector * makedxvector ();
extern void freeELMArray( ELM **e, int count);
extern void freedxvector( dxvector *arrayd);
extern int addToArray( dxvector *arrayd, ELM *item);
extern int indexExistsInArray( dxvector *arrayd, int idx);
int getPositionInArray( dxvector *arrayd, int idx);
extern int addToArrayAtPos( dxvector *arrayd, ELM *item, int pos);
extern void merge( ELM **ELMs, int lower, int upper, int desc);
extern void sortArray( ELM **ELMs, int lower, int upper, int desc);
ELM* getELMFromArray( dxvector *arrayd, int idx);
void freedxvectorShallow( dxvector *arrayd);

#endif

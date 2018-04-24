#ifndef _ELM_H_
#define _ELM_H_

#include "graphclass.h"

#define ELM_IDX(n) ((n)->idx)
#define ELM_DATA(n) ((n)->data)

extern void initELM( ELM *e);
ELM *makeELM ();
extern void freeELM( ELM *e);

#endif

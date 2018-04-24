#ifndef _dxqueue_H_
#define _dxqueue_H_

#define dxqueue_HEAD(n) ((n)->head)
#define dxqueue_TAIL(n) ((n)->tail)

void EQenqueue( dxqueue *q, ELM *e);
ELM * EQdequeue( dxqueue *q);

#endif

#include "graphclass.h"
#include "dxatom.h"
#include "dxqueue.h"
#include "dxlist.h"

void EQenqueue( dxqueue *q, ELM *e){

	dxlist *head, *el;

	head = dxqueue_HEAD( q);

	el = (dxlist *) malloc( sizeof ( dxlist));
	ELinit( el);

	dxlist_CURR( el) = e;
	dxlist_NEXT( el) = head;

	dxlist_PREV( head) = el;

	dxqueue_HEAD( q) = el;

}

ELM * EQdequeue( dxqueue *q){

	dxlist *tail, *prev;
	ELM *e;

	tail = dxqueue_TAIL(q);
	prev = dxlist_PREV( tail);

	dxlist_NEXT( prev) = NULL;
	e = dxlist_CURR( tail);

	tail = ELfreeNonRecursive( tail);

	return e;

}

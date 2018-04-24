#ifndef _dxlist_H_
#define _dxlist_H_

#define dxlist_CURR(n) ((n)->curr)
#define dxlist_PREV(n) ((n)->prev)
#define dxlist_NEXT(n) ((n)->next)

void ELinit( dxlist *el);
dxlist *ELfreeNonRecursive( dxlist *el);
dxlist *ELfreeRecursive( dxlist *el);

#endif

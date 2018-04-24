#ifndef _dxstack_H_
#define _dxstack_H_

#define dxstack_CURR(n) ((n)->curr)
#define dxstack_NEXT(n) ((n)->next)

extern int isdxstackEmpty( dxstack *s);
extern void initdxstack( dxstack *s);
extern void pushdxstack( dxstack **s, ELM *e);
extern ELM* popdxstack( dxstack **s);

#endif

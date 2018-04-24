#ifndef _dxheap_H
#define _dxheap_H

void PQinsertELM( ELM *x, dxvector *q);
void PQinsert( int x, dxvector *q);
void PQdeleteMin( dxvector *q);
int PQgetMin( dxvector *q);
ELM* PQgetMinELM( dxvector *q);
void PQprint( dxvector *q);

#endif


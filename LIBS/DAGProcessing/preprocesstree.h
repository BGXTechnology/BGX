
#ifndef _preprocessTREE_H_
#define _preprocessTREE_H_

extern preprocessinfo * preprocesscreatePartitions( dxvector *eulertour);
extern int preprocessgetLowestFromCandidates( dxvector *d, int indices[4]);
extern vertex *preprocesstreeLCAfromNodes( vertex *n1, vertex *n2, compinfo *ci);

#endif /* _preprocessTREE_H_ */

#ifndef _dxgraphserv_H_
#define _dxgraphserv_H_
#include <stdbool.h>
#include "graphclass.h"

bool GUvertInList( vertex *n, vertices *nl);
vertices* GUmergeLists( vertices *nla, vertices *nlb);
void GUremoveEdge( vertex *src, vertex *tar);

#endif /* _dxgraphserv_H_ */

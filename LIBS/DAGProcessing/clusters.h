#ifndef clusters_H_
#define clusters_H_
#include "graphclass.h"
dag *getClassHierarchy (char *filename);
void createEdgeInHierarchy (dag *g, char *source, char *dest);

#endif 

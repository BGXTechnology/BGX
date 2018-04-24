#ifndef _DAG_H_
#define _DAG_H_

extern dag * DAGgenGraph();

extern vertex * DAGaddVertex( dag* g, char *label);
extern vertex* getVertexFromLabel (dag *g, char *label);
extern void * DAGgetVertexAnnotation( dag *g, vertex *from);

extern void DAGaddEdge( dag *g, vertex * from, vertex * to);

extern vertex * DAGgetpreprocess( dag *g, vertex * from, vertex * to);
void preprocessDAG( dag *g);

#endif /* _DAG_H_ */

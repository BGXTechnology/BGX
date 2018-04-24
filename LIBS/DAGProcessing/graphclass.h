#ifndef _graphclass_H_
#define _graphclass_H_

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <unistd.h>

struct ELM{
  int idx;
  void *data;
};

struct dxvector{
  struct ELM **ELMs;
  int totalELMs;
  int allocELMs;
};

struct MATRIX{
  struct dxvector **array2d;
  int totalrows;
  int totalcols;
};

struct dxstack{
  struct ELM *curr;
  struct dxstack *next;
};

struct dxlist{
  struct ELM *curr;
  struct dxlist *prev;
  struct dxlist *next;
};

struct dxqueue{
  struct dxlist *head;
  struct dxlist *tail;
};

struct preprocessINFO{
  int numintra;
  int blocksize;
  struct dxvector *blockmin;
  struct MATRIX *intermat;
  struct MATRIX **intramats;
  struct MATRIX *pcptmat;
  struct MATRIX *pcpcmat;
};

struct COMPINFO{
  struct dxvector *csrc;
  struct dxvector *ctar;
  struct dxvector *tltable;
  struct dxvector *eulertour;
  struct dxvector *prearr;
  struct MATRIX *crossclos;
  struct MATRIX *tlc;
  struct preprocessINFO *preprocess;
  struct MATRIX *dist;
  struct VERTICES *topolist;
};


typedef enum {
	edgetree,edgecross,edgeforward,edgeback, edgedefault
} graph_edgetype;
typedef enum {
	tree_labeling,nontree_labeling,edge_labeling
} graph_label_mode;

//typedef enum {vertices,edges} dot_output_mode;
typedef struct ELM ELM;
typedef struct dxvector dxvector;
typedef struct MATRIX matrix;
typedef struct dxstack dxstack;
typedef struct dxlist dxlist;
typedef struct dxqueue dxqueue;
typedef struct preprocessINFO preprocessinfo;
typedef struct COMPINFO compinfo;

typedef struct VERTEX{
	char label[400];
	int pre;
	int premax;
	int post;
	int topo;
	int eulerid;
	int depth;
	int numtopovisits;
	int graphfromrelchildvisits;
	int numparents;
	int numchildren;
	int row;
	int reachcola;
	int reachcolb;
	int preprocesscol;
	int distcol;
	bool isCompRoot;
	bool isTopoVisited;
	bool isDFSVisited;
	bool isRchColaMarked;
	bool isRchColbMarked;
	bool isRowMarked;
	bool isDotVisited;
	struct VERTICES *ancestors;
	struct EDGES *parents;
	struct EDGES *children;
} vertex;

typedef struct VERTICES{
	struct VERTEX *curr;
	struct VERTICES *next;
} vertices;

typedef struct EDGES {
	graph_edgetype edgetype;
	bool wasClassified;
	struct VERTEX *target;
	struct EDGES *next;
} edges;

typedef struct DAG{
	int dirty;
	vertices *vs;
	vertex *top;
	vertex *bottom;
	compinfo *info;
} dag;

#define VERTEX_LABEL(v) ((v)->label)
#define VERTEX_PRE(v) ((v)->pre)
#define VERTEX_PREMAX(v) ((v)->premax)
#define VERTEX_POST(v) ((v)->post)
#define VERTEX_TOPO(v) ((v)->topo)
#define VERTEX_EULERID(v) ((v)->eulerid)
#define VERTEX_DEPTH(v) ((v)->depth)
#define VERTEX_NUMTOPOVISITS(v) ((v)->numtopovisits)
#define VERTEX_graphfromrelCHILDVISITS(v) ((v)->graphfromrelchildvisits)
#define VERTEX_NUMPARENTS(v) ((v)->numparents)
#define VERTEX_NUMCHILDREN(v) ((v)->numchildren)
#define VERTEX_ROW(v) ((v)->row)
#define VERTEX_REACHCOLA(v) ((v)->reachcola)
#define VERTEX_REACHCOLB(v) ((v)->reachcolb)
#define VERTEX_preprocessCOL(v) ((v)->preprocesscol)
#define VERTEX_DISTCOL(v) ((v)->distcol)
#define VERTEX_ISCOMPROOT(v) ((v)->isCompRoot)
#define VERTEX_ISTOPOVISITED(v) ((v)->isTopoVisited)
#define VERTEX_ISDFSVISITED(v) ((v)->isDFSVisited)
#define VERTEX_ISRCHCOLAMARKED(v) ((v)->isRchColaMarked)
#define VERTEX_ISRCHCOLBMARKED(v) ((v)->isRchColbMarked)
#define VERTEX_ISROWMARKED(v) ((v)->isRowMarked)
#define VERTEX_ISDOTVISITED(v) ((v)->isDotVisited)
#define VERTEX_ANCESTORS(v) ((v)->ancestors)
#define VERTEX_PARENTS(v) ((v)->parents)
#define VERTEX_CHILDREN(v) ((v)->children)

#define EDGES_EDGETYPE(e) ((e)->edgetype)
#define EDGES_WASCLASSIFIED(e) ((e)->wasClassified)
#define EDGES_TARGET(e) ((e)->target)
#define EDGES_NEXT(e) ((e)->next)

#define	VERTICES_CURR(vs) ((vs)->curr)
#define	VERTICES_NEXT(vs) ((vs)->next)

#define DAG_VS(g) ((g)->vs)
#define DAG_TOP(g) ((g)->top)
#define DAG_BOTTOM(g) ((g)->bottom)
#define DAG_INFO(g) ((g)->info)
#define DAG_DIRTY(g) ((g)->dirty)

#define COMPINFO_CSRC(n) ((n)->csrc)
#define COMPINFO_CTAR(n) ((n)->ctar)
#define COMPINFO_TLTABLE(n) ((n)->tltable)
#define COMPINFO_EULERTOUR(n) ((n)->eulertour)
#define COMPINFO_PREARR(n) ((n)->prearr)
#define COMPINFO_CROSSCLOS(n) ((n)->crossclos)
#define COMPINFO_TLC(n) ((n)->tlc)
#define COMPINFO_preprocess(n) ((n)->preprocess)
#define COMPINFO_DIST(n) ((n)->dist)
#define COMPINFO_TOPOLIST(n) ((n)->topolist)

#define preprocessINFO_NUMINTRA(n) ((n)->numintra)
#define preprocessINFO_BLOCKSIZE(n) ((n)->blocksize)
#define preprocessINFO_BLOCKMIN(n) ((n)->blockmin)
#define preprocessINFO_INTERMAT(n) ((n)->intermat)
#define preprocessINFO_INTRAMATS(n) ((n)->intramats)
#define preprocessINFO_INTRAMATS_POS(n, i) ((n)->intramats[i])
#define preprocessINFO_PCPTMAT(n) ((n)->pcptmat)
#define preprocessINFO_PCPCMAT(n) ((n)->pcpcmat)

extern void freeCompInfo( compinfo *ci);
extern void freepreprocessInfo( preprocessinfo *linfo);

dag* makedag ();
vertex* makevertex ();
vertices* makevertices ();
void freevertices (vertices *vs);
edges* makeedges ();
compinfo* makecompinfo ();
preprocessinfo* makepreprocessinfo ();
edges *freeCurrentEdge ( edges *e);

#endif

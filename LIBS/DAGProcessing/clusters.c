#include "clusters.h"
#include "graphclass.h"
#include "dag.h"
#include <string.h>

void createEdgeInHierarchy (dag *g, char *source, char *dest) {
	vertex *v_source, *v_dest;
	v_source = getVertexFromLabel (g, source);
	v_dest = getVertexFromLabel (g, dest);
	if (v_source==NULL) {
		DAGaddVertex (g, source);
	}
	if (v_dest==NULL) {
		DAGaddVertex (g, dest);
	}
	v_source = getVertexFromLabel (g, source);
	v_dest = getVertexFromLabel (g, dest);

	if (v_source != NULL && v_dest !=NULL) {
		DAGaddEdge (g, v_source, v_dest);
	} else {
		printf("Either source or destination vertex does not exist.\n");
		exit(-1);
	}
}

char *getClassName (char **str) {
	char *open_tag = "<b>", *close_tag = "</b>";
	char *name = NULL;
	char *first=NULL, *last=NULL;
	int open=0, len;

	while(**str!='\0') {
		if(strstr(*str, open_tag)==*str) {
			*str += strlen(open_tag);
			first = *str;
			open++;
			continue;
		}
		if(open) {
			if(strstr(*str, close_tag)==*str) {
				last = *str - 1;
				*str += strlen(close_tag);
				break;
			}
		}
		(*str)++;
	}

	if (first!=NULL && last!=NULL) {
		len = (last-first)+2;
		name  = (char *) malloc( sizeof(char) * len);
		name[len-1] = '\0';
		memcpy(name, first, len-1);
		//puts(name);
	}
	return name;
}

void getAllWithinDelimiters (char **str, char *parent, dag *g) {
	char *begin = "<li ", *end = "</li>";
	char *child=NULL, *oldstr;
	char *s_begin, *s_end;

	//puts(parent);
	while (**str!='\0'){
		oldstr = *str;
		s_begin = strstr(*str, begin);
		s_end = strstr(*str, end);
		if (s_begin != NULL && s_end != NULL && s_begin < s_end) {
			*str += strlen (begin);
			child = getClassName(str);
			//printf ("\"%s\" -> \"%s\";\n", parent, child);
			if (strcmp(parent, child)!=0) {
				createEdgeInHierarchy(g, parent, child);
			}
			getAllWithinDelimiters (str, child, g);
		} else {
			*str = strstr(*str, end) + strlen(end);
			break;
		}
		//puts(parent);
		if (oldstr == *str) (*str)++;
	}
}

#define MAXBUFLEN 500000

dag *getClassHierarchy (char *filename) {
	dag *g=makedag();
	char ul[MAXBUFLEN+1];
	FILE *fp = fopen ( filename, "r");

	if(fp==NULL) {
		printf("\n\nUnable to open file %s; check that the file exists.\n\n", filename);
		exit(-1);
	}

    size_t newLen = fread(ul, sizeof(char), MAXBUFLEN, fp);
    if (newLen == 0) {
        fputs("Error reading file\n\n", stderr);
        exit(-1);
    } else {
        ul[++newLen] = '\0'; /* Just to be safe. */
    }

    char *test = ul;

    char *parent = getClassName (&test);

    getAllWithinDelimiters(&test, parent, g);
    DAG_TOP (g) = getVertexFromLabel (g, "java.lang.Object");

	fclose (fp);
	return g;
}



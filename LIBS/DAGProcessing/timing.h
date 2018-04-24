#ifndef _TIMING_H_
#define _TIMING_H_
#include "graphclass.h"
typedef struct test_result {
	int total;
	double cratio;
	double ppt_adaptive;
	double ppt_greedy;
	double ppt_ratio;
	double qt_adaptive;
	double qt_greedy;
	double qt_ratio;
}tr;

void init_result(tr* result);
tr* time_difference(dag *g);
#endif

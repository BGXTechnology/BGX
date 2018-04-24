# cmake_minimum_required(VERSION <specify CMake version here>)
project(code C)

set(CMAKE_C_STANDARD 11)

include_directories(.)

add_executable(code
        dxheap.c
        dxheap.h
        clusters.c
        clusters.h
        propogator.c
        propogator.h
        dag.c
        dag.h
        throuhierarch.c
        throuhierarch.h
        dxvector.c
        dxvector.h
        dxatom.c
        dxatom.h
        dxmyltiarr.c
        dxmyltiarr.h
        dxlist.c
        dxlist.h
        dxqueue.c
        dxqueue.h
        dxstack.c
        dxstack.h
        graphclass.c
        graphclass.h
        dxgraphserv.c
        dxgraphserv.h
        preprocess.c
        preprocess.h
        preprocesscross.c
        preprocesscross.h
        preprocesstree.c
        preprocesstree.h
        protoio.c
        protoio.h
        graphfromrel.c
        graphfromrel.h
        query.c
        query.h
        reachhelper.c
        reachhelper.h
        reachlabel.c
        reachlabel.h
        allprint.c
        allprint.h
        timing.c
        timing.h
        topo.c
        topo.h)

#ifndef _REACHHELPER_H_
#define _REACHHELPER_H_


dxvector *buildTransitiveLinkTable(
		dxvector *arrayd, dxvector *csrc,
		dxvector *ctar, dxvector *prearr);

extern void setSrcTarArrays( dxvector *arrayd, 
    		      	     dxvector** arrX, 
			     dxvector** arrY);

extern matrix* computeTLCMatrix( dxvector *arrayd, 
    				 dxvector* arrX, 
				 dxvector* arrY);

#endif

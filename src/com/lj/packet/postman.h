#ifndef _POSTMAN_H
#define _POSTMAN_H

#ifdef __cplusplus
extern "C" {
#endif

#ifdef WIN32
#include <winsock2.h>
#else
#define SOCKET int
#endif

/*
return -9 when socket terminate by remote 
return -8 when terminate by a signal
return -3 when msg too long to fit in the buffer
return -2 when msg format error
return -1 when socket IO error 
return 0 when timeout and no msg got
return 1 when suc and msg got

 * packet received stored in pPacket, received packet size stored in *size.
 * timeout: time in microsecond.
 *          -1, block until message got
 *          0, no block, return at once if no message
*/
int GetPacket(SOCKET sockfd, char *pPacket, int *size, long timeout);


/*
return 0 when error encode msg
-8 when terminate by a signal
-1 when socket io error
1 when suc
*/
int SendPacket(SOCKET sockfd, char *pPacket, int size);

#ifdef __cplusplus
}
#endif

#endif /*_POSTMAN_H*/

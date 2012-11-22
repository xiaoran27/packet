#ifndef WIN32
#include <unistd.h>
#endif

#ifdef WIN32
#include <Winsock2.h>
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#endif

#include <errno.h>
#include "codec_rawmsg.h"
#include "postman.h"


static int readn(SOCKET fd, register char *ptr, register int nbytes)
{
	int nleft, nread;

	nleft = nbytes;
	while(nleft > 0 ) {
		nread = recv(fd, ptr, nleft, 0);
		if(nread < 0 ) return(nread); /*error, return < 0*/
		else if(nread == 0) break;   /*EOF*/
		nleft -= nread;
		ptr += nread;
	}
	return (nbytes - nleft);  /*return >= 0*/
}
static int writen(SOCKET fd, register char *ptr, register int nbytes)
{
	int nleft, nwritten;

	nleft = nbytes;
	while(nleft > 0) {
		nwritten = send(fd, ptr, nleft, 0);
		if(nwritten <= 0) return (nwritten); /*error*/
		nleft -= nwritten;
		ptr += nwritten;
	}
	return(nbytes - nleft);
}


int GetPacket(SOCKET sockfd, char *pPacket, int *size, long timeout)
{
	char buf[81920];
	int ret, ret1, bufSize = sizeof(buf);
	int rcvlen = 0;

	fd_set rset;
	struct timeval tv;
	struct timeval tv_cal;

	tv_cal.tv_sec = 0;
	while(timeout >= 1000000) {
		tv_cal.tv_sec ++;
		timeout -= 1000000;
	}
	tv_cal.tv_usec = timeout;

	while(1) {

		FD_ZERO(&rset);
		FD_SET(sockfd, &rset);

		errno = 0;
		if(timeout == -1) {
			ret = select(sockfd+1, &rset, 0, 0, 0);
		}
		else {
			tv = tv_cal;
			ret = select(sockfd+1, &rset, 0, 0, &tv);
		}

		if(ret <0) {
			if(errno == EINTR) return -8;
			return -1;
		}
		else if(ret == 0) return 0;
		else {
			errno = 0;
			if((ret = recv(sockfd, buf + rcvlen, bufSize - rcvlen, MSG_PEEK)) < 0 ) {
				if(errno == EINTR) return -8;
				return -1;
			}
			else if(ret == 0){
				/*the connection has been gracefully closed, see VC++ recv help*/
				return -9;
			}
			else {
				if((ret1 = parse_rawmsg(buf, rcvlen + ret)) < 0 ) 
					return -2;  /*bad format*/
				else if (ret1 == 0 ) {/* continue; incomplete*/
					if (rcvlen + ret >= bufSize) return -3;
					errno = 0;
					ret1 = readn (sockfd, buf + rcvlen, ret);
					if (ret1 < 0) {
						if(errno == EINTR) return -8;
						return -1;
					}
					rcvlen += ret1;
				}
				else {
					errno = 0;
					if(readn(sockfd, buf + rcvlen, ret1 - rcvlen) != ret1 - rcvlen ) {
						if(errno == EINTR) return -8;
						return -1;
					}
					rcvlen += ret1 - rcvlen;
					if((*size = decode_rawmsg(pPacket, *size, buf, rcvlen)) < 0 ) 
						return -2;
					break;
		
				}
			}
		}
	}
	return 1;
}


int SendPacket(SOCKET sockfd, char *pPacket, int size)
{
	char buf[1024];
	int bufSize = sizeof(buf);
	int ret;

	if((ret = encode_rawmsg(buf, bufSize, pPacket, size)) < 0 ) 
		return 0;
	errno = 0;
	if(writen(sockfd, buf, ret ) != ret ) {
		if(errno == EINTR) return -8;
		return -1;
	}
	return 1;
}




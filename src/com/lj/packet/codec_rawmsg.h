#ifndef CODEC_RAWMSG_H
#define CODEC_RAWMSG_H

#ifdef __cplusplus
extern "C"
{
#endif

/* type definition from asn1type.h*/
#ifndef _ASN1TYPE_H_  
typedef unsigned char ASN1OCTET;
#endif

/*
 * return the actual message length if contains a full message, <= rawlen
 * return -1 if bad format
 * return 0 if incomplete message
 */
int parse_rawmsg (const ASN1OCTET rawbuf[], size_t rawlen);

/*
 * return -1 if incorrect format or insufficient buffer
 * return the message length
 *
 * Decode procedure:
 * 1.	去掉两个连续的0xFF尾部。
 * 2.	每有一个0xFF字节，则去掉其后的0x00字节。
 */
int decode_rawmsg (ASN1OCTET dstbuf[], int dstmax, const ASN1OCTET srcbuf[], int srclen);

/*
 * return -1 if insufficient buffer
 * return the destination length
 *
 * Encode procedure:
 * 1.	内部每含有一个0xFF字节，则在其后加入一个0x00字节。
 * 2.	在消息末尾加上两个0xFF字节。
 */
int encode_rawmsg (ASN1OCTET dstbuf[], int dstmax, const ASN1OCTET srcbuf[], int srclen);

#ifdef __cplusplus
}
#endif

#endif /*end of CODEC_RAWMSG_H*/


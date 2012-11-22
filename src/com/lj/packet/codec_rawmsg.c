#include <stdio.h>
#include <assert.h>
#include <time.h>
/* NEED NOT include "codec_rawmsg.h" here*/

#ifdef __cplusplus
extern "C"
{
#endif

/* type definition from asn1type.h*/
#ifndef _ASN1TYPE_H_  
typedef unsigned char ASN1OCTET;
#endif

#define MEET_CHAR  0xff
#define FILL_CHAR  0x00

#if 0
#ifndef NDEBUG
static void debug_output (const char *prompt, const unsigned char rawbuf[], size_t rawlen)
{
	size_t n;
	FILE *fp;
	time_t now;
	char buf[256];

	fp = fopen ("../log/rawmsg.log", "a");
	if (fp == NULL) fp = stderr;
	time (&now);
	fprintf (fp, "\n%s %s\n", ctime_r (&now, buf), prompt);
	fprintf (fp, "(%d)", rawlen);
	for (n = 0; n < rawlen; ++n)
		fprintf (fp, " %02x", rawbuf[n]); 
	fprintf (fp, "\n");
	if (fp != stderr)
		fclose (fp);
}
#else
#define debug_output(prompt, rawbuf, rawlen)
#endif
#endif

/*
 * return the actual message length if contains a full message, <= rawlen
 * return -1 if bad format
 * return 0 if incomplete message
 */
int parse_rawmsg (const ASN1OCTET rawbuf[], size_t rawlen)
{
	size_t i;
	
	i = 0;
	while (i < rawlen)
	{
		if (rawbuf[i] == MEET_CHAR)
		{
			++i;
			if (i >= rawlen) /* incomplete message*/
				return 0;
			if (rawbuf[i] == FILL_CHAR)
				++i;
			else if (rawbuf[i] == MEET_CHAR) /* end of message*/
				return i + 1;
			else // unexpected char in message*/
			{
				/*debug_output ("parse_rawmsg: unexpected char in message", rawbuf, rawlen);*/
				return -1;
			}
		}
		else
		{
			++i;
		}
	}
	return 0; /* incomplete message*/
}

/*
 * return -1 if incorrect format or insufficient buffer
 * return the message length
 */
int decode_rawmsg (ASN1OCTET dstbuf[], int dstmax, const ASN1OCTET srcbuf[], int srclen)
{
	int i;
	int dstlen;

	assert (dstbuf);
	assert (srcbuf);
	assert (srclen > 0);
	assert (dstbuf <= srcbuf || dstbuf >= srcbuf + srclen);

	i = 0;
	dstlen = 0;
	while (i < srclen)
	{
		if (srcbuf[i] == MEET_CHAR)
		{
			++i;
			if ( i >= srclen) /*next char expected*/
			{
				/*debug_output ("decode_rawmsg: next char expected", srcbuf, srclen);*/
				return -1;
			}
			if (srcbuf[i] == FILL_CHAR)
			{
				if (dstlen >= dstmax)
				{
					/*debug_output ("decode_rawmsg: insufficient buffer1", srcbuf, srclen);*/
					return -1;
				}
				dstbuf[dstlen++] = MEET_CHAR;
				++i;
			}
			else if (srcbuf[i] == MEET_CHAR)
			{
				if (i + 1 != srclen) /* end of message*/
				{
					/*debug_output ("decode_rawmsg: end of message occurred", srcbuf, srclen);*/
					return -1;
				}
				return dstlen;
			}
			else /* unexpected char*/
			{
				/*debug_output ("decode_rawmsg: unexpected char", srcbuf, srclen);*/
				return -1;
			}
		}
		else
		{
			if (dstlen >= dstmax)
			{
				/*debug_output ("decode_rawmsg: insufficient buffer2", srcbuf, srclen);*/
				return -1;
			}
			dstbuf[dstlen++] = srcbuf[i];
			++i;
		}
	}
	/*debug_output ("decode_rawmsg: incomplete message", srcbuf, srclen);*/
	return -1;
}

/* 
 * return -1 if insufficient buffer
 * return the destination length
 */
int encode_rawmsg (ASN1OCTET dstbuf[], int dstmax, const ASN1OCTET srcbuf[], int srclen)
{
	int i;
	int dstlen = 0;

	assert (dstbuf);
	assert (srcbuf);
	assert (srclen > 0);
	assert (dstmax >= 2);

	i = 0;
	while (i < srclen)
	{
		if (srcbuf[i] == MEET_CHAR)
		{
			if (dstlen + 1 >= dstmax)
			{
				/*debug_output ("encode_rawmsg: insufficient buffer1", srcbuf, srclen);*/
				return -1;
			}
			dstbuf[dstlen++] = MEET_CHAR;
			dstbuf[dstlen++] = FILL_CHAR;
		}
		else
		{
			if (dstlen >= dstmax)
			{
				/*debug_output ("encode_rawmsg: insufficient buffer2", srcbuf, srclen);*/
				return -1;
			}
			dstbuf[dstlen++] = srcbuf[i];
		}
		++i;
	}
	if (dstlen + 2 > dstmax)
	{
		/*debug_output ("encode_rawmsg: insufficient buffer3", srcbuf, srclen);*/
		return -1;
	}
	dstbuf[dstlen++] = MEET_CHAR;
	dstbuf[dstlen++] = MEET_CHAR;
	return dstlen;
}

#ifdef __cplusplus
}
#endif


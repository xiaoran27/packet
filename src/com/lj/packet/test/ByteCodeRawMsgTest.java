/*
 * Created on 2005-7-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.lj.packet.test;

import junit.framework.TestCase;

import com.lj.packet.ByteCodecRawMsg;

/**
 * @author Xiaoran27
 * Date 2005-7-19
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ByteCodeRawMsgTest extends TestCase {

	byte[] src,dst,olddst;
	int srcLen, dstLen;
	int rtn;
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ByteCodeRawMsgTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for CodeRawMsgTest.
	 * @param arg0
	 */
	public ByteCodeRawMsgTest(String arg0) {
		super(arg0);
		
		src = arg0.getBytes();
		init();

	}
	
	private void init(){
		srcLen = src.length;
		dstLen = srcLen*2 + 2;
		dst = new byte[dstLen];
		
		System.out.println("Source: len = " + src.length + "; src = " + String.valueOf(src));
	}
	public void testEncodeRawMsg() {
		//TODO Implement encodeRawMsg().
		rtn = ByteCodecRawMsg.encodeRawMsg(dst,dstLen,src, srcLen);
		System.out.println("Encode: rtn = " + String.valueOf(rtn) + "; dst = " + new String(dst,0,rtn));
		
		olddst = new byte[rtn];
		System.arraycopy(dst,0,olddst,0,rtn);
		
	}

	public void testDecodeRawMsg() {
		//TODO Implement decodeRawMsg().
		rtn = ByteCodecRawMsg.encodeRawMsg(dst,dstLen,src, srcLen);
		System.out.println("Encode: rtn = " + String.valueOf(rtn) + "; dst = " + new String(dst,0,rtn));
		src = new byte[rtn];
		rtn = ByteCodecRawMsg.decodeRawMsg(src, rtn,dst,rtn);
		System.out.println("Decode: rtn = " + String.valueOf(rtn) + "; src = " + new String(src,0,rtn));
		
	}
	
	public void testParseRawMsg() {
		//TODO Implement parseRawMsg().
		rtn = ByteCodecRawMsg.encodeRawMsg(dst,dstLen,src, srcLen);
		System.out.println("Encode: rtn = " + String.valueOf(rtn) + "; dst = " + new String(dst,0,rtn));
		olddst = new byte[rtn];
		System.arraycopy(dst,0,olddst,0,rtn);

		System.out.println("Pcode1: len = " + String.valueOf(olddst.length) + "; src = " + new String(olddst,0,olddst.length));
		rtn = ByteCodecRawMsg.parseRawMsg(olddst,olddst.length);
		System.out.println("Pcode1: rtn = " + String.valueOf(rtn) );

		olddst[olddst.length - 1] = 0x00;
		System.out.println("Pcode2: len = " + String.valueOf(olddst.length) + "; src = " + new String(olddst,0,olddst.length));
		rtn = ByteCodecRawMsg.parseRawMsg(olddst,olddst.length);
		System.out.println("Pcode2: rtn = " + String.valueOf(rtn) );
		
		olddst[0] = ByteCodecRawMsg.MEET_CHAR;
		olddst[olddst.length - 1] = ByteCodecRawMsg.MEET_CHAR;
		System.out.println("Pcode2: len = " + String.valueOf(olddst.length) + "; src = " + new String(olddst,0,olddst.length));
		rtn = ByteCodecRawMsg.parseRawMsg(olddst,olddst.length);
		System.out.println("Pcode2: rtn = " + String.valueOf(rtn) );

	}

}

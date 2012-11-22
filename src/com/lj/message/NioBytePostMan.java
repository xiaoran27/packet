/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* V,suntf,2007-5-24
* + logger 查看耗时
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-1-3
* 引用HexUtil
*----------------------------------------------------------------------------*
* V,xiaoran27,2008-4-23
* M log info -> debug
*----------------------------------------------------------------------------*
* V,xiaoran27,2009-1-6
* + fd.socket().close()
* M optional
*----------------------------------------------------------------------------*
* V,xiaoran27,2009-5-11
* + if (nbytes == 0) return 0;
\*************************** END OF CHANGE REPORT HISTORY ********************/

package com.lj.message;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.lj.utils.HexUtil;

public final class NioBytePostMan {
    private static final Logger logger = Logger.getLogger( NioBytePostMan.class );

    public NioBytePostMan() {
        super();
    }


    /**
     * 从socket中读取数据,返回读取的字节数
     *
     * @param fd - SocketChannel 确定的连接
     * @param ptr - byte[] 存放收到的数据
     * @param nbytes - 收数据的最大字节数
     * @return int 实际收到的字节数
     * return -101 if socket is disconnected or not opened.
     * return -10 other Exception.
     * @throws IOException
     */
    public static int readn(SocketChannel fd, byte[] ptr, int nbytes) throws IOException
    {
        if (fd == null || ptr == null || nbytes < 0 || ptr.length < nbytes) {
            return -100;
        }else if (nbytes == 0){
        	return 0;
        }

        int nleft, nreads;
        
        nleft = nbytes;
        nreads = 0;
        
        while(nleft > 0 ) {
        	
            ByteBuffer tptr = ByteBuffer.allocate(nleft);
            int nread=0;
            try{

                nread = fd.read(tptr);
                tptr.flip();
                if(nread == 0 ){/*EOF*/
                    break;
                }else if (nread<0){ //-1
                    try{
                        fd.close(); //认为连接断了
                        fd.socket().close();
                    }catch(Exception e1){}
                    return -101;
                }

            }catch (IOException ioe){
            	logger.error("readn(SocketChannel , byte[] , int ) - IOException ioe="+ioe,ioe);
                if(ioe instanceof SocketTimeoutException){
                    return nreads;
                }else{
                    try{
                        fd.close(); //认为连接断了
                        fd.socket().close();
                    }catch(Exception e){}
                }
                
                throw ioe;
            }catch (Exception e){
                logger.error("readn(SocketChannel , byte[] , int ) - Exception e="+e,e);

                return -10;
            }

            nleft -= nread;
            System.arraycopy(tptr.array(),0,ptr,nreads,nread);
            nreads += nread;

        }
        

        if (logger.isDebugEnabled()) {
            logger.debug("readn(SocketChannel , byte[] , int ) - sc="+fd);
            logger.debug("readn(SocketChannel , byte[] , int ) - all bytes(HEX): "+HexUtil.byteToHex(ptr,nreads));
        }

        return nreads;  /*return >= 0*/
    }

    /**
     * 向socket中写数据,返回写的字节数
     * @param fd - SocketChannel 确定的连接
     * @param ptr - byte[] 存放要写的数据
     * @param nbytes - 要写的最大字节数
     * @return int 实际写的字节数
     *  return -101 if socket is disconnected or not opened.
     *  return -10 other Exception.
     * @throws IOException
     */
    public static int writen(SocketChannel fd, byte[] ptr, int nbytes) throws IOException
    {
        if (fd == null || ptr == null || nbytes < 0 || ptr.length < nbytes) {
            return -100;
        }else if (nbytes == 0){
        	return 0;
        }

        int nleft, nwrites;

        nleft = nbytes;
        nwrites = 0;

        while(nleft > 0 ) {

            ByteBuffer tptr = ByteBuffer.allocate(nleft);
            tptr.put(ptr,nwrites,nleft);
            int nwrite=0;
            try{

                tptr.flip();
                nwrite = fd.write(tptr);
                if(0 == nwrite){
                    continue;
                }

            }catch (IOException ioe){
            	logger.error("writen(SocketChannel , byte[] , int ) - IOException ioe="+ioe,ioe);
                if(ioe instanceof SocketTimeoutException){
                    return nwrites;
                }else{
                    try{
                        fd.close(); //认为连接断了
                        fd.socket().close();
                    }catch(Exception e){}
                }
                throw ioe;
            }catch (Exception e){
                logger.error("writen(SocketChannel , byte[] , int ) - Exception e="+e,e);
                return -10;
            }

            nleft -= nwrite;
            nwrites += nwrite;

        }

        if (logger.isDebugEnabled()) {
            logger.debug("writen(SocketChannel , byte[] , int ) - sc="+fd);
            logger.debug("writen(SocketChannel , byte[] , int ) - all bytes(HEX): "+HexUtil.byteToHex(ptr,nwrites));
        }

        return nwrites;
    }


    public static void main(String[] args) {

    }
}

============================2010-12-7 by xiaoran27===========================
1. M com.dbms.DbmsSync  //#作为首列值，后面某个列(仅某一列)分行

============================2010-5-20 by xiaoran27===========================
1. + com.javayjm.excel
2. M ByteRawMessage不在判断socket是否超时

============================2010-2-11 by xiaoran27===========================
1. + com.dbms.DbmsSync

============================2008-1-* by xiaoran27===========================
1. 修改static的IMessage变量为局部; 分配足够空间存放收到的数据包.

============================2007-3-14 by xiaoran27===========================
1. 增加com.lj.message包对消息进行处理, om.lj.config读取配置文件

============================2006-10-16 15:41 by xiaoran27===========================
1. 解码(调用Decoder.getOneMessageLength(...))时, 返回0也是不完整的包
2. 分析包支持msgc和FF结尾的包格式设置. + synchronized public BlockingQueue<byte[]> getRecievePacket(boolean codecIsFF)

============================2006-10-13 15:43 by xiaoran27===========================
1. 修复分析数据包移位的错误(取要发送的数据包拼接时位置移动错误)

============================2006-10-8 15:23 by xiaoran27===========================
1. 修复发送数据的错误(取要发送的数据拼接时位置没有移动)


功能说明:
  对socket的数据进行读写和封装及编解码
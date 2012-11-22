/************************ CHANGE REPORT HISTORY ******************************\
** Product VERSION,UPDATED BY,UPDATE DATE                                     *
*   DESCRIPTION OF CHANGE                                                     *
*-----------------------------------------------------------------------------*
* Example:
*-----------------------------------------------------------------------------*
* V,xiaoran27,2008-4-16
*   create
\*************************** END OF CHANGE REPORT HISTORY ********************/
package com.lj.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 数组的附加操作
 *
 * @author $author$
 * @version $Revision: 1.1 $
  */
public class ArraysUtil {
  /**
   * Creates a new ArraysUtil object.
   */
  private ArraysUtil() {
  }

  /**
   * 搜索指定的 byte 型数组,以获得指定的值的位置.
   *
   * @param a 要搜索的数组
   * @param key 要搜索的值
   * @param begin 开始位置. 0~a.length
   * @param end 结束位置. 0~a.length & end > begin
   *
   * @return DOCUMENT ME!
   */
  public static int search(byte[] a, byte key, int begin, int end) {
    if ((null != a) && (begin >= 0) && (end > begin)) {
      for (int i = begin; i < Math.min(a.length, end); i++) {
        if (a[i] == key) {
          return i;
        }
      }
    }

    return -1;
  }

  /**
   * 搜索指定的 byte 型数组,以获得指定的值的位置.
   *
   * @see #search(byte[], byte, int, int)
   */
  public static int search(byte[] a, byte key, int begin) {
    return search(a, key, begin, a.length);
  }

  /**
   * 搜索指定的 byte 型数组,以获得指定的值的位置.
   *
   * @see #search(byte[], byte, int, int)
   */
  public static int search(byte[] a, byte key) {
    return search(a, key, 0, a.length);
  }

  /**
   * 根据给定的值来拆分指定的 byte 型数组.
   *
   * @see #split(byte[], byte, int, int)
   */
  public static List<byte[]> split(byte[] a, byte split, int start) {
    return split(a, split, start, a.length);
  }

  /**
   * 根据给定的值来拆分指定的 byte 型数组.
   *
   * @see #split(byte[], byte, int, int)
   */
  public static List<byte[]> split(byte[] a, byte split) {
    return split(a, split, 0, a.length);
  }

  /**
   * 根据给定的值来拆分指定的 byte 型数组.
   *
   * @param a 要搜索的数组
   * @param split 分隔值
   * @param begin 开始位置. 0~a.length
   * @param end 结束位置. 0~a.length & end > begin
   *
   * @return 拆分所得数组的列表
   */
  public static List<byte[]> split(byte[] a, byte split, int begin, int end) {
    int pos = begin - 1;
    byte[] bytes = null;
    List<byte[]> list = new ArrayList<byte[]>();

    while (true) {
      begin = pos + 1;
      pos = ArraysUtil.search(a, split, begin, end);

      if (pos < begin) {
        bytes = new byte[end - begin];
      } else {
        bytes = new byte[pos - begin];
      }

      System.arraycopy(a, begin, bytes, 0, bytes.length);
      list.add(bytes);

      if ((pos < begin) || ((pos + 1) >= end)) {
        break;
      }
    }

    return list;
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    byte[] data = new byte[] { 1, 0, 2, 2, 0, 3, 3, 3, 0, 0, 5, 0 };
    byte eof = 0;
    List<byte[]> list = null;

    list = split(data, eof, 0);

    for (byte[] a : list) {
      System.out.print(Arrays.toString(a));
    }

    System.out.println();

    list = split(data, eof, 1);

    for (byte[] a : list) {
      System.out.print(Arrays.toString(a));
    }

    System.out.println();

    list = split(data, eof, 2);

    for (byte[] a : list) {
      System.out.print(Arrays.toString(a));
    }

    System.out.println();

    list = split(data, eof, 9);

    for (byte[] a : list) {
      System.out.print(Arrays.toString(a));
    }

    System.out.println();
  }
}

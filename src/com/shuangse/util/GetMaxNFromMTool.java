package com.shuangse.util;

import java.util.Date;
import java.util.Random;

/**
 * @doc 从一亿个数中找出最大的100个，取100个的数组，先建个最小堆，堆顶是100个中最小的
 *      剩下100000000-100个再一个个去和这个最小的比，如果比它大，就把新数字放堆顶去，置 换到真确位置。
 * */
public class GetMaxNFromMTool {

  /**
   * @param args
   */
  public static void main(String[] args) {
    int n = 10;
    Date startTime = new Date();
    System.out.println(startTime);

    for (int i = 0; i < n; i++) {// 重复n次 算算平均时间
      start();
    }
    Date endTime = new Date();
    System.out.println(endTime);
    System.out.println(n + "次的总时间为" + (endTime.getTime() - startTime.getTime())
        + "毫秒");

  }

  public static void start() {//
    // TODO Auto-generated method stub
    int number = 100000000;// 一亿个数
    int maxnum = 2000000000;// 随机数最大值
    int i = 0;
    int num2 = 100;// 取最大的多少个

    Date startTime = new Date();
    System.out.println(startTime);
    Random random = new Random();
    int[] top = new int[num2];
    for (i = 0; i < num2; i++) {
      top[i] = Math.abs(random.nextInt(maxnum));
    }

    buildHeap(top, 0, top.length);// 构建最小堆， top[0]为最小元素
    int numberCount = number - 100;
    for (i = num2; i < numberCount; i++) {

      int currentNumber2 = Math.abs(random.nextInt(maxnum));

      // System.out.println(currentNumber2);//显示很慢的大概要10分钟还是别了，
      // //写入文件什么的更慢，文件还超大，还是算了吧
      if (top[0] < currentNumber2) {// 有大于 top[0] 的元素则交换 重构最小堆
        top[0] = currentNumber2;
        shift(top, 0, top.length, 0); // 构建最小堆 top[0]为最小元素，只要从0开始就行了
        // check(top, 0, top.length); // 检查下是不是最小堆
      }

    }
    for (int ttop : top) {
      System.out.println(ttop);
    }

    Date endTime = new Date();
    System.out.println(endTime);
    System.out.println("用了" + (endTime.getTime() - startTime.getTime()) + "毫秒");

  }

  public static void buildHeap(int[] array, int from, int len) {
    int pos = (len - 1) / 2;// 从第一个非叶子节点开始
    for (int i = pos; i >= 0; i--) {
      shift(array, from, len, i);
    }

  }

  /**
   * @author gundam
   * @param array
   *          top数组
   * @param from
   *          从哪个开始
   * @param len
   *          数组长度
   * @param pos
   *          当前节点index
   * */
  public static void shift(int[] array, int from, int len, int pos) {
    // 暂时保存该节点的值
    int tmp = array[from + pos];

    int index = pos * 2 + 1;// 得到当前pos节点的左节点
    while (index < len)// 存在左节点
    {
      if (index + 1 < len && array[from + index] > array[from + index + 1])// 如果存在右节点
      // 则比较
      {
        // 如果右边节点比左边节点小，就和右边的比较
        index += 1;
      }

      if (tmp > array[from + index]) {
        array[from + pos] = array[from + index];// 进行了置换操作后，以置换的子节点为起点 （如果存在子节点）
                                                // 还要继续进行该操作
        pos = index;
        index = pos * 2 + 1;

      } else {
        break;
      }
    }
    // 最终全部置换完毕后 ，把临时变量赋给最后的节点
    array[from + pos] = tmp;
  }

  public static void check(int[] array, int from, int len) {
    // 检查是不是最小堆。看上去应该都是的。。可以不用检验了

    int pos = 0; // 从堆顶开始查
    int max = (len - 1) / 2;
    for (int i = pos; i <= max; i++) {
      shift2(array, from, len, i);
    }

  }

  public static void shift2(int[] array, int from, int len, int pos) {

    int tmp = array[from + pos];

    int index = pos * 2 + 1;// 得到当前pos节点的左节点
    if (index < len)// 存在左节点
    {
      if (index + 1 < len && array[from + index] > array[from + index + 1])// 如果存在右节点
      // 则比较
      {
        // 如果右边节点比左边节点小
        index += 1;
      }

      if (tmp > array[from + index]) {
        System.err.print("error 这算法有毛病");
      }
    }

  }
}

package com.shuangse.util;

import java.util.Date;
import java.util.Random;

/**
 * @doc ��һ�ڸ������ҳ�����100����ȡ100�������飬�Ƚ�����С�ѣ��Ѷ���100������С��
 *      ʣ��100000000-100����һ����ȥ�������С�ıȣ���������󣬾Ͱ������ַŶѶ�ȥ���� ������ȷλ�á�
 * */
public class GetMaxNFromMTool {

  /**
   * @param args
   */
  public static void main(String[] args) {
    int n = 10;
    Date startTime = new Date();
    System.out.println(startTime);

    for (int i = 0; i < n; i++) {// �ظ�n�� ����ƽ��ʱ��
      start();
    }
    Date endTime = new Date();
    System.out.println(endTime);
    System.out.println(n + "�ε���ʱ��Ϊ" + (endTime.getTime() - startTime.getTime())
        + "����");

  }

  public static void start() {//
    // TODO Auto-generated method stub
    int number = 100000000;// һ�ڸ���
    int maxnum = 2000000000;// ��������ֵ
    int i = 0;
    int num2 = 100;// ȡ���Ķ��ٸ�

    Date startTime = new Date();
    System.out.println(startTime);
    Random random = new Random();
    int[] top = new int[num2];
    for (i = 0; i < num2; i++) {
      top[i] = Math.abs(random.nextInt(maxnum));
    }

    buildHeap(top, 0, top.length);// ������С�ѣ� top[0]Ϊ��СԪ��
    int numberCount = number - 100;
    for (i = num2; i < numberCount; i++) {

      int currentNumber2 = Math.abs(random.nextInt(maxnum));

      // System.out.println(currentNumber2);//��ʾ�����Ĵ��Ҫ10���ӻ��Ǳ��ˣ�
      // //д���ļ�ʲô�ĸ������ļ������󣬻������˰�
      if (top[0] < currentNumber2) {// �д��� top[0] ��Ԫ���򽻻� �ع���С��
        top[0] = currentNumber2;
        shift(top, 0, top.length, 0); // ������С�� top[0]Ϊ��СԪ�أ�ֻҪ��0��ʼ������
        // check(top, 0, top.length); // ������ǲ�����С��
      }

    }
    for (int ttop : top) {
      System.out.println(ttop);
    }

    Date endTime = new Date();
    System.out.println(endTime);
    System.out.println("����" + (endTime.getTime() - startTime.getTime()) + "����");

  }

  public static void buildHeap(int[] array, int from, int len) {
    int pos = (len - 1) / 2;// �ӵ�һ����Ҷ�ӽڵ㿪ʼ
    for (int i = pos; i >= 0; i--) {
      shift(array, from, len, i);
    }

  }

  /**
   * @author gundam
   * @param array
   *          top����
   * @param from
   *          ���ĸ���ʼ
   * @param len
   *          ���鳤��
   * @param pos
   *          ��ǰ�ڵ�index
   * */
  public static void shift(int[] array, int from, int len, int pos) {
    // ��ʱ����ýڵ��ֵ
    int tmp = array[from + pos];

    int index = pos * 2 + 1;// �õ���ǰpos�ڵ����ڵ�
    while (index < len)// ������ڵ�
    {
      if (index + 1 < len && array[from + index] > array[from + index + 1])// ��������ҽڵ�
      // ��Ƚ�
      {
        // ����ұ߽ڵ����߽ڵ�С���ͺ��ұߵıȽ�
        index += 1;
      }

      if (tmp > array[from + index]) {
        array[from + pos] = array[from + index];// �������û����������û����ӽڵ�Ϊ��� ����������ӽڵ㣩
                                                // ��Ҫ�������иò���
        pos = index;
        index = pos * 2 + 1;

      } else {
        break;
      }
    }
    // ����ȫ���û���Ϻ� ������ʱ�����������Ľڵ�
    array[from + pos] = tmp;
  }

  public static void check(int[] array, int from, int len) {
    // ����ǲ�����С�ѡ�����ȥӦ�ö��ǵġ������Բ��ü�����

    int pos = 0; // �ӶѶ���ʼ��
    int max = (len - 1) / 2;
    for (int i = pos; i <= max; i++) {
      shift2(array, from, len, i);
    }

  }

  public static void shift2(int[] array, int from, int len, int pos) {

    int tmp = array[from + pos];

    int index = pos * 2 + 1;// �õ���ǰpos�ڵ����ڵ�
    if (index < len)// ������ڵ�
    {
      if (index + 1 < len && array[from + index] > array[from + index + 1])// ��������ҽڵ�
      // ��Ƚ�
      {
        // ����ұ߽ڵ����߽ڵ�С
        index += 1;
      }

      if (tmp > array[from + index]) {
        System.err.print("error ���㷨��ë��");
      }
    }

  }
}

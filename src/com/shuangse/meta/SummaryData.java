package com.shuangse.meta;

public class SummaryData {

    public int totalOccursCnt;    //һ�������˼���    
    public int maxContOccursCnt; //��������˼���
    public int curContOccursCnt; //��ǰ�����˼���
    public int maxMissCnt; //�����©����
    public int curMissCnt; //��ǰ��©����
    
    public SummaryData() {
        totalOccursCnt = 0;
        maxContOccursCnt = 0;
        curContOccursCnt = 0;
        maxMissCnt = 0;
        curMissCnt = 0;
    }
}

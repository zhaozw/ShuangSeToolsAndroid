package com.shuangse.meta;

public class SummaryData {

    public int totalOccursCnt;    //一共出现了几次    
    public int maxContOccursCnt; //最大连出了几次
    public int curContOccursCnt; //当前连出了几次
    public int maxMissCnt; //最大遗漏几次
    public int curMissCnt; //当前遗漏几次
    
    public SummaryData() {
        totalOccursCnt = 0;
        maxContOccursCnt = 0;
        curContOccursCnt = 0;
        maxMissCnt = 0;
        curMissCnt = 0;
    }
}

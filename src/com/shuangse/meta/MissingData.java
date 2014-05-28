package com.shuangse.meta;

import java.util.HashSet;
import java.util.Set;

public class MissingData {
    public static class Num {
        public int num;
        public int missingVal;
        
        public Num() {
            num = 0;
            missingVal = -1;
        }
        public Num(int n, int missVal) {
            num = n;
            missingVal = missVal;
        }
    };
    
    public Num[] number = new Num[33];
        
    public MissingData(int[] _num, int[] _missingVal) {
        for(int i=0; i<33; i++) {
            number[i] = new Num(_num[i], _missingVal[i]);
        }
    }
    
    public int getMissValueOfNum(int num) {
        for(int i=0; i<33; i++) {
            if(number[i].num == num) {
                return number[i].missingVal;
            }
        }
        return -1;
    }
    
    public void setMissValueOfNum(int num, int missValue) {
        for(int i=0; i<33; i++) {
            if(number[i].num == num) {
                number[i].missingVal = missValue;
                return;
            }
        }
        return;
    }
    
    public Set<Integer> getNumbersOfMissingVal(int missingVal) {
        Set<Integer> tmpSet=new HashSet<Integer>();
        
        for(int i=0; i<33; i++) {
            if(number[i].missingVal == missingVal) {
                tmpSet.add(number[i].num);
            }
        }
        
        return tmpSet;
    }
    
}

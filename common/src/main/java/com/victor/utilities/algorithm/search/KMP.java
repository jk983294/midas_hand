package com.victor.utilities.algorithm.search;

import com.victor.utilities.visual.VisualAssist;

public class KMP {

    private String s, pat;
    private int fail[];	//fail函数

    /**
     * 计算pat字符串的fail函数
     */
    private void fail(){
        int len = pat.length();
        fail = new int[len];
        fail[0] = -1;
        for (int i =1; i < len;i++){
            int m = fail[i-1];
            while(( m >= 0) && (pat.charAt(i) != pat.charAt(m+1))) m = fail[m];//find max m
            fail[i] = pat.charAt(i) == pat.charAt(m+1) ? m + 1 : -1;
        }
    }

    public int kmp(){//find pat in s
        fail();
        int i = 0, j = 0, lenS = s.length(), lenP = pat.length();
        while ((i < lenS) && (j < lenP)){
            if (s.charAt(i) == pat.charAt(j)){
                i++;
                j++;
            }
            else{
                if (j == 0){
                    i++;
                } else{
                    j = fail[j-1] +1;
                }
            }
        }

        if ((j<lenP) || (lenP== 0)){
            VisualAssist.print("no target pattern string");
            return -1;
        }
        else{//first matched pattern string pos in s
            VisualAssist.print("target pattern string in s locate in pos : " + (i-lenP));
            return (i-lenP);
        }
    }


    void EKMP(String s, String t)//s[]为主串，t[]为模版串
    {
        int next[],extend[]; //extend[i]表示原 串以第i开始与模式串的前缀的最长匹配
        int i,j,p,l;
        int len = t.length();
        int len1 = s.length();
        next = new int[len];
        extend = new int[len1];
        next[0] = len;
        j=0;
        while(1 + j < len && t.charAt(j) == t.charAt(1 + j)) j++;
        next[1] = j;
        int a=1;
        for(i=2; i<len; i++) {
            p = next[a]+a-1;
            l = next[i-a];
            if(i+l<p+1) next[i]=l;
            else {
                j= Math.max(0, p - i + 1);
                while(i+j<len && t.charAt(i+j)==t.charAt(0+j)) j++;
                next[i] = j;
                a = i;
            }
        }
        j=0;
        while(j < len1 && j < len && s.charAt(j) ==t.charAt(j)) j++;
        extend[0] = j;
        a = 0;
        for(i=1; i < len1; i++){
            p = extend[a]+a-1;
            l = next[i-a];
            if(l + i < p + 1) extend[i] = next[i-a];
            else {
                j = Math.max(0, p - i + 1);
                while(i+j < len1 && j<len && s.charAt(i + j)==t.charAt(j)) j++;
                extend[i] = j;
                a = i;
            }
        }
    }

    public static void main(String[] args) {
        KMP kmp = new KMP();
        kmp.s = "fksabcabcacabdjf";
        kmp.pat = "abcabcacab";
        kmp.kmp();
        VisualAssist.print(kmp.fail);
    }
}

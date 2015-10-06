package com.victor.utilities.utils;

import com.victor.utilities.visual.VisualAssist;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * unit test for DateHelper
 */
public class IoHelperTest {

    @Test
    public void toJsonTest() throws IOException {
        TestA a = new TestA();
        TestA b = new TestA();
        TestA c = new TestA();
        c.setList(null);
        c.setSet(null);
        c.setMap(null);
        a.getList().add(b);
        a.getList().add(c);
        a.getSet().add(b);
        a.getSet().add(c);
        a.getMap().put("b", b);
        a.getMap().put("c", c);
        VisualAssist.print(IoHelper.toJson(a));
        IoHelper.toJsonFileWithIndent(a, "D:\\test.json");
    }

    public static class TestA {
        private String x1 = "x1";
        private double x2 = 2;
        private double[] x3 = new double[3];
        private List<TestA> list = new ArrayList<>();
        private Set<TestA> set = new HashSet<>();
        private Map<String, TestA> map = new HashMap<>();

        public TestA() {
        }

        public String getX1() {
            return x1;
        }

        public void setX1(String x1) {
            this.x1 = x1;
        }

        public double getX2() {
            return x2;
        }

        public void setX2(double x2) {
            this.x2 = x2;
        }

        public double[] getX3() {
            return x3;
        }

        public void setX3(double[] x3) {
            this.x3 = x3;
        }

        public List<TestA> getList() {
            return list;
        }

        public void setList(List<TestA> list) {
            this.list = list;
        }

        public Set<TestA> getSet() {
            return set;
        }

        public void setSet(Set<TestA> set) {
            this.set = set;
        }

        public Map<String, TestA> getMap() {
            return map;
        }

        public void setMap(Map<String, TestA> map) {
            this.map = map;
        }
    }
}

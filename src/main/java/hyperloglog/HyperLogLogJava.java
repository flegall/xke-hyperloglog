package hyperloglog;

import java.util.ArrayList;
import java.util.List;

import scala.NotImplementedError;

public class HyperLogLogJava {
    private final int n;
    private int count = 0;
    private List<Integer> registers = new ArrayList<>();

    private double biasCorrectionForHyperLogLog;
    private double biasCorrectionForLogLog;


    public HyperLogLogJava(int registersBit) {
        throw new NotImplementedError();
    }

    public int getN() {
        return n;
    }

    public int getCount() {
        return count;
    }

    public List<Integer> getRegisters() {
        return registers;
    }

    public double getBiasCorrectionForHyperLogLog() {
        return biasCorrectionForHyperLogLog;
    }

    public double getBiasCorrectionForLogLog() {
        return biasCorrectionForLogLog;
    }

    public void addHash(long hashcode) {
        throw new NotImplementedError();
    }

    public double getLogLogCount() {
        throw new NotImplementedError();
    }

    public double getHyperLogLogCount() {
        throw new NotImplementedError();
    }

    static int computeFirstOneRank(long bucketHash) {
        throw new NotImplementedError();
    }

    static int computeRegisterIndex(long hash, int n) {
        throw new NotImplementedError();
    }

   static double linearMean(List<Integer> registers) {
       throw new NotImplementedError();
   }
}
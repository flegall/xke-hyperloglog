package hyperloglog;

import java.util.ArrayList;
import java.util.List;
import com.google.common.base.Function;

import static com.google.common.collect.Lists.transform;
import static java.lang.Math.max;
import static java.lang.Math.pow;

public class HyperLogLogJava {
    private final int n;
    private int count = 0;
    private List<Integer> registers = new ArrayList<>();

    private double biasCorrectionForHyperLogLog;
    private double biasCorrectionForLogLog;


    public HyperLogLogJava(int registersBit) {
        this.n = 1 << registersBit;
        for (int i = 0; i < n; i++) {
            registers.add(0);
        }
        biasCorrectionForHyperLogLog = 1.0 / (2.0 * Math.log(2) * (1.0 + (3.0 * Math.log(2) - 1) / (double) n));
        biasCorrectionForLogLog = 0.395;
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
        int registerIndex = computeRegisterIndex(hashcode, n);
        int firstOneRank = computeFirstOneRank(hashcode);

        registers.set(registerIndex, max(firstOneRank, registers.get(registerIndex)));

        count += 1;
    }

    public double getLogLogCount() {
        return pow(2.0, linearMean(registers)) * n * biasCorrectionForLogLog;
    }

    public double getHyperLogLogCount() {
        double nDouble = n;
        List<Double> inverses = transform(registers, new Function<Integer, Double>() {
            @Override
            public Double apply(Integer value) {
                return 1.0 / pow(2.0, value);
            }
        });
        double sumOfInverses = 0;
        for (Double inverse : inverses) {
            sumOfInverses += inverse;
        }
        return nDouble * nDouble * biasCorrectionForHyperLogLog / sumOfInverses;
    }

    static int computeFirstOneRank(long bucketHash) {
        return Long.numberOfLeadingZeros(bucketHash) + 1;
    }

    static int computeRegisterIndex(long hash, int n) {
        return (int) hash & (n - 1);
    }

   static double linearMean(List<Integer> registers) {
       double result = 0;
       for (Integer register : registers) {
           result += register.doubleValue();
       }
       return result / (double) registers.size();
   }
}
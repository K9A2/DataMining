package com.stormlin.kmeans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * 用于聚类分析的 K-Means 算法。
 *
 * @author: stormlin
 * @project: DataMining
 * @package: com.stormlin.kmeans
 * @DataTime: 2017/6/5 22:15
 */
public class KMeans {

    public static void KMeans(int classCount) {

        //输入数据
        double[][] input = {
                {50, 50, 9},
                {28, 9, 4},
                {17, 15, 3},
                {25, 40, 5},
                {28, 40, 2},
                {50, 50, 1},
                {50, 40, 9},
                {50, 40, 9},
                {40, 40, 5},
                {50, 50, 9},
                {50, 50, 5},
                {50, 50, 9},
                {40, 40, 9},
                {40, 32, 17},
                {50, 50, 9}
        };
        //基准样本点
        double[] A = {0.3, 0, 0.19};
        double[] B = {0.7, 0.76, 0.5};
        double[] C = {1, 1, 0.5};

        int[] rands = getUnrepeatedRandomNumbers(1, 17, 10);

        //规格化
        //getInputNormalized(input);

        //重复计算直至收敛

    }

    /**
     * 计算 [min, max] 范围内不重复的 n 个随机数
     *
     * @param min   范围最小值
     * @param max   范围最大值
     * @param count 所需随机数个数
     * @return 不重复的随机数数组
     */
    private static int[] getUnrepeatedRandomNumbers(int min, int max, int count) {

        int[] result = new int[count];
        int i = 0;
        HashSet<Integer> temp = new HashSet<>();

        while (true) {
            if (temp.size() == 3) {
                break;
            }
            temp.add((int) (Math.random() * (max - min)) + min);
        }

        for (Integer item : temp) {
            result[i] = item;
        }

        return result;

    }

    /**
     * 计算 a，b 之间的欧几里得距离
     *
     * @param a 第一个数
     * @param b 第二个数
     * @return 两数之间的欧几里得距离
     */
    private static double getEuclidDistance(double[] a, double[] b) {
        if (a.length != b.length) {
            return -1;
        }
        double result = 0;

        for (int i = 0; i < a.length; i++) {
            result += Math.pow(a[i] - b[i], 2);
        }

        return Math.sqrt(result);
    }

    /**
     * 按列规格化输入数组
     *
     * @param input 未规格化的数组
     * @return 规格化结果
     */
    private static void getInputNormalized(double[][] input) {
        double[] max = new double[3];
        double[] min = new double[3];
        double tempMax = 0;
        double tempMin = 0;

        //按列计算最大值和最小值
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input.length; j++) {
                if (input[i][j] >= tempMax) {
                    tempMax = input[i][j];
                }
                if (input[i][j] <= tempMin) {
                    tempMin = input[i][j];
                }
            }
            max[i] = tempMax;
            min[i] = tempMin;
        }

        for (double[] line : input) {
            for (int i = 0; i < line.length; i++) {
                line[i] = (line[i] - min[i]) / (max[i] - min[i]);
            }
        }

    }

}

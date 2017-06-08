package com.stormlin.kmeans;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 用于聚类分析的 K-Means 算法。
 *
 * @author: stormlin
 * @project: DataMining
 * @package: com.stormlin.kmeans
 * @DataTime: 2017/6/5 22:15
 */
public class KMeans {

    /**
     * 在输入的三维点集 points 中按照 K 类进行聚类分析
     *
     * @param points 输入点集
     * @param K      分类数目
     */
    public static List<List<Point>> Analyze(List<Point> points, int K) {

        List<List<Point>> result = new ArrayList<>(K);
        for (int k = 0; k < K; k++) {
            result.add(new ArrayList<>());
        }

        //随机选择基准样本点
        int[] rands = getUnrepeatedRandomNumbers(0, points.size(), K);
        Point[] randomClusterCenter = new Point[K];
        for (int i = 0; i < rands.length; i++) {
            randomClusterCenter[i] = points.get(rands[i]);
        }

        //新旧两个质心数组
        Point[] oldClusterCenter;
        Point[] newClusterCenter;

        oldClusterCenter = getArrayCopy(randomClusterCenter);
        newClusterCenter = getArrayCopy(randomClusterCenter);

        //两点之间的欧几里得距离
        double[][] distance = new double[points.size()][K];
        //计算每一行与 K 个不同质心之间的欧几里得距离，重复计算直至收敛
        while (true) {
            //计算所有点对 K 个不同质心之间的欧几里得距离，并分到欧几里得距离最小的一类中
            for (int i = 0; i < points.size(); i++) {
                //计算
                for (int k = 0; k < K; k++) {
                    distance[i][k] = getEuclidDistance(points.get(i), oldClusterCenter[k]);
                }
                double min = distance[i][0];
                //分类
                for (int k = 0; k < K; k++) {
                    if (distance[i][k] < min) {
                        min = distance[i][k];
                        points.get(i).setClassID(k);
                    }
                }
                //把每一类的点加入到各自的分类中
                result.get(points.get(i).getClassID()).add(points.get(i));
            }
            dumpToFile(result);
            //重新计算每一类的质心
            for (int k = 0; k < K; k++) {
                newClusterCenter[k] = getClusterCenter(result.get(k));
            }
            //质心不再移动则退出
            if (!isClusterCenterChanged(oldClusterCenter, newClusterCenter)) {
                break;
            }
            for (int k = 0; k < K; k++) {
                result.set(k, new ArrayList<>());
            }
            oldClusterCenter = getArrayCopy(newClusterCenter);
        }

        return result;
    }

    /**
     * 判断质心是否移动
     *
     * @param a 质心数组 a
     * @param b 质心数组 b
     * @return 如果移动，则返回 true；否则返回 false
     */
    private static boolean isClusterCenterChanged(Point[] a, Point[] b) {

        for (int i = 0; i < a.length; i++) {
            if (a[i].getX() != b[i].getX()) {
                return true;
            } else if (a[i].getY() != b[i].getY()) {
                return true;
            } else if (a[i].getZ() != b[i].getZ()) {
                return true;
            }
        }

        return false;

    }

    /**
     * 计算三维点集 points 中的质心
     *
     * @param points 需要计算质心的三维点集
     * @return 质心
     */
    private static Point getClusterCenter(List<Point> points) {
        if (points.size() == 0) {
            return null;
        }
        if (points.size() == 1) {
            return new Point(points.get(0).getX(), points.get(0).getY(), points.get(0).getZ());
        }

        double x = 0;
        double y = 0;
        double z = 0;

        for (Point point : points) {
            x += point.getX();
            y += point.getY();
            z += point.getZ();
        }
        x = x / points.size();
        y = y / points.size();
        z = z / points.size();

        return new Point(x, y, z);
    }

    /**
     * 获得数组 b 的拷贝
     *
     * @param b 数组 b
     * @return 数组 b 的拷贝
     */
    @Nullable
    private static Point[] getArrayCopy(Point[] b) {

        Point[] a = new Point[b.length];

        if (a.length == 0 || b.length == 0) {
            return null;
        }

        System.arraycopy(b, 0, a, 0, a.length);

        return a;
    }

    /**
     * 计算 [min, max) 范围内不重复的 n 个随机数
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
            if (temp.size() == count) {
                break;
            }
            temp.add((int) (Math.random() * (max - min)) + min);
        }

        for (Integer item : temp) {
            result[i] = item;
            i++;
        }

        return result;

    }

    /**
     * 计算 a，b 两点之间的欧几里得距离
     *
     * @param a 第一个数
     * @param b 第二个数
     * @return 两数之间的欧几里得距离
     */
    private static double getEuclidDistance(Point a, Point b) {

        double result = 0;

        result += Math.pow(a.getX() - b.getX(), 2);
        result += Math.pow(a.getY() - b.getY(), 2);
        result += Math.pow(a.getZ() - b.getZ(), 2);

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

    /**
     * 把计算结果输出到文件中
     *
     * @param result 计算结果
     */
    private static void dumpToFile(List<List<Point>> result) {

        String outputFilePath = System.getProperty("user.dir") + "\\test\\output.txt";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, true));
            for (List<Point> line : result) {
                for (Point item : line) {
                    writer.write(item.getName() + ",");
                }
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

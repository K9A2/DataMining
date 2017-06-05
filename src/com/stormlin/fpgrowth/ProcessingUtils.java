/*
Created by stormlin on 2017/6/5. All rights reserved.
 */

package com.stormlin.fpgrowth;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

/**
 * 实用工具类，包含了所需的工具方法
 */
public class ProcessingUtils {

    /**
     * 打印当前系统时间
     */
    public static void printCurrentTime() {
        System.out.println(new Date());
    }

    /**
     * 预处理方法。负责把提供原数据的 csv 文件处理成这个 FP-Growth 算法可以处理的格式。
     *
     * @param csvFilePath    数据源 csv 文件路径
     * @param inputSeparator csv 文件中的分隔符
     * @return 可用于 FP-Growth 算法的输入结果
     */
    @Nullable
    public static List<List<String>> preProcess(String csvFilePath, String inputSeparator, char targetClass) {

        List<List<String>> csvInput = new ArrayList<>();

        //读取 CSV 文件，构建原始输入
        File csvFile = new File(csvFilePath);

        if (!csvFile.exists() || !csvFile.isFile()) {
            System.out.println("无法读取指定文件，程序退出");
            return null;
        }

        //按文本文件的方式读取 CSV 文件
        System.out.println("正在读取 CSV 文件");
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            while ((line = reader.readLine()) != null) {
                csvInput.add(Arrays.asList(line.split(inputSeparator)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //去除 CSV 文件中的标题行
        csvInput.remove(0);

        //筛选出符合类别要求的记录项
        List<List<String>> result = new ArrayList<>();
        for (List<String> aCsvInput : csvInput) {
            if ((aCsvInput.size() == 3) && aCsvInput.get(2).charAt(0) == targetClass) {
                result.add(aCsvInput);
            }
        }

        List<String> merged = new ArrayList<>();
        List<List<String>> FPInput = new ArrayList<>();
        int i;
        int j;
        int inputItemCount = result.size();

        System.out.println("CSV 文件读取完毕，开始预处理过程");

        //读取数据并合并同一次结束记录的连续几行记录
        for (i = 0; i < inputItemCount; i++) {
            merged.add(result.get(i).get(1));
            for (j = i; j < inputItemCount - 1; j++) {
                if (Objects.equals(result.get(j).get(0), result.get(j + 1).get(0))) {
                    merged.add(result.get(j + 1).get(1));
                } else {
                    break;
                }
            }
            i = j;
            if (merged.size() == 1) {
                merged.clear();
                continue;
            }
            Collections.sort(merged);
            //note: add() 是否是引用式的添加，还是复制式的添加？
            FPInput.add(new ArrayList<>(merged));
            merged.clear();
        }

        return FPInput;

    }

    /**
     * 再处理方法。负责把 FP-Growth 算法输出的数据重整为人类可以阅读的频繁项集结果序列。
     *
     * @param dictionaryFilePath 字典文件路径
     * @param fpSeparator        FP-Growth 输出中的分隔符
     * @param fpOutput           FP-Growth 的输出结果
     * @return 再处理结果，可以直接输出
     */
    @Nullable
    public static List<List<String>> reProcess(String dictionaryFilePath, String fpSeparator, List<List<String>> fpOutput) {

        List<List<String>> result = new ArrayList<>();

        //制作字典
        Dictionary<String, String> dictionary = new Hashtable<>();
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(dictionaryFilePath));
            while ((line = reader.readLine()) != null) {
                //fixme: dictionary 中还没能添加 b3589980 的键值对
                List<String> dictionaryLine = Arrays.asList(line.split(fpSeparator));
                if (dictionaryLine.size() == 2) {
                    dictionary.put(dictionaryLine.get(0), dictionaryLine.get(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        //以文件流的形式读取 CSV 文件时，会把标题行读出来，所以需要去除
        dictionary.remove("图书记录号（种）");

        //合并与去重
        getResultFiltered(result, fpOutput);

        //替换
        for (int i = 0; i < result.size(); i++) {
            List<String> line = result.get(i);
            for (int j = 0; j < line.size(); j++) {
                line.set(j, dictionary.get(line.get(j)));
            }
            result.set(i, line);
        }

        writeResultToFile(new File("E:\\replace.txt"), result);

        return result;
    }

    /**
     * 对 FP-Growth 输出结果进行合并与去重
     *
     * @param result        空输出结果集
     * @param removedPrefix 逆转后的
     */
    private static void getResultFiltered(List<List<String>> result, List<List<String>> removedPrefix) {

        //合并与去重
        HashSet<String> temp = new HashSet<>();

        int elementLeft = removedPrefix.size();

        //任意两行之间如果有交集，就合并他们
        while (removedPrefix.size() != 0) {
            temp.addAll(removedPrefix.get(0));
            for (int j = 1; j < elementLeft - 1; j++) {
                if (isIntersected(temp, removedPrefix.get(j))) {
                    temp.addAll(removedPrefix.get(j));
                    removedPrefix.remove(j);
                    elementLeft--;
                    j--;
                }
            }
            result.add(new ArrayList<>(temp));
            temp.clear();
            elementLeft--;
            removedPrefix.remove(0);
        }

    }

    /**
     * 判定两个集合是否有交集。有交集返回 true；没有交集返回 false。
     *
     * @param a 集合 A
     * @param b 集合 B
     * @return 结果
     */
    private static boolean isIntersected(HashSet<String> a, List<String> b) {

        for (String anA : a) {
            for (String aB : b) {
                if (anA.equals(aB)) {
                    return true;
                }
            }
        }
        return false;

    }

    /**
     * 把计算结果输出到指定的文件中
     *
     * @param outputFile 指定的输出文件
     * @param result     计算结果
     */
    private static void writeResultToFile(File outputFile, List<List<String>> result) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            for (List<String> line : result) {
                for (String item : line) {
                    writer.write(item + ",");
                }
                writer.newLine();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把最终的计算结果输出到文件中
     *
     * @param outputFilePath 输出文件路径
     * @param result         计算结果
     */
    public static void getOutput(String outputFilePath, List<List<String>> result) {
        File outputFile = new File(outputFilePath);
        if (!outputFile.exists()) {
            try {
                if (!outputFile.createNewFile()) {
                    System.out.println("无法输出计算结果，程序退出");
                    System.exit(-1);
                }
                System.out.println("创建输出文件：" + outputFile.getPath());
                writeResultToFile(outputFile, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (!outputFile.delete()) {
                    System.out.println("无法输出计算结果，程序退出");
                    System.exit(-1);
                }
                System.out.println("目标文件已存在，将删除源文件，并创建新的同名文件：" + outputFile.getPath());
                if (!outputFile.createNewFile()) {
                    System.out.println("无法输出计算结果，程序退出");
                    System.exit(-1);
                }
                writeResultToFile(outputFile, result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

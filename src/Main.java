import com.stormlin.fpgrowth.FPGrowth;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

/**
 * @Author stormlin
 * @DATE 2017/5/11
 * @TIME 0:12
 * @PROJECT DataMining
 * @PACKAGE PACKAGE_NAME
 */

public class Main {

    public static void main(String[] args) {

        //当前路径
        String workingDictionaryPath = System.getProperty("user.dir");

        //默认 CSV 文件输入位置
        String csvFilePath = workingDictionaryPath + "\\test\\test.csv";

        //默认书名字典位置
        String dictionaryFilePath = workingDictionaryPath + "\\test\\dictionary.csv";

        //输出文件路径
        String outputFilePath = workingDictionaryPath + "\\test\\output.txt";

        //分隔符
        String inputSeparator = ",";

        //FP-Growth 输出分隔符
        String fpSeparator = " ";

        //频繁项中间结果
        List<StringBuilder> fpOutput = new ArrayList<>();

        //CSV 输入结果
        List<List<String>> fpInput;

        //最终结果
        List<List<String>> result;

        printCurrentTime();

        System.out.println(System.getProperty("user.dir"));

        /*
        预处理过程
         */
        fpInput = preProcess(csvFilePath, inputSeparator);

        /*
        FP-Growth 算法处理
         */
        FPGrowth fpGrowth = new FPGrowth(3);
        fpGrowth.getFPOutput(fpInput, null, fpOutput);

        /*
        再处理过程
         */
        if ((result = reProcess(dictionaryFilePath, fpSeparator, fpOutput)) == null) {
            System.out.println("无法进行再处理过程，程序退出。");
            System.exit(-1);
        }

        System.out.println("处理完成，开始输出计算结果");
        /*
        结果输出
         */
        getOutput(outputFilePath, result);

        System.out.println("输出完成，程序结束");

        printCurrentTime();

    }

    /**
     * 打印当前系统时间
     */
    private static void printCurrentTime() {
        System.out.println(new Date());
    }

    /**
     * 预处理方法。负责把提供原数据的 csv 文件处理成这个 FP-Growth 算法可以处理的格式。
     *
     * @param csvFilePath    数据源 csv 文件路径
     * @param inputSeparator csv 文件中的分隔符
     * @return 可用于 FP-Growth 算法的输入结果
     */
    private static List<List<String>> preProcess(String csvFilePath, String inputSeparator) {

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

        //去除只有一项的行，然后合并属于同一次借书操作的连续几行记录
        List<String> merged = new ArrayList<>();
        List<List<String>> FPInput = new ArrayList<>();
        int i;
        int j;
        int inputItemCount = csvInput.size();

        System.out.println("CSV 文件读取完毕，开始预处理过程");

        //由于以二进制方式读取的数据会含有标题行，故下标从 1 开始
        for (i = 1; i < inputItemCount; i++) {
            merged.add(csvInput.get(i).get(1));
            for (j = i; j < inputItemCount - 1; j++) {
                if (Objects.equals(csvInput.get(j).get(0), csvInput.get(j + 1).get(0))) {
                    merged.add(csvInput.get(j + 1).get(1));
                } else {
                    break;
                }
            }
            i = j;
            if (merged.size() == 1) {
                merged.clear();
            }
            Collections.sort(merged);
            //note: add() 是否是引用式的添加，还是复制式的添加？
            FPInput.add(new ArrayList<>(merged));
            merged.clear();
        }

        return FPInput;

    }

    /**
     * 再处理方法。负责把 FP-Growth 算法输出的数据重整为人类可以阅读的频繁项集。
     *
     * @param dictionaryFilePath 字典文件路径
     * @param fpSeparator        FP-Growth 输出中的分隔符
     * @param fpOutput           FP-Growth 的输出结果
     * @return 再处理结果，可以直接输出
     */
    @Nullable
    private static List<List<String>> reProcess(String dictionaryFilePath, String fpSeparator, List<StringBuilder> fpOutput) {

        List<List<String>> result = new ArrayList<>();

        //去除前缀后的结果
        List<List<String>> removedPrefix = new ArrayList<>();

        //去除前缀
        for (StringBuilder line : fpOutput) {
            List<String> lineList = Arrays.asList(line.toString().substring(line.toString().indexOf("b")).split(fpSeparator));
            removedPrefix.add(lineList);
        }

        //合并与去重
        getResultFiltered(result, removedPrefix);

        //制作字典
        Dictionary<String, String> dictionary = new Hashtable<>();

        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(dictionaryFilePath));
            while ((line = reader.readLine()) != null) {
                List<String> dictionaryLine = Arrays.asList(line.split(","));
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

        //替换
        for (List<String> line : result) {
            for (int i = 0; i < line.size(); i++) {
                line.set(i, dictionary.get(line.get(i)));
            }
        }

        return result;
    }

    /**
     * 对 FP-Growth 输出结果进行合并与去重
     *
     * @param result  空输出结果集
     * @param removedPrefix 逆转后的
     * @return 处理结果
     */
    private static List<List<String>> getResultFiltered(List<List<String>> result, List<List<String>> removedPrefix) {

        //合并与去重
        List<String> row = new ArrayList<>();
        HashSet<String> hashSet;

        //两个行之间如果有交集，就合并他们
        for (int i = 0; i < removedPrefix.size(); i++) {
            row.addAll(removedPrefix.get(i));
            for (int j = i + 1; j < removedPrefix.size(); j++) {
                if (isIntersected(row, removedPrefix.get(j))) {
                    row.addAll(removedPrefix.get(j));
                } else {
                    i = j - 1;
                    hashSet = new HashSet<>(row);
                    result.add(new ArrayList<>(hashSet));
                    row.clear();
                    hashSet.clear();
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 判定两个集合是否有交集。有交集返回 true；没有交集返回 false。
     * @param a 集合 A
     * @param b 集合 B
     * @return 结果
     */
    private static boolean isIntersected(List<String> a, List<String> b) {

        List<String> A = new ArrayList<>(a);
        List<String> B = new ArrayList<>(b);

        return A.retainAll(B);

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
    private static void getOutput(String outputFilePath, List<List<String>> result) {
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

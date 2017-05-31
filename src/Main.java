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

    //todo: 改写为 GUI 程序
    //todo: 在每一步添加时间戳
    public static void main(String[] args) {

        //默认文本输入文件位置
        String txtFilePath = "D:\\test.txt";

        //默认 CSV 文件输入位置
        String csvFilePath = "D:\\test.csv";

        //默认书名字典位置
        String dictionaryFilePath = "D:\\dictionary.csv";

        //数据在 Excel 文件中的第几个 sheet，从 0 开始
        int sheetIndex = 0;

        //分隔符
        String inputSeparator = ",";

        //FP-Growth 输出分隔符
        String fpSeperator = " ";

        //频繁项中间结果
        List<StringBuilder> fpOutput = new ArrayList<>();

        //CSV 输入结果
        List<List<String>> fpInput;

        //最终结果
        List<List<String>> result = null;

        /*
        预处理过程
         */
        fpInput = preProcess(csvFilePath, inputSeparator);

        /*
        FP-Growth 算法处理
         */
        FPGrowth fpGrowth = new FPGrowth(4);
        //List<List<String>> transactions = fpGrowth.loadTransaction("D:\\preProcess.txt");
        fpGrowth.FPGrowth(fpInput, null, fpOutput);
        //fpGrowth.FPGrowth(fpInput, null, fpOutput);

        for (StringBuilder line : fpOutput) {
            System.out.println(line.toString());
        }

        /*
        String fpFilePath = "D:\\fpOutput.txt";
        File fp = new File(fpFilePath);
        System.out.println("处理完成，开始输出结果");
        if (fpOutput.size() != 0) {
            try {
                fp.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(fp));
                for (StringBuilder line : fpOutput) {
                    writer.write(line.toString() + "\n");
                }
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Found nothing.");
        }

        /*
        再处理过程
         */

//        try {
//            String line;
//            BufferedReader reader = new BufferedReader(new FileReader("D:\\temp.txt"));
//            while ((line = reader.readLine()) != null) {
//                fpOutput.add(new StringBuilder(line));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
/*
        result = reProcess(dictionaryFilePath, fpSeperator, fpOutput);

        //结果输出
        String outputFilePath = "D:\\output.txt";
        File outputFile = new File(outputFilePath);
        System.out.println("处理完成，开始输出结果");
        if (result.size() != 0) {
            try {
                outputFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
                for (List<String> line : result) {
                    for (String item : line) {
                        writer.write(item + "，");
                    }
                    writer.write("\n");
                }
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Found nothing.");
        }

        System.out.println("输出完成，程序结束");
*/
        //输出读取到的 CSV 文件数据
//        String outputFilePath = "D:\\output.txt";
//        File outputFile = new File(outputFilePath);
//        try {
//            outputFile.createNewFile();
//            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
//
//            for (List<String> line : reverse) {
//                for (String item :
//                        line) {
//                    writer.write(item + " ");
//                }
//                writer.write(line.toString() + "\n");
//            }
//
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 预处理方法。负责把提供原数据的 csv 文件处理成这个 FP-Growth 算法可以处理的格式。
     * @param csvFilePath 数据源 csv 文件路径
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
     * @param dictionaryFilePath 字典文件路径
     * @param fpSeperator   FP-Growth 输出中的分隔符
     * @param fpOutput FP-Growth 的输出结果
     * @return 再处理结果，可以直接输出
     */
    private static List<List<String>> reProcess(String dictionaryFilePath, String fpSeperator, List<StringBuilder> fpOutput) {

        List<List<String>> result = new ArrayList<>();

        //去除前缀以及逆序后的结果
        List<List<String>> reverse = new ArrayList<>();

        for (StringBuilder line : fpOutput) {
            List<String> lineList = Arrays.asList(line.toString().substring(line.toString().indexOf("b")).split(fpSeperator));
            //Collections.sort(lineList);
            Collections.reverse(lineList);
            reverse.add(lineList);
        }

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


        //合并与去重
        List<String> row = new ArrayList<>();
        HashSet<String> hashSet = null;

        for (int i = 0; i < reverse.size(); i++) {
            row.addAll(reverse.get(i));
            for (int j = i + 1; j < reverse.size(); j++) {
                if (Objects.equals(reverse.get(i).get(0), reverse.get(j).get(0))) {
                    row.addAll(reverse.get(j));
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

        //替换
        for (List<String> line : result) {
            for (int i = 0; i < line.size(); i++) {
                line.set(i, dictionary.get(line.get(i)));
            }
        }


//        List<List<String>> temp = new ArrayList<>();
//
//        row.clear();
//        if (hashSet != null) {
//            hashSet.clear();
//        }
//
//        for (int i = 0; i < result.size(); i++) {
//            row.addAll(result.get(i));
//            for (int j = i + 1; j < result.size(); j++) {
//                if (Objects.equals(result.get(i).get(0), result.get(j).get(0))) {
//                    row.addAll(result.get(j));
//                } else {
//                    i = j - 1;
//                    hashSet = new HashSet<>(row);
//                    temp.add(new ArrayList<>(hashSet));
//                    row.clear();
//                    hashSet.clear();
//                    break;
//                }
//            }
//        }

        return result;
    }

}

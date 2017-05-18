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
        String separator = ",";

        //频繁项中间结果
        List<StringBuilder> result = new ArrayList<>();

        //Excel 输入结果
        List<List<String>> csvInput = new ArrayList<>();

        /*
        预处理
         */
        //读取 CSV 文件，构建原始输入
        File csvFile = new File(csvFilePath);

        if (!csvFile.exists() || !csvFile.isFile()) {
            System.out.println("无法读取指定文件，程序退出");
            return;
        }

        //按文本文件的方式读取 CSV 文件
        System.out.println("正在读取 CSV 文件");
        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(csvFile));
            while ((line = reader.readLine()) != null) {
                csvInput.add(Arrays.asList(line.split(separator)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
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
            } else {
                Collections.sort(merged);
                //note: add() 是否是引用式的添加，还是复制式的添加？
                FPInput.add(new ArrayList<>(merged));
                merged.clear();
            }
        }

        System.out.println("预处理结束，开始 FP-Growth 过程");

//        String outputFilePath = "D:\\output.txt";
//        File outputFile = new File(outputFilePath);
//        try {
//            outputFile.createNewFile();
//            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
//
//            for (List<String> row : FPInput) {
//                for (String item : row) {
//                    writer.write(item + " ");
//                }
//                writer.write("\n");
//            }
//
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



        /*
        FP-Growth 算法处理
         */
        FPGrowth fpGrowth = new FPGrowth(4);
        List<List<String>> transactions = fpGrowth.loadTransactions("D:\\test.txt", " ");
        fpGrowth.FPGrowth(transactions, null, result);
        //fpGrowth.FPGrowth(FPInput, null, result);

        /*
        再处理
         */
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
            return;
        }
        //以文件流的形式读取 CSV 文件时，会把标题行读出来，所以需要去除
        dictionary.remove("图书记录号（种）");

        //去除前缀以及逆序后的结果
        List<List<String>> reverse = new ArrayList<>();



        //结果输出
        System.out.println("处理完成，开始输出结果");
        if (result.size() != 0) {
            for (StringBuilder row :
                    result) {
                System.out.println(row.toString());
            }
        } else {
            System.out.println("Found nothing.");
        }

        System.out.println("输出完成，程序结束");

    }

}

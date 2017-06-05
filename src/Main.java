import com.stormlin.fpgrowth.FPGrowth;

import java.util.ArrayList;
import java.util.List;

import static com.stormlin.fpgrowth.ProcessingUtils.*;

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
        String fpSeparator = ",";

        //频繁项中间结果
        List<List<String>> fpOutput = new ArrayList<>();

        //CSV 输入结果
        List<List<String>> fpInput;

        //最终结果
        List<List<String>> result;

        //需要计算的分类
        char requiredClass = 'B';

        printCurrentTime();

        System.out.println(System.getProperty("user.dir"));

        /*
        预处理过程
         */
        fpInput = preProcess(csvFilePath, inputSeparator, requiredClass);

        /*
        FP-Growth 算法处理
         */
        FPGrowth fpGrowth = new FPGrowth(2);
        fpGrowth.getFPOutput(fpInput, null, fpOutput);

        System.out.println("开始再处理");

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

}

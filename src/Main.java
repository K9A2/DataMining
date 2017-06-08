import com.stormlin.fpgrowth.FPGrowth;
import com.stormlin.kmeans.Point;

import java.util.ArrayList;
import java.util.List;

import static com.stormlin.fpgrowth.ProcessingUtils.*;
import static com.stormlin.kmeans.KMeans.Analyze;

/**
 * @Author stormlin
 * @DATE 2017/5/11
 * @TIME 0:12
 * @PROJECT DataMining
 * @PACKAGE PACKAGE_NAME
 */

public class Main {

    public static void main(String[] args) {

        //kmeans();
        fpgorwth();

    }

    /**
     * 数据挖掘算法 K-Means 部分
     */
    private static void kmeans() {
        //输入数据
        double[][] input = {
                {0.29, 1, 1},
                {0.29, 0.18, 0.58},
                {0.12, 0.3, 0.54},
                {1, 1, 1},
                {0.06, 0.42, 0.6},
                {0.53, 0.64, 0.8},
                {0.24, 1, 0.8},
                {1, 1, 1},
                {0.29, 0.8, 0.56},
                {0.53, 0.8, 1},
                {0.18, 0.8, 1},
                {0.53, 0.8, 0.8},
                {0.29, 0.8, 0.8},
                {0.53, 0.8, 1},
                {0.53, 1, 0.8},
                {0.53, 1, 0.8}
        };

        String[] names = {"中国", "日本", "韩国", "印尼", "澳大利亚", "朝鲜", "伊拉克", "泰国", "伊朗", "沙特", "阿联酋"
                , "卡塔尔", "乌兹别克斯坦", "巴林", "阿曼", "约旦"};

        List<Point> points = new ArrayList<>(input.length);

        for (int i = 0; i < input.length; i++) {
            Point newPoint = new Point(input[i][0], input[i][1], input[i][2]);
            newPoint.setName(names[i]);
            newPoint.setClassID(0);
            points.add(newPoint);
        }

        List<List<Point>> result = Analyze(points, 3);
        for (List<Point> aResult : result) {
            for (Point anAResult : aResult) {
                System.out.print(anAResult.getName() + " ");
            }
            System.out.println();
        }
    }

    /**
     * 数据挖掘算法 FP-Growth 部分
     */
    private static void fpgorwth() {
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

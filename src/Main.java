import javax.swing.*;
import java.io.*;
import java.lang.reflect.Array;
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

        //默认文本输入文件位置
        String txtFilePath = "D:\\test.txt";

        //默认 CSV 文件输入位置
        String csvFilePath = "D:\\test.csv";

        //默认书名字典位置
        String dictionaryFilePath = "D:\\dictionary.xlsx";

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

//        for (List<String> row :
//                FPInput) {
//            for (String item :
//                    row) {
//                System.out.print(item + " ");
//            }
//            System.out.println();
//        }

        String outputFilePath = "D:\\output.txt";
        File outputFile = new File(outputFilePath);
        try {
            outputFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            for (List<String> row : FPInput) {
                for (String item : row) {
                    writer.write(item + " ");
                }
                writer.write("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        /*
        FP-Growth 算法处理
         */
        FPGrowth fpGrowth = new FPGrowth(2);
        List<List<String>> transactions = fpGrowth.loadTransactions("D:\\test.txt", " ");
        fpGrowth.FPGrowth(transactions, null, result);
        //fpGrowth.FPGrowth(FPInput, null, result);

        /*
        再处理
         */

        System.out.println("处理完成，开始输出结果");

        //结果输出
        if (result.size() != 0) {
            for (StringBuilder row :
                    result) {
                System.out.println(row.toString());
            }
        } else {
            System.out.println("Found nothing.");
        }

        System.out.println("输出完成，程序结束");

//        // 创建 JFrame 实例
//        JFrame frame = new JFrame("Login Example");
//        // Setting the width and height of frame
//        frame.setSize(350, 200);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        /* 创建面板，这个类似于 HTML 的 div 标签
//         * 我们可以创建多个面板并在 JFrame 中指定位置
//         * 面板中我们可以添加文本字段，按钮及其他组件。
//         */
//        JPanel panel = new JPanel();
//        // 添加面板
//        frame.add(panel);
//        /*
//         * 调用用户定义的方法并添加组件到面板
//         */
//        placeComponents(panel);
//
//        // 设置界面可见
//        frame.setVisible(true);

//        //todo: 改写为 GUI 窗口程序
//        JFileChooser fd = new JFileChooser();
////fd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        fd.showDialog(new JLabel(), "选择文件");
//        File f = fd.getSelectedFile();
//        if(f != null){}

    }

    private static void placeComponents(JPanel panel) {

        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建 JLabel
        JLabel userLabel = new JLabel("User:");
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        /*
         * 创建文本域用于用户输入
         */
        JTextField userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        // 输入密码的文本域
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        /*
         *这个类似用于输入的文本域
         * 但是输入的信息会以点号代替，用于包含密码的安全性
         */
        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);

        // 创建登录按钮
        JButton loginButton = new JButton("login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);
    }

}

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author stormlin
 * @DATE 2017/5/11
 * @TIME 0:12
 * @PROJECT DataMining
 * @PACKAGE PACKAGE_NAME
 */

public class Main {

    public static void main(String[] args) {

        String txtFilePath = "D:\\test.txt";

        String separator = " ";

        List<StringBuilder> result = new ArrayList<>();

        FPGrowth fpGrowth = new FPGrowth(2);
        List<List<String>> transactions = fpGrowth.loadTransactions(txtFilePath, separator);
        fpGrowth.FPGrowth(transactions, null, result);

        //System.out.println();

        if (result.size() != 0) {
            for (StringBuilder row :
                    result) {
                System.out.println(row.toString());
            }
        } else {
            System.out.println("Found nothing.");
        }


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

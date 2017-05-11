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
        FPTree fptree = new FPTree(4);
        List<List<String>> transactions = fptree.loadTransaction("D:\\test.txt");
        fptree.FPGrowth(transactions, null);
    }

}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

class FPGrowth {

    //最小支持度
    private int minSupport;

    FPGrowth(int support) {
        this.minSupport = support;
    }

    /**
     * 加载事务集
     *
     * @param file      文件路径名
     * @param separator 各项的分隔符
     */
    List<List<String>> loadTransactions(String file, String separator) {
        //事务集
        List<List<String>> transactions = new ArrayList<>();

        //从事务集文件中按行读取数据，并按照约定的分隔符组成数组，以存储到事务集 List 中
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(file)));
            String line;
            while ((line = br.readLine()) != null) {
                transactions.add(Arrays.asList(line.split(separator)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactions;
    }

    /**
     * 构建头项表，按递减排好序
     *
     * @return 返回大于等于最小支持度的结点集合
     */
    private List<TNode> buildHeaderTable(List<List<String>> transactions) {
        //todo: 确认此方法是否每次执行的结果都是一样的，如果一样的就可以抽出来，去除重复计算
        //大于最小支持度的节点 List
        List<TNode> headers = new ArrayList<>();
        //存储节点名称和节点实体的键值对
        Map<String, TNode> nodeMap = new HashMap<>();

        //note: 不要用记事本保存文件，里面会含有可恶的 BOM
        //为所有在 transactions 中出现过的 item 都新增一个表项。
        for (List<String> line : transactions) {
            for (String itemName : line) {
                if (nodeMap.keySet().contains(itemName)) {
                    //如果此节点已经出现过，那么就给此节点的支持度加一
                    nodeMap.get(itemName).increaseCount(1);
                } else {
                    //如果还没有出现过这个 item，则在 map 中新增此节点
                    nodeMap.put(itemName, new TNode(itemName));
                }
            }
        }

        //筛选满足最小支持度的节点
        for (TNode item : nodeMap.values()) {
            if (item.getCount() >= minSupport) {
                headers.add(item);
            }
        }

        //按count值从高到低排序
        Collections.sort(headers);

        return headers;
    }

    /**
     * FP-Growth 核心算法
     *
     * @param transactions 事务集
     * @param postPattern  模式
     * @param result       结果
     */
    void FPGrowth(List<List<String>> transactions, List<String> postPattern, List<StringBuilder> result) {
        //构建头项表
        //todo: 确认是否每一次都要 build 一次 HeaderTable
        List<TNode> headerTable = buildHeaderTable(transactions);
        //构建FP树
        TNode tree = buildFPTree(headerTable, transactions);
        //当前行
        StringBuilder currentLine = new StringBuilder();
        //当树为空时退出
        if (tree.getChildren() == null || tree.getChildren().size() == 0) {
            return;
        }
        //输出频繁项集
        if (postPattern != null) {
            for (TNode head : headerTable) {
                //currentLine.delete(0, currentLine.length());
                System.out.print(head.getCount() + " " + head.getItemName());
                //currentLine.append(head.getCount()).append(" ").append(head.getItemName());
                for (String item : postPattern) {
                    System.out.print(" " + item);
                    //currentLine.append(" ").append(item);
                }
                System.out.println();
                //result.add(currentLine);
            }
        }
        //遍历每一个头项表节点
        for (TNode head : headerTable) {
            List<String> newPostPattern = new LinkedList<>();
            newPostPattern.add(head.getItemName());//添加本次模式基
            //加上将前面累积的前缀模式基
            if (postPattern != null) {
                newPostPattern.addAll(postPattern);
            }
            //定义新的事务数据库
            List<List<String>> newTransaction = new LinkedList<>();
            TNode nextnode = head.getNext();
            //去除名称为head.getItemName()的模式基，构造新的事务数据库
            while (nextnode != null) {
                int count = nextnode.getCount();
                List<String> parentNodes = new ArrayList<>();//nextnode节点的所有祖先节点
                TNode parent = nextnode.getParent();
                while (parent.getItemName() != null) {
                    parentNodes.add(parent.getItemName());
                    parent = parent.getParent();
                }
                //向事务数据库中重复添加count次parentNodes
                while ((count--) > 0) {
                    newTransaction.add(parentNodes);//添加模式基的前缀 ，因此最终的频繁项为:  parentNodes -> newPostPattern
                }
                //下一个同名节点
                nextnode = nextnode.getNext();
            }
            //每个头项表节点重复上述所有操作，递归
            FPGrowth(newTransaction, newPostPattern, result);
        }
    }

    /**
     * 构建FP-Tree
     *
     * @param headerTable 头项表
     * @return
     */
    private TNode buildFPTree(List<TNode> headerTable, List<List<String>> transactions) {
        //todo: 确认此方法的执行结果是否唯一
        //FP 树根节点
        TNode rootNode = new TNode();
        for (List<String> items : transactions) {
            LinkedList<String> itemsDesc = sortItemsByDesc(items, headerTable);
            //寻找添加itemsDesc为子树的父节点
            TNode subtreeRoot = rootNode;
            if (subtreeRoot.getChildren().size() != 0) {
                TNode tempNode = subtreeRoot.findChildren(itemsDesc.peek());
                while (!itemsDesc.isEmpty() && tempNode != null) {
                    tempNode.increaseCount(1);
                    subtreeRoot = tempNode;
                    itemsDesc.poll();
                    tempNode = subtreeRoot.findChildren(itemsDesc.peek());
                }
            }
            //将itemsDesc中剩余的节点加入作为subtreeRoot的子树
            addSubTree(headerTable, subtreeRoot, itemsDesc);
        }
        return rootNode;
    }

    /**
     * @param headertable 头项表
     * @param subtreeRoot 子树父节点
     * @param itemsDesc   被添加的子树
     */
    private void addSubTree(List<TNode> headertable, TNode subtreeRoot, LinkedList<String> itemsDesc) {
        if (itemsDesc.size() > 0) {
            TNode thisnode = new TNode(itemsDesc.pop());//构建新节点
            subtreeRoot.getChildren().add(thisnode);
            thisnode.setParent(subtreeRoot);
            //将thisnode加入头项表对应节点链表的末尾
            for (TNode node : headertable) {
                if (node.getItemName().equals(thisnode.getItemName())) {
                    TNode lastNode = node;
                    while (lastNode.getNext() != null) {
                        lastNode = lastNode.getNext();
                    }
                    lastNode.setNext(thisnode);
                }
            }
            subtreeRoot = thisnode;//更新父节点为当前节点
            //递归添加剩余的items
            addSubTree(headertable, subtreeRoot, itemsDesc);
        }
    }

    /**
     * 把 items 按照降序排列
     *
     * @param items 需要排序的输入
     * @param headerTable 每一项的列表
     * @return 已排序的结果
     */
    private LinkedList<String> sortItemsByDesc(List<String> items, List<TNode> headerTable) {
        //以降序排列的 items
        LinkedList<String> itemsDesc = new LinkedList<>();

        //
        for (TNode node : headerTable) {
            if (items.contains(node.getItemName())) {
                itemsDesc.add(node.getItemName());
            }
        }

        return itemsDesc;
    }

    /**
     * fp-tree节点的数据结构（一个item表示一个节点）
     *
     * @author shimin
     */
    public class TNode implements Comparable<TNode> {
        private String itemName; //项目名
        private int count; //事务数据库中出现次数
        private TNode parent; //父节点
        private List<TNode> children; //子节点
        private TNode next;//下一个同名节点

        public TNode() {
            this.children = new ArrayList<>();
        }

        public TNode(String name) {
            this.itemName = name;
            this.count = 1;
            this.children = new ArrayList<>();
        }

        public TNode findChildren(String childName) {
            for (TNode node : this.getChildren()) {
                if (node.getItemName().equals(childName)) {
                    return node;
                }
            }
            return null;
        }

        public TNode getNext() {
            return next;
        }

        public TNode getParent() {
            return parent;
        }

        public void setNext(TNode next) {
            this.next = next;
        }

        public void increaseCount(int num) {
            count += num;
        }

        public int getCount() {
            return count;
        }

        public String getItemName() {
            return itemName;
        }

        public List<TNode> getChildren() {
            return children;
        }

        public void setParent(TNode parent) {
            this.parent = parent;
        }

        @Override
        public int compareTo(TNode o) {
            return o.getCount() - this.getCount();
        }

        public String toString(){
            return String.valueOf(this.count);
        }
    }
}
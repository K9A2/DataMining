package com.stormlin.fpgrowth;

import java.util.*;

public class FPGrowth {

    //最小支持度
    private int minSupport;

    /**
     * 自定义构造函数，在此处设定 FP-Growth 的最小支持度
     *
     * @param minSupport 最小支持度
     */
    public FPGrowth(int minSupport) {
        this.minSupport = minSupport;
    }

    /**
     * 计算频繁项集
     *
     * @param transactions 待处理的事物集
     * @param postPattern  累积的前缀模式基
     * @param fpOutput     计算结果集
     */
    public void getFPOutput(List<List<String>> transactions, List<String> postPattern, List<List<String>> fpOutput) {

        //构建头项表
        List<TNode> headerTable = buildHeaderTable(transactions);
        //构建FP树
        TNode tree = buildFPTree(headerTable, transactions);

        //当树为空时退出
        if (tree.getChildren() == null || tree.getChildren().size() == 0) {
            return;
        }

        //输出频繁项集
        if (postPattern != null) {
            for (TNode head : headerTable) {
                StringBuilder line = new StringBuilder();
                line.append(head.getItemName());
                for (String item : postPattern) {
                    line.append(" ").append(item);
                }
                fpOutput.add(Arrays.asList(line.toString().split(" ")));
            }
        }

        //遍历每一个头项表节点
        for (TNode head : headerTable) {
            List<String> newPostPattern = new LinkedList<>();
            //添加本次模式基
            newPostPattern.add(head.getItemName());
            //加上将前面累积的前缀模式基
            if (postPattern != null) {
                newPostPattern.addAll(postPattern);
            }
            //定义新的事务数据库
            List<List<String>> newTransaction = new LinkedList<>();
            TNode nextNode = head.getNext();
            //去除名称为 head.getItemName() 的模式基，构造新的事务数据库
            while (nextNode != null) {
                int count = nextNode.getCount();
                //nextNode 节点的所有祖先节点
                List<String> parentNodes = new ArrayList<>();
                TNode parent = nextNode.getParent();
                while (parent.getItemName() != null) {
                    parentNodes.add(parent.getItemName());
                    parent = parent.getParent();
                }
                //向事务数据库中重复添加 count 次 parentNodes
                while ((count--) > 0) {
                    //添加模式基的前缀 ，因此最终的频繁项为:  parentNodes -> newPostPattern
                    newTransaction.add(parentNodes);
                }
                //下一个同名节点
                nextNode = nextNode.getNext();
            }
            //每个头项表节点重复上述所有操作，递归
            getFPOutput(newTransaction, newPostPattern, fpOutput);
        }

    }

    /**
     * 构建头项表，按降序排列
     *
     * @return 构建好的头表项
     */
    private List<TNode> buildHeaderTable(List<List<String>> transactions) {

        List<TNode> list = new ArrayList<>();
        Map<String, TNode> nodeMap = new HashMap<>();

        //为每一个 item 构建一个节点
        for (List<String> lines : transactions) {
            for (String itemName : lines) {
                //为 item 构建节点
                if (!nodeMap.keySet().contains(itemName)) {
                    nodeMap.put(itemName, new TNode(itemName));
                } else {
                    //若已经构建过该节点，出现次数加 1
                    nodeMap.get(itemName).increaseCount();
                }
            }
        }

        //筛选满足最小支持度的item节点
        for (TNode item : nodeMap.values()) {
            if (item.getCount() >= minSupport) {
                list.add(item);
            }
        }

        //按 count 值从高到低排序
        Collections.sort(list);

        return list;
    }

    /**
     * 构建 FP-Tree
     *
     * @param headerTable 头项表
     * @return 按照头表项构建好的 FP-Tree
     */
    private TNode buildFPTree(List<TNode> headerTable, List<List<String>> transactions) {

        TNode rootNode = new TNode();

        for (List<String> items : transactions) {
            LinkedList<String> itemsDesc = sortItemsByDesc(items, headerTable);
            //寻找添加 itemsDesc 为子树的父节点
            TNode subtreeRoot = rootNode;
            if (subtreeRoot.getChildren().size() != 0) {
                TNode tempNode = subtreeRoot.findChildren(itemsDesc.peek());
                while (!itemsDesc.isEmpty() && tempNode != null) {
                    tempNode.increaseCount();
                    subtreeRoot = tempNode;
                    itemsDesc.poll();
                    tempNode = subtreeRoot.findChildren(itemsDesc.peek());
                }
            }
            //将 itemsDesc 中剩余的节点加入作为 subtreeRoot 的子树
            addSubTree(headerTable, subtreeRoot, itemsDesc);
        }

        return rootNode;

    }

    /**
     * 把子树添加到头表项中
     *
     * @param headerTable 头项表
     * @param subtreeRoot 子树父节点
     * @param itemsDesc   被添加的子树
     */
    private void addSubTree(List<TNode> headerTable, TNode subtreeRoot, LinkedList<String> itemsDesc) {

        if (itemsDesc.size() > 0) {
            //构建新节点
            TNode thisNode = new TNode(itemsDesc.pop());
            subtreeRoot.getChildren().add(thisNode);
            thisNode.setParent(subtreeRoot);
            //将 thisNode 加入头项表对应节点链表的末尾
            for (TNode node : headerTable) {
                if (node.getItemName().equals(thisNode.getItemName())) {
                    TNode lastNode = node;
                    while (lastNode.getNext() != null) {
                        lastNode = lastNode.getNext();
                    }
                    lastNode.setNext(thisNode);
                }
            }
            //更新父节点为当前节点
            subtreeRoot = thisNode;
            //递归添加剩余的 items
            addSubTree(headerTable, subtreeRoot, itemsDesc);
        }
    }

    /**
     * 把 Item 以降序排序
     *
     * @param items       待排序的 items
     * @param headerTable 有序的 headTable
     * @return 已排序的 items
     */
    private LinkedList<String> sortItemsByDesc(List<String> items, List<TNode> headerTable) {

        LinkedList<String> itemsDesc = new LinkedList<>();

        for (TNode node : headerTable) {
            if (items.contains(node.getItemName())) {
                itemsDesc.add(node.getItemName());
            }
        }

        return itemsDesc;

    }

}
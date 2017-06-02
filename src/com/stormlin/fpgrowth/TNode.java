package com.stormlin.fpgrowth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author stormlin
 * @DATE 2017/6/2
 * @TIME 9:13
 * @PROJECT DataMining
 * @PACKAGE PACKAGE_NAME
 */
public class TNode implements Comparable<TNode> {
    private String itemName; //项目名
    private int count; //事务数据库中出现次数
    private TNode parent; //父节点
    private List<TNode> children; //子节点
    private TNode next;//下一个同名节点

    TNode() {
        this.children = new ArrayList<>();
    }

    TNode(String name) {
        this.itemName = name;
        this.count = 1;
        this.children = new ArrayList<>();
    }

    TNode findChildren(String childName) {
        for (TNode node : this.getChildren()) {
            if (node.getItemName().equals(childName)) {
                return node;
            }
        }
        return null;
    }

    TNode getNext() {
        return next;
    }

    void setNext(TNode next) {
        this.next = next;
    }

    TNode getParent() {
        return parent;
    }

    void setParent(TNode parent) {
        this.parent = parent;
    }

    void increaseCount() {
        count += 1;
    }

    int getCount() {
        return count;
    }

    String getItemName() {
        return itemName;
    }

    List<TNode> getChildren() {
        return children;
    }

    @Override
    public int compareTo(@NotNull TNode o) {
        return o.getCount() - this.getCount();
    }
}

package com.stormlin.kmeans;

/**
 * 代表一个点
 *
 * @Author stormlin
 * @DATE 2017/6/6
 * @TIME 14:38
 * @PROJECT DataMining
 * @PACKAGE com.stormlin.kmeans
 */
public class Point {

    private double x = 0;
    private double y = 0;
    private double z = 0;

    private int classID = 0;
    private String name = "";

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
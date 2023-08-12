package com.example.inventoryapplication.log;

import java.io.Serializable;

public class ItemLog implements Serializable {

    private String type;
    private String name;
    private int minAmt;
    private int actualAmt;
    private String creationDate;
    private String lastUpdateDate;

    public String getID() {
        return type + "_" + name;
    }

    public String getType() {
        return type;
    }

    protected void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public int getMinAmt() {
        return minAmt;
    }

    protected void setMinAmt(int minAmt) {
        this.minAmt = minAmt;
    }

    public int getActualAmt() {
        return actualAmt;
    }

    protected void setActualAmt(int actualAmt) {
        this.actualAmt = actualAmt;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }
}

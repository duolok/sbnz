package com.ftn.sbnz.model.models;

public class RegionHierarchy {
    private String child;
    private String parent;
    
    public RegionHierarchy() {}
    
    public RegionHierarchy(String child, String parent) {
        this.child = child;
        this.parent = parent;
    }
    
    public String getChild() {
        return child;
    }
    
    public void setChild(String child) {
        this.child = child;
    }
    
    public String getParent() {
        return parent;
    }
    
    public void setParent(String parent) {
        this.parent = parent;
    }
}
package com.robomi.robomifront;

public class ManagerData {
    private int seq;
    private String name;
    private String img_path;
    private String create_date;
    private String update_date;
    private int type;

    public int getSeq(){return seq;}
    public int getType(){return type;}
    public String getName(){return name;}
    public String getImgPath(){return img_path;}
    public String getCreateDate(){return create_date;}
    public String getUpdateDate(){return update_date;}
}

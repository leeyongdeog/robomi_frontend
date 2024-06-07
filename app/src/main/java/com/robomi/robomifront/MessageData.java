package com.robomi.robomifront;

import java.time.LocalDateTime;

public class MessageData {
    private int seq;
    private String name;
    private String img_path;
    private int status;
    private String update_date;

    public int getSeq(){return seq;}
    public int getStatus(){return status;}
    public String getName(){return name;}
    public String getImgPath(){return img_path;}
    public String getUpdateDate(){return update_date;}
}

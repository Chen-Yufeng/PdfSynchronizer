package com.ifchan.pdftest3.Entities;

import java.io.Serializable;

/**
 * Created by user on 2018/2/7.
 */

public class AnnoInfo implements Serializable {
    public int page;
    public String content;

    public AnnoInfo(){
        super();
    }

    public AnnoInfo(int page, String content){
        super();
        this.page = page;
        this.content = content;
    }

}

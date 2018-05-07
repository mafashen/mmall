package com.mmall.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import org.codehaus.jackson.annotate.JsonIgnore;

@Getter
@Setter
public class Category implements Serializable{

    private Integer id;

    private Integer parentId;

    private String name;

    private Boolean status;

    private Integer sortOrder;

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;

    private List<Category> children;

    public Category(Integer id, Integer parentId, String name, Boolean status, Integer sortOrder, Date createTime, Date updateTime) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.status = status;
        this.sortOrder = sortOrder;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Category() {
        super();
    }

}
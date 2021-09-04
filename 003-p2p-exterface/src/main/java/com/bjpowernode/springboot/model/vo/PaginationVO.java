package com.bjpowernode.springboot.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author: gg
 * @create: 2021-08-29 20:23
 */
public class PaginationVO<T> implements Serializable {

    /**
     * 总条数
     */
    private Integer total;

    /**
     * 分页显示的数据
     */
    private List<T> dataList;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}



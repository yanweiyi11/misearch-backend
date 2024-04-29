package com.yanweiyi.misearchbackend.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源接口（新接入数据源必须实现）
 *
 * @author yanweiyi
 */
public interface DataSource <T> {

    /**
     * 搜索
     *
     * @param searText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searText, long pageNum, long pageSize);

}

package com.yanweiyi.misearchbackend.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanweiyi.misearchbackend.datasource.*;
import com.yanweiyi.misearchbackend.model.dto.search.SearchRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 搜索门面
 *
 * @author yanweiyi
 */
@Component
public class SearchFacade {

    @Resource
    private PostDataSource postDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
    @Resource
    private UserDataSource userDataSource;
    @Resource
    private DataSourceRegistry dataSourceRegistry;

    public Page doSearch(SearchRequest searchRequest, String category, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long size = searchRequest.getPageSize();

        DataSource dataSource = dataSourceRegistry.getDataSourceByType(category);
        Page page = dataSource.doSearch(searchText, current, size);
        return page;
    }
}

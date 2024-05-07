package com.yanweiyi.misearchbackend.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanweiyi.misearchbackend.common.ErrorCode;
import com.yanweiyi.misearchbackend.datasource.*;
import com.yanweiyi.misearchbackend.exception.BusinessException;
import com.yanweiyi.misearchbackend.model.dto.search.SearchRequest;
import com.yanweiyi.misearchbackend.model.entity.Picture;
import com.yanweiyi.misearchbackend.model.enums.SearchEnum;
import com.yanweiyi.misearchbackend.model.vo.PostVO;
import com.yanweiyi.misearchbackend.model.vo.SearchVO;
import com.yanweiyi.misearchbackend.model.vo.UserVO;
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

    public SearchVO doSearch(SearchRequest searchRequest, HttpServletRequest request) {
        String type = searchRequest.getType();

        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long size = searchRequest.getPageSize();

        // ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        SearchEnum searchEnum = SearchEnum.getEnumByValue(type);
        SearchVO searchVO = new SearchVO();
        if (searchEnum == null) {
            Page<PostVO> postPage = postDataSource.doSearch(searchText, current, size);
            Page<UserVO> userPage = userDataSource.doSearch(searchText, current, size);
            Page<Picture> picturePage = pictureDataSource.doSearch(searchText, current, size);
            try {
                searchVO.setPostList(postPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setUserList(userPage.getRecords());
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            DataSource dataSource = dataSourceRegistry.getDataSourceByType(type);
            Page page = dataSource.doSearch(searchText, current, size);
            searchVO.setDataList(page.getRecords());
        }
        return searchVO;
    }
}

package com.yanweiyi.misearchbackend.controller;

import com.yanweiyi.misearchbackend.common.BaseResponse;
import com.yanweiyi.misearchbackend.common.ResultUtils;
import com.yanweiyi.misearchbackend.manager.SearchFacade;
import com.yanweiyi.misearchbackend.model.dto.search.SearchRequest;
import com.yanweiyi.misearchbackend.model.vo.SearchVO;
import com.yanweiyi.misearchbackend.service.PictureService;
import com.yanweiyi.misearchbackend.service.PostService;
import com.yanweiyi.misearchbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author yanweiyi
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private PostService postService;

    @Resource
    private PictureService pictureService;

    @Resource
    private UserService userService;

    @Resource
    private SearchFacade searchFacade;

    @PostMapping("/all")
    public BaseResponse<SearchVO> doSearch(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.doSearch(searchRequest, request));
    }
}

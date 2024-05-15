package com.yanweiyi.misearchbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanweiyi.misearchbackend.common.BaseResponse;
import com.yanweiyi.misearchbackend.common.ErrorCode;
import com.yanweiyi.misearchbackend.common.ResultUtils;
import com.yanweiyi.misearchbackend.exception.BusinessException;
import com.yanweiyi.misearchbackend.manager.SearchFacade;
import com.yanweiyi.misearchbackend.model.dto.search.SearchRequest;
import com.yanweiyi.misearchbackend.model.entity.User;
import com.yanweiyi.misearchbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    private SearchFacade searchFacade;

    @Resource
    private UserService userService;

    @PostMapping("/{category}")
    public BaseResponse<Page> doSearch(@RequestBody SearchRequest searchRequest, @PathVariable String category, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(searchFacade.doSearch(searchRequest, category, request));
    }
}

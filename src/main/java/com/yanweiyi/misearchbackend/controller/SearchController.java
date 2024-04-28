package com.yanweiyi.misearchbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanweiyi.misearchbackend.common.BaseResponse;
import com.yanweiyi.misearchbackend.common.ErrorCode;
import com.yanweiyi.misearchbackend.common.ResultUtils;
import com.yanweiyi.misearchbackend.exception.BusinessException;
import com.yanweiyi.misearchbackend.model.dto.post.PostQueryRequest;
import com.yanweiyi.misearchbackend.model.dto.search.SearchRequest;
import com.yanweiyi.misearchbackend.model.dto.user.UserQueryRequest;
import com.yanweiyi.misearchbackend.model.entity.Picture;
import com.yanweiyi.misearchbackend.model.vo.PostVO;
import com.yanweiyi.misearchbackend.model.vo.SearchVo;
import com.yanweiyi.misearchbackend.model.vo.UserVO;
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
import java.util.concurrent.CompletableFuture;

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

    @PostMapping("/all")
    public BaseResponse<SearchVo> doSearch(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        String searchText = searchRequest.getSearchText();

        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);

        CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> postService.listPostVOByPage(postQueryRequest, request));
        CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> userService.listUserVoByPage(userQueryRequest));
        CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> pictureService.searchPicture(searchText, searchRequest.getCurrent(), searchRequest.getPageSize()));

        CompletableFuture.allOf(userTask, postTask, pictureTask).join();

        SearchVo searchVo = new SearchVo();
        try {
            Page<PostVO> postPage = postTask.get();
            Page<UserVO> userPage = userTask.get();
            Page<Picture> picturePage = pictureTask.get();
            searchVo.setPostList(postPage.getRecords());
            searchVo.setPictureList(picturePage.getRecords());
            searchVo.setUserList(userPage.getRecords());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(searchVo);
    }

}

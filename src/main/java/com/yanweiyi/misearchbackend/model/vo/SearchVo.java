package com.yanweiyi.misearchbackend.model.vo;

import com.yanweiyi.misearchbackend.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索
 */
@Data
public class SearchVo implements Serializable {

    private static final long serialVersionUID = 2993490323532911908L;

    private List<UserVO> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

}

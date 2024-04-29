package com.yanweiyi.misearchbackend.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanweiyi.misearchbackend.model.entity.Picture;
import com.yanweiyi.misearchbackend.service.PictureService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 图片服务实现类
 *
 * @author yanweiyi
 */
@Service
public class PictureDataSource implements DataSource<Picture> {

    @Resource
    private PictureService pictureService;

    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        return pictureService.searchPicture(searchText, pageNum, pageSize);
    }
}

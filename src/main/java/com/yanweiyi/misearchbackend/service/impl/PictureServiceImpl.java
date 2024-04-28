package com.yanweiyi.misearchbackend.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanweiyi.misearchbackend.common.ErrorCode;
import com.yanweiyi.misearchbackend.exception.BusinessException;
import com.yanweiyi.misearchbackend.model.entity.Picture;
import com.yanweiyi.misearchbackend.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片服务实现类
 *
 * @author yanweiyi
 */
@Service
public class PictureServiceImpl implements PictureService {
    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        List<Picture> picturesList = new ArrayList<>();
        long cursor = (pageNum - 1) * pageSize;
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%d&cw=1603&ch=924", searchText, cursor);
        try {
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select(".iuscp.isv");
            for (Element element : elements) {
                if (picturesList.size() >= pageSize) {
                    break;
                }

                // 解析图片url和标题
                String jsonData = element.selectFirst(".iusc").attr("m");
                Map<String, Object> jsonMap = JSONUtil.toBean(jsonData, Map.class);
                String imageUrl = (String) jsonMap.get("murl");
                String title = element.selectFirst(".inflnk").attr("aria-label");

                Picture picture = new Picture();
                picture.setUrl(imageUrl);
                picture.setTitle(title);
                picturesList.add(picture);
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Page<Picture> picturePage = new Page<>(pageNum, pageSize);
        picturePage.setRecords(picturesList);
        return picturePage;
    }
}

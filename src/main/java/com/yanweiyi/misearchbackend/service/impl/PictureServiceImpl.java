package com.yanweiyi.misearchbackend.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanweiyi.misearchbackend.common.ErrorCode;
import com.yanweiyi.misearchbackend.exception.BusinessException;
import com.yanweiyi.misearchbackend.model.entity.Picture;
import com.yanweiyi.misearchbackend.service.PictureService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 图片服务实现类
 *
 * @author yanweiyi
 */
@Service
public class PictureServiceImpl implements PictureService {

    // 定义默认搜索关键词的常量数组
    private static final String[] DEFAULT_SEARCH_KEYWORDS = {"Java", "SpringBoot", "Vue", "React", "C++"};


    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        List<Picture> picturesList = new ArrayList<>();
        long cursor = pageNum * pageSize + 10;

        // 如果 searchText 为空，则从默认搜索关键词中随机选择一个
        if (StringUtils.isBlank(searchText)) {
            int randomIndex = ThreadLocalRandom.current().nextInt(DEFAULT_SEARCH_KEYWORDS.length);
            // 默认搜索关键词，因为不输入关键词，请求的链接就会被转发，从而分不了页
            searchText = DEFAULT_SEARCH_KEYWORDS[randomIndex];
        }

        String url = String.format("https://cn.bing.com/images/search?q=%s&form=HDRSC2&first=%s", searchText, cursor);
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
        picturePage.setTotal(99999L);
        picturePage.setRecords(picturesList);
        return picturePage;
    }
}

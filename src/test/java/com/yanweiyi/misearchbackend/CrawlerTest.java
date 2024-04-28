package com.yanweiyi.misearchbackend;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yanweiyi.misearchbackend.model.entity.Picture;
import com.yanweiyi.misearchbackend.model.entity.Post;
import com.yanweiyi.misearchbackend.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yanweiyi
 */
@SpringBootTest
@Slf4j
public class CrawlerTest {

    @Resource
    protected PostService postService;

    @Test
    void testFetchPicture() throws IOException {
        List<Picture> picturesList = new ArrayList<>();

        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%d&cw=1603&ch=924", "ikun", 1);
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        for (Element element : elements) {
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
        System.out.println(picturesList);
    }

    @Test
    void testFetchPassage() throws InterruptedException {
        // 获取数据
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        List<Post> postList = new ArrayList<>();
        for (int i = 5; i < 11; i++) {
            JSONObject jsonObject = JSONUtil.createObj()
                    .set("current", i)
                    .set("pageSize", 8)
                    .set("sortField", "createTime")
                    .set("sortOrder", "descend")
                    .set("category", "文章")
                    .set("tags", JSONUtil.createArray())
                    .set("reviewStatus", 1);
            String json = jsonObject.toString();
            String result = HttpRequest.post(url).body(json).execute().body();
            Thread.sleep(6000);
            // Json 转对象
            Map map = JSONUtil.toBean(result, Map.class);
            JSONObject data = (JSONObject) map.get("data");
            JSONArray records = (JSONArray) data.get("records");
            for (Object record : records) {
                JSONObject tempRecord = (JSONObject) record;
                Post post = new Post();
                post.setTitle(tempRecord.getStr("title"));
                post.setContent(tempRecord.getStr("content"));
                post.setTags(JSONUtil.toJsonStr(tempRecord.get("tags")));
                post.setThumbNum(tempRecord.getInt("thumbNum"));
                post.setFavourNum(tempRecord.getInt("favourNum"));
                post.setUserId(1784196602036596737L);
                postList.add(post);
            }
        }
        // 数据入库
        boolean b = postService.saveBatch(postList);
        Assertions.assertTrue(b);
    }
}

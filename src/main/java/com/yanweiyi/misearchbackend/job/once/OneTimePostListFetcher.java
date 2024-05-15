package com.yanweiyi.misearchbackend.job.once;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yanweiyi.misearchbackend.model.entity.Post;
import com.yanweiyi.misearchbackend.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.*;

/**
 * 单词爬取文章
 *
 * @author yanweiyi
 */
// @Component
@Slf4j
public class OneTimePostListFetcher implements CommandLineRunner {

    @Resource
    protected PostService postService;

    @Override
    public void run(String... args) {
        // 初始化请求 URL 和文章列表
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        List<Post> postList = new ArrayList<>();

        // 遍历页码，获取每一页的文章数据
        for (int pageIndex = 1; pageIndex <= 300; pageIndex++) {
            log.info("OneTimePostListFetcher starts execution, currently crawling page {}", pageIndex);
            try {
                // 封装请求头
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept-Encoding", "gzip, deflate, br, zstd");
                headers.put("Accept-Language", "en,zh-CN;q=0.9,zh;q=0.8,en-GB;q=0.7,en-US;q=0.6");
                headers.put("Content-Length", "107");
                headers.put("Content-Type", "application/json");
                headers.put("Cookie", "SESSION=MjAyYWUwZjktNmRjMi00NzA1LWE4OTItYTZiMmE5OWMwNWM2");
                headers.put("Dnt", "1");
                headers.put("Origin", "https://www.code-nav.cn");
                headers.put("Priority", "u=1, i");
                headers.put("Referer", "https://www.code-nav.cn/");
                headers.put("Sec-Ch-Ua", "\"Chromium\";v=\"124\", \"Microsoft Edge\";v=\"124\", \"Not-A.Brand\";v=\"99\"");
                headers.put("Sec-Ch-Ua-Mobile", "?0");
                headers.put("Sec-Ch-Ua-Platform", "\"Windows\"");
                headers.put("Sec-Fetch-Dest", "empty");
                headers.put("Sec-Fetch-Mode", "cors");
                headers.put("Sec-Fetch-Site", "same-site");
                headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0");

                // 构建请求体
                String requestBody = JSONUtil.createObj()
                        .set("current", pageIndex)
                        .set("pageSize", 10)
                        .set("sortField", "createTime")
                        .set("sortOrder", "descend")
                        .set("category", "文章")
                        .set("tags", JSONUtil.createArray())
                        .set("reviewStatus", 1)
                        .toString();

                // 发送请求并获取响应
                String responseJson = HttpRequest
                        .post(url)
                        .addHeaders(headers)
                        .body(requestBody)
                        .execute()
                        .body();

                // 解析响应数据并转换为文章对象列表
                Map responseMap = JSONUtil.toBean(responseJson, Map.class);
                JSONObject dataObject = (JSONObject) responseMap.get("data");
                JSONArray recordsArray = (JSONArray) dataObject.get("records");
                for (Object record : recordsArray) {
                    JSONObject recordJson = (JSONObject) record;
                    Post post = new Post();
                    post.setTitle(recordJson.getStr("title"));
                    post.setContent(recordJson.getStr("content"));
                    post.setTags(JSONUtil.toJsonStr(recordJson.get("tags")));
                    post.setThumbNum(recordJson.getInt("thumbNum"));
                    post.setFavourNum(recordJson.getInt("favourNum"));
                    post.setUserId(1784196602036596737L);
                    postList.add(post);
                }

                // 随机睡眠以模拟用户操作
                Thread.sleep(RandomUtil.randomInt(1, 6) * 1000L);
            } catch (Exception e) {
                log.warn("failed to fetch page {}, reason: {}", pageIndex, e.getMessage(), e);
            }
        }

        // 将获取到的文章数据批量保存到数据库
        boolean save = postService.saveBatch(postList);
        if (save) {
            log.info("OneTimePostListFetcher is over end");
        } else {
            log.error("failed to save to database!");
        }
    }
}

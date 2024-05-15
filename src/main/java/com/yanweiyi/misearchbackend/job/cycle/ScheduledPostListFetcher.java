package com.yanweiyi.misearchbackend.job.cycle;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanweiyi.misearchbackend.model.entity.Post;
import com.yanweiyi.misearchbackend.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 定时爬取文章
 *
 * @author yanweiyi
 */
@Component
@Slf4j
public class ScheduledPostListFetcher {

    @Resource
    protected PostService postService;

    private static final int PAGE_COUNT_TO_FETCH = 3;

    /**
     * 每周六凌晨 2 点执行一次
     */
    @Scheduled(cron = "0 0 2 ? * SAT")
    public void run() {
        // 初始化请求 URL 和文章列表
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        List<Post> postList = new ArrayList<>();

        // 爬取最新的 3 页数据
        for (int pageIndex = 1; pageIndex <= PAGE_COUNT_TO_FETCH; pageIndex++) {
            log.info("ScheduledPostListFetcher starts execution, currently crawling page {}", pageIndex);
            try {
                // 封装请求头
                Map<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("Accept-Encoding", "gzip, deflate, br, zstd");
                requestHeaders.put("Accept-Language", "en,zh-CN;q=0.9,zh;q=0.8,en-GB;q=0.7,en-US;q=0.6");
                requestHeaders.put("Content-Length", "107");
                requestHeaders.put("Content-Type", "application/json");
                requestHeaders.put("Cookie", "SESSION=MjAyYWUwZjktNmRjMi00NzA1LWE4OTItYTZiMmE5OWMwNWM2");
                requestHeaders.put("Dnt", "1");
                requestHeaders.put("Origin", "https://www.code-nav.cn");
                requestHeaders.put("Priority", "u=1, i");
                requestHeaders.put("Referer", "https://www.code-nav.cn/");
                requestHeaders.put("Sec-Ch-Ua", "\"Chromium\";v=\"124\", \"Microsoft Edge\";v=\"124\", \"Not-A.Brand\";v=\"99\"");
                requestHeaders.put("Sec-Ch-Ua-Mobile", "?0");
                requestHeaders.put("Sec-Ch-Ua-Platform", "\"Windows\"");
                requestHeaders.put("Sec-Fetch-Dest", "empty");
                requestHeaders.put("Sec-Fetch-Mode", "cors");
                requestHeaders.put("Sec-Fetch-Site", "same-site");
                requestHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 Edg/124.0.0.0");


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
                        .addHeaders(requestHeaders)
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

                // 间隔 1 秒钟
                Thread.sleep(1000L);
            } catch (Exception e) {
                log.warn("failed to fetch page {}, reason: {}", pageIndex, e.getMessage(), e);
            }
        }
        // 查询文章数据是否已存在
        List<Post> newPostList = new ArrayList<>();
        for (Post post : postList) {
            LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Post::getTitle, post.getTitle());
            List<Post> existPost = postService.list(queryWrapper);
            if (!existPost.isEmpty()) {
                log.info("article already exists: {}", post);
            } else {
                newPostList.add(post);
            }
        }
        // 批量保存到数据库
        boolean save = postService.saveBatch(newPostList);
        if (save) {
            log.info("article fetching finished at {}", new Date());
        } else {
            log.error("failed to save to database!");
        }
    }
}

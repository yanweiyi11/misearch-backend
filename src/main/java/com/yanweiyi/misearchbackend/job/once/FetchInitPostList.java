package com.yanweiyi.misearchbackend.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yanweiyi.misearchbackend.model.entity.Post;
import com.yanweiyi.misearchbackend.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author yanweiyi
 */
// todo 取消注释开启任务后，每次启动 springboot 项目时，会执行一次 run 方法
// @Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {

    @Resource
    protected PostService postService;

    @Override
    public void run(String... args) {
        // 获取数据
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        List<Post> postList = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 20; i++) {
            log.info("FetchInitPostList run current number: {}", i);
            try {
                JSONObject jsonObject = JSONUtil.createObj()
                        .set("current", i)
                        .set("pageSize", 20)
                        .set("sortField", "createTime")
                        .set("sortOrder", "descend")
                        .set("category", "文章")
                        .set("tags", JSONUtil.createArray())
                        .set("reviewStatus", 1);
                String json = jsonObject.toString();
                String result = HttpRequest.post(url).body(json).execute().body();
                // 生成一个随机数
                int sleepTime = random.nextInt(6) + 3; // 0-5 + 3 = 3-8
                // 随机睡眠3到8秒
                Thread.sleep(sleepTime * 1000);
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
            } catch (Exception e) {
                log.warn("loop to {} times with exception: {}", i, e.getMessage(), e);
            }
        }
        // 数据入库
        boolean b = postService.saveBatch(postList);
        log.info("init postList result: {}", b);
    }
}

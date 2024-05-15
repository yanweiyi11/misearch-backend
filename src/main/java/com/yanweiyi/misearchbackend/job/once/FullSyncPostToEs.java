package com.yanweiyi.misearchbackend.job.once;

import cn.hutool.core.collection.CollUtil;
import com.yanweiyi.misearchbackend.esdao.PostEsDao;
import com.yanweiyi.misearchbackend.model.dto.post.PostEsDTO;
import com.yanweiyi.misearchbackend.model.entity.Post;
import com.yanweiyi.misearchbackend.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步文章到 es
 */
// @Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Resource
    private PostEsDao postEsDao;

    @Override
    public void run(String... args) {
        // 从数据库获取所有文章数据
        List<Post> postList = postService.list();
        if (CollUtil.isEmpty(postList)) {
            return;
        }

        // 将帖子列表转换为 Elasticsearch 的 DTO 列表
        List<PostEsDTO> postEsDTOList = postList.stream().map(PostEsDTO::objToDto).collect(Collectors.toList());

        // 定义每次同步到Elasticsearch的数据量大小，用于分批同步数据到 Elasticsearch，以避免一次性同步大量数据可能导致的性能问题。
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);

        // 分批次将数据同步到Elasticsearch
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            // 执行同步操作
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }

        log.info("FullSyncPostToEs end, total {}", total);
    }
}

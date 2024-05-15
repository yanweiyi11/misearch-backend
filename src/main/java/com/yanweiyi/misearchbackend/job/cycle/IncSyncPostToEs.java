package com.yanweiyi.misearchbackend.job.cycle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.yanweiyi.misearchbackend.esdao.PostEsDao;
import com.yanweiyi.misearchbackend.mapper.PostMapper;
import com.yanweiyi.misearchbackend.model.dto.post.PostEsDTO;
import com.yanweiyi.misearchbackend.model.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 增量同步文章到 es
 */
@Component
@Slf4j
public class IncSyncPostToEs {

    @Resource
    private PostMapper postMapper;

    @Resource
    private PostEsDao postEsDao;

    /**
     * 每六凌晨 3 点执行一次
     */
    @Scheduled(cron = "0 0 3 ? * SAT")
    public void run() {
        // 查询近 三周 内的数据
        Date threeWeeksAgoDate = DateUtil.offsetWeek(new Date(), -3);
        List<Post> postList = postMapper.listPostWithDelete(threeWeeksAgoDate);
        if (CollUtil.isEmpty(postList)) {
            log.info("no inc post");
            return;
        }
        List<PostEsDTO> postEsDTOList = postList.stream()
                .map(PostEsDTO::objToDto)
                .collect(Collectors.toList());
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("IncSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("IncSyncPostToEs end, total {}", total);
    }
}

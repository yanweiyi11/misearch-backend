package com.yanweiyi.misearchbackend.datasource;

import com.yanweiyi.misearchbackend.model.enums.SearchEnum;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanweiyi
 */
@Component
public class DataSourceRegistry {

    @Resource
    private PostDataSource postDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
    @Resource
    private UserDataSource userDataSource;

    @PostConstruct
    public void init() {
        dataSourceMap = new HashMap() {{
            put(SearchEnum.POST.getValue(), postDataSource);
            put(SearchEnum.PICTURE.getValue(), pictureDataSource);
            put(SearchEnum.USER.getValue(), userDataSource);
        }};
    }

    private Map<String, DataSource> dataSourceMap;

    public DataSource getDataSourceByType(String type) {
        if (dataSourceMap == null) {
            return null;
        }
        return dataSourceMap.get(type);
    }
}

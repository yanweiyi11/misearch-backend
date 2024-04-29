package com.yanweiyi.misearchbackend.model.dto.search;

import com.yanweiyi.misearchbackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanweiyi
 */
@Data
public class SearchRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -2311061089379627251L;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 搜索类型
     */
    private String type;
}

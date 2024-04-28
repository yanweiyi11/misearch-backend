package com.yanweiyi.misearchbackend.model.dto.picture;

import com.yanweiyi.misearchbackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yanweiyi
 */
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -4533513666348835119L;

    /**
     * 搜索词
     */
    private String searchText;

}

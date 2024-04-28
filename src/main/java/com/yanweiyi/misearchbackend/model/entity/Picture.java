package com.yanweiyi.misearchbackend.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yanweiyi
 */
@Data
public class Picture implements Serializable {

    private static final long serialVersionUID = -5016855908162164188L;

    private String title;

    private String url;

}

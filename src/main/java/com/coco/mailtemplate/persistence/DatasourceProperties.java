package com.coco.mailtemplate.persistence;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatasourceProperties {
    private String driver;
    private String uri;
    private String username;
    private String password;
}
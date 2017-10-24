package com.xiuyukeji.plugin.translation.translator.http;

/**
 * 请求参数接口
 *
 * @author Created by jz on 2017/10/24 14:49
 */
public interface HttpParams {
    String send2String(String baseUrl) throws Exception;

    HttpParams put(String key, String value);
}

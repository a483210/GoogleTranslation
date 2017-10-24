package com.xiuyukeji.plugin.translation.translator.trans;

/**
 * 翻译接口
 *
 * @author Created by jz on 2017/10/24 14:46
 */
public interface Translator {
    String translation(Language from, Language to, String query);
}

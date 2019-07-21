package com.pinyougou.page.service;

import java.io.IOException;

public interface ItemPageService {
    void genItemHtml(Long goodsId);

    void deleteByIds(Long[] ids) throws IOException;
}

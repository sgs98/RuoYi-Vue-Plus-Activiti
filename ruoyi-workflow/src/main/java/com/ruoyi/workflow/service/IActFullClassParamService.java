package com.ruoyi.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.workflow.domain.ActFullClassParam;

import java.util.List;

/**
 * 方法参数Service接口
 *
 * @author gssong
 * @date 2021-12-17
 */
public interface IActFullClassParamService extends IService<ActFullClassParam> {

    /**
     * 按照业务规则id查询
     * @return
     */
    List<ActFullClassParam> queryListByFullClassId(Long fullClassId);

}

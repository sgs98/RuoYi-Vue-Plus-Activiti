package com.ruoyi.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.workflow.domain.ActHiActInst;

import java.util.List;

/**
 * 流程执行历史Service接口
 *
 * @author gssong
 * @date 2022-01-23
 */
public interface IActHiActInstService extends IService<ActHiActInst> {

    /**
     * 按照节点id删除执行节点
     * @param nodeId
     */
    void deleteActHiActInstByActId(String nodeId);

    /**
     * 按照节点ids删除执行节点
     * @param nodeIds
     */
    void deleteActHiActInstByActIds(List<String> nodeIds);
}

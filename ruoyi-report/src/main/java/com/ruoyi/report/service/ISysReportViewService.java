package com.ruoyi.report.service;

import com.ruoyi.report.domain.SysReportView;
import com.ruoyi.report.domain.vo.SysReportViewVo;
import com.ruoyi.report.domain.bo.SysReportViewBo;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 报表查看Service接口
 *
 * @author ruoyi
 * @date 2022-08-07
 */
public interface ISysReportViewService {

    /**
     * 查询报表查看
     */
    SysReportViewVo queryById(Long id);

    /**
     * 查询报表查看列表
     */
    TableDataInfo<SysReportViewVo> queryPageList(SysReportViewBo bo, PageQuery pageQuery);

    /**
     * 查询报表查看列表
     */
    List<SysReportViewVo> queryList(SysReportViewBo bo);

    /**
     * 修改报表查看
     */
    Boolean insertByBo(SysReportViewBo bo);

    /**
     * 修改报表查看
     */
    Boolean updateByBo(SysReportViewBo bo);

    /**
     * 校验并批量删除报表查看信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}

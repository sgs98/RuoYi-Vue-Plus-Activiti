package com.ruoyi.workflow.mapper;

import com.ruoyi.common.core.domain.R;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 任务Mapper接口
 *
 * @author gssong
 * @date 2022-07-24
 */
public interface TaskMapper {

    @Update("update act_hi_comment set message_=#{comment},full_msg_=#{comment} where id_ = #{commentId}")
    void editComment(@Param("commentId") String commentId, @Param("comment") String comment);
}

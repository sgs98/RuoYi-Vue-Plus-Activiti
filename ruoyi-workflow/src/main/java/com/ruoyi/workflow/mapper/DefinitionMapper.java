package com.ruoyi.workflow.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface DefinitionMapper {

    @Update("update act_re_procdef set description_=#{description} where id_ = #{definitionId}")
    void updateDescriptionById(@Param("definitionId") String definitionId, @Param("description") String description);

}

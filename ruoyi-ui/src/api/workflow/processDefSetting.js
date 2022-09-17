import request from '@/utils/request'

// 查询流程定义与单配置列表
export function listProcessDefSetting(query) {
  return request({
    url: '/workflow/processDefSetting/list',
    method: 'get',
    params: query
  })
}

// 查询流程定义与单配置详细
export function getProcessDefSetting(id) {
  return request({
    url: '/workflow/processDefSetting/' + id,
    method: 'get'
  })
}

// 按流程定义id查询流程定义与单配置详细
export function getProcessDefSettingByDefId(id) {
  return request({
    url: '/workflow/processDefSetting/getProcessDefSettingByDefId/' + id,
    method: 'get'
  })
}

// 校验表单是否关联
export function checkProcessDefSetting(defId,param,businessType) {
  return request({
    url: `/workflow/processDefSetting/checkProcessDefSetting/${defId}/${param}/${businessType}` ,
    method: 'get'
  })
}

// 新增流程定义与单配置
export function addProcessDefSetting(data) {
  return request({
    url: '/workflow/processDefSetting',
    method: 'post',
    data: data
  })
}

// 修改流程定义与单配置
export function updateProcessDefSetting(data) {
  return request({
    url: '/workflow/processDefSetting',
    method: 'put',
    data: data
  })
}

// 删除流程定义与单配置
export function delProcessDefSetting(id) {
  return request({
    url: '/workflow/processDefSetting/' + id,
    method: 'delete'
  })
}

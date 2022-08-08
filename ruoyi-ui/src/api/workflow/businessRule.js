import request from '@/utils/request'

// 查询业务规则列表
export function listBusinessRule(query) {
  return request({
    url: '/workflow/businessRule/list',
    method: 'get',
    params: query
  })
}

// 查询业务规则详细
export function getBusinessRule(id) {
  return request({
    url: '/workflow/businessRule/' + id,
    method: 'get'
  })
}

// 新增业务规则
export function addbusinessRule(data) {
  return request({
    url: '/workflow/businessRule',
    method: 'post',
    data: data
  })
}

// 修改业务规则
export function updateBusinessRule(data) {
  return request({
    url: '/workflow/businessRule',
    method: 'put',
    data: data
  })
}

// 删除业务规则
export function delBusinessRule(id) {
  return request({
    url: '/workflow/businessRule/' + id,
    method: 'delete'
  })
}

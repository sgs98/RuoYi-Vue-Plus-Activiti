import request from '@/utils/request'

// 查询流程单列表
export function listDynamicForm(query) {
  return request({
    url: '/workflow/dynamicForm/list',
    method: 'get',
    params: query
  })
}

// 查询启用流程单列表
export function listDynamicFormEnable(query) {
  return request({
    url: '/workflow/dynamicForm/enableList',
    method: 'get',
    params: query
  })
}

// 查询流程单详细
export function getDynamicForm(id) {
  return request({
    url: '/workflow/dynamicForm/' + id,
    method: 'get'
  })
}

// 新增流程单
export function addDynamicForm(data) {
  return request({
    url: '/workflow/dynamicForm',
    method: 'post',
    data: data
  })
}

// 修改流程单
export function updateDynamicForm(data) {
  return request({
    url: '/workflow/dynamicForm',
    method: 'put',
    data: data
  })
}

// 修改流程单
export function editForm(data) {
  return request({
    url: '/workflow/dynamicForm/editForm',
    method: 'put',
    data: data
  })
}

// 删除流程单
export function delDynamicForm(id) {
  return request({
    url: '/workflow/dynamicForm/' + id,
    method: 'delete'
  })
}

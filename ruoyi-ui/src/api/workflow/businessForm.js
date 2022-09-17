import request from '@/utils/request'

// 查询业务表单列表
export function listBusinessForm(query) {
  return request({
    url: '/workflow/businessForm/list',
    method: 'get',
    params: query
  })
}

// 查询业务表单详细
export function getBusinessForm(id) {
  return request({
    url: '/workflow/businessForm/' + id,
    method: 'get'
  })
}

// 新增业务表单
export function addBusinessForm(data) {
  return request({
    url: '/workflow/businessForm',
    method: 'post',
    data: data
  })
}

// 修改业务表单
export function updateBusinessForm(data) {
  return request({
    url: '/workflow/businessForm',
    method: 'put',
    data: data
  })
}

// 删除业务表单
export function delBusinessForm(id) {
  return request({
    url: '/workflow/businessForm/' + id,
    method: 'delete'
  })
}

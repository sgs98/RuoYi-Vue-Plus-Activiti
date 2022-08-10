import request from '@/utils/request'

// 查询报表查看列表
export function listReportView(query) {
  return request({
    url: '/report/reportView/list',
    method: 'get',
    params: query
  })
}

// 查询报表查看详细
export function getReportView(id) {
  return request({
    url: '/report/reportView/' + id,
    method: 'get'
  })
}

// 新增报表查看
export function addReportView(data) {
  return request({
    url: '/report/reportView',
    method: 'post',
    data: data
  })
}

// 修改报表查看
export function updateReportView(data) {
  return request({
    url: '/report/reportView',
    method: 'put',
    data: data
  })
}

// 删除报表查看
export function delReportView(id) {
  return request({
    url: '/report/reportView/' + id,
    method: 'delete'
  })
}

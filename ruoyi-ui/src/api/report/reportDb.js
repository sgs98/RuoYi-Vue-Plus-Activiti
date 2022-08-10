import request from '@/utils/request'

// 查询报表数据列表
export function listReportDb(query) {
  return request({
    url: '/report/reportDb/list',
    method: 'get',
    params: query
  })
}

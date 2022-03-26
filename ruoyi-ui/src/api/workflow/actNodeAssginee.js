import request from "@/utils/request";

export function add(data) {
  return request({
    url: '/workflow/actNodeAssignee',
    method: 'post',
    data: data
  })
}

export function edit(data) {
  return request({
    url: '/workflow/actNodeAssignee',
    method: 'put',
    data: data
  })
}

export function del(id) {
  return request({
    url: '/workflow/actNodeAssignee/'+id,
    method: 'delete'
  })
}

export function getInfo(processDefinitionId,nodeId) {
  return request({
    url: `/workflow/actNodeAssignee/${processDefinitionId}/${nodeId}`,
    method: 'get'
  })
}

export function copy(processDefinitionId,key) {
  return request({
    url: `/workflow/actNodeAssignee/copy/${processDefinitionId}/${key}`,
    method: 'post'
  })
}


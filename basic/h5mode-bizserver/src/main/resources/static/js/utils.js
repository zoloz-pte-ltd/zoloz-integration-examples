const baseUrl = '.'; // request Api url

function getUrlParam(name) {
  const reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)'); // 构造一个含有目标参数的正则表达式对象
  const r = window.location.search.substr(1).match(reg);
  if (r != null) { return r[2]; }
  // ?前有#号
  const after = window.location.hash.split('?')[1];
  if (after) {
    const re = after.match(reg); // 匹配目标参数
    if (re != null) { return re[2]; }
  }
  return ''; // 返回参数值
}

async function request(url, options) {
  try {
    const response = await fetch(url, options);
    const data = await response.json();
    return data;
  } catch (e) {
    // 网络错误特殊标识
    return {error: 'NETWORK_ERROR'};
  }
}



/*
 * Copyright (c) 2020 ZOLOZ PTE.LTD.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

var elements = document.getElementsByClassName('routerView');
var zListContentEl = document.getElementById('zListContent');
var all_options = zListContentEl.options;
var clientDocType = '';
var zListLabelEl = document.getElementById('zListLabel');
var zHelperMsgEl = document.getElementById('zHelperMsg');
var docSelectorModel = {
  submit_key: 'clientDocType',
  content: '',
  optional_label: '',
  error_msg: 'Field can\'t be blank',
  options: [
    {key: '00000001003', value: 'Passport'},
    {key: '08520000001', value: 'HKID1'},
    {key: '08520000002', value: 'HKID2'},
    {key: '00630000001', value: 'UMID'},
    {key: '00630000002', value: 'TIN ID'},
    {key: '00630000024', value: 'Philhealth Card'},
    {key: '00630000004', value: 'Driver\'s License'},
    {key: '00630000020', value: 'SSS ID'},
    {key: '00630000022', value: 'Voter\'s ID'},
    {key: '00630000017', value: 'PRC ID'},
    {key: '00630000029', value: 'POB'},
    {key: '00000001004', value: 'New Passport(Two Pages)'},
  ]
};
window.onload = () => {
  for (const element of elements) {
    element.style.minHeight = window.innerHeight + 'px';
  }
  zListContentEl.children[0].style.display = 'none';
  docSelectorModel.options.forEach(item => {
    const newOption = document.createElement('option');
    newOption.setAttribute('value', item.key);
    newOption.innerHTML = item.value;
    zListContentEl.appendChild(newOption);
  })
  if(window.localStorage.getItem(docSelectorModel.submit_key)) {
    for(var i = 0; i<all_options.length; i++) {
      if(all_options[i].value === window.localStorage.getItem(docSelectorModel.submit_key)) {
        all_options[i].selected = true;
        zListLabelEl.style.top = 0;
        zListLabelEl.style.fontSize = '0.9rem';
      }
    }
  }
clientDocType = zListContentEl.options[zListContentEl.selectedIndex].value;
}
onchange = (e) => {
  zListLabelEl.style.top = 0;
  zListLabelEl.style.fontSize = '0.9rem';
  clientDocType = e.target.value;
  window.localStorage.setItem(docSelectorModel.submit_key, e.target.value);
}
onFocus = () => {
    zListLabelEl.style.color = '#787878';
    zListContentEl.style.borderColor = '#aaa';
    zHelperMsgEl.innerHTML = '';
}

initDocPro = async (data) => {
  const url = '/api/idrecognition/initialize';
  const options = {
    method: 'POST',
    mode: 'cors',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  };
  return request(url, options);
}

takePhoto = async () => {
  if(clientDocType === '') {
    zHelperMsgEl.style.color = '#FF3342';
    zListLabelEl.style.color = '#FF3342';
    zListContentEl.style.borderColor = '#FF3342';
    zHelperMsgEl.innerHTML = docSelectorModel.error_msg;
    return;
  }
  const initData = {
    docType: clientDocType,
  };
  const response = await initDocPro(initData);
  if (response.error === 'NETWORK_ERROR') {
    alert('Network Error');
  } else if (response.result && response.result.resultCode === 'SUCCESS') {
    const idrecognizeUrl = 'https://sg-production-cdn.zoloz.com/page/zoloz-doc-fe/index.html';
    const clientCfg = response.clientCfg;
    const state = response.transactionId;
    const href = window.location.href;
    const baseUrl = href.split('#')[0];
    const callbackUrl = baseUrl + '/result.html';
    window.location.href = `${idrecognizeUrl}?state=${state}&clientcfg=${encodeURIComponent(clientCfg)}&callbackurl=${encodeURIComponent(callbackUrl)}`;
  } else {
    alert('System Error');
  }
}

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
var clientDocType = null;
var serviceLevel = null;
var zListLabelEl = document.getElementById('zListLabel');
var zHelperMsgEl = document.getElementById('zHelperMsg');
var zListContentElS = document.getElementById('zListContentS');
var all_optionsS = zListContentElS.options;
var zListLabelElS = document.getElementById('zListLabelS');
var zHelperMsgElS = document.getElementById('zHelperMsgS');
var docSelectorModel = {
  submit_key: 'clientDocType',
  content: '',
  optional_label: '',
  error_msg: 'Field can\'t be blank',
  options: [
    {key: '00630000001', value: 'UMID'},
    {key: '00630000002', value: 'TIN ID'},
    {key: '00000001003', value: 'Passport'},
    {key: '00630000024', value: 'Philhealth Card'},
    {key: '00630000004', value: 'Driver\'s License'},
    {key: '00630000020', value: 'SSS ID'},
    {key: '00630000022', value: 'Voter\'s ID'},
    {key: '08520000001', value: 'HKID1'},
    {key: '08520000002', value: 'HKID2'},
    {key: '00600000001', value: 'MYKAD'},
    {key: '08800000001', value: 'NID'},
  ]
};

var serviceLevelSelectorModel = {
  submit_key: 'serviceLevel',
  content: '',
  optional_label: '',
  error_msg: 'Field can\'t be blank',
  options: [
    {key: 'REALID0001', value: 'REALID0001'},
    {key: 'REALID0002', value: 'REALID0002'},
    {key: 'REALID0011', value: 'REALID0011'},
  ]  
}
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

  zListContentElS.children[0].style.display = 'none';
  serviceLevelSelectorModel.options.forEach(item => {
    const newOption = document.createElement('option');
    newOption.setAttribute('value', item.key);
    newOption.innerHTML = item.value;
    zListContentElS.appendChild(newOption);
  })

  if(window.localStorage.getItem(serviceLevelSelectorModel.submit_key)) {
    for(var i = 0; i<all_optionsS.length; i++) {
      if(all_optionsS[i].value === window.localStorage.getItem(serviceLevelSelectorModel.submit_key)) {
        all_optionsS[i].selected = true;
        zListLabelElS.style.top = 0;
        zListLabelElS.style.fontSize = '0.9rem';
      }
    }
  }
  serviceLevel = zListContentElS.options[zListContentElS.selectedIndex].value;
  
  zListContentElS.addEventListener('change', (e) => {
    console.log('cs');
    zListLabelElS.style.top = 0;
    zListLabelElS.style.fontSize = '0.9rem';
    serviceLevel = e.target.value;
    window.localStorage.setItem(serviceLevelSelectorModel.submit_key, e.target.value);
  });

  zListContentEl.addEventListener('change', (e) => {
    console.log('c');
    zListLabelEl.style.top = 0;
    zListLabelEl.style.fontSize = '0.9rem';
    clientDocType = e.target.value;
    window.localStorage.setItem(docSelectorModel.submit_key, e.target.value);
  });

}

onFocus = () => {
    zListLabelEl.style.color = '#787878';
    zListContentEl.style.borderColor = '#aaa';
    zHelperMsgEl.innerHTML = '';
}

onFocusS = () => {
  zListLabelElS.style.color = '#787878';
  zListContentElS.style.borderColor = '#aaa';
  zHelperMsgElS.innerHTML = '';
}

initRealId = async (data) => {
  const url = baseUrl + '/api/initialize';
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
  if(serviceLevel === '') {
    zHelperMsgElS.style.color = '#FF3342';
    zListLabelElS.style.color = '#FF3342';
    zListContentElS.style.borderColor = '#FF3342';
    zHelperMsgElS.innerHTML = serviceLevelSelectorModel.error_msg;
    return;
  }
  const initData = {
    docType: clientDocType,
    serviceLevel
  };
  const response = await initRealId(initData);
  if (response.error === 'NETWORK_ERROR') {
    alert('Network Error');
  } else if (response && response.result && response.result.resultCode === 'SUCCESS') {
    const realidUrl = 'https://sg-production-cdn.zoloz.com/page/zoloz-realid-fe/index.html';
    const clientCfg = response.clientCfg;
    window.location.href = `${realidUrl}?clientcfg=${encodeURIComponent(clientCfg)}`;
  } else {
    alert('System Error');
  }
}

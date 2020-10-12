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

window.onload = () => {
  var elements = document.getElementsByClassName('routerView');
  for (const element of elements) {
    element.style.minHeight = window.innerHeight + 'px';
  }
}

initFace = (data) => {
  const url = baseUrl + '/api/facecapture/initialize';
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

scanFace = async () => {
  const initData = { };
  const response = await initFace(initData);
  console.log(response);
  if (response.error === 'NETWORK_ERROR') {
    alert('Network Error');
  } else if (response.result && response.result.resultCode === 'SUCCESS') {
    const faceUrl = 'https://sg-production-cdn.zoloz.com/page/zoloz-face-fe/index.html';
    const state = response.transactionId;
    const clientCfg = response.clientCfg;
    const href = window.location.href;
    const baseUrl = href.split('#')[0];
    const callbackUrl = baseUrl + '/result.html';
    window.location.href = `${faceUrl}?state=${state}&clientcfg=${encodeURIComponent(clientCfg)}&callbackurl=${encodeURIComponent(callbackUrl)}`;
  } else {
    alert('System Error');
  }
}

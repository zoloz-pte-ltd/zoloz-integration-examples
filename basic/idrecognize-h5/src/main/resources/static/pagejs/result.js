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
var imageSrcEl = document.getElementById('imageSrc');
var resultEl = document.getElementById('result');
var idNumberEl = document.getElementById('idNumber');
var recognationResultEl = document.getElementById('recognationResult');
var imageSrc = '';
var result = '';
var idNumber = '';
var recognationResult = '';
window.onload = () => {
  this.created();
  for (const element of elements) {
    element.style.minHeight = window.innerHeight + 'px';
  }
}

checkDocPro = async (data) => {
  const url = '/api/idrecognition/checkresult';
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

created = async () => {
  const response = JSON.parse(decodeURIComponent(getUrlParam('response')));
  const state = response.state;
  if (response.code === 1000 || response.code === 2006) {
    const checkData = {
      transactionId: state,
    };
    const results = await checkDocPro(checkData);
    if (results.retCode === 404) {
      alert('Network Error');
    } else {
      if (results.extInfo && results.extInfo.imageContent && results.extInfo.imageContent.length) {
        imageSrc = `data:image/png;base64,${results.extInfo.imageContent[0]}`;
      }
      result = results.result.resultCode;
      if (results.extInfo && results.extInfo.ocrResult && results.extInfo.ocrResult.ID_NUMBER) {
        idNumber = results.extInfo.ocrResult.ID_NUMBER;
      }
      if (results.extInfo && results.extInfo.recognitionResult) {
        recognationResult = results.extInfo.recognitionResult;
      }
      resultEl.innerHTML = this.result;
      idNumberEl.innerHTML = this.idNumber;
      recognationResultEl.innerHTML = this.recognationResult;
      imageSrcEl.setAttribute('src', this.imageSrc);
    }
  } else {
    window.history.back();
  }
}

okClick = () => {
  window.history.go(-1);
}
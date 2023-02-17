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
var docImageEl = document.getElementById('docImage');
var faceImageEl = document.getElementById('faceImage');
var faceImageElS = document.getElementById('faceImageEyeClose');
var ekycResultEl = document.getElementById('ekycResult');
var scoreEl = document.getElementById('score');
var riskEl = document.getElementById('risk');
var interruptEl = document.getElementById('interrupt');
var resultMainEl = document.getElementById('resultMain');
var docImage = '';
var faceImage = '';
var faceImageEyeClose = '';
var ekycResult = '';
var score = '';
var risk = '';
window.onload = () => {
  this.created();
  for (const element of elements) {
    element.style.minHeight = window.innerHeight + 'px';
  }
}


checkResult = async (data) => {
  const url = baseUrl + '/api/checkresult';
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
  if (response.code === 1003) {
    interruptEl.style.display = 'block';
    resultMainEl.style.display = 'none';
    return;
  }
  const checkData = {
    transactionId: state,
    isReturnImage: 'Y',
    extraImageControlList: ['FACE_EYE_CLOSE']
  }
  const result = await checkResult(checkData);
  if (result.retCode === 404) {
    alert('Network Error');
  } else {
    if (result.extIdInfo && result.extIdInfo.frontPageImg) {
      docImage = `data:image/png;base64,${result.extIdInfo.frontPageImg}`;
    }
    if (result.extFaceInfo && result.extFaceInfo.faceImg) {
      faceImage = `data:image/png;base64,${result.extFaceInfo.faceImg}`;
    }
    if (result.extFaceInfo && result.extFaceInfo.extraImages && result.extFaceInfo.extraImages.FACE_EYE_CLOSE) {
      faceImageEyeClose = `data:image/png;base64,${result.extFaceInfo.extraImages.FACE_EYE_CLOSE}`;
    }
    ekycResult = result.ekycResult || '-';
    score = (result.extFaceInfo && result.extFaceInfo.faceScore.toFixed(2)) || '-';
    if (result.extRiskInfo && result.extRiskInfo.ekycResultRisk) {
      risk = result.extRiskInfo.ekycResultRisk;
    } else {
      risk = '-';
    }
    ekycResultEl.innerHTML = this.ekycResult;
    scoreEl.innerHTML = this.score;
    riskEl.innerHTML = this.risk;
    docImageEl.setAttribute('src', this.docImage)
    faceImageEl.setAttribute('src', this.faceImage)
    faceImageElS.setAttribute('src', this.faceImageEyeClose)
  }
}

okClick = () => {
  window.history.go(-1);
}

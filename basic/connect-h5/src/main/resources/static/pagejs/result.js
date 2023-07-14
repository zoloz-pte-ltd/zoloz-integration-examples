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

let elements = document.getElementsByClassName("routerView");
let faceImageEl = document.getElementById("faceImage");
let refImageEl = document.getElementById("refImage");
let scoreEl = document.getElementById("score");
let riskEl = document.getElementById("risk");
let resultEl = document.getElementById("result");

let imageSrc = "";
let refSrc = "";
let result = "";
let score = "";
let risk = "";

window.onload = () => {
  this.created();
  for (const element of elements) {
    element.style.minHeight = window.innerHeight + "px";
  }
};

checkResult = async (data) => {
  const url = baseUrl + "/api/connect/checkresult";
  const options = {
    method: "POST",
    mode: "cors",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  };
  return request(url, options);
};

created = async () => {
  const response = JSON.parse(decodeURIComponent(getUrlParam("response")));
  const transactionId = response.state;
  if (response.code === 1000 || response.code === 2006) {
    const checkData = {
      transactionId: transactionId,
    };
    const result = await checkResult(checkData);
    if (
      result.result.resultCode !== "SUCCESS" &&
      result.result.resultCode !== "NOT_SAME_PERSON"
    ) {
      alert("Network Error");
    } else {
      if (result.extFaceInfo) {
        if (result.extFaceInfo.aliveImage) {
          this.imageSrc = `data:image/png;base64,${result.extFaceInfo.aliveImage}`;
        }
        if (result.extFaceInfo.refImage) {
          this.refSrc = `data:image/png;base64,${result.extFaceInfo.refImage}`;
        }
        if (result.extFaceInfo.faceScore) {
          this.score = result.extFaceInfo.faceScore;
        }
        this.risk = String(result.extFaceInfo.faceAttack);
        faceImageEl.setAttribute("src", this.imageSrc);
        refImageEl.setAttribute("src", this.refSrc);
        resultEl.innerHTML = result.result.resultCode;
        scoreEl.innerHTML = this.score;
        riskEl.innerHTML = this.risk;
      }
    }
  } else {
    window.history.back();
  }
};

okClick = () => {
  window.history.go(-1);
};

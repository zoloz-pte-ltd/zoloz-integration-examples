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
  let elements = document.getElementsByClassName("routerView");
  for (const element of elements) {
    element.style.minHeight = window.innerHeight + "px";
  }
  setOptions(livenessMode, "livenessMode");
  setOptions(antiInjectionMode, "antiInjectionMode");
  setOptions(actionRandom, "actionRandom");
  setOptions(sceneCode, "sceneCode");
  // setCheckbox(actionFrame, "actionFrame");
  setCheckbox(actionCheckItems, "actionCheckItems");
};

setOptions = (data, key) => {
  let htmlStr = "";
  data.forEach((e) => {
    if (e.selected) {
      htmlStr +=
        "<option value='" + e.value + "' selected>" + e.label + "</option>";
    } else {
      htmlStr += "<option value='" + e.value + "'>" + e.label + "</option>";
    }
  });
  document.getElementById(key).innerHTML += htmlStr;
};

setCheckbox = (data, key) => {
  let htmlStr = "";
  data.forEach((e) => {
    if (e.selected) {
      htmlStr +=
        "<div><input type='checkbox' name='" +
        key +
        "'  value='" +
        e.value +
        "' checked />" +
        e.label +
        "</div>";
    } else {
      htmlStr +=
        "<div><input type='checkbox' name='" +
        key +
        "'  value='" +
        e.value +
        "' />" +
        e.label +
        "</div>";
    }
  });
  document.getElementById(key).innerHTML += htmlStr;
};

getOption = (key) => {
  const elem = document.getElementById(key);
  return elem.options[elem.selectedIndex].value;
};

getCheckbox = (key) => {
  const elem = document.getElementsByName(key);
  const array = [];
  elem.forEach((e) => {
    if (e.checked) {
      array.push(e.value);
    }
  });
  return array;
};

initFace = (data) => {
  const url = baseUrl + "/api/connect/initialize";
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

scanFace = async () => {
  const sceneCode = getOption("sceneCode");
  const livenessMode = getOption("livenessMode");
  const antiInjectionMode = getOption("antiInjectionMode");
  const actionRandom = getOption("actionRandom");
  // const actionFrame = getCheckbox("actionFrame");
  const actionCheckItems = getCheckbox("actionCheckItems");
  const initData = {
    metaInfo: "MOB_H5",
    sceneCode: sceneCode ? sceneCode : "",
    productConfig: {
      livenessMode: livenessMode ? livenessMode : "",
      antiInjectionMode: antiInjectionMode ? antiInjectionMode : "",
      actionRandom: actionRandom ? actionRandom : "",
      // actionFrame: actionFrame,
      actionCheckItems: actionCheckItems,
    },
  };
  const response = await initFace(initData);
  if (response.error === "NETWORK_ERROR") {
    alert("Network Error");
  } else if (response.result && response.result.resultCode === "SUCCESS") {
    const faceUrl =
      "https://sg-production-cdn.zoloz.com/page/zoloz-face-fe/index.html";
    const transactionId = response.transactionId;
    const clientCfg = response.clientCfg;
    const href = window.location.href;
    const baseUrl = href.split("#")[0];
    const callbackUrl = baseUrl + "/result.html";
    let jumpUrl = `${faceUrl}?state=${transactionId}&clientcfg=${encodeURIComponent(
      clientCfg
    )}&callbackurl=${encodeURIComponent(callbackUrl)}`;
    window.location.href = jumpUrl;
  } else {
    alert("System Error");
  }
};

const serviceLevel = [
  { label: "C1", value: "C1" },
  { label: "C2", value: "C2" },
  { label: "C3", value: "C3" },
];

const livenessMode = [
  { label: "CLOSED", value: "CLOSED" },
  { label: "LOOSE", value: "LOOSE" },
  { label: "STANDARD", value: "STANDARD", selected: true },
  { label: "STRICT", value: "STRICT" },
];

const antiInjectionMode = [
  { label: "CLOSED", value: "CLOSED", selected: true },
  { label: "LOOSE", value: "LOOSE" },
  { label: "STANDARD", value: "STANDARD" },
  { label: "STRICT", value: "STRICT" },
];

const actionCheckItems = [
  { label: "FACEBLINK", value: "FACEBLINK", selected: true },
  { label: "MOUTHOPEN", value: "MOUTHOPEN" },
  { label: "HEADSHAKE", value: "HEADSHAKE" },
  { label: "HEADLOWER", value: "HEADLOWER" },
  { label: "HEADRAISE", value: "HEADRAISE" },
];

const actionFrame = [{ label: "EYECLOSE", value: "EYECLOSE" }];

const actionRandom = [
  { label: "Y", value: "Y" },
  { label: "N", value: "N", selected: true },
];

const sceneCode = [
  { label: "LOGIN", value: "login" },
  { label: "RISKVERIFY", value: "riskVerify" },
  { label: "PAYMENT", value: "payment" },
  { label: "CHANGE PASSWORD", value: "changePassword" },
];

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

let faceImageEl = document.getElementById("faceImage");

let imageSrc = "";

window.onload = () => {
  let elements = document.getElementsByClassName("routerView");
  for (const element of elements) {
    element.style.minHeight = window.innerHeight + "px";
  }
};

changePic = () => {
  const fileRead = new FileReader();
  const f = document.getElementById("imgFile").files[0];
  fileRead.readAsDataURL(f);
  fileRead.onload = function (e) {
    faceImageEl.src = this.result;
    imageSrc = e.target.result;
  };
};

enrollClick = async () => {
  const base64Label = "base64,";
  const base64ImageContent = imageSrc.substring(
    imageSrc.indexOf(base64Label) + base64Label.length
  );
  const data = {
    base64ImageContent: base64ImageContent,
  };
  if (!imageSrc || imageSrc === "") {
    alert("Please input one picture.");
  } else {
    const url = baseUrl + "/api/connect/enroll";
    const options = {
      method: "POST",
      mode: "cors",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    };
    const response = await request(url, options);
    if (response.error === "NETWORK_ERROR") {
      alert("Network Error");
    } else if (response.result && response.result.resultCode === "SUCCESS") {
      alert("Enroll Success!");
    } else {
      alert("System Error");
    }
  }
};

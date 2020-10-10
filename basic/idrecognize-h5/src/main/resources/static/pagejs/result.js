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
  const state = getUrlParam('state');
  if (response.code === 1000 || response.code === 2006) {
    const checkData = {
      transactionId: state,
    };
    const results = await checkDocPro(checkData);
    if (results.retCode === 404) {
      alert('Network Error');
    } else {
      if (results.zDocExtInfo && results.zDocExtInfo.imageContent && results.zDocExtInfo.imageContent.length) {
        imageSrc = `data:image/png;base64,${results.zDocExtInfo.imageContent[0]}`;
      }
      result = results.result.resultCode;
      if (results.zDocExtInfo && results.zDocExtInfo.ocrResult && results.zDocExtInfo.ocrResult.ID_NUMBER) {
        idNumber = results.zDocExtInfo.ocrResult.ID_NUMBER;
      }
      if (results.zDocExtInfo && results.zDocExtInfo.recognitionResult) {
        recognationResult = results.zDocExtInfo.recognitionResult;
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
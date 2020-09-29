  var elements = document.getElementsByClassName('routerView');
  var docImageEl = document.getElementById('docImage');
  var faceImageEl = document.getElementById('faceImage');
  var ekycResultEl = document.getElementById('ekycResult');
  var scoreEl = document.getElementById('score');
  var riskEl = document.getElementById('risk');
  var interruptEl = document.getElementById('interrupt');
  var resultMainEl = document.getElementById('resultMain');
  var docImage = '';
  var faceImage = '';
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
      isReturnImage: 'Y'
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
    }
  }

  okClick = () => {
    window.history.go(-1);
  }

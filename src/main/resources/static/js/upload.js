var responseJson;
var lastImageName;

function loadPuzzle() {
  fetch("/api/v1/puzzle?" + new URLSearchParams({
    rows: 5,
    columns: 5,
    filename: lastImageName,
  }).toString())
  .then(response => response.json())
  .then(data => responseJson = {data})
  .then(loadBoard());
};

document.getElementById('image').onchange = function(){
  lastImageName = String(this.value);
  lastImageName = lastImageName.split('\\')[2].trim();
  console.log(lastImageName);
}
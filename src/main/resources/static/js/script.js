var rows = 5;
var columns = 5;

var currTile;
var otherTile;

var turns = 0;

// window.onload = loadBoard();
window.onload = loadPuzzle();

function loadBoard() {
  fillBoard();
  let pieces = generateTiles();
  fillTiles(pieces);

  let boardElement = document.getElementById("board");
  boardElement.style.width = `${80 * columns}px`;
  boardElement.style.height = `${80 * rows}px`;
}

function fillBoard() {
  let whiteBoard = document.getElementById("board");

  while (whiteBoard.firstChild) {
    whiteBoard.removeChild(whiteBoard.lastChild);
  }

  for (let i = 0; i < rows; i ++) {
    for (let j = 0; j < columns; j++) {
      let tile = document.createElement("img");
      tile.src = "./images/blank.jpg";

      tile.addEventListener("dragstart", dragStart);
      tile.addEventListener("dragover", dragOver);
      tile.addEventListener("dragenter", dragEnter);
      tile.addEventListener("dragleave", dragLeave);
      tile.addEventListener("drop", dragDrop);
      tile.addEventListener("dragend", dragEnd);

      whiteBoard.append(tile);
    }
  }
}

function generateTiles() {
  let pieces = [];
  for(let i = 0; i <= rows*columns-1; i++) {
    pieces.push(i.toString());
  }
  pieces.reverse();
  for(let i = 0; i < pieces.length; i++) {
    let j = Math.floor(Math.random() * pieces.length);

    let tmp = pieces[i];
    pieces[i] = pieces[j];
    pieces[j] = tmp;
  }
  return pieces;
}

/**
 * 
 * @param {Array} pieces 
 */
function fillTiles(pieces) {
  let tilesBoard = document.getElementById("pieces");

  while (tilesBoard.firstChild) {
    tilesBoard.removeChild(tilesBoard.lastChild);
  }
  
  for(let i = 0; i < pieces.length; i++) {
    setTimeout(3);
    let tile = document.createElement("img");
    let dirName = lastImageName.split('.')[0];
    tile.src = `./images/puzzles/${dirName}/${pieces[i]}.png`;
    tile.id = pieces[i];

    tile.addEventListener("dragstart", dragStart);
    tile.addEventListener("dragover", dragOver);
    tile.addEventListener("dragenter", dragEnter);
    tile.addEventListener("dragleave", dragLeave);
    tile.addEventListener("drop", dragDrop);
    tile.addEventListener("dragend", dragEnd);

    tilesBoard.append(tile);
  }
}


function dragStart() {
  currTile = this;
}

/**
 * 
 * @param {Event} e 
 */
function dragOver(e) {
  e.preventDefault();
}

/**
 * 
 * @param {Event} e 
 */
function dragEnter(e) {
  e.preventDefault();
}

function dragLeave() {

}

function dragDrop() {
  otherTile = this;
}

function dragEnd() {
  if (currTile.src.includes("blank")) {
    return;
  }
  let currImg = currTile.src;
  let otherImg = otherTile.src;
  let currId = currTile.id;
  let otherId = otherTile.id;

  currTile.src = otherImg;
  currTile.id = otherId;
  otherTile.src = currImg;
  otherTile.id = currId;

  document.getElementById("turns").innerText = turns++;
}

function check() {
  let board = document.getElementById("board"), child;
  
  const puzzles = [];
  const solved = [];
  for(let i = 0; i < rows*columns; i++) {
    solved.push(i);
  }

  for (let i = 0; i < board.childNodes.length; i++) {
    child = board.childNodes[i];
    if(child.id !== "") {
      puzzles.push(Number(child.id));
    }
  }

  compare(puzzles, solved);
}


/**
 * 
 * @param {Array} puzzles - array of puzzles indexes
 * @param {Array} solved - array of answer indexes
 */
function compare(puzzles, solved) {
  let isSolved = true;
  let message = document.createElement("h2");

  for(let i = 0; i < puzzles.length; i++) {
    if(puzzles[i] !== solved[i]) {
      isSolved = false;
      break;
    }
  }

  if (isSolved === false || puzzles.length === 0) {
    message.innerText = "You are wrong :(";
    message.style.color = "red";
    message.id = "congrats";
    if (document.getElementById("congrats") === null) {
      document.getElementById("turns").append(message);
    }
    return;
  }
  message.innerText = "Congrats!";
  message.style.color = "green";
  message.id = "congrats";
  if (document.getElementById("congrats") === null) {
    document.getElementById("turns").append(message);
  }
}


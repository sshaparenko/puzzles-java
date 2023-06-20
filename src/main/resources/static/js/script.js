var rows = 5;
var columns = 5;

var currTile;
var otherTile;

var turns = 0;

window.onload = function() {
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

      document.getElementById("board").append(tile);
    }
  }

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

  for(let i = 0; i < pieces.length; i++) {
    let tile = document.createElement("img");
    tile.src = `./images/puzzles/5458efac19a676b73986c953c6aba8ae/${pieces[i]}.jpg`;
    tile.id = pieces[i];

    tile.addEventListener("dragstart", dragStart);
    tile.addEventListener("dragover", dragOver);
    tile.addEventListener("dragenter", dragEnter);
    tile.addEventListener("dragleave", dragLeave);
    tile.addEventListener("drop", dragDrop);
    tile.addEventListener("dragend", dragEnd);

    document.getElementById("pieces").append(tile);
  }

  let piecesElement = document.getElementById("pieces");
  let boardElement = document.getElementById("board");
  boardElement.style.width = `${80 * columns}px`;
  boardElement.style.height = `${80 * rows}px`;
}

function dragStart() {
  currTile = this;
}

function dragOver(e) {
  e.preventDefault();
}

function dragEnter(e) {
  e.preventDefault();
}

function dragLeave() {

}

function dragDrop() {
  otherTile = this;
}

//bag: with swapping two puzzles, the ids should not be changed!
function dragEnd() {
  if (currTile.src.includes("blank")) {
    return;
  }
  if (currTile.id !== null && otherTile.id !== null) {

  }
  let currImg = currTile.src;
  let otherImg = otherTile.src;
  let currId = currTile.id;
  let otherId = otherTile.id;

  currTile.src = otherImg;
  currTile.id = otherId;
  otherTile.src = currImg;
  otherTile.id = currId;

  turns += 1;
  document.getElementById("turns").innerText = turns;
}

function check() {
  let board = document.getElementById("board"), child;
  let message = document.createElement("h2");
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

  console.log(solved);
  console.log(puzzles);

  for(let i = 0; i < puzzles.length; i++) {
    if(puzzles[i] !== solved[i]) {
      message.innerText = "You are wrong :(";
      message.style.color = "red";
      document.getElementById("turns").append(message);
      return false;
    } else {
      message.innerText = "Congrats!";
      message.style.color = "green";
      document.getElementById("turns").append(message);
      return true;
    }
  }
}
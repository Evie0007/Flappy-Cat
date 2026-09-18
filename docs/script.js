// Flappy Cat - HTML5 Canvas port of the original Java Swing game
// Original: App.java / FlappyCat.java / TitleScreen.java / GameOverScreen.java

const canvas = document.getElementById("board");
const ctx = canvas.getContext("2d");

const boardWidth = canvas.width;
const boardHeight = canvas.height;

function loadImage(src) {
  const img = new Image();
  img.src = src;
  return img;
}

const images = {
  titleBg: loadImage("assets/bkg_1.png"),
  gameBg: loadImage("assets/bkg_2.png"),
  gameOverBg: loadImage("assets/bkg_3.png"),
  cat: loadImage("assets/donutcat.png"),
  topPipe: loadImage("assets/toppipe1.png"),
  bottomPipe: loadImage("assets/bottompipe1.png"),
};

const catWidth = 70;
const catHeight = 70;
const catStartX = boardWidth / 8;
const catStartY = boardHeight / 2;

const pipeWidth = 80;
const pipeHeight = 500;

const gravity = 1;

let highestScore = 0;

let state = "title"; // "title" | "playing" | "gameover"

// --- Game state (re-created each run) ---
let cat, pipes, velocityX, velocityY, speedMultiplier, scoreThreshold, score, gameOver;
let lastFrameTime = 0;
let placePipeIntervalId = null;
let animationFrameId = null;
let lastScore = 0;

function resetGame() {
  cat = { x: catStartX, y: catStartY, width: catWidth, height: catHeight };
  pipes = [];
  velocityX = -4;
  velocityY = 0;
  speedMultiplier = 1.0;
  scoreThreshold = 10;
  score = 0;
  gameOver = false;
}

function placePipes() {
  const randomPipeY = 0 - pipeHeight / 3 - Math.random() * (pipeHeight / 2);
  const openingSpace = boardHeight / 2.5;

  pipes.push({ x: boardWidth, y: randomPipeY, width: pipeWidth, height: pipeHeight, img: images.topPipe, passed: false });
  pipes.push({ x: boardWidth, y: randomPipeY + pipeHeight + openingSpace, width: pipeWidth, height: pipeHeight, img: images.bottomPipe, passed: false });
}

function collision(a, b) {
  return (
    a.x < b.x + b.width &&
    a.x + a.width > b.x &&
    a.y < b.y + b.height &&
    a.y + a.height > b.y
  );
}

function move() {
  velocityY += gravity;
  cat.y += velocityY;
  cat.y = Math.max(cat.y, 0);

  for (const pipe of pipes) {
    pipe.x += velocityX * speedMultiplier;

    if (!pipe.passed && cat.x > pipe.x + pipe.width) {
      score += 0.5;
      pipe.passed = true;
    }

    if (collision(cat, pipe)) {
      gameOver = true;
    }
  }

  if (cat.y > boardHeight) {
    gameOver = true;
  }

  if (score >= scoreThreshold) {
    speedMultiplier += 0.2;
    scoreThreshold += 10;
  }
}

function drawGame() {
  ctx.drawImage(images.gameBg, 0, 0, boardWidth, boardHeight);
  ctx.drawImage(images.cat, cat.x, cat.y, cat.width, cat.height);

  for (const pipe of pipes) {
    ctx.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height);
  }

  ctx.fillStyle = "white";
  ctx.font = "32px Calibri, sans-serif";
  ctx.textBaseline = "alphabetic";
  ctx.fillText(String(Math.trunc(score)), 10, 40);
}

function drawTitle() {
  ctx.drawImage(images.titleBg, 0, 0, boardWidth, boardHeight);
}

function drawGameOver() {
  ctx.drawImage(images.gameOverBg, 0, 0, boardWidth, boardHeight);

  ctx.fillStyle = "white";
  ctx.font = "bold 24px Calibri, sans-serif";
  ctx.textBaseline = "alphabetic";

  const scoreText = "Score: " + Math.trunc(lastScore);
  const highestScoreText = "Highest Score: " + Math.trunc(highestScore);

  ctx.textAlign = "center";
  ctx.fillText(scoreText, boardWidth / 2, 70);
  ctx.fillText(highestScoreText, boardWidth / 2, 110);
  ctx.textAlign = "left";
}

const FRAME_MS = 1000 / 60;

function gameLoop(timestamp) {
  if (state !== "playing") return;

  if (timestamp - lastFrameTime >= FRAME_MS) {
    lastFrameTime = timestamp;
    move();
    drawGame();

    if (gameOver) {
      endGame();
      return;
    }
  }

  animationFrameId = requestAnimationFrame(gameLoop);
}

function startGame() {
  state = "playing";
  resetGame();
  placePipes();
  placePipeIntervalId = setInterval(placePipes, 1500);
  lastFrameTime = 0;
  animationFrameId = requestAnimationFrame(gameLoop);
}

function endGame() {
  clearInterval(placePipeIntervalId);
  cancelAnimationFrame(animationFrameId);
  lastScore = score;
  if (score > highestScore) highestScore = score;
  state = "gameover";
  drawGameOver();
}

function showTitle() {
  state = "title";
  drawTitle();
}

// --- Input ---
canvas.tabIndex = 0;
canvas.focus();

window.addEventListener("keydown", (e) => {
  if (e.code !== "Space") return;
  e.preventDefault();

  if (state === "title") {
    startGame();
  } else if (state === "playing") {
    velocityY = -9;
  } else if (state === "gameover") {
    startGame();
  }
});

canvas.addEventListener("mousedown", () => {
  if (state === "gameover") {
    startGame();
  }
});

canvas.addEventListener("touchstart", (e) => {
  e.preventDefault();
  if (state === "title") {
    startGame();
  } else if (state === "playing") {
    velocityY = -9;
  } else if (state === "gameover") {
    startGame();
  }
});

// --- Boot: wait for images before showing the title screen ---
let loadedCount = 0;
const totalImages = Object.keys(images).length;
Object.values(images).forEach((img) => {
  img.onload = () => {
    loadedCount++;
    if (loadedCount === totalImages) {
      showTitle();
    }
  };
});

const canvas = document.getElementById("maze-canvas");
const context = canvas.getContext("2d");

const state = {
  maze: null,
  solution: [],
  start: null,
  end: null,
  pickMode: null,
};

const elements = {
  statusBar: document.getElementById("status-bar"),
  hintBar: document.getElementById("hint-bar"),
  mazeFile: document.getElementById("maze-file"),
  generateRows: document.getElementById("generate-rows"),
  generateCols: document.getElementById("generate-cols"),
  startRow: document.getElementById("start-row"),
  startCol: document.getElementById("start-col"),
  endRow: document.getElementById("end-row"),
  endCol: document.getElementById("end-col"),
  pickStart: document.getElementById("pick-start"),
  pickEnd: document.getElementById("pick-end"),
  pickClear: document.getElementById("pick-clear"),
};

document.getElementById("upload-maze").addEventListener("click", uploadMaze);
document.getElementById("generate-maze").addEventListener("click", generateMaze);
document.getElementById("solve-maze").addEventListener("click", solveMaze);
elements.pickStart.addEventListener("click", () => setPickMode("start"));
elements.pickEnd.addEventListener("click", () => setPickMode("end"));
elements.pickClear.addEventListener("click", () => setPickMode(null));
canvas.addEventListener("click", handleCanvasClick);

function setStatus(message) {
  elements.statusBar.textContent = message;
}

function setHint(message) {
  elements.hintBar.textContent = message;
}

function setPickMode(mode) {
  state.pickMode = mode;
  elements.pickStart.classList.toggle("active", mode === "start");
  elements.pickEnd.classList.toggle("active", mode === "end");
  if (mode === "start") {
    setHint("Click a maze cell to set the start point.");
  } else if (mode === "end") {
    setHint("Click a maze cell to set the end point.");
  } else {
    setHint("Choose a pick mode and click inside the maze canvas to fill coordinates.");
  }
}

function updateCoordinateInputs() {
  elements.startRow.value = state.start ? state.start.row : "";
  elements.startCol.value = state.start ? state.start.col : "";
  elements.endRow.value = state.end ? state.end.row : "";
  elements.endCol.value = state.end ? state.end.col : "";
}

async function uploadMaze() {
  const file = elements.mazeFile.files[0];
  if (!file) {
    setStatus("Choose a maze file first.");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch("/api/mazes/upload", {
    method: "POST",
    body: formData,
  });
  await handleMazeResponse(response, "Loaded maze from file.");
}

async function generateMaze() {
  const response = await fetch("/api/mazes/generate", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      rows: Number(elements.generateRows.value),
      cols: Number(elements.generateCols.value),
    }),
  });
  await handleMazeResponse(response, "Generated maze.");
}

async function handleMazeResponse(response, successMessage) {
  const payload = await response.json();
  if (!response.ok) {
    setStatus(payload.message || "Maze operation failed.");
    return;
  }

  state.maze = payload;
  state.solution = [];
  state.start = null;
  state.end = null;
  updateCoordinateInputs();
  drawScene();
  setStatus(`${successMessage} ${payload.rows}x${payload.cols}`);
  setPickMode(null);
}

async function solveMaze() {
  if (!state.maze) {
    setStatus("Load or generate a maze before solving it.");
    return;
  }

  state.start = {
    row: Number(elements.startRow.value),
    col: Number(elements.startCol.value),
  };
  state.end = {
    row: Number(elements.endRow.value),
    col: Number(elements.endCol.value),
  };

  const response = await fetch("/api/mazes/solve", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      maze: state.maze,
      start: state.start,
      end: state.end,
    }),
  });
  const payload = await response.json();
  if (!response.ok) {
    setStatus(payload.message || "Maze solving failed.");
    return;
  }

  state.solution = payload.path;
  drawScene();
  setStatus(`Solved maze in ${payload.path.length} cells.`);
}

function handleCanvasClick(event) {
  if (!state.maze) {
    return;
  }

  const rect = canvas.getBoundingClientRect();
  const scaleX = canvas.width / rect.width;
  const scaleY = canvas.height / rect.height;
  const x = (event.clientX - rect.left) * scaleX;
  const y = (event.clientY - rect.top) * scaleY;
  const cell = cellFromCanvasClick(x, y, state.maze);

  if (!cell) {
    return;
  }

  if (state.pickMode === "start") {
    state.start = cell;
    state.solution = [];
    updateCoordinateInputs();
    drawScene();
    setStatus(`Selected start point: ${cell.row}, ${cell.col}`);
  } else if (state.pickMode === "end") {
    state.end = cell;
    state.solution = [];
    updateCoordinateInputs();
    drawScene();
    setStatus(`Selected end point: ${cell.row}, ${cell.col}`);
  } else {
    setStatus(`Clicked cell: ${cell.row}, ${cell.col}`);
  }
}

function drawScene() {
  context.clearRect(0, 0, canvas.width, canvas.height);
  context.fillStyle = "#ffffff";
  context.fillRect(0, 0, canvas.width, canvas.height);

  if (!state.maze) {
    return;
  }

  drawMaze(state.maze);
  if (state.solution.length > 0) {
    drawSolution(state.solution, state.maze);
  }
  if (state.start) {
    drawMarker(state.start, state.maze, "#228b22");
  }
  if (state.end) {
    drawMarker(state.end, state.maze, "#dc143c");
  }
}

function drawMaze(maze) {
  const metrics = renderMetrics(maze);
  context.strokeStyle = "#000000";
  context.lineWidth = metrics.wallThickness;
  context.strokeRect(0, 0, metrics.fieldSize, metrics.fieldSize);

  for (let row = 0; row < maze.rows; row += 1) {
    for (let col = 0; col < maze.cols; col += 1) {
      const x = col * metrics.cellWidth;
      const y = row * metrics.cellHeight;

      if (maze.rightWalls[row][col]) {
        const rightX = x + metrics.cellWidth;
        context.beginPath();
        context.moveTo(rightX, y);
        context.lineTo(rightX, y + metrics.cellHeight);
        context.stroke();
      }

      if (maze.bottomWalls[row][col]) {
        const bottomY = y + metrics.cellHeight;
        context.beginPath();
        context.moveTo(x, bottomY);
        context.lineTo(x + metrics.cellWidth, bottomY);
        context.stroke();
      }
    }
  }
}

function drawSolution(path, maze) {
  const metrics = renderMetrics(maze);
  context.strokeStyle = "#1d4ed8";
  context.lineWidth = metrics.wallThickness;

  for (let index = 0; index < path.length - 1; index += 1) {
    const from = path[index];
    const to = path[index + 1];
    context.beginPath();
    context.moveTo(centerX(from, metrics), centerY(from, metrics));
    context.lineTo(centerX(to, metrics), centerY(to, metrics));
    context.stroke();
  }
}

function drawMarker(cell, maze, color) {
  const metrics = renderMetrics(maze);
  const radius = Math.min(metrics.cellWidth, metrics.cellHeight) * 0.18;
  context.fillStyle = color;
  context.beginPath();
  context.arc(centerX(cell, metrics), centerY(cell, metrics), radius, 0, Math.PI * 2);
  context.fill();
}

function cellFromCanvasClick(x, y, maze) {
  const metrics = renderMetrics(maze);
  if (x < 0 || y < 0 || x > metrics.fieldSize || y > metrics.fieldSize) {
    return null;
  }
  return {
    row: Math.min(maze.rows, Math.floor(y / metrics.cellHeight) + 1),
    col: Math.min(maze.cols, Math.floor(x / metrics.cellWidth) + 1),
  };
}

function renderMetrics(maze) {
  return {
    fieldSize: 500,
    wallThickness: 2,
    cellWidth: 500 / maze.cols,
    cellHeight: 500 / maze.rows,
  };
}

function centerX(cell, metrics) {
  return (cell.col - 0.5) * metrics.cellWidth;
}

function centerY(cell, metrics) {
  return (cell.row - 0.5) * metrics.cellHeight;
}

drawScene();

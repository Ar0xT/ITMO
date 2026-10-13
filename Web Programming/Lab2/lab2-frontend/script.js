// lab 2 page script it  sends points to the Java server and shows its answers

const SERVER_URL = "/fcgi-bin/server.jar";

const form = document.getElementById("pointForm");
const errorBox = document.getElementById("error");
const yInput = document.getElementById("y");
const tableBody = document.getElementById("results-body");
const xButtons = document.querySelectorAll("button[data-x]");
const rBoxes = document.querySelectorAll('input[name="r"]');

const canvas = document.getElementById("myCanvas");
const ctx = canvas.getContext("2d");
const CENTER = canvas.width / 2;   // the axes cross in the middle of the canvas
const SCALE = 55;                  // pixels per unit

let selectedX = null;
let selectedR = null;


// X buttons, the clicked one gets highlighted and remembered
for (const button of xButtons) {
    button.addEventListener("click", () => {
        for (const other of xButtons) {
            other.classList.remove("selected");
        }
        button.classList.add("selected");
        selectedX = button.dataset.x;
    });
}

// R checkboxes, ticking one unticks the others
for (const box of rBoxes) {
    box.addEventListener("change", () => {
        for (const other of rBoxes) {
            if (other !== box) {
                other.checked = false;
            }
        }
        selectedR = box.checked ? box.value : null;
        drawArea();
    });
}

// the Check button
form.addEventListener("submit", event => {
    // keep the page, the request is sent by fetch instead
    event.preventDefault();

    const y = yInput.value.trim().replace(",", ".");
    const error = validate(selectedX, y, selectedR);
    if (error !== null) {
        showError(error);
        return;
    }

    const query = new URLSearchParams({ x: selectedX, y: y, r: selectedR });
    sendRequest(SERVER_URL + "?" + query);
});

// the Clear button resets the inputs itself, this clears the rest
form.addEventListener("reset", () => {
    for (const button of xButtons) {
        button.classList.remove("selected");
    }
    selectedX = null;
    selectedR = null;
    showError("");
    drawArea();
});


// returns a message about the first problem, or null when everything is fine
function validate(x, y, r) {
    if (x === null) {
        return "Pick an X value";
    }
    if (!/^-?\d+(\.\d+)?$/.test(y)) {
        return "Y must be a number like 1.5 or -2";
    }
    // only the whole part is compared, so a long input like 2.9999999999 is not rounded up to 3
    if (Math.abs(parseInt(y)) >= 3) {
        return "Y must be strictly between -3 and 3";
    }
    if (r === null) {
        return "Tick one R value";
    }
    return null;
}

// sends a GET request and shows either the history or the server's error
async function sendRequest(url) {
    try {
        const response = await fetch(url);
        const data = await response.json();
        if (response.ok) {
            showError("");
            showResults(data.results);
        } else {
            showError(data.error);
        }
    } catch (e) {
        showError("The server is not available");
    }
}

function showError(message) {
    errorBox.textContent = message;
}

// fills the table with every result the server sent
function showResults(results) {
    tableBody.replaceChildren();
    for (const result of results) {
        const row = document.createElement("tr");
        addCell(row, result.x);
        addCell(row, result.y);
        addCell(row, result.r);
        addCell(row, result.hit ? "Hit" : "Miss");
        addCell(row, new Date(result.time).toLocaleString("ru-RU"));
        addCell(row, (result.execTimeNs / 1000000).toFixed(3) + " ms");
        tableBody.appendChild(row);
    }
}

function addCell(row, text) {
    const cell = document.createElement("td");
    cell.textContent = text;
    row.appendChild(cell);
}


// draws the area for the ticked R, or for an R of 3 when none is ticked
function drawArea() {
    const r = selectedR === null ? 3 : Number(selectedR);
    const size = r * SCALE;   // R in pixels
    const half = size / 2;    // half of R in pixels

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = "#2596be";

    // square, top right
    ctx.fillRect(CENTER, CENTER - size, size, size);

    // quarter circle, bottom right
    ctx.beginPath();
    ctx.moveTo(CENTER, CENTER);
    ctx.arc(CENTER, CENTER, half, 0, Math.PI / 2);
    ctx.fill();

    // triangle, bottom left
    ctx.beginPath();
    ctx.moveTo(CENTER, CENTER);
    ctx.lineTo(CENTER - half, CENTER);
    ctx.lineTo(CENTER, CENTER + size);
    ctx.fill();

    drawAxes(size, half);
}

// both axes, with tick marks and labels at R and half of R
function drawAxes(size, half) {
    const marks = [
        { offset: size, label: "R" },
        { offset: half, label: "R/2" },
        { offset: -half, label: "-R/2" },
        { offset: -size, label: "-R" }
    ];

    ctx.strokeStyle = "black";
    ctx.fillStyle = "black";
    ctx.font = "12px Arial";

    ctx.beginPath();
    // the two axes
    ctx.moveTo(0, CENTER);
    ctx.lineTo(canvas.width, CENTER);
    ctx.moveTo(CENTER, 0);
    ctx.lineTo(CENTER, canvas.height);
    // a small tick at every mark on both axes
    for (const mark of marks) {
        ctx.moveTo(CENTER + mark.offset, CENTER - 4);
        ctx.lineTo(CENTER + mark.offset, CENTER + 4);
        ctx.moveTo(CENTER - 4, CENTER - mark.offset);
        ctx.lineTo(CENTER + 4, CENTER - mark.offset);
    }
    ctx.stroke();

    for (const mark of marks) {
        ctx.fillText(mark.label, CENTER + mark.offset - 8, CENTER + 16);   // under the x axis
        ctx.fillText(mark.label, CENTER + 6, CENTER - mark.offset + 4);    // next to the y axis
    }
    ctx.fillText("x", canvas.width - 10, CENTER - 6);
    ctx.fillText("y", CENTER + 6, 10);
}


// draw the default area and load the history as soon as the page opens
drawArea();
sendRequest(SERVER_URL);
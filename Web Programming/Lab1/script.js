const pointForm = document.querySelector("#pointForm");
const table = document.getElementById("results");
let results = [];
const saved = localStorage.getItem("results");
if (saved !== null) {
    results = JSON.parse(saved);
}
results.forEach(function (item) {
    addRow(table, item.x, item.y, item.r, item.hitStatus, item.time);
})
const canvas = document.getElementById("myCanvas");
const SIZE = 400;
const originX = SIZE / 2;
const originY = SIZE / 2;
const scale = 35;

const ctx = canvas.getContext('2d');
drawArea(0);
document.getElementById("r").addEventListener("change", function () {
    drawArea(Number(this.value));
});

pointForm.addEventListener("submit", function(event) {
    event.preventDefault();
    
    const errorBox = document.getElementById("error");
    errorBox.textContent = "";

    const x = validateX(errorBox);
    if (x === undefined) return;

    const y = validateY(errorBox);
    if (y === undefined) return;

    const r = validateR(errorBox);
    if (r === undefined) return;

    const hitStatus = hasHit(x, y, r);
    console.log(hitStatus);

    const time = Date.now();
    save(x, y, r, hitStatus, time);
    addRow(table, x, y, r, hitStatus, time);
});


function save(x, y, r, hitStatus, time) {
    results.push({x, y, r, hitStatus, time});
    localStorage.setItem("results", JSON.stringify(results));
}

function hasHit(x, y, r) {
    const inCircle    = x >= 0 && y >= 0 && x*x + y*y <= r*r;
    const inRectangle = x >= 0 && x <= r/2 && y <= 0 && y >= -r;
    const inTriangle  = x <= 0 && y <= 0 && y >= -x - r/2;

    return inCircle || inRectangle || inTriangle;
}

function addRow(table, x, y, r, hitStatus, time) {
    const row = document.createElement("tr");
    addCell(row, x);
    addCell(row, y);
    addCell(row, r);
    addCell(row, hitStatus);
    addCell(row, new Date(time).toLocaleString('ru-RU'));
    table.appendChild(row);
}

function addCell(row, value) {
    const cell = document.createElement("td");
    cell.textContent = value;
    row.appendChild(cell);
}

function validateX(errorBox) {
    const xChecked = document.querySelector('input[name="x"]:checked');
    if (xChecked === null) {
        errorBox.textContent = "Pick an X value";
        return;
    }
    return Number(xChecked.value);
}

function validateY(errorBox) {
    const yRaw = document.getElementById("y").value.trim();
    if (yRaw === "") {
        errorBox.textContent = "Pick a Y value";
        return;
    }
    const y = Number(yRaw);
    if (!(Number.isFinite(y))) {
        errorBox.textContent = "Y must be a number between [-3; 3]";
        return;
    }

    if (y < -3 || y > 3) {
        errorBox.textContent = "Y must be a number between [-3; 3]";
        return;
    }
    return y;
}

function validateR(errorBox) {
    const rRaw = document.getElementById("r").value;
    if (rRaw === "") {
        errorBox.textContent = "Pick an R value";
        return;
    }
    return Number(rRaw);
}


function drawArea(r) {
    ctx.clearRect(0, 0, SIZE, SIZE);
    drawAxis();
    drawCircle(r);
    drawRectangle(r);
    drawTriangle(r);
}

function drawAxis() {
    ctx.beginPath();
    ctx.strokeStyle = "black";
    ctx.moveTo(0, originY);
    ctx.lineTo(SIZE, originY);
    ctx.moveTo(originX, 0);
    ctx.lineTo(originX, SIZE);
    ctx.stroke();
}

function drawCircle(r) {
    ctx.beginPath();
    ctx.moveTo(originX, originY);
    ctx.arc(originX, originY, r * scale, Math.PI * 1.5, Math.PI * 2);
    ctx.closePath();
    ctx.fillStyle = "#2596be";
    ctx.fill();
}

function drawRectangle(r) {
    ctx.fillStyle = "#2596be";
    ctx.fillRect(originX, originY, (r / 2) * scale, r * scale);
}

function drawTriangle(r) {
    ctx.beginPath();
    ctx.moveTo(originX, originY);
    ctx.lineTo(originX - (r / 2) * scale, originY);
    ctx.lineTo(originX, originY + (r / 2) * scale);
    ctx.closePath();
    ctx.fillStyle = "#2596be";
    ctx.fill();
}


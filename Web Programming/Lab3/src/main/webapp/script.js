// canvas height = width = 400, pixels per unit = 60
const SIZE = 400;
const CENTER = SIZE / 2;
const SCALE = 60;

const canvas = document.getElementById("graph");
const ctx = canvas.getContext("2d");
const form = document.getElementById("point-form");
const xInput = document.getElementById("x");
const message = document.getElementById("message");
const rBoxes = document.querySelectorAll('input[name="r"]');

restoreLastR();
redraw();

for (const box of rBoxes) {
    box.addEventListener("change", () => onRChange(box));
}
form.addEventListener("submit", onSubmit);
canvas.addEventListener("click", onCanvasClick);

function restoreLastR() {
    if (points.length === 0) {
        return;
    }
    const lastR = points[points.length - 1].r;
    for (const box of rBoxes) {
        box.checked = Number(box.value) === lastR;
    }
}


function redraw() {
    ctx.clearRect(0, 0, SIZE, SIZE);
    const r = getR();
    if (r !== null) {
        drawArea(r);
    }
    drawAxes();
    if (r !== null) {
        drawTicks(r);
    }
    drawPoints();
}


function getR() {
    for (const box of rBoxes) {
        if (box.checked) {
            return Number(box.value);
        }
    }
    return null;
}

function drawArea(r) {
    ctx.fillStyle = "rgba(51, 153, 255, 0.6)";

    // square
    ctx.fillRect(toPixelX(0), toPixelY(r), SCALE * r, SCALE * r);

    // triangle
    ctx.beginPath();
    ctx.moveTo(toPixelX(0), toPixelY(0));
    ctx.lineTo(toPixelX(-r / 2), toPixelY(0));
    ctx.lineTo(toPixelX(0), toPixelY(r));
    ctx.closePath();
    ctx.fill();

    // quarter circle
    ctx.beginPath();
    ctx.moveTo(toPixelX(0), toPixelY(0));
    ctx.arc(toPixelX(0), toPixelY(0), SCALE * r / 2, 0, Math.PI / 2);
    ctx.closePath();
    ctx.fill();
}

// math coordinates to canvas pixels
function toPixelX(x) {
    return CENTER + SCALE * x;
}

function toPixelY(y) {
    return CENTER - SCALE * y;
}

function drawAxes() {
    ctx.strokeStyle = "black";
    ctx.fillStyle = "black";
    ctx.lineWidth = 1;
    ctx.font = "12px sans-serif";

    ctx.beginPath();
    ctx.moveTo(0, CENTER);
    ctx.lineTo(SIZE, CENTER);
    ctx.moveTo(CENTER, 0);
    ctx.lineTo(CENTER, SIZE);
    ctx.stroke();

    ctx.textAlign = "left";
    ctx.fillText("x", SIZE - 12, CENTER - 8);
    ctx.fillText("y", CENTER + 8, 12);
}

function drawTicks(r) {
    drawTick(-r, "-R");
    drawTick(-r / 2, "-R/2");
    drawTick(r / 2, "R/2");
    drawTick(r, "R");
}


function drawTick(value, label) {
    const px = toPixelX(value);
    const py = toPixelY(value);

    ctx.beginPath();
    ctx.moveTo(px, CENTER - 4);
    ctx.lineTo(px, CENTER + 4);
    ctx.moveTo(CENTER - 4, py);
    ctx.lineTo(CENTER + 4, py);
    ctx.stroke();

    ctx.textAlign = "center";
    ctx.fillText(label, px, CENTER - 8);
    ctx.textAlign = "left";
    ctx.fillText(label, CENTER + 8, py + 4);
}


function drawPoints() {
    for (const p of points) {
        ctx.beginPath();
        ctx.arc(toPixelX(p.x), toPixelY(p.y), 4, 0, 2 * Math.PI);
        ctx.fillStyle = p.hit ? "green" : "red";
        ctx.fill();
    }
}



function onRChange(changedBox) {
    if (changedBox.checked) {
        for (const box of rBoxes) {
            if (box !== changedBox) {
                box.checked = false;
            }
        }
    }
    showMessage("");
    redraw();
}

function showMessage(text) {
    message.textContent = text;
}

function onSubmit(event) {
    const error = validateForm();
    if (error !== null) {
        event.preventDefault();
        showMessage(error);
    }
}

// gives an error message for the user or null when the form is fine
function validateForm() {
    const x = parseNumber(xInput.value);
    if (x === null || x <= -3 || x >= 3) {
        return "X must be a number greater than -3 and less than 3.";
    }
    if (getR() === null) {
        return "Choose R.";
    }
    return null;
}

// gives a number or null when the text is not a plain decimal number
function parseNumber(text) {
    const value = text.trim().replace(",", ".");
    if (!/^[+-]?\d+(\.\d+)?$/.test(value)) {
        return null;
    }
    return Number(value);
}

function onCanvasClick(event) {
    const r = getR();
    if (r === null) {
        showMessage("Can't find the coordinates of the click. Choose R first.");
        return;
    }

    const rect = canvas.getBoundingClientRect();
    const pixelX = (event.clientX - rect.left) * canvas.width / rect.width;
    const pixelY = (event.clientY - rect.top) * canvas.height / rect.height;

    const x = toMathX(pixelX);
    const y = toMathY(pixelY);

    if (x <= -3 || x >= 3 || y < -2 || y > 2) {
        showMessage("This point is outside the allowed range. X must be between -3 and 3 and Y between -2 and 2.");
        return;
    }
    window.location.href = "controller?x=" + x + "&y=" + y + "&r=" + r;
}

// canvas pixels back to math coordinates
function toMathX(pixelX) {
    return Math.round((pixelX - CENTER) / SCALE * 1000) / 1000;
}

function toMathY(pixelY) {
    return Math.round((CENTER - pixelY) / SCALE * 1000) / 1000;
}
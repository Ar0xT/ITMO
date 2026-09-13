const canvas = document.getElementById("myCanvas");
const SIZE = 400;
const originX = SIZE / 2;
const originY = SIZE / 2;
const scale = 35;

const ctx = canvas.getContext('2d');

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

drawArea(2);

document.getElementById("r").addEventListener("change", function () {
    drawArea(Number(this.value));
});
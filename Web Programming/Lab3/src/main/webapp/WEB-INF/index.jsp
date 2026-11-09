<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Lab 3</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>

<header>Tserendorj Ariunbat, P3213, Variant 777</header>

<form id="point-form" action="controller" method="get">
    <label for="x">X, a number from -3 to 3</label>
    <input type="text" id="x" name="x" autocomplete="off">

    <label for="y">Y</label>
    <select id="y" name="y">
        <option value="-2">-2</option>
        <option value="-1.5">-1.5</option>
        <option value="-1">-1</option>
        <option value="-0.5">-0.5</option>
        <option value="0" selected>0</option>
        <option value="0.5">0.5</option>
        <option value="1">1</option>
        <option value="1.5">1.5</option>
        <option value="2">2</option>
    </select>

    <fieldset>
        <legend>R</legend>
        <label><input type="checkbox" name="r" value="1"> 1</label>
        <label><input type="checkbox" name="r" value="1.5"> 1.5</label>
        <label><input type="checkbox" name="r" value="2"> 2</label>
        <label><input type="checkbox" name="r" value="2.5"> 2.5</label>
        <label><input type="checkbox" name="r" value="3"> 3</label>
    </fieldset>

    <p id="message" class="error"></p>
    <button type="submit">Check</button>
</form>

<canvas id="graph" width="400" height="400"></canvas>

<table>
    <thead>
    <tr>
        <th>X</th>
        <th>Y</th>
        <th>R</th>
        <th>Result</th>
        <th>Time</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="res" items="${results}">
        <tr>
            <td>${res.x}</td>
            <td>${res.y}</td>
            <td>${res.r}</td>
            <td class="${res.hit ? 'hit' : 'miss'}">${res.hit ? 'Hit' : 'Miss'}</td>
            <td>${res.time}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<c:if test="${not empty results}">
    <form action="controller" method="get">
        <input type="hidden" name="action" value="clear">
        <button type="submit">Clear results</button>
    </form>
</c:if>

<script>
    const points = [
        <c:forEach var="res" items="${results}">
        {x: ${res.x}, y: ${res.y}, r: ${res.r}, hit: ${res.hit}},
        </c:forEach>
    ];
</script>
<script src="script.js"></script>

</body>
</html>

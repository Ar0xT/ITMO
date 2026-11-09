<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Lab 3 result</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body class="result-page">

<c:set var="state" value="${not empty error ? 'error' : (result.hit ? 'hit' : 'miss')}"/>
<video id="bg" class="bg" src="media/result-${state}.mp4" poster="media/result-${state}.jpg"
       autoplay muted playsinline></video>

<main>
    <c:choose>
        <c:when test="${state == 'error'}">
            <p class="error">${error}</p>
        </c:when>
        <c:otherwise>
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
                <tr>
                    <td>${result.x}</td>
                    <td>${result.y}</td>
                    <td>${result.r}</td>
                    <td class="${result.hit ? 'hit' : 'miss'}">${result.hit ? 'Hit' : 'Miss'}</td>
                    <td>${result.time}</td>
                </tr>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <p><a href="controller">Back to the form</a></p>
</main>

<script>
    // after the intro, keep looping only the calm part that starts at 2.5 seconds
    const bg = document.getElementById("bg");
    bg.addEventListener("ended", () => {
        bg.currentTime = 2.5;
        bg.play();
    });
</script>

</body>
</html>

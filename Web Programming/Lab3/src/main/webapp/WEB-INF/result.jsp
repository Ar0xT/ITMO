<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Form Result</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<c:choose>
    <c:when test="${not empty error}">
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
                    <td class="${result.hit ? 'hit': 'miss'}">${result.hit ? "HIT" : "MISS"}</td>
                    <td>${result.time}</td>
                </tr>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>

<p><a href="controller">Back to the form</a></p>

</body>
</html>

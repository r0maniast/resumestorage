<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/main.css">
    <jsp:useBean id="resume" type="ru.romankrivtsov.resume_storage.model.Resume" scope="request"/>
    <title>Резюме ${resume.fullName}</title>
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<section>
    <h1>${resume.fullName}&nbsp;<a class="btn" href="resume?uuid=${resume.uuid}&action=edit"><img src="img/pencil.png" width="20"
                                                                                      height="20"
                                                                                      alt="Редактировать"></a></h1>
    <p>
        <c:forEach var="contactEntry" items="${resume.contacts}">
            ${contactEntry.key.toHtml(contactEntry.value)}<br/>
        </c:forEach>
    </p>
    <hr>
    <table>
        <c:forEach var="sectionEntry" items="${resume.sections}">
            <c:set var="type" value="${sectionEntry.key}"/>
            <c:set var="section" value="${sectionEntry.value}"/>
            <tr>
                <th><span class="section-title">${type.title}</span></th>
            </tr>
            <c:choose>
                <c:when test="${type=='OBJECTIVE'}">
                    <tr>
                        <td>
                            ${section.content}
                        </td>
                    </tr>
                </c:when>
                <c:when test="${type=='PERSONAL'}">
                    <tr>
                        <td colspan="2">
                            ${section.content}
                        </td>
                    </tr>
                </c:when>
                <c:when test="${type=='QUALIFICATIONS' || type=='ACHIEVEMENT'}">
                    <tr>
                        <td colspan="2">
                            <ul>
                                <c:forEach var="item" items="${section.items}">
                                    <li>${item}</li>
                                </c:forEach>
                            </ul>
                        </td>
                    </tr>
                </c:when>
                <c:when test="${type=='EXPERIENCE' || type=='EDUCATION'}">
                    <c:forEach var="org" items="${section.organizations}">
                        <tr>
                            <td colspan="2">
                                <c:choose>
                                    <c:when test="${empty org.homePage.url}">
                                        <span class="org-title">${org.homePage.name}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="org-title" href="${org.homePage.url}">${org.homePage.name}</a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <c:forEach var="pos" items="${org.positions}">
                            <tr>
                                <td width="15%">${pos.datesFormatted}</td>
                                <td><b>${pos.title}</b><br>${pos.description}</td>
                            </tr>
                        </c:forEach>
                    </c:forEach>
                </c:when>
            </c:choose>
        </c:forEach>
    </table>
    <br>
    <br>
    <button class="btn" type="button" onclick="window.history.back()">Вернуться к списку</button>
</section>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>

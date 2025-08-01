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
    <form method="post" action="resume">
        <input type="hidden" name="uuid" value="${resume.uuid}">
        <h3>Полное имя:</h3>
        <dl>
            <dd>
                <input type="text" name="fullName" size="50" value="${resume.fullName}">
            </dd>
        </dl>
        <h3>Контакты</h3>
        <c:forEach var="type" items="${contactTypes}">
            <dl>
                <dt>${type.title}</dt>
                <dd>
                    <input type="text" name="${type.name()}" size="30" value="${resume.getContact(type)}">
                </dd>
            </dl>
        </c:forEach>
        <hr>
        <c:forEach var="type" items="${sectionTypes}">
            <c:set var="section" value="${resume.getSection(type)}"/>
            <h3><a>${type.title}</a></h3>
            <c:choose>
                <c:when test="${type=='OBJECTIVE'}">
                    <input type="text" name="${type}" size=75 value="${section.content}">
                </c:when>
                <c:when test="${type=='PERSONAL'}">
                    <textarea name='${type}' cols=75 rows=5>${section.content}</textarea>
                </c:when>
                <c:when test="${type=='QUALIFICATIONS' || type=='ACHIEVEMENT'}">
                        <textarea name='${type}' cols=75
                                  rows=5>${section.itemsString}</textarea>
                </c:when>
                <c:when test="${type=='EXPERIENCE' || type=='EDUCATION'}">
                    <c:forEach var="org" items="${section.organizations}"
                               varStatus="counter">
                        <dl>
                            <dt>Название организации</dt>
                            <dd>
                                <input type="text" name="${type}" size=100 value="${org.homePage.name}">
                            </dd>
                        </dl>
                        <dl>
                            <dt>Сайт организации</dt>
                            <dd>
                                <input type="text" name="${type}url" size=100 value="${org.homePage.url}">
                            </dd>
                        </dl>
                        <div style="margin-left: 30px">
                            <c:forEach var="pos" items="${org.positions}">
                                <dl>
                                    <dt>Начальная дата:</dt>
                                    <dd>
                                        <input type="text" name="${type}${counter.index}startDate" size=10
                                               value="${pos.startDateFormatted}" placeholder="MM/yyyy">
                                    </dd>
                                </dl>
                                <dl>
                                    <dt>Конечная дата:</dt>
                                    <dd>
                                        <input type="text" name="${type}${counter.index}endDate" size=10
                                               value="${pos.endDateFormatted}"
                                               placeholder="MM/yyyy">
                                    </dd>
                                </dl>
                                <dl>
                                    <dt>Должность:</dt>
                                    <dd>
                                        <input type="text" name="${type}${counter.index}title" size=75
                                               value="${pos.title}">
                                    </dd>
                                </dl>
                                <dl>
                                    <dt>Описание:</dt>
                                    <dd>
                                            <textarea name="${type}${counter.index}description" rows=10
                                                      cols=75>${pos.description}</textarea>
                                    </dd>
                                </dl>
                                <br>
                            </c:forEach>
                        </div>
                    </c:forEach>
                </c:when>
            </c:choose>
        </c:forEach>
        <button class="btn" type="submit">Сохранить</button>
        <button class="btn" type="button" onclick="window.history.back()">Отменить</button>
    </form>
</section>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>

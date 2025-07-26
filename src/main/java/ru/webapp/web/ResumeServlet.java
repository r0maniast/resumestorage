package ru.webapp.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webapp.Config;
import ru.webapp.model.*;
import ru.webapp.storage.Storage;
import ru.webapp.util.DateUtil;
import ru.webapp.util.HtmlUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResumeServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ResumeServlet.class);

    Storage storage = Config.get().getStorage();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Start processing POST request");
        request.setCharacterEncoding("UTF-8");
        String uuid = request.getParameter("uuid");
        String fullName = request.getParameter("fullName");
        log.debug("Received parameters: uuid={}, fullName={}", uuid, fullName);

        final boolean isCreate = (uuid == null || uuid.length() == 0);
        Resume r;
        if (isCreate) {
            r = new Resume(fullName);
            log.info("Creating new resume for fullName={}", fullName);
        } else {
            r = storage.get(uuid);
            r.setFullName(fullName);
            log.info("Updating resume uuid={}, new fullName={}", uuid, fullName);
        }

        for (ContactType type : ContactType.values()) {
            String value = request.getParameter(type.name());
            if (HtmlUtil.isEmpty(value)) {
                r.getContacts().remove(type);
                log.debug("Removed contact type {}", type);
            } else {
                r.setContact(type, value);
                log.debug("Set contact {} = {}", type, value);
            }
        }

        for (SectionType type : SectionType.values()) {
            String value = request.getParameter(type.name());
            String[] values = request.getParameterValues(type.name());
            if (HtmlUtil.isEmpty(value) && (values == null || values.length < 2)) {
                r.getSections().remove(type);
                log.debug("Removed section type {}", type);
            } else {
                switch (type) {
                    case OBJECTIVE, PERSONAL -> {
                        r.setSection(type, new TextSection(value));
                        log.debug("Set TextSection for type {} with value {}", type, value);
                    }
                    case ACHIEVEMENT, QUALIFICATIONS -> {
                        r.setSection(type, new ListSection(value.split("\\n")));
                        log.debug("Set ListSection for type {} with values {}", type, value.split("\\n"));
                    }
                    case EXPERIENCE, EDUCATION -> {
                        List<Organization> orgs = new ArrayList<>();
                        String[] urls = request.getParameterValues(type.name() + "url");
                        for (int i = 0; i < values.length; i++) {
                            String name = values[i];
                            if (!HtmlUtil.isEmpty(name)) {
                                List<Organization.Position> positions = new ArrayList<>();
                                String pfx = type.name() + i;
                                String[] startDates = request.getParameterValues(pfx + "startDate");
                                String[] endDates = request.getParameterValues(pfx + "endDate");
                                String[] titles = request.getParameterValues(pfx + "title");
                                String[] descriptions = request.getParameterValues(pfx + "description");
                                for (int j = 0; j < titles.length; j++) {
                                    if (!HtmlUtil.isEmpty(titles[j])) {
                                        positions.add(new Organization.Position(
                                                DateUtil.parse(startDates[j]),
                                                DateUtil.parse(endDates[j]),
                                                titles[j],
                                                descriptions[j]));
                                        log.debug("Added position: title={}, startDate={}, endDate={}", titles[j], startDates[j], endDates[j]);
                                    }
                                }
                                orgs.add(new Organization(new Link(name, urls[i]), positions));
                                log.debug("Added organization: name={}, url={}", name, urls[i]);
                            }
                        }
                        r.setSection(type, new OrganizationSection(orgs));
                        log.debug("Set OrganizationSection for type {} with organizations {}", type, orgs);
                    }
                }
            }
        }
        if (isCreate) {
            storage.save(r);
            log.info("Saved new resume uuid={}", r.getUuid());
        } else {
            storage.update(r);
            log.info("Updated resume uuid={}", r.getUuid());
        }
        response.sendRedirect("resume");
        log.debug("Redirected to resume list");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Start processing GET request");
        String action = request.getParameter("action");
        String uuid = request.getParameter("uuid");
        log.debug("Received parameters: action={}, uuid={}", action, uuid);

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        log.debug("Request analysis: requestURI={}, contextPath={}, path={}, action={}", 
                 requestURI, contextPath, path, action);

        if (path.startsWith("/css/") || path.startsWith("/img/")) {
            log.debug("Static resource requested: {}, passing to default servlet", path);
            return;
        }
        
        if (!path.equals("/resume") && !path.equals("/") && action == null) {
            log.debug("Invalid URL requested: {}, redirecting to resume list", path);
            response.sendRedirect("resume");
            return;
        }

        if (action == null) {
            request.setAttribute("resumes", storage.getAllSorted());
            request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
            log.debug("Forwarded to list.jsp with all resumes");
            return;
        }
        Resume r;
        switch (action) {
            case "delete" -> {
                storage.delete(uuid);
                log.info("Deleted resume uuid={}", uuid);
                response.sendRedirect("resume");
                log.debug("Redirected to resume list after delete");
                return;
            }
            case "view" -> r = storage.get(uuid);
            case "add" -> r = Resume.EMPTY;
            case "edit" -> {
                r = storage.get(uuid);
                for (SectionType type : SectionType.values()) {
                    Section section = r.getSection(type);
                    switch (type) {
                        case OBJECTIVE, PERSONAL -> {
                            if (section == null) {
                                section = TextSection.EMPTY;
                                log.debug("Set empty TextSection for type {}", type);
                            }
                        }
                        case ACHIEVEMENT, QUALIFICATIONS -> {
                            if (section == null) {
                                section = ListSection.EMPTY;
                                log.debug("Set empty ListSection for type {}", type);
                            }
                        }
                        case EXPERIENCE, EDUCATION -> {
                            OrganizationSection orgSection = (OrganizationSection) section;
                            List<Organization> emptyFirstOrganizations = new ArrayList<>();
                            emptyFirstOrganizations.add(Organization.EMPTY);
                            if (orgSection != null) {
                                for (Organization org : orgSection.getOrganizations()) {
                                    List<Organization.Position> emptyFirstPositions = new ArrayList<>();
                                    emptyFirstPositions.add(Organization.Position.EMPTY);
                                    emptyFirstPositions.addAll(org.getPositions());
                                    emptyFirstOrganizations.add(new Organization(org.getHomePage(), emptyFirstPositions));
                                }
                            }
                            section = new OrganizationSection(emptyFirstOrganizations);
                            log.debug("Prepared OrganizationSection for editing for type {}", type);
                        }
                    }
                    r.setSection(type, section);
                }
            }
            default -> {
                log.error("Illegal action requested: {}", action);
                throw new IllegalStateException("Action " + action + " is illegal");
            }
        }
        request.setAttribute("resume", r);
        String jsp = "view".equals(action) ? "/WEB-INF/jsp/view.jsp" : "/WEB-INF/jsp/edit.jsp";
        request.getRequestDispatcher(jsp).forward(request, response);
        log.debug("Forwarded to {}", jsp);
    }
}

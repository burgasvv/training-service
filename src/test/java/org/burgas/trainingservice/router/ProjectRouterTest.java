package org.burgas.trainingservice.router;

import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.identity.Authority;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dao.project.Project;
import org.burgas.trainingservice.dto.course.CourseRequest;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.burgas.trainingservice.dto.project.ProjectRequest;
import org.burgas.trainingservice.mapper.CourseMapper;
import org.burgas.trainingservice.mapper.IdentityMapper;
import org.burgas.trainingservice.repository.ProjectRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class ProjectRouterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IdentityMapper identityMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ProjectRepository projectRepository;

    private void createIdentity() {
        var identityRequest = IdentityRequest.builder()
                .authority(Authority.ADMIN).email("admin@gmail.com").password("admin").status(true)
                .firstname("Admin").lastname("Admin").patronymic("Admin").about("About Admin")
                .build();
        Identity entity = identityMapper.toEntity(identityRequest);
        identityMapper.identityRepository.save(entity);
    }

    private Identity getIdentity(@SuppressWarnings("SameParameterValue") String email) {
        return identityMapper.identityRepository.findIdentityByEmail(email).orElseThrow();
    }

    private MvcResult getLogin() throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/security/login")
                                .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
                )
                .andReturn();
    }

    private void createCourse() {
        var courseRequest = CourseRequest.builder()
                .name("Test Course")
                .description("Description about Test Course")
                .build();
        Course entity = courseMapper.toEntity(courseRequest);
        courseMapper.courseRepository.save(entity);
    }

    private Course getCourse(@SuppressWarnings("SameParameterValue") String name) {
        return courseMapper.courseRepository.findCourseByName(name).orElseThrow();
    }

    @Test
    @Order(value = 1)
    public void createProject() throws Exception {
        createIdentity();
        createCourse();
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Course course = getCourse("Test Course");
        var projectRequest = ProjectRequest.builder()
                .name("Project For Course")
                .description("Description about Project For Course")
                .courseId(course.getId())
                .build();
        var mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(projectRequest);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/projects/create")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andReturn();
    }

    @Test
    @Order(value = 2)
    public void updateProject() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Project project = projectRepository.findProjectByName("Project For Course").orElseThrow();
        var projectRequest = ProjectRequest.builder()
                .id(project.getId())
                .name("New Project For Course")
                .build();
        var mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(projectRequest);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/projects/update")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(httpSession)
                                .content(content)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andReturn();
    }

    @Test
    @Order(value = 3)
    public void getProjectById() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Project project = projectRepository.findProjectByName("New Project For Course").orElseThrow();
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/projects/by-id")
                                .accept(MediaType.APPLICATION_JSON)
                                .param("projectId", project.getId().toString())
                                .session(httpSession)
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Order(value = 4)
    public void deleteProject() throws Exception {
        Identity identity = getIdentity("admin@gmail.com");
        Course course = getCourse("Test Course");
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Project project = projectRepository.findProjectByName("New Project For Course").orElseThrow();
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/projects/delete")
                                .param("projectId", project.getId().toString())
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/courses/delete")
                                .param("courseId", course.getId().toString())
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andReturn();
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/identities/delete")
                                .param("identityId", identity.getId().toString())
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andReturn();
    }
}

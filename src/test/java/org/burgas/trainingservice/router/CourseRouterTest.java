package org.burgas.trainingservice.router;

import org.burgas.trainingservice.dao.course.Course;
import org.burgas.trainingservice.dao.identity.Authority;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dto.course.CourseRequest;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.burgas.trainingservice.mapper.IdentityMapper;
import org.burgas.trainingservice.repository.CourseRepository;
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
public class CourseRouterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IdentityMapper identityMapper;

    @Autowired
    private CourseRepository courseRepository;

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

    @Test
    @Order(value = 1)
    public void createCourse() throws Exception {
        createIdentity();
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        var courseRequest = CourseRequest.builder()
                .name("Test Course")
                .description("Description about Test Course")
                .build();
        var mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(courseRequest);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/courses/create")
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
    @Order(value = 2)
    public void updateCourse() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Course course = courseRepository.findCourseByName("Test Course").orElseThrow();
        var courseRequest = CourseRequest.builder()
                .id(course.getId())
                .name("New Test Course")
                .build();
        var mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(courseRequest);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/courses/update")
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
    @Order(value = 3)
    public void getCourses() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/courses")
                                .accept(MediaType.APPLICATION_JSON)
                                .session(httpSession)
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Order(value = 4)
    public void getCourseById() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Course course = courseRepository.findCourseByName("New Test Course").orElseThrow();
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/courses/by-id")
                                .param("courseId", course.getId().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .session(httpSession)
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Order(value = 5)
    public void deleteCourse() throws Exception {
        Identity identity = getIdentity("admin@gmail.com");
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Course course = courseRepository.findCourseByName("New Test Course").orElseThrow();
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/courses/delete")
                                .param("courseId", course.getId().toString())
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
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

package org.burgas.trainingservice.router;

import org.burgas.trainingservice.dao.file.File;
import org.burgas.trainingservice.dao.identity.Authority;
import org.burgas.trainingservice.dao.identity.Identity;
import org.burgas.trainingservice.dto.file.FileRequest;
import org.burgas.trainingservice.dto.identity.IdentityRequest;
import org.burgas.trainingservice.repository.IdentityRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class IdentityRouterTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IdentityRepository identityRepository;

    private MvcResult getLogin() throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/security/login")
                                .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin@gmail.com", "admin"))
                )
                .andReturn();
    }

    @Test
    @Order(value = 1)
    public void createIdentity() throws Exception {
        var identityRequest = IdentityRequest.builder()
                .authority(Authority.ADMIN)
                .email("admin@gmail.com")
                .password("admin")
                .status(true)
                .firstname("Admin")
                .lastname("Admin")
                .patronymic("Admin")
                .about("About Admin")
                .build();
        var mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(identityRequest);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/identities/create")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Order(value = 2)
    public void updateIdentity() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Identity identity = identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow();
        var identityRequest = IdentityRequest.builder()
                .id(identity.getId())
                .about("About new Admin")
                .build();
        var mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(identityRequest);
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/identities/update")
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
    public void getAllIdentities() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/identities")
                                .accept(MediaType.APPLICATION_JSON)
                                .session(httpSession)
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andReturn();
    }

    @Test
    @Order(value = 4)
    public void getIdentityById() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Identity identity = identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow();
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/identities/by-id")
                                .accept(MediaType.APPLICATION_JSON)
                                .session(httpSession)
                                .param("identityId", identity.getId().toString())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andReturn();
    }

    @Test
    @Order(value = 5)
    public void uploadIdentityImage() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Identity identity = identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow();
        MockPart testImage = new MockPart(
                "image", "image.jpg",
                "test image bytes".getBytes(StandardCharsets.UTF_8),
                MediaType.IMAGE_JPEG
        );
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/v1/identities/upload-image")
                                .param("identityId", identity.getId().toString())
                                .part(testImage)
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Order(value = 6)
    public void removeIdentityImage() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Identity identity = identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow();
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/identities/remove-image")
                                .param("identityId", identity.getId().toString())
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Order(value = 7)
    public void uploadIdentityFiles() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Identity identity = identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow();
        MockPart firstFile = new MockPart(
                "file", "file.txt",
                "test first file bytes".getBytes(StandardCharsets.UTF_8),
                MediaType.APPLICATION_OCTET_STREAM
        );
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/v1/identities/upload-files")
                                .param("identityId", identity.getId().toString())
                                .part(firstFile)
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Order(value = 8)
    public void removeIdentityFiles() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Identity identity = identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow();
        Set<UUID> fileIds = identity.getFiles().parallelStream().map(File::getId).collect(Collectors.toSet());
        var fileRequest = new FileRequest(fileIds);
        var mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(fileRequest);
        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/v1/identities/remove-files")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("identityId", identity.getId().toString())
                                .content(content)
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    @Order(value = 9)
    public void deleteIdentity() throws Exception {
        MvcResult loginResult = getLogin();
        MockHttpSession httpSession = (MockHttpSession) loginResult.getRequest().getSession();
        assert httpSession != null;
        Identity identity = identityRepository.findIdentityByEmail("admin@gmail.com").orElseThrow();
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/identities/delete")
                                .param("identityId", identity.getId().toString())
                                .session(httpSession)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andReturn();
    }
}

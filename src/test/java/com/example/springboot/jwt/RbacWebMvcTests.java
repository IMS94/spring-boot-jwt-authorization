package com.example.springboot.jwt;

import com.example.springboot.jwt.controller.ProductController;
import com.example.springboot.jwt.entity.Product;
import com.example.springboot.jwt.security.SecurityConfiguration;
import com.example.springboot.jwt.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc
@Import({WebSecurityConfig.class, SecurityConfiguration.class})
class RbacWebMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private ProductService productService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void getProductsWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    @Test
    void addProductWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Book\",\"description\":\"A book\"}"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    @Test
    void deleteProductWithoutAuthenticationReturnsUnauthorized() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(productService);
    }

    @ParameterizedTest
    @MethodSource("getProductRoleCases")
    void getProductsRespectsRbac(String[] authorities, int expectedStatus, boolean shouldInvokeService) throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products")
                        .with(jwtWithAuthorities(authorities)))
                .andExpect(status().is(expectedStatus));

        if (shouldInvokeService) {
            verify(productService).getAllProducts();
        } else {
            verify(productService, never()).getAllProducts();
        }
    }

    @ParameterizedTest
    @MethodSource("addProductRoleCases")
    void addProductRespectsRbac(String[] authorities, int expectedStatus, boolean shouldInvokeService) throws Exception {
        mockMvc.perform(post("/products")
                        .with(jwtWithAuthorities(authorities))
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Book\",\"description\":\"A book\"}"))
                .andExpect(status().is(expectedStatus));

        if (shouldInvokeService) {
            verify(productService).addProduct(any(Product.class));
        } else {
            verify(productService, never()).addProduct(any(Product.class));
        }
    }

    @ParameterizedTest
    @MethodSource("deleteProductRoleCases")
    void deleteProductRespectsRbac(String[] authorities, int expectedStatus, boolean shouldInvokeService) throws Exception {
        mockMvc.perform(delete("/products/1")
                        .with(jwtWithAuthorities(authorities)))
                .andExpect(status().is(expectedStatus));

        if (shouldInvokeService) {
            verify(productService).deleteProductById(1L);
        } else {
            verify(productService, never()).deleteProductById(anyLong());
        }
    }

    private static Stream<Arguments> getProductRoleCases() {
        return Stream.of(
                Arguments.of(new String[]{"STAFF_MEMBER"}, 200, true),
                Arguments.of(new String[]{"ASSISTANT_MANAGER"}, 403, false),
                Arguments.of(new String[]{"MANAGER"}, 403, false),
                Arguments.of(new String[]{"ADMIN"}, 403, false),
                Arguments.of(new String[]{"INTERN"}, 403, false),
                Arguments.of(new String[]{"ASSISTANT_MANAGER", "STAFF_MEMBER"}, 200, true),
                Arguments.of(new String[]{"MANAGER", "STAFF_MEMBER"}, 200, true),
                Arguments.of(new String[]{"ADMIN", "STAFF_MEMBER"}, 200, true)
        );
    }

    private static Stream<Arguments> addProductRoleCases() {
        return Stream.of(
                Arguments.of(new String[]{"STAFF_MEMBER"}, 403, false),
                Arguments.of(new String[]{"ASSISTANT_MANAGER"}, 200, true),
                Arguments.of(new String[]{"MANAGER"}, 200, true),
                Arguments.of(new String[]{"ADMIN"}, 200, true),
                Arguments.of(new String[]{"INTERN"}, 403, false),
                Arguments.of(new String[]{"ASSISTANT_MANAGER", "STAFF_MEMBER"}, 200, true),
                Arguments.of(new String[]{"MANAGER", "STAFF_MEMBER"}, 200, true),
                Arguments.of(new String[]{"ADMIN", "STAFF_MEMBER"}, 200, true)
        );
    }

    private static Stream<Arguments> deleteProductRoleCases() {
        return Stream.of(
                Arguments.of(new String[]{"STAFF_MEMBER"}, 403, false),
                Arguments.of(new String[]{"ASSISTANT_MANAGER"}, 403, false),
                Arguments.of(new String[]{"MANAGER"}, 200, true),
                Arguments.of(new String[]{"ADMIN"}, 200, true),
                Arguments.of(new String[]{"INTERN"}, 403, false),
                Arguments.of(new String[]{"ASSISTANT_MANAGER", "STAFF_MEMBER"}, 403, false),
                Arguments.of(new String[]{"MANAGER", "STAFF_MEMBER"}, 200, true),
                Arguments.of(new String[]{"ADMIN", "STAFF_MEMBER"}, 200, true)
        );
    }

    private static RequestPostProcessor jwtWithAuthorities(String... authorities) {
        List<GrantedAuthority> grantedAuthorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return jwt().authorities(grantedAuthorities);
    }
}

package com.molkovor.catalogue.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductRestControllerTestIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/products.sql")
    void getProduct_ProductExists_ReturnsProduct() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders.get("/catalogue-api/products/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));
        // when
        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(
                                """
                                            {"id": 1, "title": "Товар №1", "details": "Описание товара №1"}
                                        """
                        )
                )
                .andDo(document("catalog/products/getProductById",
                        preprocessResponse(prettyPrint(), new HeadersModifyingOperationPreprocessor()
                                .remove("Vary")),
                        responseFields(
                                fieldWithPath("id").description("ID товара").type("int"),
                                fieldWithPath("title").description("Название товара").type("String"),
                                fieldWithPath("details").description("Описание товара").type("String")
                        )));
    }

    @Test
    void getProduct_ProductDoesNotExist_ReturnsNoSuchElementException() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders.get("/catalogue-api/products/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));
        // when
        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().json("""
                                {"detail":  "Товар не найден"}
                                """)
                );
    }

    @Test
    void getProduct_UserIsUnauthorized_ReturnsForbidden() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders.get("/catalogue-api/products/1")
                .with(jwt());
        // when
        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void updateProduct_RequestIsValid_ReturnsNoContent() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Новое название",
                          "details": "Новое описание"
                        }
                        """)
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        //when
        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void updateProduct_RequestIsInValid_ReturnsBadRequest() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "   ",
                          "details": null
                        }
                        """)
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        //when
        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "errors": ["Название товара должно быть указано"]
                                }
                                """)
                );
    }

    @Test
    void updateProduct_ProductDoesNotExist_ReturnsNoSuchElementException() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders.patch("/catalogue-api/products/1")
                .locale(Locale.of("ru"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "   ",
                          "details": null
                        }
                        """)
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        //when
        mockMvc.perform(request)
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().json("""
                                {"detail": "Товар не найден"}
                                """)
                );
    }

    @Test
    @Sql("/sql/products.sql")
    void deleteProduct_ReturnsNoContent() throws Exception {
        // given
        RequestBuilder requestDelete = MockMvcRequestBuilders.delete("/catalogue-api/products/1")
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));
        RequestBuilder requestGetAll = MockMvcRequestBuilders.get("/catalogue-api/products")
                .param("filter", "")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        // when
        mockMvc.perform(requestDelete)
                .andDo(print())
                .andExpectAll(
                        status().isNoContent()
                );

        mockMvc.perform(requestGetAll)
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                  [
                                      {
                                          "id": 2,
                                          "title": "Шоколад",
                                          "details": "Темный шоколад"
                                      },
                                      {
                                          "id": 3,
                                          "title": "Товар №3",
                                          "details": "Описание товара №3"
                                      },
                                      {
                                          "id": 4,
                                          "title": "Молоко",
                                           "details": "Жирность 3.2%"
                                       }
                                  ]
                                """)

                );
    }
}
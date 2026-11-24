package com.mx.cbtis.bromitas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.view.xslt.XsltView;
import org.springframework.web.servlet.view.xslt.XsltViewResolver;
import org.springframework.http.MediaType;
import com.mx.cbtis.controller.GestorController;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Ejercicio5ClienteApplicationTests {

    @Autowired ApplicationContext context;
    @Autowired RequestMappingHandlerMapping mappings;

	@Test
	void contextLoads() {
		assertNotNull(context.getBean(GestorController.class));
        assertTrue(mappings.getHandlerMethods().keySet().stream()
            .anyMatch(mapping -> mapping.getPatternValues().contains("/inicio")),
            "The real Spring context must register the client routes");
	}

    @Test
    void contextoSeguroSinSseNiXslt() {
        assertAll("Context must not enable the Spring features affected by the accepted dependency advisories",
            () -> assertTrue(context.getBeansOfType(XsltViewResolver.class).isEmpty(), "No XSLT view resolver may be enabled"),
            () -> assertTrue(context.getBeansOfType(XsltView.class).isEmpty(), "No XSLT view may be enabled"));
        mappings.getHandlerMethods().forEach((mapping,handler) -> {
            assertTrue(mapping.getPatternValues().stream().noneMatch(pattern -> pattern.contains("/**")),
                () -> "Wildcard handler found: " + mapping);
            assertFalse(SseEmitter.class.isAssignableFrom(handler.getReturnType().getParameterType()),
                () -> "SSE emitter handler found: " + handler);
            String genericReturnType=handler.getReturnType().getGenericParameterType().getTypeName();
            assertFalse(genericReturnType.contains("SseEmitter") || genericReturnType.contains("ServerSentEvent"),
                () -> "Nested SSE response type found: " + handler);
            assertTrue(mapping.getProducesCondition().getProducibleMediaTypes().stream()
                .noneMatch(type -> type.isCompatibleWith(MediaType.TEXT_EVENT_STREAM)),
                () -> "Event stream response found: " + mapping);
        });
    }

}

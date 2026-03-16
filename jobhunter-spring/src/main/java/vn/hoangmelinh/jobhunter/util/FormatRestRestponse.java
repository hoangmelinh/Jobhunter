package vn.hoangmelinh.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.core.io.Resource;

import jakarta.servlet.http.HttpServletResponse;
import vn.hoangmelinh.jobhunter.domain.response.RestResponse;
import vn.hoangmelinh.jobhunter.util.annotation.ApiMessage;

@ControllerAdvice
public class FormatRestRestponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int statusCode = servletResponse.getStatus();

        if (body instanceof RestResponse || body instanceof String || body instanceof Resource) {
            return body;
        }

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(statusCode);

        if (statusCode >= 400) {

            return body;
        } else {

            res.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message != null ? message.value() : "Call Api Successfullty");

        }
        return res;
    }

}

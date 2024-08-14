package com.liubs.shadowrpcfly.protocol.serializer.javaserializer;

import com.liubs.shadowrpcfly.protocol.entity.JavaSerializeRPCRequest;
import com.liubs.shadowrpcfly.protocol.entity.JavaSerializeRPCResponse;
import com.liubs.shadowrpcfly.protocol.model.IModelParser;
import com.liubs.shadowrpcfly.protocol.model.RequestModel;
import com.liubs.shadowrpcfly.protocol.model.ResponseModel;

/**
 * @author Liubsyy
 * @date 2024/1/20
 **/
public class JavaModelParser implements IModelParser<JavaSerializeRPCRequest, JavaSerializeRPCResponse> {

    @Override
    public RequestModel fromRequest(JavaSerializeRPCRequest request) {
        RequestModel requestModel = new RequestModel();
        requestModel.setTraceId(request.getTraceId());
        requestModel.setServiceName(request.getServiceName());
        requestModel.setMethodName(request.getMethodName());
        requestModel.setParamTypes(request.getParamTypes());
        requestModel.setParams(request.getParams());
        return requestModel;
    }

    @Override
    public JavaSerializeRPCRequest toRequest(RequestModel requestModel) {
        JavaSerializeRPCRequest request = new JavaSerializeRPCRequest();
        request.setTraceId(requestModel.getTraceId());
        request.setServiceName(requestModel.getServiceName());
        request.setMethodName(requestModel.getMethodName());
        request.setParamTypes(requestModel.getParamTypes());
        request.setParams(requestModel.getParams());
        return request;
    }

    @Override
    public ResponseModel fromResponse(JavaSerializeRPCResponse response) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setTraceId(response.getTraceId());
        responseModel.setCode(response.getCode());
        responseModel.setResult(response.getResult());
        responseModel.setErrorMsg(response.getErrorMsg());
        return responseModel;
    }

    @Override
    public JavaSerializeRPCResponse toResponse(ResponseModel responseModel) {
        JavaSerializeRPCResponse response = new JavaSerializeRPCResponse();
        response.setTraceId(responseModel.getTraceId());
        response.setCode(responseModel.getCode());
        response.setResult(responseModel.getResult());
        response.setErrorMsg(responseModel.getErrorMsg());
        return response;
    }
}

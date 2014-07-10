package org.kie.remote.services.rest;

import static org.junit.Assert.assertEquals;
import static org.kie.remote.services.MockSetupTestHelper.DEPLOYMENT_ID;
import static org.kie.remote.services.MockSetupTestHelper.setupProcessMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.UserTaskService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.remote.services.StartProcessEveryStrategyTest;
import org.kie.remote.services.cdi.DeploymentInfoBean;
import org.kie.remote.services.cdi.ProcessRequestBean;
import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessInstanceResponse;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.kie.remote.services.MockSetupTestHelper.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ EmptyContext.class, ProcessInstanceIdContext.class })
public class RestStartProcessEveryStrategyTest extends RuntimeResource implements StartProcessEveryStrategyTest {

    private DeploymentInfoBean runtimeMgrMgrMock;
    private KieSession kieSessionMock;
    private ProcessService processServiceMock;
    private UserTaskService userTaskServiceMock;

    private MultivaluedMapImpl<String, String> queryParams;

    public void setRuntimeMgrMgrMock(DeploymentInfoBean mock) {
        this.runtimeMgrMgrMock = mock;
    }

    @Override
    public void setProcessServiceMock(ProcessService processServiceMock) {
        this.processServiceMock = processServiceMock;
    }

    @Override
    public void setUserTaskServiceMock(UserTaskService userTaskServiceMock) {
        this.userTaskServiceMock = userTaskServiceMock;
    }

    @Override
    public void setupTestMocks() {
        this.uriInfo = mock(UriInfo.class);
        doReturn(queryParams).when(uriInfo).getQueryParameters();

        this.processRequestBean = new ProcessRequestBean();
        this.processRequestBean.setProcessService(processServiceMock);
        this.processRequestBean.setUserTaskService(userTaskServiceMock);

        HttpHeaders headersMock = mock(HttpHeaders.class);
        this.headers = headersMock;
        doReturn(new MultivaluedMapImpl<String, String>()).when(headersMock).getRequestHeaders();
    }


    @Before
    public void before() {
        this.queryParams = new MultivaluedMapImpl<String, String>();
        this.deploymentId = DEPLOYMENT_ID;
    }

    @Test
    public void startProcessAndDoStuffPerProcessStartegyTest() throws Exception {
        // This method does some static mock magic to make sure
        // that EmptyContext.get() throws an exception if it is called here
        // (since a ProcessInstanceIdContext should be used instead

        setupProcessMocks(this, RuntimeStrategy.PER_PROCESS_INSTANCE);
        doReturn(new URI("http://localhost:8080/test/rest/process/" + TEST_PROCESS_DEF_NAME + "/start")).when(uriInfo)
                .getRequestUri();

        Response resp = process_defId_start(TEST_PROCESS_DEF_NAME);
        // verify ksession is called
        JaxbProcessInstanceResponse procInstResp = (JaxbProcessInstanceResponse) resp.getEntity();
        assertEquals("Invalid process instance id", TEST_PROCESS_INST_ID, procInstResp.getId());

        // Do rest call with process instance id this time. This will fail if:
        // - the ProcessInstanceIdContext is not used (and an EmptyContext is used instead)
        // - The ProcessInstanceIdContext constructor gets a null value for the process instance id
        queryParams.add(PROC_INST_ID_PARAM_NAME, String.valueOf(TEST_PROCESS_INST_ID));
        queryParams.add("signal", "test");
        resp = signal();

        // verify ksession is called
        verify(processServiceMock, times(2)).execute(any(String.class), any(Command.class));
        PowerMockito.verifyStatic(times(0));
        EmptyContext.get();
    }

    @Test
    public void startProcessAndDoStuffPerRequestStrategyTest() throws Exception {
        setupProcessMocks(this, RuntimeStrategy.PER_REQUEST);
        doReturn(new URI("http://localhost:8080/test/rest/process/" + TEST_PROCESS_DEF_NAME + "/start")).when(uriInfo)
                .getRequestUri();

        Response resp = process_defId_start(TEST_PROCESS_DEF_NAME);
        // verify non-process contexts are used
        JaxbProcessInstanceResponse procInstResp = (JaxbProcessInstanceResponse) resp.getEntity();
        assertEquals("Invalid process instance id", TEST_PROCESS_INST_ID, procInstResp.getId());

        queryParams.add("signal", "test");
        resp = signal();

        // verify ksession is called
        verify(processServiceMock, times(2)).execute(any(String.class), any(Command.class));
    }

    @Test
    public void startProcessAndDoStuffSingletonStrategyTest() throws Exception {
        setupProcessMocks(this, RuntimeStrategy.SINGLETON);
        doReturn(new URI("http://localhost:8080/test/rest/process/" + TEST_PROCESS_DEF_NAME + "/start")).when(uriInfo)
                .getRequestUri();

        Response resp = process_defId_start(TEST_PROCESS_DEF_NAME);
        // verify non-process contexts are used
        JaxbProcessInstanceResponse procInstResp = (JaxbProcessInstanceResponse) resp.getEntity();
        assertEquals("Invalid process instance id", TEST_PROCESS_INST_ID, procInstResp.getId());

        queryParams.add("signal", "test");
        resp = signal();

        // verify ksession is called
        verify(processServiceMock, times(2)).execute(any(String.class),any(Command.class));
    }

}

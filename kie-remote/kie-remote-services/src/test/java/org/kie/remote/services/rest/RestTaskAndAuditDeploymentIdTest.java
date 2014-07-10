package org.kie.remote.services.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.remote.services.MockSetupTestHelper.FOR_INDEPENDENT_TASKS;
import static org.kie.remote.services.MockSetupTestHelper.FOR_PROCESS_TASKS;
import static org.kie.remote.services.MockSetupTestHelper.TASK_ID;
import static org.kie.remote.services.MockSetupTestHelper.setupTaskMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jbpm.kie.services.api.IdentityProvider;
import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.command.ClearHistoryLogsCommand;
import org.jbpm.process.audit.command.FindProcessInstancesCommand;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.commands.ClaimTaskCommand;
import org.jbpm.services.task.commands.CompleteTaskCommand;
import org.jbpm.services.task.commands.TaskCommand;
import org.junit.Test;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.remote.services.TaskDeploymentIdTest;
import org.kie.remote.services.cdi.DeploymentInfoBean;
import org.kie.remote.services.cdi.ProcessRequestBean;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandResponse;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsResponse;
import org.kie.services.client.serialization.jaxb.rest.JaxbExceptionResponse;

@SuppressWarnings("unchecked")
public class RestTaskAndAuditDeploymentIdTest extends TaskResource implements TaskDeploymentIdTest {

    private final static String USER = "user";

    private DeploymentInfoBean runtimeMgrMgrMock;

    private ProcessService processServiceMock;
    private UserTaskService userTaskServiceMock;

    private AuditLogService auditLogService = mock(AuditLogService.class);
    
    @Override
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

    public void setupTestMocks() {
        // REST
        this.uriInfo = mock(UriInfo.class);
        doReturn(new MultivaluedMapImpl<String,String>()).when(uriInfo).getQueryParameters();
        this.identityProvider = mock(IdentityProvider.class);
        doReturn(USER).when(identityProvider).getName();

        this.processRequestBean = new ProcessRequestBean();
        this.processRequestBean.setProcessService(processServiceMock);
        this.processRequestBean.setUserTaskService(userTaskServiceMock);

       
        HttpHeaders headersMock = mock(HttpHeaders.class);
        this.headers = headersMock; 
        doReturn(new MultivaluedMapImpl<String, String>()).when(headersMock).getRequestHeaders();
    }

    @Test
    public void testRestUrlIndependentTaskProcessing() throws URISyntaxException {
        setupTaskMocks(this, FOR_INDEPENDENT_TASKS);
        
        String oper = "claim";
        doReturn(new URI("http://localhost:8080/test/rest/task/" + TASK_ID + "/" + oper)).when(uriInfo).getRequestUri();
        this.taskId_oper(TASK_ID, oper);
        
        oper = "complete";
        doReturn(new URI("http://localhost:8080/test/rest/task/" + TASK_ID + "/" + oper)).when(uriInfo).getRequestUri();
        this.taskId_oper(TASK_ID, oper);
        
        verify(userTaskServiceMock, times(2)).execute(any(String.class), any(TaskCommand.class));
    }

    /**
     * When doing operations with a non-process (independent) task, 
     * the injected (non-runtime engine) taskService should be used. 
     */
    @Test
    public void testRestExecuteCommandIndependentTaskProcessing() {
        setupTaskMocks(this, FOR_INDEPENDENT_TASKS);
        
        JaxbCommandsRequest 
        cmdsRequest = new JaxbCommandsRequest(new ClaimTaskCommand(TASK_ID, USER));
        this.execute(cmdsRequest);
        cmdsRequest = new JaxbCommandsRequest(new CompleteTaskCommand(TASK_ID, USER, null));
        this.execute(cmdsRequest);
       
        // verify
        verify(userTaskServiceMock, times(2)).execute(any(String.class), any(TaskCommand.class));
    }

    @Test
    public void testRestUrlProcessTaskProcessing() throws Exception {
        setupTaskMocks(this, FOR_PROCESS_TASKS);
        
        String oper = "claim";
        doReturn(new URI("http://localhost:8080/test/rest/task/" + TASK_ID + "/" + oper)).when(uriInfo).getRequestUri();
        this.taskId_oper(TASK_ID, oper);
        
        oper = "complete";
        doReturn(new URI("http://localhost:8080/test/rest/task/" + TASK_ID + "/" + oper)).when(uriInfo).getRequestUri();
        this.taskId_oper(TASK_ID, oper);
        
        // verify
        verify(userTaskServiceMock, times(2)).execute(any(String.class), any(TaskCommand.class));
    }

    @Test
    public void testRestExecuteCommandProcessTaskProcessing() {
        setupTaskMocks(this, FOR_PROCESS_TASKS);

        JaxbCommandsRequest cmdsRequest = new JaxbCommandsRequest(new ClaimTaskCommand(TASK_ID, USER));
        this.execute(cmdsRequest);
        cmdsRequest = new JaxbCommandsRequest(new CompleteTaskCommand(TASK_ID, USER, null));
        this.execute(cmdsRequest);
    
        // verify
        verify(userTaskServiceMock, times(2)).execute(any(String.class), any(TaskCommand.class));
    }
    
    @Test
    public void testRestAuditCommandWithoutDeploymentId() {
        // setup
        setupTaskMocks(this, FOR_PROCESS_TASKS);
        this.processRequestBean.setAuditLogService(auditLogService);

        // run cmd (no deploymentId set on JaxbConmandsRequest object
        JaxbCommandsRequest 
        cmdsRequest = new JaxbCommandsRequest(new FindProcessInstancesCommand());
        JaxbCommandsResponse 
        response = this.execute(cmdsRequest);
       
        // check result
        assertEquals( "Number of response objects", 1, response.getResponses().size() );
        JaxbCommandResponse<?> 
        responseObj = response.getResponses().get(0);
        assertFalse( "Command did not complete successfully", responseObj instanceof JaxbExceptionResponse );
        
        // run cmd (no deploymentId set on JaxbConmandsRequest object
        cmdsRequest = new JaxbCommandsRequest(new ClearHistoryLogsCommand());
        response = this.execute(cmdsRequest);
        
        // check result
        assertEquals( "Number of response objects", 0, response.getResponses().size() );
        
        // verify
        verify(auditLogService, times(1)).findProcessInstances();
        verify(auditLogService, times(1)).clear();
    }
}

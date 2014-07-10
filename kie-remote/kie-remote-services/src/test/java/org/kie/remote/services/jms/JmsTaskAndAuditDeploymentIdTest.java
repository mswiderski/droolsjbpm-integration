package org.kie.remote.services.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.kie.remote.services.MockSetupTestHelper.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.audit.command.ClearHistoryLogsCommand;
import org.jbpm.process.audit.command.FindProcessInstancesCommand;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.commands.ClaimTaskCommand;
import org.jbpm.services.task.commands.CompleteTaskCommand;
import org.jbpm.services.task.commands.TaskCommand;
import org.junit.Test;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandResponse;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsResponse;
import org.kie.services.client.serialization.jaxb.rest.JaxbExceptionResponse;
import org.kie.remote.services.TaskDeploymentIdTest;
import org.kie.remote.services.cdi.DeploymentInfoBean;
import org.kie.remote.services.cdi.ProcessRequestBean;

@SuppressWarnings("unchecked")
public class JmsTaskAndAuditDeploymentIdTest extends RequestMessageBean implements TaskDeploymentIdTest {

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
        this.runtimeMgrMgr = runtimeMgrMgrMock;

        this.processRequestBean = new ProcessRequestBean();
        this.processRequestBean.setProcessService(processServiceMock);
        this.processRequestBean.setUserTaskService(userTaskServiceMock);
        
        // audit log service
        doReturn(new ArrayList<ProcessInstanceLog>()).when(auditLogService).findProcessInstances();
        doNothing().when(auditLogService).clear();
        this.processRequestBean.setAuditLogService(auditLogService);
    }

    @Test
    public void testJmsIndependentTaskProcessing() {
        setupTaskMocks(this, FOR_INDEPENDENT_TASKS);

        JaxbCommandsRequest 
        cmdsRequest = new JaxbCommandsRequest(new ClaimTaskCommand(TASK_ID, USER));
        this.jmsProcessJaxbCommandsRequest(cmdsRequest);
        cmdsRequest = new JaxbCommandsRequest(new CompleteTaskCommand(TASK_ID, USER, null));
        this.jmsProcessJaxbCommandsRequest(cmdsRequest);
       
        // verify
        verify(userTaskServiceMock, times(2)).execute(any(String.class), any(TaskCommand.class));
    }

    @Test
    public void testJmsProcessTaskProcessing() {
        setupTaskMocks(this, FOR_PROCESS_TASKS);

        JaxbCommandsRequest 
        cmdsRequest = new JaxbCommandsRequest(new ClaimTaskCommand(TASK_ID, USER));
        this.jmsProcessJaxbCommandsRequest(cmdsRequest);
        cmdsRequest = new JaxbCommandsRequest(new CompleteTaskCommand(TASK_ID, USER, null));
        this.jmsProcessJaxbCommandsRequest(cmdsRequest);
        
        // verify
        verify(userTaskServiceMock, times(2)).execute(any(String.class), any(TaskCommand.class));
    }

    @Test
    public void testJmsAuditCommandWithoutDeploymentId() {
        setupTaskMocks(this, FOR_PROCESS_TASKS);

        // run cmd (no deploymentId set on JaxbConmandsRequest object
        JaxbCommandsRequest 
        cmdsRequest = new JaxbCommandsRequest(new FindProcessInstancesCommand());
        JaxbCommandsResponse 
        response = this.jmsProcessJaxbCommandsRequest(cmdsRequest);
       
        // check result
        assertEquals( "Number of response objects", 1, response.getResponses().size() );
        JaxbCommandResponse<?> 
        responseObj = response.getResponses().get(0);
        assertFalse( "Command did not complete successfully", responseObj instanceof JaxbExceptionResponse );
        
        // run cmd (no deploymentId set on JaxbConmandsRequest object
        cmdsRequest = new JaxbCommandsRequest(new ClearHistoryLogsCommand());
        response = this.jmsProcessJaxbCommandsRequest(cmdsRequest);
        
        // check result
        assertEquals( "Number of response objects", 0, response.getResponses().size() );
        
        // verify
        verify(auditLogService, times(1)).findProcessInstances();
        verify(auditLogService, times(1)).clear();
    }

}

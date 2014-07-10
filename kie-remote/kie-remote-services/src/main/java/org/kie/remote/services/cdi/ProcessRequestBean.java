package org.kie.remote.services.cdi;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.command.AuditCommand;
import org.jbpm.services.api.ProcessService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.task.commands.GetContentCommand;
import org.jbpm.services.task.commands.GetTaskCommand;
import org.jbpm.services.task.commands.GetTaskContentCommand;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.remote.common.exception.RestOperationException;
import org.kie.remote.services.exception.DeploymentNotFoundException;
import org.kie.remote.services.rest.RuntimeResource;
import org.kie.remote.services.rest.TaskResource;
import org.kie.remote.services.util.ExecuteAndSerializeCommand;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsResponse;
import org.kie.services.shared.AcceptedCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.services.client.serialization.jaxb.rest.JaxbRequestStatus.*;
import static org.kie.services.shared.ServicesVersion.*;

/**
 * This class is used by both the {@link RuntimeResource} and {@link TaskResource} to do the core operations on
 * the Deployment/Runtime's {@link KieSession} and {@link TaskService}.
 * </p>
 * It contains the necessary logic to do the following:
 * <ul>
 * <li>Retrieve the KieSession or TaskService</li>
 * <li>Execute the submitted command</li>
 * </ul>
 */
@ApplicationScoped
public class ProcessRequestBean {

    private static final Logger logger = LoggerFactory.getLogger(ProcessRequestBean.class);
    
    /* KIE processing */

    @Inject
    private ProcessService processService;

    @Inject
    private UserTaskService userTaskService;

    /** AuditLogService **/
    private static final String PERSISTENCE_UNIT_NAME = "org.jbpm.domain";
    
    @PersistenceUnit(unitName = PERSISTENCE_UNIT_NAME)
    private EntityManagerFactory emf;
   
    private AuditLogService auditLogService;
  
    // Injection methods for tests

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public void setUserTaskService(UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }

    public void setAuditLogService(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // Audit Log Service logic 
    
    @PostConstruct
    public void initAuditLogService() { 
        auditLogService = new JPAAuditLogService(emf);
        if( emf == null ) { 
            ((JPAAuditLogService) auditLogService).setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
        }
    }
    
    public AuditLogService getAuditLogService() { 
        return auditLogService;
    }
    
    // Methods used
    
    public void processCommand(Command cmd, JaxbCommandsRequest request, int i, JaxbCommandsResponse jaxbResponse) { 
        String version = request.getVersion();
        if( version == null ) { 
            version = "pre-6.0.3";
        }
        if( ! version.equals(VERSION) ) { 
            logger.warn( "Request received from client version [{}] while server is version [{}]! THIS MAY CAUSE PROBLEMS!", version, VERSION);
        }
        
        String cmdName = cmd.getClass().getSimpleName();
        logger.debug("Processing command " + cmdName);
        String errMsg = "Unable to execute " + cmdName + "/" + i;
        
        Object cmdResult = null;
        try {
            if( cmd instanceof TaskCommand<?> ) { 
                TaskCommand<?> taskCmd = (TaskCommand<?>) cmd;
                cmdResult = doTaskOperation(
                        taskCmd.getTaskId(), 
                        request.getDeploymentId(), 
                        request.getProcessInstanceId(), 
                        null, 
                        taskCmd);
            } else if( cmd instanceof AuditCommand<?>) { 
                AuditCommand<?> auditCmd = ((AuditCommand<?>) cmd);
                auditCmd.setAuditLogService(getAuditLogService());
                cmdResult = auditCmd.execute(null);
            } else {
                cmdResult = doKieSessionOperation(
                        cmd, 
                        request.getDeploymentId(), 
                        request.getProcessInstanceId());
            }
        } catch (PermissionDeniedException pde) {
            logger.warn(errMsg, pde);
            jaxbResponse.addException(pde, i, cmd, PERMISSIONS_CONFLICT);
        } catch (Exception e) {
            logger.warn(errMsg, e);
            jaxbResponse.addException(e, i, cmd, FAILURE);
        } 
        if (cmdResult != null) {
            try {
                // addResult could possibly throw an exception, which is why it's here and not above
                jaxbResponse.addResult(cmdResult, i, cmd);
            } catch (Exception e) {
                errMsg = "Unable to add result from " + cmdName + "/" + i;
                logger.error(errMsg, e);
                jaxbResponse.addException(e, i, cmd, FAILURE);
            }
        }
    }
    
    /**
     * Executes a command on the {@link KieSession} from the proper {@link RuntimeManager}. This method
     * ends up synchronizing around the retrieved {@link KieSession} in order to avoid race-conditions.
     * 
     * @param cmd The command to be executed.
     * @param deploymentId The id of the runtime.
     * @param processInstanceId The process instance id, if available.
     * @return The result of the {@link Command}.
     */
    public Object doKieSessionOperation(Command<?> cmd, String deploymentId, Long processInstanceId) {
        if( deploymentId == null ) {
            throw new DeploymentNotFoundException("No deployment id supplied! Could not retrieve runtime to execute " + cmd.getClass().getSimpleName());
        }

        Object result = processService.execute(deploymentId, cmd);
        return result;
    }

   
    /**
     * Returns the actual variable instance from the runtime (as opposed to retrieving the string value of the
     * variable via the history/audit operations. 
     * 
     * @param deploymentId The id of the runtime
     * @param processInstanceId The process instance id (required)
     * @param varName The name of the variable
     * @return The variable object instance.
     */
    public Object getVariableObjectInstanceFromRuntime(String deploymentId, long processInstanceId, String varName) { 
        String errorMsg = "Unable to retrieve variable '" + varName + "' from process instance " + processInstanceId;
        Object procVar = processService.getProcessInstanceVariable(processInstanceId, varName);

        return procVar;
    }

    // task operations ------------------------------------------------------------------------------------------------------------
    
    /**
     * There are 3 possibilities here: <ol>
     * <li>This is an operation that should be done on a deployment if possible, but it's an independent task.</li>
     * <li>This is an operation that should be done on a deployment, and a deployment/runtime is available.</li>
     * <li>This is an operation that does <b>not</b> modify the {@link KieSession} and should be done via the injected {@link TaskService}.</li>
     * </ol>
     * 
     * @param taskId
     * @param deploymentId
     * @param processInstanceId
     * @param cmd
     * @param onDeployment
     * @param errorMsg
     * @return
     */
    private Object doTaskOperation(Long taskId, String deploymentId, Long processInstanceId, Task task, TaskCommand<?> cmd) { 
        boolean onDeployment = false;
        if( AcceptedCommands.TASK_COMMANDS_THAT_INFLUENCE_KIESESSION.contains(cmd.getClass()) )  {
           onDeployment = true;
        }
      
        // take care of serialization
        if( cmd instanceof GetTaskCommand 
                || cmd instanceof GetContentCommand 
                || cmd instanceof GetTaskContentCommand ) { 
           cmd = new ExecuteAndSerializeCommand(cmd); 
        }

        return userTaskService.execute(deploymentId, cmd);
    }


    public Object doRestTaskOperation(Long taskId, String deploymentId, Long processInstanceId, Task task, TaskCommand<?> cmd) {
        try { 
            return doTaskOperation(taskId, deploymentId, processInstanceId, task, cmd);
        } catch (PermissionDeniedException pde) {
            throw RestOperationException.conflict(pde.getMessage(), pde);
        } catch (RuntimeException re) {
            throw re;
        }
    }

}

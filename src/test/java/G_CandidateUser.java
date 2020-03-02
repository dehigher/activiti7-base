import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试候选人用户组
 */
public class G_CandidateUser {

    /**
     * 准备工作，部署流程定义并启动流程实例
     */
    @Test
    public void prepare() {
        //部署流程定义
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("diagram/holiday3.bpmn")
                .addClasspathResource("diagram/holiday3.png") // 可以省略
                .deploy();
        System.out.println("流程定义部署成功");
        // 启动流程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variables = new HashMap<>();
        variables.put("applyer", "zhangfei");
        variables.put("candidates", "liubei,sunquan,chaocao");
        variables.put("manager","shimayi");
        variables.put("num", 4);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holiday", "businessKey", variables);
        System.out.println("流程实例启动成功，instanceId="+processInstance.getDeploymentId());
    }

    /**
     * 以候选人身份查询任务
     */
    @Test
    public void queryCandidateTask() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> taskList = defaultProcessEngine.getTaskService().createTaskQuery()
                .active()
                .taskCandidateUser("sunquan")
                .processDefinitionKey("holiday")
                .list();
        //4.任务列表的展示
        for (Task task : taskList) {
            System.out.println("流程实例ID:" + task.getProcessInstanceId());
            System.out.println("任务ID:" + task.getId());
            System.out.println("任务负责人:" + task.getAssignee());
            System.out.println("任务名称:" + task.getName());
        }
    }

    /**
     * 候选人拾取任务、并完成任务
     */
    @Test
    public void claimTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        // 拾取任务 param1: taskId, param2: user
        taskService.claim("47502", "sunquan");
        // 处理任务
        taskService.complete("47502");
    }

    /**
     *  归还任务
     */
    public void backTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        taskService.setAssignee("47502", null);
    }

    /**
     *  委托任务
     */
    public void assigneeTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        taskService.setAssignee("47502", "chaocao");
    }
 }

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试流程变量相关操作
 * 1）全局流程变量设置
 *      启动流程实例时设置
 *      通过流程实例设置
 *      完成任务时设置
 *      通过任务设置
 * 2）局部变量
 *      用的少省略
 */
public class F_ProcessVariableTest {

    /**
     * 全局流程变量设置-启动流程实例时
     */
    @Test
    public void setGlobalProcessVariableByStartProcessInstanceTest() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 准备流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("applyer", "zhangsan");
        // holiday: 流程定义Key、businessKey: 业务关联字段、 variables: 流程变量
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey("holiday", "businessKey", variables);
        System.out.println("流程实例启动成功");
    }

    /**
     * 全局流程变量设置-完成任务时
     * // 张三完成请假单
     */
    @Test
    public void setGlobalProcessVariableByCompeleteTaskTest() {
        // 根据assignee查询任务
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .active()
                .processDefinitionKey("holiday")
                .taskAssignee("zhangsan")
                .singleResult();
        // 完成任务，并设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("deptLeader", "lishi");
        taskService.complete(task.getId(), variables);
        System.out.println("成功");
    }

    /**
     * 全局变量设置-通过流程实例设置
     * lishi 根据任务获得ProcessInstance
     * 通过ProcessInstance设置流程变零
     */
    @Test
    public void setGlobalProcessVariableByProcessInstance() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .active()
                .processDefinitionKey("holiday")
                .taskAssignee("lishi")
                .singleResult();
        // 设置variable
        RuntimeService runtimeService = defaultProcessEngine.getRuntimeService();
        runtimeService.setVariable(task.getProcessInstanceId(), "manager", "wangwu");
        System.out.println("成功");
    }

    /**
     * 全局变量设置-通过Task设置流程变量
     */
    @Test
    public void setGlobalProcessVariableByTask() {
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = defaultProcessEngine.getTaskService();
        taskService.setVariable("22503","num", 7);
        System.out.println("成功");
    }
}

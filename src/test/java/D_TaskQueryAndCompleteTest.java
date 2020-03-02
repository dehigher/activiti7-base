import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.List;

public class D_TaskQueryAndCompleteTest {

    /**
     * 查询任务
     * zhangsan完成自己任务列表的查询
     * 单个任务
     */
    @Test
    public void taskQueryByAssingerTest01() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.根据流程定义的key,负责人assignee来实现当前用户的任务列表查询
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("holiday")
                .taskAssignee("zhangsan")
                .singleResult();

        //4.任务列表的展示
        System.out.println("流程实例ID:"+task.getProcessInstanceId());
        System.out.println("任务ID:"+task.getId());  //5002
        System.out.println("任务负责人:"+task.getAssignee());
        System.out.println("任务名称:"+task.getName());
    }

    /**
     * 查询任务
     * zhangsan完成自己任务列表的查询
     * 多个任务
     */
    @Test
    public void taskQueryByAssingerTest02() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.根据流程定义的key,负责人assignee来实现当前用户的任务列表查询
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("holiday")
                .taskAssignee("zhangsan")
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
     * 处理任务
     * zhangsan完成自己的任务
     * 根据任务id处理
     */
    @Test
    public void completeTesk() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.处理任务,结合当前用户任务列表的查询操作的话,任务ID:7505
        taskService.complete("7505");
    }

    /**
     * 查询并直接处理掉任务
     * 上面两步并一步,其实就是一个意思，先查tesk 获得任务id
     * 查询当前用户wangwu的任务并处理掉
     */
    @Test
    public void queryAndCompleteTaskTest() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到TaskService对象
        TaskService taskService = processEngine.getTaskService();

        //3.查询当前用户的任务
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("holiday")
                .taskAssignee("zhangfei")
                .singleResult();

        //4.处理任务,结合当前用户任务列表的查询操作的话,任务ID:task.getId()
        taskService.complete(task.getId());

        //5.输出任务的id
        System.out.println(task.getId());
    }


}

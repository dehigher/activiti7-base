import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * 测试流程实例相关操作
 *  1）、根据流程定义的key启动流程实例
 *  2）、查询ProcessInstance
 *  3）、挂起激活流程实例
 */
public class C_InstanceTest {

    @Test
    public void startProcessInstance() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RunService对象
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //3.创建流程实例流程定义的key需要知道 holiday
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holiday", "");
        //4.输出实例的相关信息
        System.out.println("流程部署ID"+processInstance.getDeploymentId());//null
        System.out.println("流程定义ID"+processInstance.getProcessDefinitionId());//holiday:1:4
        System.out.println("流程实例ID"+processInstance.getId());//2501
        System.out.println("活动ID"+processInstance.getActivityId());//null
    }

    /**
     * 需求：根据taskId查询到流程实例
     *      流程实例中有businessKey进而关联到业务数据
     */
    public void queryProcessInstance() {
        // TODO
    }

    /**
     * 挂起激活单个流程实例
     * 该流程实例下所有任务不可执行，直到激活
     * 如果需要该流程定义下所有流程实例都挂起，就通过流程定义挂起|激活
     */
    @Test
    public void suspendedAndActiveTest() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //3.查询流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId("2501").singleResult();

        //4.得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processInstance.isSuspended();

        String processInstanceId = processInstance.getId();
        //5.判断
        if(suspended){
            //说明是暂停，就可以激活操作
            runtimeService.activateProcessInstanceById(processInstanceId);
            System.out.println("流程："+processInstanceId+"激活");
        }else{
            runtimeService.suspendProcessInstanceById(processInstanceId);
            System.out.println("流程定义："+processInstanceId+"挂起");
        }

    }
}

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

/**
 * 测试生成所需的25张表
 * -- 获取ProcessEngine
 */
public class A_GetProcessEngineTest {
    // 方式一：通过Configuration 获取 ProcessEnginess
    @Test
    public void testGenerateTable() {
        // 由于配置文件名和beanname都是按约定来的，所以直接使用Default,不需要另外指定
        ProcessEngineConfiguration configuration = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResourceDefault();
        ProcessEngine processEngine = configuration.buildProcessEngine();
        System.out.println(processEngine);
    }

    // 方式二：上述方式的简化, 通过ProcessEngines获取ProcessEngine
    @Test
    public void testGenerateTable2() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
    }
}

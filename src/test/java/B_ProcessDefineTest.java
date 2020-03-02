import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.junit.Test;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * 测试流程定义相关操作
 *  1）部署流程定义
 *      有两种方式： 直接部署、zip包部署
 *  2）流程定义查询
 *  3）流程定义资源文件的读取
 *  4）删除流程定义
 */
public class B_ProcessDefineTest {

    /**
     * 方式一： 直接部署
     */
    @Test
    public void deployProcessDef01() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 流程定义用的service就是repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        Deployment deployment = deploymentBuilder.addClasspathResource("diagram/holiday2.bpmn")
                .addClasspathResource("diagram/holiday2.png") // png文件非必须
                .deploy();
        //4.deployment输出部署的一些信息
        System.out.println(deployment.getName());
        System.out.println(deployment.getId());
    }

    /**
     * 方式二： zip 包部署
     *  流程制作出来后要上传到服务器 zip文件更便于上传
     */
    @Test
    public void deployProcessDef02() {
        //1.创建ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.转化出ZipInputStream流对象
        InputStream is = B_ProcessDefineTest.class.getClassLoader().getResourceAsStream("diagram/holidayBPMN.zip");
        //将 inputstream流转化为ZipInputStream流
        ZipInputStream zipInputStream = new ZipInputStream(is);
        //3.进行部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请单流程")
                .deploy();
        //4.输出部署的一些信息
        System.out.println(deployment.getName());
        System.out.println(deployment.getId());
    }

    /**
     * 流程定义查询
     */
    @Test
    public void queryProcessDef() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.创建RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.得到ProcessDefinitionQuery对象,可以认为它就是一个查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //4.设置条件，并查询出当前的所有流程定义   查询条件：流程定义的key=holiday
        //orderByProcessDefinitionVersion() 设置排序方式,根据流程定义的版本号进行排序
        List<ProcessDefinition> list = processDefinitionQuery.processDefinitionKey("holiday")
                .orderByProcessDefinitionVersion()
                .desc().list();

        //5.输出流程定义信息
        for(ProcessDefinition processDefinition :list){
            System.out.println("流程定义ID："+processDefinition.getId());
            System.out.println("流程定义名称："+processDefinition.getName());
            System.out.println("流程定义的Key："+processDefinition.getKey());
            System.out.println("流程定义的版本号："+processDefinition.getVersion());
            System.out.println("流程部署的ID:"+processDefinition.getDeploymentId());

        }
    }

    /**
     * 需求：
     * 1.从Activiti的act_ge_bytearray表中读取两个资源文件
     * 2.将两个资源文件保存到路径：   G:\Activiti7开发计划\Activiti7-day03\资料
     * 技术方案：
     *     1.第一种方式使用actviti的api来实现
     *     2.第二种方式：其实就是原理层面，可以使用jdbc的对blob类型，clob类型数据的读取，并保存
     *        IO流转换，最好commons-io.jar包可以轻松解决IO操作
     * 真实应用场景：用户想查看这个请假流程具体有哪些步骤要走？
     */
    @Test
    public void main() throws IOException {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.得到查询器:ProcessDefinitionQuery对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        //4.设置查询条件
        processDefinitionQuery.processDefinitionKey("holiday");//参数是流程定义的key

        //5.执行查询操作,查询出想要的流程定义
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        //6.通过流程定义信息，得到部署ID
        String deploymentId = processDefinition.getDeploymentId();

        //7.通过repositoryService的方法,实现读取图片信息及bpmn文件信息(输入流)
        //getResourceAsStream()方法的参数说明：第一个参数部署id,第二个参数代表资源名称
        //processDefinition.getDiagramResourceName() 代表获取png图片资源的名称
        //processDefinition.getResourceName()代表获取bpmn文件的名称
        InputStream pngIs = repositoryService
                .getResourceAsStream(deploymentId,processDefinition.getDiagramResourceName());
        InputStream bpmnIs = repositoryService
                .getResourceAsStream(deploymentId,processDefinition.getResourceName());

        //8.构建出OutputStream流
        OutputStream pngOs =
                new FileOutputStream("G:\\Activiti\\"+processDefinition.getDiagramResourceName());

        OutputStream bpmnOs =
                new FileOutputStream("G:\\Activiti\\"+processDefinition.getResourceName());

        //9.输入流，输出流的转换  commons-io-xx.jar中的方法
        IOUtils.copy(pngIs,pngOs);
        IOUtils.copy(bpmnIs,bpmnOs);
        //10.关闭流
        pngOs.close();
        bpmnOs.close();
        pngIs.close();
        bpmnIs.close();

    }

    /**
     * 流程定义删除
     * 注意事项：
     *     1.当我们正在执行的这一套流程没有完全审批结束的时候，此时如果要删除流程定义信息就会失败。
     *     // 因为外键关联
     *     2.如果公司层面要强制删除,可以使用repositoryService.deleteDeployment("1",true);
     *     //参数true代表级联删除，此时就会先删除没有完成的流程结点，最后就可以删除流程定义信息  false的值代表不级联
     */
    @Test
    public void delProcessDef() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.创建RepositoryService对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.执行删除流程定义  参数代表流程部署的id
        //特别注意是流程部署的id，不是流程定义的id,在act_re_deployment表中的id
        //流程部署删除了，对应的流程定义就删除了
        repositoryService.deleteDeployment("5001");
        System.out.println("success");
    }


    /**
     *  流程定义挂起|激活
     *  全部流程实例挂起与激活
     *  挂起之后实例的任务就不能执行了，直到激活
     *  如果需要挂起|激活单个流程实例的任务，可以在通过流程实例操作
     */
    @Test
    public void suspendedAndActiveTest() {
        //1.得到ProcessEngine对象
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        //2.得到RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //3.查询流程定义的对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("holiday").singleResult();

        //4.得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processDefinition.isSuspended();
        String processDefinitionId = processDefinition.getId();
        //5.判断
        if(suspended){
            //说明是暂停，就可以激活操作
            repositoryService.activateProcessDefinitionById(processDefinitionId,true
                    ,null);
            System.out.println("流程定义："+processDefinitionId+"激活");
        }else{
            repositoryService.suspendProcessDefinitionById(processDefinitionId,true,null);
            System.out.println("流程定义："+processDefinitionId+"挂起");
        }
    }
}

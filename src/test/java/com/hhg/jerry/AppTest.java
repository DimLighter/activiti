package com.hhg.jerry;


import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest {


    //初始化数据库  activiti框架需要表来存储流程  默认会建很多表的
    @BeforeClass
    public static void initProcessEngine(){
        ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
                .buildProcessEngine();
    }


    /*
     部署流程，将流程定义文件（xml，png）部署到activity框架的表中，运行后会改动以下表
      1：act_re_deployment ,相当于部署记录 有id和name
      2： act_ge_bytearray,部署的文件，有id，部署id，文件二进制
      3：act_re_procdef,流程定义，有id（启动流程可以用此id启动），name，key（启动流程也可以用key），版本，部署id，资源文件（xml，png）
      */
    @Test
    public void deploy(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Deployment deployment = processEngine.getRepositoryService().createDeployment()
                .addClasspathResource("qj.bpmn")
                .addClasspathResource("qj.png")
                .name("qjd")
                .deploy();
        System.out.println("deployment id:"+deployment.getId() + " name:" + deployment.getName());
    }

    /**
     * 查询所有的部署流程
     */
    @Test
    public void queryAllDeplyoment(){
        //得到流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Deployment> lists = processEngine.getRepositoryService()
                .createDeploymentQuery()
                .orderByDeploymenTime()//按照部署时间排序
                .desc()//按照降序排序
                .list();
        for (Deployment deployment:lists) {
            System.out.println("部署id:" + deployment.getId() +"    部署名称" + deployment.getName());
        }
    }

    /**
     * 查询所有的流程定义
     */
    @Test
    public void testQueryAllPD(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<ProcessDefinition> pdList = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
        for (ProcessDefinition pd : pdList) {
            System.out.println("ProcessDefinition id:" + pd.getId() + " name:" + pd.getName() + " deploymentId:" + pd.getDeploymentId());
        }
    }

    /**
     * 开始一个流程，找到流程定义表  通过id或者key开启（多个相同的key取最高版本的那个）
     * 1：act_ru_execution : 流程执行表插入一条数据 对应ProcessInstance对象?
     * 2：act_ru_task: 流程任务，即流程的第一个activity，流程开始了
     */
    @Test
    public void startProcessInstance(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById("qja:1:4");
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("qja");
        System.out.println("ProcessInstance id:" + processInstance.getId() + " proc_def_id:" + processInstance.getProcessDefinitionId()
            +" activiti_id:" + processInstance.getActivityId());
    }

    /**
     * 完成某个task，会流转到下一个task
     * 1： act_ru_execution的act_id会变化到下一个task的id
     * 2：act_ru_task会删除当前task并插入下一个流程的task
     * 3：act_hi_taskinst会插入一条被删除的task的记录
     */
    @Test
    public void completeTask(){
        ProcessEngines.getDefaultProcessEngine().getTaskService().complete("10004");
    }

    @Test
    public void testQueryTask(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> tasks = processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee("ted")
                .list();
        for (Task task : tasks) {
            System.out.println("Task id:" + task.getId() + " name:" + task.getName() +
                    " assignee:" + task.getAssignee() + " executionId : " + task.getExecutionId() + " procDefId:" + task.getProcessDefinitionId());
        }
    }

    /**
     * 删除已经部署的Activiti流程
     */
    @Test
    public void testDelete(){
        //得到流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //第一个参数是部署的流程的ID，第二个true表示是进行级联删除
        processEngine.getRepositoryService()
                .deleteDeployment("20001",true);
    }

    /**
     * 查看已经完成的任务和当前在执行的任务
     */
    @Test
    public void findHistoryTask(){
        ProcessEngine defaultProcessEngine = ProcessEngines.getDefaultProcessEngine();
        //如果只想获取到已经执行完成的，那么就要加入completed这个过滤条件
        List<HistoricTaskInstance> historicTaskInstances1 = defaultProcessEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskDeleteReason("completed")
                .list();
        //如果只想获取到已经执行完成的，那么就要加入completed这个过滤条件
        List<HistoricTaskInstance> historicTaskInstances2 = defaultProcessEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .list();
        System.out.println("执行完成的任务：" + historicTaskInstances1.size());
        System.out.println("所有的总任务数（执行完和当前未执行完）：" +historicTaskInstances2.size());
    }

    @Test
    public void testProcessDefinitionEntity(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        /**
         * 根据pdid得到ProcessDefinitionEntry,act_re_procdef
         */
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)processEngine.getRepositoryService()
                .getProcessDefinition("qja:2:17504");
        System.out.println();

    }

    /**
     * 根据pdid得到processDefinitionEntity中的activityimpl
     */
    @Test
    public void testGetActivityImpl(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        /**
         * 根据pdid得到ProcessDefinitionEntry
         */
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)processEngine.getRepositoryService()
                .getProcessDefinition("qja:2:17504");
        /**
         * ActivityImpl是一个对象
         * 一个activityImpl代表processDefinitionEntity中的一个节点
         */
        List<ActivityImpl> activityImpls = processDefinitionEntity.getActivities();
        for (ActivityImpl activityImpl : activityImpls) {
            System.out.print(activityImpl.getId());
            System.out.print(" hegiht:"+activityImpl.getHeight());
            System.out.print(" width:"+activityImpl.getWidth());
            System.out.print(" x:"+activityImpl.getX());
            System.out.println(" y:"+activityImpl.getY());
        }
    }

    /**
     * 得到ProcessDefinitionEntity中的所有的ActivityImpl的所有的PvmTransition
     */
    @Test
    public void testSequenceFlow(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        /**
         * 根据pdid得到ProcessDefinitionEntry
         */
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)processEngine.getRepositoryService()
                .getProcessDefinition("qja:2:17504");

        /**
         * ActivityImpl是一个对象
         * 一个activityImpl代表processDefinitionEntity中的一个节点
         */
        List<ActivityImpl> activityImpls = processDefinitionEntity.getActivities();
        for (ActivityImpl activityImpl : activityImpls) {
            /**
             * 得到一个activityimpl的所有的outgoing
             */
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitions) {
                System.out.println("sequenceFlowId:"+pvmTransition.getId());
            }
        }
    }

    /**
     * 得到当前正在执行的流程实例的activityimpl-->PvmTransition
     */
    @Test
    public void testQueryActivityImpl_Ing(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity)processEngine.getRepositoryService()
                .getProcessDefinition("qja:2:17504");
        //根据piid获取到activityId
        ProcessInstance pi = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId("32501")
                .singleResult();
        //根据流程实例得到当前正在执行的流程实例的正在执行的节点
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(pi.getActivityId());
        System.out.print("流程实例ID:"+pi.getId());
        System.out.print(" 当前正在执行的节点:"+activityImpl.getId());
        System.out.print(" hegiht:"+activityImpl.getHeight());
        System.out.print(" width:"+activityImpl.getWidth());
        System.out.print(" x:"+activityImpl.getX());
        System.out.println(" y:"+activityImpl.getY());
    }
}

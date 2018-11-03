package com.hhg.jerry;


import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
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
                .name("qjaa")
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
            System.out.println(deployment.getId() +"    部署名称" + deployment.getName());
        }
    }

    /**
     * 开始一个流程，找到流程定义表  通过id或者key开启（多个相同的key取最高版本的那个）
     * 1：act_ru_execution : 流程执行表插入一条数据 对应ProcessInstance对象
     * 2：act_ru_task: 流程任务，即流程的第一个activity，流程开始了
     */
    @Test
    public void startProcessInstance(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById("qja:1:4");
        System.out.println("ProcessInstance id:" + processInstance.getId() + " name:" + processInstance.getName());
    }

    /**
     * 完成某个task，会流转到下一个task
     * 1： act_ru_execution的act_id会变化到下一个task的id
     * 2：act_ru_task会删除当前task并插入下一个流程的task
     * 3：act_hi_taskinst会插入一条被删除的task的记录
     */
    @Test
    public void completeTask(){
        ProcessEngines.getDefaultProcessEngine().getTaskService().complete("12504");
    }


}

package com.hhg.jerry;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.BeforeClass;

import java.util.Map;

/**
 * Created by lina on 2018/11/4.
 */
public class ActivitiBaseTest {

    @BeforeClass
    public static void initProcessEngine(){
        ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml")
                .buildProcessEngine();
    }

    public void deploy(String fileName, String deployName){
        String bpmnFile = fileName + ".bpmn";
        String pngFile = fileName + ".png";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Deployment deployment = processEngine.getRepositoryService().createDeployment()
                .addClasspathResource(bpmnFile)
                .addClasspathResource(pngFile)
                .name(deployName)
                .deploy();
        System.out.println("deployment id:"+deployment.getId() + " name:" + deployment.getName());
    }

    public void startProcessInstanceById(String key){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(key);
        System.out.println("ProcessInstance id:" + processInstance.getId() + " proc_def_id:" + processInstance.getProcessDefinitionId()
                +" activiti_id:" + processInstance.getActivityId());
    }

    public void startProcessInstanceById(String key, Map<String, Object> map){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(key, map);
        System.out.println("ProcessInstance id:" + processInstance.getId() + " proc_def_id:" + processInstance.getProcessDefinitionId()
                +" activiti_id:" + processInstance.getActivityId());
    }

    public void completeTask(String taskId){
        ProcessEngines.getDefaultProcessEngine().getTaskService().complete(taskId);
    }

    public void completeTask(String taskId, Map<String, Object> map){
        ProcessEngines.getDefaultProcessEngine().getTaskService().complete(taskId, map);
    }
}

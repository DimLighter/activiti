package com.hhg.jerry;

import com.sun.javafx.tk.Toolkit;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lina on 2018/11/4.
 */
public class VariableTest {

    @Test
    public void startInstanceWithVariable(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Map<String, Object> map = new HashMap(){};
        map.put("name","dennis");
        map.put("age",30);
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("qja", map);
        System.out.println("ProcessInstance id:" + processInstance.getId() + " proc_def_id:" + processInstance.getProcessDefinitionId()
                +" activiti_id:" + processInstance.getActivityId());

        String name = (String)processEngine.getRuntimeService().getVariable(processInstance.getId(), "name");
        Integer age = (Integer)processEngine.getRuntimeService().getVariable(processInstance.getId(), "age");

        System.out.println(name);
        System.out.println(age);
    }

    @Test
    public void taskVariable(){
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        User user = new User();
        user.setId(111L);
        user.setName("weimin");
        user.setAge(31);
        User uuu = (User)taskService.getVariable("35006","user");
        taskService.setVariable("35006","user", user);
//        Task task = processEngine.getTaskService().createTaskQuery().taskId("35006").singleResult();
    }
}

package com.hhg.jerry.gateway;

import com.hhg.jerry.ActivitiBaseTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lina on 2018/11/4.
 */
public class ExclusiveTest extends ActivitiBaseTest {

    @Test
    public void deployTest(){
        deploy("exclusivegateway", "exclusiveDeployNamefixBug");
    }

    @Test
    public void startProcessInstanceTest(){
        Map<String, Object> map = new HashMap();
        map.put("employee","dennis");
        startProcessInstanceById("exclusiveId", map);
    }

    @Test
    public void completeTest(){
        Map<String, Object> map = new HashMap();
        map.put("money",99);
        completeTask("45005", map);
    }
}

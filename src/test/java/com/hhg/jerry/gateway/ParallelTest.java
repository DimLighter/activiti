package com.hhg.jerry.gateway;

import com.hhg.jerry.ActivitiBaseTest;
import org.junit.Test;

/**
 * Created by lina on 2018/11/4.
 * 分两种  fork join
 * fork：直接从当前活动到网关后定义的所有活动
 * join : 等待所有条件进入 则通过该网关
 * 并行网关不会解析条件。 即使顺序流中定义了条件，也会被忽略。
 *
 */
public class ParallelTest extends ActivitiBaseTest{

    @Test
    public void deployTest(){
        deploy("parallelgateway","paraDeployName");
    }

    @Test
    public void startProcessInstanceTest(){
        startProcessInstanceById("paraTestId");
    }

    @Test
    public void complete(){
        completeTask("12503");
    }
}

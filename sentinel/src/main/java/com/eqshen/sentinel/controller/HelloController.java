package com.eqshen.sentinel.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSONObject;
import com.eqshen.sentinel.constant.SentinelResourceConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/hello")
@Slf4j
public class HelloController {

    /**
     * 通过抛出异常的方式
     * @return
     */
    @GetMapping("/sayHello")
    public JSONObject sayHello(){
        JSONObject result = new JSONObject();
        try (final Entry entry = SphU.entry(SentinelResourceConstant.SAY_HELLO)){
            result.put("success",true);
            result.put("msg","欢迎!");
        }catch (Exception e){
            result.put("success",false);
            result.put("msg","请求过于频繁！");
            log.error("触发熔断！！！");
        }
        return result;
    }

    @GetMapping("/sayBye")
    public JSONObject sayBye(){
        JSONObject result = new JSONObject();
        if(SphO.entry(SentinelResourceConstant.SAY_BYE)){
            try{
                result.put("success",true);
                result.put("msg","再见!");
            }finally {
                //必须和SphO.entry 成对出现
                SphO.exit();
            }
        }else {
            result.put("success",false);
            result.put("msg","请求过于频繁！");
            log.error("触发熔断！！！");
        }
        return result;
    }

    /**
     * 注解方式
     * 需要通过dashboard控制台添加限流规则
     * @return
     */
    @GetMapping("/sayBye2")
    @SentinelResource(value = SentinelResourceConstant.SAY_BYE2,
            blockHandler = "blockHandlerForBye2")
    public JSONObject sayBye2(){
        JSONObject result = new JSONObject();
        result.put("success",true);
        result.put("msg","再见2!");
        return result;
    }

    private JSONObject blockHandlerForBye2(BlockException e){
        JSONObject result = new JSONObject();
        result.put("success",false);
        result.put("msg","请求过于频繁！");
        log.error(">>>> 触发熔断，bye2");
        return result;
    }

    /**
     * 定义sentinel规则
     */
    @PostConstruct
    private void initFlowRules(){
        List<FlowRule> flowRules = new ArrayList<>();

        FlowRule flowRule = new FlowRule();
        flowRule.setResource(SentinelResourceConstant.SAY_HELLO);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_QPS);//限流规则类型
        flowRule.setCount(2); //qps阈值，每秒最多通过该值


        FlowRule flowRule2 = new FlowRule();
        flowRule2.setResource(SentinelResourceConstant.SAY_BYE);
        flowRule2.setGrade(RuleConstant.FLOW_GRADE_QPS);//限流规则类型
        flowRule2.setCount(2); //qps阈值，每秒最多通过该值

        flowRules.add(flowRule);
        flowRules.add(flowRule2);
        FlowRuleManager.loadRules(flowRules);
    }
}

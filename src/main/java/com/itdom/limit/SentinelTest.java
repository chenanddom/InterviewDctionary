package com.itdom.limit;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import sun.misc.FloatConsts;

import java.util.ArrayList;
import java.util.List;

/**
 * 一句话的功能说明
 * <p>
 * 限流算法的实现
 * @author administer
 * @date 2021/9/9
 * @since 1.0.0
 */
public class SentinelTest {
    public static void main(String[] args) {
      List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("tutorial");
        rule.setCount(1);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
        while (true){
            Entry entry = null;
            try {
                SphU.entry("tutorial");
                System.out.println("hello demo");
            } catch (BlockException e) {
                System.out.println("blocked");
            }finally {
                if (entry!=null){
                    entry.exit();
                }
            }
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

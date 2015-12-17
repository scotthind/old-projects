package userinterface.util;

import java.util.List;

import rulemodule.util.RuleFiredEvent;

/**
 * The RuleListener interface listens for incoming rules, ruleIDs, and RuleFiredEvents from the Rule Module
 * @author Kevin Desmond
 */
public interface RuleListener {
	void didReceiveRule(String ruleID);
	void didReceiveAvailableRules(List<String> availableRules);
	void didReceiveRuleFiredEvents(List<RuleFiredEvent> ruleFiredEvents);
}
